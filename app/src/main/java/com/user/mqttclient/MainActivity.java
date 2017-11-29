package com.user.mqttclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.library.bubbleview.BubbleTextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    MqttService mMqttService = new MqttService();
    private String mBrokerUrl = "tcp://192.168.199.56:1883";
    private String mClientID = "clintID";
    private String mUser = "USER0";
    private String mPassWord = "admin";
    private String mTextPub;
    private String mTopicName = "ANYUSER";
    private int qos = 2;

    private EditText mEditTextPub;
    private Button mBtnPub;
    private RecyclerView mRecyclerView;
    private bubbleAdapter mBubbleAdapter;

    List<DataModel> updateMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycle_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBubbleAdapter = new bubbleAdapter();
        mRecyclerView.setAdapter(mBubbleAdapter);
        initMessage();

        mEditTextPub = findViewById(R.id.editText_pub);
        mEditTextPub.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged");
                if (mEditTextPub.getText().length() > 0) {
                    mBtnPub.setEnabled(true);
                } else {
                    mBtnPub.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged");
            }
        });
        mBtnPub = findViewById(R.id.btn_pub);
        if (mEditTextPub.getText().length() == 0) {
            mBtnPub.setEnabled(false);
        } else {
            mBtnPub.setEnabled(true);
        }
        mBtnPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextPub = mEditTextPub.getText().toString();
                Log.d(TAG, "mTextPub:" + mTextPub);
                updateMessage(mTextPub, DataModel.TYPE_PUBLISH);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mMqttService.publish(mTopicName, qos, mTextPub);
                    }
                }).start();
            }
        });
        EventBus.getDefault().register(this);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmsss");
        String time = format.format(new Date());
        mClientID = mClientID + time;
        Log.d(TAG, "mClientID: " + mClientID);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMqttService.init(getApplicationContext(), mBrokerUrl, mClientID, mUser, mPassWord);
            }
        }).start();
    }

    @Subscribe
    public void onEvent(MqttMessage message) {
        Log.d(TAG, "onEvent message: " + message.toString());
        updateMessage(message.toString(), DataModel.TYPE_SUBSCRIBE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mMqttService.disConnect();
        super.onDestroy();
    }

    private class bubbleAdapter extends RecyclerView.Adapter {
        private List<DataModel> mMessageList = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
            RecyclerView.ViewHolder viewHolder = null;
            if (viewType == DataModel.TYPE_SUBSCRIBE) {
                View mView = mInflater.inflate(R.layout.item_client, parent, false);
                viewHolder = new clientHolder(mView);
            } else if (viewType == DataModel.TYPE_PUBLISH) {
                View mView = mInflater.inflate(R.layout.item_host, parent, false);
                viewHolder = new hostHolder(mView);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof clientHolder) {
                ((clientHolder) holder).mBubbleTxtView.setText(this.mMessageList.get(position).mContent);
            } else {
                ((hostHolder) holder).mBubbleTxtView.setText(this.mMessageList.get(position).mContent);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return this.mMessageList.get(position).mType;
        }

        @Override
        public int getItemCount() {
            return this.mMessageList.size();
        }


        public void setData(List<DataModel> model) {
            this.mMessageList.addAll(model);
        }

        public void setData(DataModel model) {
            this.mMessageList.add(model);
        }

        private class hostHolder extends RecyclerView.ViewHolder {
            BubbleTextView mBubbleTxtView;

            public hostHolder(View itemView) {
                super(itemView);
                mBubbleTxtView = itemView.findViewById(R.id.textView);
            }
        }

        private class clientHolder extends RecyclerView.ViewHolder {
            BubbleTextView mBubbleTxtView;

            public clientHolder(View itemView) {
                super(itemView);
                mBubbleTxtView = itemView.findViewById(R.id.textView);
            }
        }
    }

    public void initMessage() {
        mBubbleAdapter.setData(updateMessage);
        mBubbleAdapter.notifyDataSetChanged();
    }

    public void updateMessage(String content, int type) {
        DataModel dataMessage = new DataModel();

        dataMessage.mContent = content;
        dataMessage.mType = type;

        mBubbleAdapter.setData(dataMessage);
        mBubbleAdapter.notifyDataSetChanged();
    }

    public class DataModel {
        public static final int TYPE_PUBLISH = 1;
        public static final int TYPE_SUBSCRIBE = 2;

        public String mContent;
        public int mType;
    }
}