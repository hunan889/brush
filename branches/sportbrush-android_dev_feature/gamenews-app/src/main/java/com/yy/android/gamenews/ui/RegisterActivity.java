package com.yy.android.gamenews.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;
import com.yy.udbsdk.UICalls;
import com.yy.udbsdk.UIError;
import com.yy.udbsdk.UIListener;

public class RegisterActivity extends FragmentActivity {
    private static final int SEND_INTERVAL = 60;
    private EditText mPhone;
    private TextView mSend;
    private LinearLayout mSendContainer;
    private ProgressBar mSending;
    private TextView mSendTips;
    private View mNextStep;
    private EditText mCode;
    private EditText mPassword;
    private LinearLayout mRegisterContainer;
    private ProgressBar mRegistering;
    private TextView mRegisterTips;
    private TextView mOk;
    private long mLastTime;
    private boolean mWaitingCallback;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            int passed = (int) ((System.nanoTime() - mLastTime) / 1000000000L);
            if (passed >= SEND_INTERVAL) {
                mSend.setText("发送");
                return;
            }
            mSend.setText(getString(R.string.my_register_wait, SEND_INTERVAL
                    - passed));
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(R.string.register_title);

        mPhone = (EditText) findViewById(R.id.phone);
        mSend = (TextView) findViewById(R.id.send);
        mSendContainer = (LinearLayout) findViewById(R.id.send_container);
        mSending = (ProgressBar) findViewById(R.id.sending);
        mSendTips = (TextView) findViewById(R.id.send_tips);
        mNextStep = findViewById(R.id.next_step);
        mCode = (EditText) findViewById(R.id.code);
        mPassword = (EditText) findViewById(R.id.password);
        mRegisterContainer = (LinearLayout) findViewById(R.id.register_container);
        mRegistering = (ProgressBar) findViewById(R.id.registering);
        mRegisterTips = (TextView) findViewById(R.id.register_tips);
        mOk = (TextView) findViewById(R.id.ok);
        mSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                send();
            }
        });
        mOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    private void send() {
        if (mWaitingCallback) {
            return;
        }
        int passed = (int) ((System.nanoTime() - mLastTime) / 1000000000L);
        if (passed < SEND_INTERVAL) {
            return;
        }

        if (mPhone.getText().length() != 11) {
            mSendTips.setText(R.string.my_register_error_length);
            mSendContainer.setVisibility(View.VISIBLE);
            return;
        }

        if (!Util.isNetworkConnected()) {
            mSendTips.setText(R.string.global_network_error);
            mSendContainer.setVisibility(View.VISIBLE);
            return;
        }

        mWaitingCallback = true;
        mPhone.setEnabled(false);
        mSending.setVisibility(View.VISIBLE);
        mSendTips.setText("");
        mSendContainer.setVisibility(View.VISIBLE);
        Bundle params = new Bundle();
//        params.putBoolean("noUIMode", true);
        params.putString("mobile", mPhone.getText().toString());
        
        UICalls.sendSmsVerifyForReg(this, new UIListener() {
            public void onCancel() {
                // not reached
            }

            public void onDone(Bundle data) {
                mWaitingCallback = false;
                mSending.setVisibility(View.INVISIBLE);
                mSendTips.setText(R.string.my_register_code_sent);
                mLastTime = System.nanoTime();
                mRunnable.run();
                mNextStep.setVisibility(View.VISIBLE);
                mOk.setEnabled(true);
//                StatService.onEvent(GameNewsApplication.getInstance(),
//                        "register_step_1", "成功", 1);
            }

            public void onError(UIError error) {
                mWaitingCallback = false;
                if (mNextStep.getVisibility() != View.VISIBLE) {
                    mPhone.setEnabled(true);
                }
                mSending.setVisibility(View.INVISIBLE);
                if (error.errorCode == UIError.R_ERR_REGISTERED) {
                    mSendTips.setText(R.string.my_register_error_registered);
                } else {
                    mSendTips.setText(getString(R.string.my_register_send_fail,
                            error.errorCode));
                }
//                StatService.onEvent(GameNewsApplication.getInstance(),
//                        "register_step_1", String.valueOf(error.errorCode)
//                                + " " + error.errorMessage, 1);
            }
        }, params);
        
//        
//        
//        UICalls.doRegister(this, new UIListener() {
//            public void onCancel() {
//                // not reached
//            }
//
//            public void onDone(Bundle data) {
//                mWaitingCallback = false;
//                mSending.setVisibility(View.INVISIBLE);
//                mSendTips.setText(R.string.my_register_code_sent);
//                mLastTime = System.nanoTime();
//                mRunnable.run();
//                mNextStep.setVisibility(View.VISIBLE);
//                mOk.setEnabled(true);
////                StatService.onEvent(GameNewsApplication.getInstance(),
////                        "register_step_1", "成功", 1);
//            }
//
//            public void onError(UIError error) {
//                mWaitingCallback = false;
//                if (mNextStep.getVisibility() != View.VISIBLE) {
//                    mPhone.setEnabled(true);
//                }
//                mSending.setVisibility(View.INVISIBLE);
//                if (error.errorCode == UIError.R_ERR_REGISTERED) {
//                    mSendTips.setText(R.string.my_register_error_registered);
//                } else {
//                    mSendTips.setText(getString(R.string.my_register_send_fail,
//                            error.errorCode));
//                }
////                StatService.onEvent(GameNewsApplication.getInstance(),
////                        "register_step_1", String.valueOf(error.errorCode)
////                                + " " + error.errorMessage, 1);
//            }
//        }, params);
    }

    private void register() {
        if (mWaitingCallback) {
            return;
        }

        if (mCode.getText().length() == 0) {
            mRegisterContainer.setVisibility(View.VISIBLE);
            mRegisterTips.setText(R.string.my_register_error_verifier_empty);
            return;
        }

        if (!Util.isNetworkConnected()) {
            mRegisterContainer.setVisibility(View.VISIBLE);
            mRegisterTips.setText(R.string.global_network_error);
            return;
        }

        mWaitingCallback = true;
        mOk.setEnabled(false);
        mRegisterContainer.setVisibility(View.VISIBLE);
        mRegistering.setVisibility(View.VISIBLE);
        mRegisterTips.setText("");
        Bundle params = new Bundle();
        params.putBoolean("noUIMode", true);
        params.putInt("step", 2);
        params.putString("phone", mPhone.getText().toString());
        params.putString("userpwd", mPassword.getText().toString());
        params.putString("verifier", mCode.getText().toString());
        UICalls.doRegister(this, new UIListener() {
            public void onCancel() {
                // not reached
            }

            public void onDone(Bundle data) {
                mWaitingCallback = false;
                Toast.makeText(RegisterActivity.this,
                        R.string.my_register_success, Toast.LENGTH_SHORT).show();
                String uname = data.getString("uname");
                LoginYYActivity.loginAfterRegister(RegisterActivity.this, uname,
                        mPassword.getText().toString());
                finish();
//                StatService.onEvent(GameNewsApplication.getInstance(),
//                        "register_step_2", "成功", 1);
            }

            public void onError(UIError error) {
                mWaitingCallback = false;
                mOk.setEnabled(true);
                mRegistering.setVisibility(View.INVISIBLE);
                if (error.errorCode == UIError.R_ERR_VERIFIER) {
                    mRegisterTips.setText(R.string.my_register_error_verifier);
                } else {
                    mRegisterTips.setText(getString(R.string.my_register_fail,
                            error.errorCode));
                }
//                StatService.onEvent(GameNewsApplication.getInstance(),
//                        "register_step_2", String.valueOf(error.errorCode)
//                                + " " + error.errorMessage, 1);
            }
        }, params);
    }
}
