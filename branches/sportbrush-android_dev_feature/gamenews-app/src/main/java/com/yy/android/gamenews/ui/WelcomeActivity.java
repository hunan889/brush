package com.yy.android.gamenews.ui;

import java.io.IOException;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.RadioGroup;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ServerConstants;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.util.AppDetailUpgradeTask;
import com.yy.android.gamenews.util.AppInitTask;
import com.yy.android.gamenews.util.AppInitTask.OnAppInitTaskListener;
import com.yy.android.gamenews.util.FileUtil;
import com.yy.android.gamenews.util.MessageAsyncTask;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class WelcomeActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = WelcomeActivity.class.getSimpleName();
	private boolean mFromNotice = false;
	private AppInitTask mAppInitTask;
	// private Button mDev;
	// private Button mPre;
	// private Button mIdc;

	private RadioGroup mEnvGroup;
	private RadioGroup mAppGroup;
	private Button mCustOkBtn;
	private String ipUrl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(LOG_TAG, "[onCreate] +");
		// StatService.trackCustomEvent(this, "onCreate", "");
		setContentView(R.layout.activity_welcome);
		String channelName = getString(R.string.channelname);
		if (Util.inTest(channelName)) {
			initTest("dev".equals(channelName));
		} else {
			init();
		}
	}

	private void initTest(boolean showChangeAppBtn) {
		String ip = Preference.getInstance().getTestIp();
		String url = Preference.getInstance().getTestUrl();
		int appType = Preference.getInstance().getTestAppType();
		if (TextUtils.isEmpty(url)) {

			ViewStub stub = (ViewStub) findViewById(R.id.app_custom_stub);
			stub.inflate();

			mCustOkBtn = (Button) findViewById(R.id.ok);
			mCustOkBtn.setOnClickListener(this);
			mEnvGroup = (RadioGroup) findViewById(R.id.cust_env_group);
			mAppGroup = (RadioGroup) findViewById(R.id.cust_app_group);

			if (!showChangeAppBtn) {
				mAppGroup.setVisibility(View.GONE);
			}
		} else {
			BaseModel.HOST = url;
			String app = initAppType(appType);
			String env = getEnvToast(ip);

			ToastUtil.showToast(app + " " + env);
			init();
		}

	}

	private void init() {
		mAppInitTask = new AppInitTask(this);
		mAppInitTask.setOnAppInitTaskListener(mOnAppInitTaskListener);
		mAppInitTask.execute();
//		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//			new MessageAsyncTask(this).execute();
//		}
		copyAssetsData();
		handleIntent();
		String[] channelNames = getResources().getStringArray(
				R.array.custom_channel_name);
		if (channelNames != null && channelNames.length > 0) {
			for (String customChannelName : channelNames) {
				String currentChannelName = getString(R.string.channelname);
				if (customChannelName != null
						&& customChannelName.trim().equals(currentChannelName)) {
					findViewById(R.id.img_custom).setVisibility(View.VISIBLE);
					break;
				}
			}
		}
	}

	private void copyAssetsData() {
		AppDetailUpgradeTask mAppDetailUpgradeTask = new AppDetailUpgradeTask(
				this);
		mAppDetailUpgradeTask.execute();
	}

	private OnAppInitTaskListener mOnAppInitTaskListener = new OnAppInitTaskListener() {
		public void onTaskFinished() {
			if (!mFromNotice) {
				startActivity(new Intent(WelcomeActivity.this,
						MainActivity.class));
				finish();
			}
		};
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mAppInitTask != null) {
			mAppInitTask.endTask();
		}
		super.onDestroy();
	}

	public void handleIntent() {
		mFromNotice = false;
		Intent intent = getIntent();
		if (intent != null) {
			int type = intent.getIntExtra(Constants.PUSH_TYPE, -1);
			long id = intent.getLongExtra(Constants.PUSH_ID, -1);
			String url = intent.getStringExtra(Constants.PUSH_URL);

			if (type != -1 && (id != -1 || url != null)) {
				Intent intent2 = new Intent(WelcomeActivity.this,
						MainActivity.class);
				if (id != -1) {
					intent2.putExtra(Constants.PUSH_ID, id);
				}
				if (url != null) {
					intent2.putExtra(Constants.PUSH_URL, url);
				}
				intent2.putExtra(Constants.PUSH_TYPE, type);
				startActivity(intent2);
				mFromNotice = true;
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (mEnvGroup.getCheckedRadioButtonId()) {
		case R.id.cust_env_test:
			ipUrl = Constants.APP_DEV_IP;
			break;
		case R.id.cust_env_pre:
			ipUrl = Constants.APP_PRE_IP;
			break;
		case R.id.cust_env_idc:
			ipUrl = Constants.APP_IDC_IP;
			break;
		default:
			break;
		}

		mCustOkBtn.setClickable(false);
		EnvironmentAsynTask asyntast = new EnvironmentAsynTask(ipUrl);
		asyntast.execute();
	}

	public class EnvironmentAsynTask extends AsyncTask<Void, Void, Boolean> {

		private String mUrl;
		private Dialog urlDialogShow;

		public EnvironmentAsynTask(String url) {
			this.mUrl = url;
		}

		@Override
		protected void onPreExecute() {
			urlDialogShow = UiUtils.loadingDialogShow(WelcomeActivity.this,
					getResources().getString(R.string.app_url_loading_title));
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				return sendUrlMessage(mUrl);
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			UiUtils.dialogDismiss(urlDialogShow);
			if (result) {
				String env = getEnvToast(ipUrl);

				int appType = Constants.ECOMM_APP_TYPE;

				if (mAppGroup.getVisibility() == View.VISIBLE) {
					switch (mAppGroup.getCheckedRadioButtonId()) {
					case R.id.cust_app_game: {
						appType = ECommAppType._Comm_APP_GAMENEWS;
						break;
					}
					case R.id.cust_app_sport: {
						appType = ECommAppType._Comm_APP_SPORTBRUSH;
						break;
					}
					case R.id.cust_app_auto: {
						appType = ECommAppType._Comm_APP_CARBRUSH;
						break;
					}
					}
				}

				Preference.getInstance().setTestAppType(appType);
				String app = initAppType(appType);

				ToastUtil.showToast(app + " " + env);
				init();
			} else {
				ToastUtil.showToast("获取当前环境失败，请选择其它环境");
				mCustOkBtn.setClickable(true);
			}
		}

		private boolean sendUrlMessage(String url) throws IOException {

			byte[] data = FileUtil.download(url);
			if (data != null) {
				url = new String(data, "utf-8");
				BaseModel.HOST = url;
				Log.d(TAG, "BaseModel.HOST = " + BaseModel.HOST);
				Preference.getInstance().setTestUrl(url);
				Preference.getInstance().setTestIp(ipUrl);
				return true;
			} else {
				return false;
			}

		}
	}

	private String initAppType(int id) {
		String message = null;
		switch (id) {
		case ECommAppType._Comm_APP_GAMENEWS: {
			Constants.ECOMM_APP_TYPE = ECommAppType._Comm_APP_GAMENEWS;
			Constants.APP_SERVANT_NAME = ServerConstants.SERVANT_NAME.GAME;

			message = "游戏刷子";
			break;
		}
		case ECommAppType._Comm_APP_SPORTBRUSH: {
			Constants.ECOMM_APP_TYPE = ECommAppType._Comm_APP_SPORTBRUSH;
			Constants.APP_SERVANT_NAME = ServerConstants.SERVANT_NAME.SPORT;

			message = "体育刷子";
			break;
		}
		case ECommAppType._Comm_APP_CARBRUSH: {
			Constants.ECOMM_APP_TYPE = ECommAppType._Comm_APP_CARBRUSH;
			Constants.APP_SERVANT_NAME = ServerConstants.SERVANT_NAME.AUTO;

			message = "汽车刷子";
			break;
		}
		}

		return message;
	}

	private String getEnvToast(String url) {
		String message = null;
		if (Constants.APP_DEV_IP.equals(url)) {
			message = "测试环境";
		} else if (Constants.APP_PRE_IP.equals(url)) {
			message = "预发布环境";
		} else if (Constants.APP_IDC_IP.equals(url)) {
			message = "正式环境";
		}

		return message;
	}

}
