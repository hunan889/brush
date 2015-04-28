package com.yy.android.gamenews.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.LoginActionFlag;
import com.duowan.gamenews.MeRsp;
import com.duowan.gamenews.PlatType;
import com.duowan.gamenews.User;
import com.duowan.gamenews.UserInitReq;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.switchsInfo;
import com.duowan.gamenews.swiths;
import com.duowan.show.NotificationRsp;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.LoginStatusEvent;
import com.yy.android.gamenews.event.MessageEvent;
import com.yy.android.gamenews.event.XGPushEvent;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.plugin.distribution.DistributionListActivity;
import com.yy.android.gamenews.plugin.message.MessageActivity;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.util.DataCleanManager;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.UpdateManager;
import com.yy.android.gamenews.util.UpdateManager.OnUpdateInfoListener;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class MyHomeActivity extends BaseActivity implements OnClickListener {

	private static final String REQUEST_CODE = "request_code";
	private View mLoginLayout;
	private View mUserInfoLayout;
	private View mLogoutView;
	private CheckBox mOnlyWifiCb;
	private CheckBox mPushMsgCb;
	private TextView mUserName;
	private TextView mFavorCountTv;
	private TextView mCacheSize;
	private TextView mVersionTv;
	private TextView mFeedBack;
	private View mEventView;
	private View mRegistView;
	private View mTequanView;
	private View mTdouHintView;
	private View mEventHintView;
	private TextView mShareTo;
	private ImageView mUserPicView;
	private ActionBar mActionBar;
	private TextView mUnreadMessageCount;

	private Preference mPref;
	private SHARE_MEDIA mLoginType;

	private String mSignUrl; // 签到地址;
	private String mTequanUrl;
	private int mFavCount;

	UMSocialService mController;

	private SwitchImageLoader mImageLoader;

	private Dialog mCleanCacheDialog;
	private View mMessageCountView;

	public static void startMyHomeActivityForResult(Activity context) {
		startMyHomeActivityForResult(context, Constants.REQUEST_LOGIN);
	}

	public static void startMyHomeActivityForResult(Activity context,
			int requestCode) {
		Intent intent = new Intent(context, MyHomeActivity.class);
		intent.putExtra(REQUEST_CODE, requestCode);
		context.startActivityForResult(intent, requestCode);
		context.overridePendingTransition(R.anim.myhome_open_enter,
				R.anim.myhome_open_exit);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_myhome);
		EventBus.getDefault().register(MyHomeActivity.this);
		mController = UMServiceFactory.getUMSocialService("com.umeng.login",
				RequestType.SOCIAL);
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(this, Constants.QQ_APP_ID,
						Constants.QQ_APP_KEY));// 为了避免每次都从服务器获取APP ID、APP
												// KEY，请设置APP ID跟APP KEY
		mController.getConfig().supportQQPlatform(this, Constants.QQ_APP_ID,
				Constants.QQ_APP_KEY, "http://www.umeng.com/social");
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		mImageLoader = SwitchImageLoader.getInstance();
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mUserPicView = (ImageView) findViewById(R.id.home_user_img);
		mLoginLayout = findViewById(R.id.home_login_layout);
		mUserInfoLayout = findViewById(R.id.home_user_img_layout);
		mLogoutView = findViewById(R.id.logout_btn);
		mLogoutView.setOnClickListener(this);

		mEventView = findViewById(R.id.my_event_btn);
		mRegistView = findViewById(R.id.my_event_sign_btn);
		mTequanView = findViewById(R.id.my_tequan);
		mTdouHintView = findViewById(R.id.my_event_has_tdou_hint);
		mEventHintView = findViewById(R.id.my_event_has_event_hint);

		mShareTo = (TextView) findViewById(R.id.share_to);
		mShareTo.setText(getString(R.string.my_share_to,
				getString(R.string.app_name)));
		mShareTo.setOnClickListener(this);
		mCacheSize = (TextView) findViewById(R.id.tv_cache_size);

		mOnlyWifiCb = (CheckBox) findViewById(R.id.cb_use_data);
		mPushMsgCb = (CheckBox) findViewById(R.id.cb_allow_push);
		mVersionTv = (TextView) findViewById(R.id.tv_version_number);
		mFavorCountTv = (TextView) findViewById(R.id.tv_favor_count);
		mUserName = (TextView) findViewById(R.id.tv_home_user_name);
		mFeedBack = (TextView) findViewById(R.id.feedback);
		mFeedBack.setOnClickListener(this);

		mPref = Preference.getInstance();
		updateSettings();
		requestMeRsp();
//		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//			addMessageRecord();// 增加进入个人消息入口
//		}
		// mAttribute = Util.getDisplayAttribute(this);
		super.onCreate(savedInstanceState);
	}

	public void onEvent(MessageEvent event) {
//		if (event != null && event.isNeedUpdate()
//				&& Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//			showPersonMessage();
//		}
	}

	public void onEvent(XGPushEvent event) {
		String msg = event.content;
		if (Constants.PUSH_MESSAGE_NEW_TDOU.equals(msg)) {
			//showTdouHint(true);
		} else if (Constants.PUSH_MESSAGE_NEW_EVENT.equals(msg)) {
			showEventHint(true);
		}
	}

	private void showPersonMessage() {
		NotificationRsp notifacation = mPref.getNotifacation();
		if (notifacation != null && notifacation.getUnreadCount() > 0
				&& mMessageCountView != null && mUnreadMessageCount != null) {
			mUnreadMessageCount.setText(String.valueOf(notifacation
					.getUnreadCount()));
			mUnreadMessageCount.setVisibility(View.VISIBLE);
		} else {
			mUnreadMessageCount.setText("0");
			mUnreadMessageCount.setVisibility(View.GONE);
		}
	}

	/**
	 * 进入个人消息入口
	 */
	private void addMessageRecord() {
		mMessageCountView = LayoutInflater.from(this).inflate(
				R.layout.global_actionbar_message, null);
		mMessageCountView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPref.setNotifacation(null);
				MessageEvent event = new MessageEvent();
				event.setNeedUpdate(MessageEvent.STATUS_SUCESS);
				EventBus.getDefault().post(event);
				MessageActivity.startMessageActivity(MyHomeActivity.this);
			}
		});
		mUnreadMessageCount = (TextView) mMessageCountView
				.findViewById(R.id.message_count);
		mUnreadMessageCount.setTextColor(getResources().getColor(
				R.color.global_white));
		mUnreadMessageCount.setBackgroundResource(R.drawable.comment_count);
		mUnreadMessageCount.setVisibility(View.GONE);
		mActionBar.setCustomizeView(mMessageCountView);
		showPersonMessage();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(MyHomeActivity.this);
	}

	private void requestMeRsp() {
		ArticleModel.getMeRsp(new ResponseListener<MeRsp>(MyHomeActivity.this) {
			@Override
			public void onResponse(MeRsp response) {
				mFavCount = response.favCount;
				mPref.saveMyFavCount(mFavCount);
				mPref.setMeRsp(response);
				if (response != null) {
					Map<Integer, switchsInfo> infoMap = response.info;
					switchsInfo sign = infoMap.get(swiths._ENUM_SWITCHS_SIGN);
					mSignUrl = sign == null ? "" : sign.url;
					switchsInfo tequan = infoMap.get(swiths._ENUM_SWITCHS_GIFT);
					mTequanUrl = tequan == null ? "" : tequan.url;
				}
				updateSettings();
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.myhome_close_enter,
				R.anim.myhome_close_exit);
		StatsUtil.statsReportAllData(this, "go_back_article",
				"go_back_article_info", "go_back_article");
	}

	private void updateSettings() {

		//屏蔽赚取T豆入口
		mRegistView.setVisibility(View.GONE);
		mRegistView.setClickable(false);
		
//		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//			mRegistView.setVisibility(View.VISIBLE);
//			mRegistView.setClickable(true);
//		} else {
//			mRegistView.setVisibility(View.GONE);
//			mRegistView.setClickable(false);
//		}

		new UpdateSettingsTask().execute();
		// boolean isOnlyWifi = mPref.isOnlyWifi();
		// boolean isPushEnabled = mPref.isPushMsgEnabled();
		//
		// mOnlyWifiCb.setChecked(isOnlyWifi);
		// mPushMsgCb.setChecked(isPushEnabled);
		// mVersionTv.setText(Util.getVersionName());
		//
		// mFavCount = mPref.getMyFavCount();
		//
		// if (mFavCount < 0) {
		// mFavCount = 0;
		// }
		// mFavorCountTv.setText("" + mFavCount);
		// if (mFavCount == 0) {
		// mFavorCountTv.setEnabled(false);
		// } else {
		// mFavorCountTv.setEnabled(true);
		// }
		//
		// long cacheSize = DataCleanManager.getAppCacheSize(this);
		// mCacheSize.setText(DataCleanManager.FormetFileSize(cacheSize));
	}

	@Override
	public void onResume() {
		updateLoginStatus(mPref.isUserLogin());
		updateSettings();
		super.onResume();
	}

	private void updateLoginStatus(boolean isLogin) {
		if (isLogin) {
			UserInitRsp rsp = mPref.getInitRsp();
			if (rsp != null) {
				User user = rsp.getUser();
				if (user == null || user.getIcon() == null
						|| "".equals(user.getIcon())) {
					mUserPicView.setImageResource(R.drawable.btn_login_yy);
				} else {
					mImageLoader.displayImage(rsp.getUser().getIcon(),
							mUserPicView, true);
				}
				mUserName.setText(rsp.getUser().getName());
			}

			mLoginLayout.setVisibility(View.INVISIBLE);
			mUserInfoLayout.setVisibility(View.VISIBLE);
			mLogoutView.setVisibility(View.VISIBLE);
		} else {
			mLoginLayout.setVisibility(View.VISIBLE);
			mUserInfoLayout.setVisibility(View.INVISIBLE);
			mLogoutView.setVisibility(View.INVISIBLE);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUEST_LOGIN: {
			if (resultCode == Constants.RESULT_LOGIN) {
				UserInitRsp rsp = (UserInitRsp) data
						.getSerializableExtra(Constants.EXTRA_USER_INIT_RSP);

				onLoginSucc(rsp);
			}
			break;
		}
		case Constants.REQUEST_LOGIN_REDIRECT: {
			if (resultCode == Constants.RESULT_LOGIN) {
				DistributionListActivity.startDistributionListActivity(this,
						DistributionListActivity.FROM_MYHOME);
				setResult(Constants.RESULT_LOGIN);
			}
			break;
		}
		}
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_qq_btn: {
			// ToastUtil.showToast("暂未支持 即将开通");
			mLoginType = SHARE_MEDIA.QZONE;
			login();
			break;
		}
		case R.id.login_weibo_btn: {
			mLoginType = SHARE_MEDIA.SINA;
			login();
			break;
		}
		case R.id.share_to: {
			DialogFragment fs = ArticleSocialDialog.newInstanceForShareApp(
					getString(R.string.my_share_to_title,
							getString(R.string.app_name)),
					getString(R.string.my_share_to_text),
					Constants.DOWNLOAD_URL,
					ArticleSocialDialog.SHARED_FROM_MYHOME);
			Util.showDialog(this, fs, "tag");
			StatsUtil.statsReportAllData(MyHomeActivity.this, "share_to",
					"share_to", "share_to");
			break;
		}
		case R.id.login_yy_btn: {
			if (!Util.isNetworkConnected()) {
				ToastUtil.showToast(R.string.http_not_connected);
				return;
			}
			Intent intent = new Intent(this, LoginYYActivity.class);
			startActivityForResult(intent, Constants.REQUEST_LOGIN);
			break;
		}
		case R.id.feedback: {
			Intent intent = new Intent(this, FeedbackActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.my_event_sign_btn: {
			// Intent intent = new Intent(this, AppWebActivity.class);
			// intent.putExtra(AppWebActivity.KEY_URL, mSignUrl);
			// intent.putExtra(AppWebActivity.KEY_TITLE,
			// AppWebActivity.TITLE_SIGN);
			// startActivity(intent);

			// 将目前“签到”按钮更改为“赚取T豆”按钮
			if (!Util.isNetworkConnected()) {
				ToastUtil.showToast(R.string.http_not_connected);
				return;
			}
			if (mPref.isUserLogin()) {
				DistributionListActivity.startDistributionListActivity(this,
						DistributionListActivity.FROM_MYHOME);
			} else {
				Intent intent = new Intent(this, LoginYYActivity.class);
				startActivityForResult(intent, Constants.REQUEST_LOGIN_REDIRECT);
			}

			mPref.setHasNewTdou(false);
			break;
		}
		case R.id.my_event_btn: {
			Intent intent = new Intent(this, AppWebActivity.class);
			String accessToken = Util.getAccessToken();
			intent.putExtra(AppWebActivity.KEY_URL, Constants.MY_EVENT_URL
					+ "&token=" + accessToken);
			intent.putExtra(AppWebActivity.KEY_TITLE, AppWebActivity.TITLE_HD);
			startActivity(intent);

			mPref.setHasNewEvent(false);
			break;
		}
		case R.id.my_tequan: {
			Intent intent = new Intent(this, AppWebActivity.class);
			intent.putExtra(AppWebActivity.KEY_URL, mTequanUrl);
			intent.putExtra(AppWebActivity.KEY_TITLE, AppWebActivity.TITLE_VIEW);
			startActivity(intent);
			break;
		}
		case R.id.logout_btn: {

			UiUtils.showDialog(this, R.string.global_caption,
					R.string.my_msg_confim_logout, R.string.global_ok,
					R.string.global_cancel, new AppDialog.OnClickListener() {

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE) {
								// 更新默认用户信息
								mIsLogin = false;
								getGameNewsUserInfo(null);

								StatsUtil.statsReport(MyHomeActivity.this,
										"stats_logout");
								StatsUtil.statsReportByMta(MyHomeActivity.this,
										"stats_logout", "更新默认用户信息");
								StatsUtil
										.statsReportByHiido("stats_logout", "");
							}
						}

						@Override
						public void onDismiss() {
							// TODO Auto-generated method stub

						}
					});
			break;
		}
		case R.id.clean_cache_layout: {

			UiUtils.showDialog(this, R.string.global_caption,
					R.string.my_msg_confim_clean, R.string.global_ok,
					R.string.global_cancel, new AppDialog.OnClickListener() {

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE) {
								mCleanCacheDialog = UiUtils.loadingDialogShow(
										MyHomeActivity.this,
										getResources().getString(
												R.string.clean_cacheing));
								new CleanCacheTask().execute();
								StatsUtil.statsReportAllData(
										MyHomeActivity.this, "clean_cache",
										"clean_cache", "clean_cache");
								// DataCleanManager
								// .cleanAppCache(MyHomeActivity.this);
								// updateSettings();
								// Toast.makeText(MyHomeActivity.this,
								// R.string.my_msg_clean_succ,
								// Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void onDismiss() {
							// TODO Auto-generated method stub

						}
					});
			break;
		}
		case R.id.cb_use_data:
			onOnlyWifiClicked(mOnlyWifiCb.isChecked());
			break;
		case R.id.my_only_wifi_layout: {
			onOnlyWifiClicked(!mOnlyWifiCb.isChecked());
			break;
		}
		case R.id.cb_allow_push:
			onAllowPushClicked(mPushMsgCb.isChecked());
			break;
		case R.id.my_allow_push_layout: {
			onAllowPushClicked(!mPushMsgCb.isChecked());
			break;
		}
		case R.id.tv_favor_count: {
			ArticleListActivity.startMyFavorListActivity(MyHomeActivity.this);
			break;
		}
		case R.id.check_update: {
			if(Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())){
				UpdateManager manager = new UpdateManager(this);

				manager.setOnUpdateInfoListener(new OnUpdateInfoListener() {

					@Override
					public void onClick(int button, boolean isForceUpdate) {
						if (isForceUpdate) {
							finish();
						}
					}

					@Override
					public void onCheckFinish(boolean needUpdate,
							boolean isForceUpdate) {
						// Do nothing
					}
				});
				manager.checkUpdate();
				StatsUtil.statsReportAllData(this, "check_update", "check_update",
						"check_update");
			}else {
				ToastUtil.showToast(R.string.sdcard_unmounted);
			}
			
			break;
		}
		}
	}

	private void onOnlyWifiClicked(boolean status) {
		mPref.setOnlyWifi(status);
		mOnlyWifiCb.setChecked(status);
		StatsUtil.statsReportAllData(this, "noly_wifi_load_pic",
				"noly_wifi_load_pic", "noly_wifi_load_pic");
	}

	private void onAllowPushClicked(boolean status) {
		mPref.setPushMsgEnabled(status);
		mPushMsgCb.setChecked(status);
		if (!status) {
			PushUtil.stop(getApplicationContext());
			StatsUtil.statsReportAllData(this, "stop_push", "stop_push",
					"stop_push");
		} else {
			PushUtil.start(getApplicationContext());
			StatsUtil.statsReportAllData(this, "start_push", "start_push",
					"start_push");
		}
	}

	private void login() {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			return;
		}
		mIsLogin = true;
		mController.doOauthVerify(MyHomeActivity.this, mLoginType,
				mAuthListener);
	}

	private UMAuthListener mAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(Bundle value, SHARE_MEDIA platform) {
			mController.getPlatformInfo(MyHomeActivity.this, mLoginType,
					mUserInfoListener);

			StatsUtil.statsReport(MyHomeActivity.this, "stats_login",
					"login_type", platform.name());
			StatsUtil.statsReportByMta(MyHomeActivity.this, "stats_login",
					"login_type", platform.name());
			StatsUtil.statsReportByHiido("stats_login", "login_type:"
					+ platform.name());
		}

		@Override
		public void onCancel(SHARE_MEDIA arg0) {

		}

		@Override
		public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
			Toast.makeText(MyHomeActivity.this, R.string.my_msg_login_fail,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStart(SHARE_MEDIA arg0) {
		}
	};

	private UMDataListener mUserInfoListener = new UMDataListener() {
		@Override
		public void onComplete(int status, Map<String, Object> info) {

			if (status != 200 || info == null) {
				Toast.makeText(MyHomeActivity.this, R.string.my_msg_login_fail,
						Toast.LENGTH_SHORT).show();
				Log.d("TestData", "发生错误：" + status);
				return;
			}

			Object accessToken = info.get("access_token");
			Object name = info.get("screen_name");
			Object image = info.get("profile_image_url");
			Object openId = info.get("openid");
			String openIdStr = openId == null ? "" : openId.toString();
			String accessTokenStr = accessToken == null ? "" : accessToken
					.toString();
			String nameStr = name == null ? "" : name.toString();
			String imageStr = image == null ? "" : image.toString();

			UserInitReq req = new UserInitReq();
			req.setUserIcon(imageStr);
			req.setUserName(nameStr);
			Map<Integer, String> token = new HashMap<Integer, String>();
			token.put(0, accessTokenStr);
			token.put(1, openIdStr);

			req.setSocialAccessToken(token);
			int platType = PlatType._PLAT_TYPE_DEFAULT;
			if (SHARE_MEDIA.QZONE.equals(mLoginType)) {
				platType = PlatType._PLAT_TYPE_QQ;
			} else if (SHARE_MEDIA.SINA.equals(mLoginType)) {
				platType = PlatType._PLAT_TYPE_SINA;
			}

			req.setPlatType(platType);
			mPref.setLoginType(platType);

			getGameNewsUserInfo(req);
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub

		}
	};

	private void getGameNewsUserInfo(UserInitReq req) {

		InitModel.sendUserInitReq(MyHomeActivity.this, mRspListener, req, true);
	}

	private void onLoginSucc(UserInitRsp rsp) {
		mPref.saveInitRsp(rsp);
		requestMeRsp();

		if (rsp != null) {
			int flag = rsp.flag;
			if ((flag & LoginActionFlag._LOGIN_ACTION_FLAG_YY_TDOU) != 0) {
				String msg = rsp.extraInfo.get(flag);
				if (msg != null && !"".equals(msg)) {
					UiUtils.showDialog(this, R.string.global_caption, msg,
							R.string.global_ok);
				}
			}
		}

		Toast.makeText(getApplicationContext(), R.string.my_msg_login_succ,
				Toast.LENGTH_SHORT).show();
		updateLoginStatus(true);

		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_USER_INIT_RSP, rsp);
		setResult(Constants.RESULT_LOGIN, intent);

		StatsUtil.statsReport(MyHomeActivity.this, "stats_login", "login_type",
				"yy");
		StatsUtil.statsReportByMta(MyHomeActivity.this, "stats_login",
				"login_type", "yy");
		StatsUtil.statsReportByHiido("stats_login", "login_type:yy");

		if (getIntent() != null
				&& getIntent().getIntExtra(REQUEST_CODE,
						Constants.REQUEST_LOGIN) == Constants.REQUEST_LOGIN_REDIRECT) {
			onBackPressed();
		}
	}

	private boolean mIsLogin;
	private ResponseListener<UserInitRsp> mRspListener = new ResponseListener<UserInitRsp>(
			this) {
		public void onResponse(UserInitRsp rsp) {
			if (mIsLogin) { // 登录
				EventBus.getDefault().post(new LoginStatusEvent(true));
				onLoginSucc(rsp);
			} else { // 退出登录
				if (mPref != null) {
					mPref.clearLoginInfo();
					mPref.setLoginType(PlatType._PLAT_TYPE_DEFAULT);
					mPref.saveDefaultInitRsp(rsp);
					Util.removeCookie(MyHomeActivity.this);
					updateLoginStatus(false);
					updateSettings();
					requestMeRsp();
				}
				EventBus.getDefault().post(new LoginStatusEvent(false));
				setResult(Constants.RESULT_LOGOUT);
			}
		};

		public void onError(Exception e) {
			if (mIsLogin) {
				mPref.setLoginType(PlatType._PLAT_TYPE_DEFAULT);
				if (e instanceof TimeoutError) {

					Toast.makeText(getApplicationContext(),
							R.string.my_msg_login_timeout, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.my_msg_login_fail, Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.my_msg_logout_fail, Toast.LENGTH_LONG).show();
			}
		};
	};

	private class CleanCacheTask extends BackgroundTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DataCleanManager.cleanAppCache(MyHomeActivity.this);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateSettings();
			UiUtils.dialogDismiss(mCleanCacheDialog);
			Toast.makeText(MyHomeActivity.this, R.string.my_msg_clean_succ,
					Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
	}

	private void showEventHint(boolean show) {
		if (mEventHintView != null) {
			mEventHintView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	private void showTdouHint(boolean show) {
		if (mTdouHintView != null) {
			mTdouHintView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	private class UpdateSettingsTask extends BackgroundTask<Void, Void, Void> {
		private boolean isOnlyWifi;
		private boolean isPushEnabled;
		private String versionName;
		private long cacheSize;
		private boolean showEvent;
		private boolean showRegist; // 签到
		private boolean showTequan;
		private boolean showEventHint;
		private boolean showTdouHint;

		@Override
		protected Void doInBackground(Void... params) {
			isOnlyWifi = mPref.isOnlyWifi();
			isPushEnabled = mPref.isPushMsgEnabled();
			versionName = Util.getVersionName();
			mFavCount = mPref.getMyFavCount();
			if (mFavCount < 0) {
				mFavCount = 0;
			}
			MeRsp rsp = mPref.getMeRsp();
			if (rsp != null) {
				// 是否显示活动, 0为不显示，其它为显示
				Map<Integer, switchsInfo> infoMap = rsp.info;
				if (infoMap != null) {
					switchsInfo event = infoMap
							.get(swiths._ENUM_SWITCHS_ACTIVITY);
					showEvent = event == null ? false : (event.flag != 0);
					if (mPref.getLoginType() == PlatType._PLAT_TYPE_YY) {
						// 是否显示签到 , 0为不显示，其它为显示
						switchsInfo sign = infoMap
								.get(swiths._ENUM_SWITCHS_SIGN);
						showRegist = sign == null ? false : (sign.flag != 0);

						// 是否显示特权 , 0为不显示，其它为显示
						switchsInfo tequan = infoMap
								.get(swiths._ENUM_SWITCHS_GIFT);
						showTequan = tequan == null ? false
								: (tequan.flag != 0);
					}
				}

			}
			cacheSize = DataCleanManager.getAppCacheSize(MyHomeActivity.this);

			showEventHint = mPref.hasNewEvent();
			showTdouHint = mPref.hasNewTdou();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mOnlyWifiCb.setChecked(isOnlyWifi);
			mPushMsgCb.setChecked(isPushEnabled);
			mVersionTv.setText(versionName);
			mFavorCountTv.setText("" + mFavCount);
			if (mFavCount == 0) {
				mFavorCountTv.setEnabled(false);
			} else {
				mFavorCountTv.setEnabled(true);
			}
			mCacheSize.setText(DataCleanManager.FormetFileSize(cacheSize));

			// if (showRegist) {
			// mRegistView.setVisibility(View.VISIBLE);
			// mRegistView.setClickable(true);
			// } else {
			// mRegistView.setVisibility(View.GONE);
			// mRegistView.setClickable(false);
			// }

			if (showEvent) {
				mEventView.setVisibility(View.VISIBLE);
				mEventView.setClickable(true);
			} else {
				mEventView.setVisibility(View.GONE);
				mEventView.setClickable(false);
			}

			if (showTequan) {
				mTequanView.setVisibility(View.VISIBLE);
				mTequanView.setClickable(true);
			} else {

				mTequanView.setVisibility(View.GONE);
				mTequanView.setClickable(false);
			}

			showTdouHint(showTdouHint);
			showEventHint(showEventHint);
			super.onPostExecute(result);
		}
	}

	// private int[] mAttribute = new int[2];
	// private boolean intercept = false;
	// // for slip out
	// // 手指向右滑动时的最小速度
	// private static final int YDISTANCE_MAX = 50;
	// // 手指向右滑动时的最小距离
	// private int XDISTANCE_MIN = 0;
	// // 记录手指按下时的横坐标。
	// private float xDown;
	// private float yDown;
	// // 记录手指移动时的横坐标。
	// private float xMove;
	// private float yMove;
	//
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// Log.d(TAG, "intercept = " + intercept);
	// return super.onTouchEvent(event);
	// }
	//
	//
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// Log.d(TAG, "intercept = " + intercept);
	// // if (intercept) {
	// // return super.dispatchTouchEvent(ev);
	// // }
	// if (XDISTANCE_MIN == 0) {
	// if (mAttribute.length > 0 && mAttribute[0] != 0) {
	// XDISTANCE_MIN = mAttribute[0] / 8;
	// } else {
	// XDISTANCE_MIN = 30;
	// }
	// if (XDISTANCE_MIN == 0) {
	// return false;
	// }
	// }
	//
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_MOVE: {
	// xMove = ev.getRawX();
	// yMove = ev.getRawY();
	// Log.d(TAG, "yMove = " + yMove + "   xMove = " + xMove);
	// // 活动的距离
	// int distanceX = (int) (xDown - xMove);
	// int distanceY = (int) Math.abs(yDown - yMove);
	// // 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
	// if (distanceX > XDISTANCE_MIN && distanceY < YDISTANCE_MAX) {
	// onBackPressed();
	// StatsUtil.statsReportAllData(this, "go_back_article",
	// "go_back_article_info", "go_back_article");
	// // intercept = true;
	// }
	// break;
	// }
	//
	// case MotionEvent.ACTION_DOWN: {
	// xDown = ev.getRawX();
	// yDown = ev.getRawY();
	// break;
	// }
	//
	// case MotionEvent.ACTION_CANCEL:
	// case MotionEvent.ACTION_UP:
	// /* Release the drag */
	// break;
	// case MotionEvent.ACTION_POINTER_UP:
	// break;
	// }
	//
	// return super.dispatchTouchEvent(ev);
	// }
}