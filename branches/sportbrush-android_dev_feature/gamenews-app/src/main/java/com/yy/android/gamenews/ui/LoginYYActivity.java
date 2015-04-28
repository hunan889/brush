package com.yy.android.gamenews.ui;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.PlatType;
import com.duowan.gamenews.UserInitReq;
import com.duowan.gamenews.UserInitRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.LoginStatusEvent;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;
import com.yy.udbsdk.UICalls;
import com.yy.udbsdk.UIError;
import com.yy.udbsdk.UIListener;

import de.greenrobot.event.EventBus;

public class LoginYYActivity extends BaseActivity implements OnClickListener {

	private static final int ERROR_CODE_LOGIN_FAIL = -9;

	private EditText mUserName;
	private EditText mPassword;
	private ActionBar mActionBar;
	private String from;
	private String url;

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	private Dialog mLoginingDialog;
	private static final String LOG_TAG = LoginYYActivity.class.getSimpleName();

	public static void loginAfterRegister(Context context, String username,
			String password) {
		Intent intent = new Intent(context, LoginYYActivity.class);
		intent.putExtra(USERNAME, username);
		intent.putExtra(PASSWORD, password);
		context.startActivity(intent);
	}

	public static void startLoginActivityForResult(Activity context) {
		Intent intent = new Intent(context, LoginYYActivity.class);
		context.startActivityForResult(intent, Constants.REQUEST_LOGIN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		setContentView(R.layout.activity_yy_login);

		mUserName = (EditText) findViewById(R.id.et_username);
		mPassword = (EditText) findViewById(R.id.et_password);

		mPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					login();
					return true;
				}
				return false;
			}
		});

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		if (getIntent().getStringExtra(USERNAME) != null
				&& getIntent().getStringExtra(PASSWORD) != null) {
			mUserName.setText(getIntent().getStringExtra(USERNAME));
			mPassword.setText(getIntent().getStringExtra(PASSWORD));
			login();
		}
		if (getIntent().getStringExtra(AppWebActivity.FROM) != null) {
			from = getIntent().getStringExtra(AppWebActivity.FROM);
			url = getIntent().getStringExtra(AppWebActivity.FROM_KEY_RUL);
		}
		super.onCreate(savedInstanceState);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login: {
			login();
			break;
		}
		case R.id.btn_get_pwd: {
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Constants.UDB_FORGET_PASSWORD_URL));
			startActivity(intent);
			break;
		}
		case R.id.btn_register: {

			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(Constants.UDB_REGIST_URL));
			startActivity(intent);
			// Intent intent = new Intent(this, RegisterActivity.class);
			// startActivity(intent);
			// UICalls.doRegister(this, mUiListener, null);
			break;
		}
		}
	}

	private void login() {
		String username = mUserName.getText().toString();
		String password = mPassword.getText().toString();

		if (username == null || "".equals(username)) {
			Toast.makeText(this, R.string.my_msg_login_no_name,
					Toast.LENGTH_LONG).show();
			mUserName.requestFocus();
			return;
		}

		if (password == null || "".equals(password)) {
			Toast.makeText(this, R.string.my_msg_login_no_pswd,
					Toast.LENGTH_LONG).show();
			mPassword.requestFocus();
			return;
		}

		mLoginingDialog = UiUtils.loginingDialogShow(this);
		Bundle params = new Bundle();
		params.putBoolean("noUIMode", true);
		params.putString("username", mUserName.getText().toString());
		params.putString("userpwd", mPassword.getText().toString());
		UICalls.setTestMode(false);// Mode: true – 测试环境(默认情况), false – 使用生产环境
		UICalls.doLogin(this, mUiListener, params);
	}

	private UIListener mUiListener = new UIListener() {

		@Override
		public void onError(UIError arg0) {
			if (UIError.R_ERR_WRONG_PWD == arg0.errorCode) {
				ToastUtil.showToast(R.string.my_msg_login_wrong_pwd);
			} else if (UIError.RR_ERR_PARSE_URL == arg0.errorCode) {
				ToastUtil.showToast(R.string.my_msg_login_server_error);
			} else {
				ToastUtil.showToast(R.string.my_msg_login_fail);
			}
			Log.d(LOG_TAG, "errorMessage = " + arg0.errorMessage
					+ ", errorDetail = " + arg0.errorDetail);
			UiUtils.dialogDismiss(mLoginingDialog);
		}

		@Override
		public void onDone(Bundle data) {
			String event_s = data.getString("event");
			String msg = "";
			if (event_s.equals("doRegister") || event_s.equals("doRegisterWeb")) {
				long yyuid = data.getLong("yyuid", 0);
				long yyid = data.getLong("yyid", 0);
				String uname = data.getString("uname");

				msg = "register success! (yyuid is:" + Long.toString(yyuid)
						+ ", yyid is:" + Long.toString(yyid) + ", uname is:"
						+ uname;
			} else if (event_s.equals("doLogin")
					|| event_s.equals("doYYTicketLogin")) {
				long yyuid = data.getLong("yyuid");
				String userName = data.getString("uname");
				byte[] token = UICalls.getTokens(Constants.YY_APP_ID);
				String tokenStr = null;
				try {
					tokenStr = new String(token, "utf-8");
				} catch (UnsupportedEncodingException e) {
					Log.e(LOG_TAG, e.getMessage());
					return;
				}
				//
				UserInitReq req = new UserInitReq();
				req.setUserName(userName);
				req.setPlatType(PlatType._PLAT_TYPE_YY);
				Map<Integer, String> map = new HashMap<Integer, String>();
				map.put(0, tokenStr);
				map.put(1, yyuid + "");
				req.setSocialAccessToken(map);
				// req.setUserIcon("http://mtq.yy.com/static/yylogo/1.jpg");

				InitModel.sendUserInitReq(LoginYYActivity.this, mRspListener,
						req, true);
			}
			Log.d(LOG_TAG, "msg = " + msg);

		}

		@Override
		public void onCancel() {
			UiUtils.dialogDismiss(mLoginingDialog);
			Log.d(LOG_TAG, "cancelled");
		}
	};

	private ResponseListener<UserInitRsp> mRspListener = new ResponseListener<UserInitRsp>(
			this) {
		public void onResponse(UserInitRsp rsp) {
			UiUtils.dialogDismiss(mLoginingDialog);
			Preference.getInstance().setLoginType(PlatType._PLAT_TYPE_YY);
			Preference.getInstance().saveInitRsp(rsp);
			if (rsp != null) {
				Intent intent = new Intent();
				intent.putExtra(Constants.EXTRA_USER_INIT_RSP, rsp);
				setResult(Constants.RESULT_LOGIN, intent);
				EventBus.getDefault().post(new LoginStatusEvent(true));
				finish();
			}
		};

		public void onError(Exception e) {
			UiUtils.dialogDismiss(mLoginingDialog);
		};
	};

}
