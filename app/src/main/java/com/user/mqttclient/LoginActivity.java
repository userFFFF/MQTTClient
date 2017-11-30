package com.user.mqttclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by user0 on 2017/11/30.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Button mBtn_Login;
    private Button mBtn_Cancel;
    private EditText mEditText_User;
    private EditText mEditText_Password;
    private CheckBox mCheckBox_Hint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(mFinishReceiver, filter);

        setContentView(R.layout.activity_login);
        mBtn_Login = findViewById(R.id.btn_login);
        mEditText_User = findViewById(R.id.edit_user);
        mEditText_Password = findViewById(R.id.edit_password);
        if (mEditText_User.getText().length() > 0 && mEditText_Password.getText().length() > 0) {
            onSetButton(mBtn_Login, true);
        } else {
            onSetButton(mBtn_Login, false);
        }
        mBtn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEditText_User.getText().toString();
                String password = mEditText_Password.getText().toString();
                Log.d(TAG, "username: " + username + " password: " + password);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        mBtn_Cancel = findViewById(R.id.btn_cancel);
        mBtn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditText_User.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditText_User.getText().length() > 0 && mEditText_Password.getText().length() > 0) {
                    onSetButton(mBtn_Login, true);
                } else {
                    onSetButton(mBtn_Login, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEditText_User.getText().length() > 0 && mEditText_Password.getText().length() > 0) {
                    onSetButton(mBtn_Login, true);
                } else {
                    onSetButton(mBtn_Login, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCheckBox_Hint = findViewById(R.id.checkbox_hint);
        mCheckBox_Hint.setActivated(false);
        mCheckBox_Hint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEditText_Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mEditText_Password.setSelection(mEditText_Password.getText().length());
                } else {
                    mEditText_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEditText_Password.setSelection(mEditText_Password.getText().length());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onSetButton(Button button, boolean enable) {
        if (enable) {
            button.setTextColor(0xFF25AE90);
            button.setEnabled(enable);
        } else {
            button.setTextColor(0xFFB5CEB0);
            button.setEnabled(enable);
        }
    }

    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("finish".equals(intent.getAction())) {
                Log.e("#########", "I am " + getLocalClassName()
                        + ",now finishing myself...");
                finish();
            }
        }
    };
}