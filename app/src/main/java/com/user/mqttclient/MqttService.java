package com.user.mqttclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by user0 on 2017/11/23.
 */

public class MqttService {
    private static final String TAG = "MqttService";
    private static MqttAndroidClient mClient;
    private MqttConnectOptions mMqttConOpt;
    //private String mClintID;
    private String mTopicName = "ANYUSER";
    private Context mContext;

    public void init(Context context, String brokerUrl, String clientID, String userName, String password) {
        mContext = context;
        mClient = new MqttAndroidClient(context, brokerUrl, clientID);
        mClient.setCallback(mMqttCallback);

        mMqttConOpt = new MqttConnectOptions();
        mMqttConOpt.setCleanSession(true);
        mMqttConOpt.setConnectionTimeout(10);
        mMqttConOpt.setKeepAliveInterval(20);
        if (userName != null) {
            mMqttConOpt.setUserName(userName);
        }
        if (password != null) {
            mMqttConOpt.setPassword(password.toCharArray());
        }

        String message = "{\"terminal_uid\":\"" + clientID + "\"}";
        mMqttConOpt.setWill(mTopicName, message.getBytes(), 0, false);
        mMqttConOpt.setAutomaticReconnect(true);

        connect();

    }

    public void connect() {
        if (mClient != null && !mClient.isConnected() && isNetworkConnected()) {
            try {
                mClient.connect(mMqttConOpt, mContext, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "connect onSuccess");
                        subscribe("#", 2);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "connect onFailure");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void disConnect() {
        if (mClient != null && mClient.isConnected()) {
            try {
                mClient.disconnect(mContext, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "disconnect onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "disconnect onFailure");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean publish(String topicName, int qos, String payload) {
        Log.i(TAG, "topicName: " + topicName + " qos: " + qos + " payload: " + payload);
        return publish(topicName, qos, payload.getBytes());
    }

    public boolean publish(String topicName, int qos, byte[] payload) {
        if (mClient != null && mClient.isConnected()) {
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);
            Log.i(TAG, "publish topicName: " + topicName + " qos: " + qos + " message: " + message.toString());

            try {
                mClient.publish(topicName, message);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean subscribe(String topicName, int qos) {
        if (mClient != null && mClient.isConnected()) {
            Log.i(TAG, "subscribe topicName: " + topicName + " qos: " + qos);
            try {
                mClient.subscribe(topicName, qos);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private MqttCallback mMqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "topic: " + topic + " message: " + message.toString());
            EventBus.getDefault().post(message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "deliveryComplete");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "connectionLost");
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "Network INFO：" + name);
            return true;
        } else {
            Log.i(TAG, "Network IS NOT CONNECTED");
            return false;
        }
    }
}