package com.yy.android.gamenews.ui;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.duowan.android.base.util.LocalLog;
import com.duowan.gamenews.LoginActionFlag;
import com.duowan.gamenews.UserInitRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.plugin.distribution.DistributionListActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.PageCaller;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.SignUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.WebViewCacheUtil;
import com.yy.android.sportbrush.R;

public class AppWebActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_ENABLE_CACHE = "enable_cache";
	public static final String TAG = AppWebActivity.class.getSimpleName();
	public static final String TAG_SOCIAL_DIALOG = "article_social_dialog";
	public static final String TAG_OPERATOR_DIALOG = "article_operator_dialog";
	public static final String TAG_ACCESS_TOKEN = "article_accesss_token";
	public static final String KEY_URL = "web_url";
	public static final String KEY_TITLE = "title";
	public static final String FROM = "from";
	public static final String FROM_KEY_RUL = "url";
	public static final int WEB_LOGIN_WEB = 100;

	public static final String TITLE_HD = "精彩活动";
	public static final String TITLE_VIEW = "网页浏览";
	public static final String TITLE_SIGN = "签到";

	private boolean needAgent;
	WebView mWebView;
	ImageView mWebReload;
	ImageView mWebBack;
	ImageView mWebForward;
	ActionBar mActionBar;
	View mMenu;
	String mUrl;
	String mTitle;
	String accessToken;
	Animation mLoadingAnimation = null;
	View mProgressBar;
	Handler mHandler = new Handler();

	// for video
	private View mCustomView;
	private int mState;
	private static final int STATE_INIT = 1;
	private static final int STATE_WEB = 5;
	private static final int STATE_VIDEO = 6;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private MyWebChromeClient mWebChromeClient;
	private Preference mPref;
	public boolean mIsAllowEntryLogin = true;

	public static void startWebActivityFromNotice(Context context, String url) {
		Intent intent = new Intent(context, AppWebActivity.class);
		try {
			String domain = Util.getUrlDomainName(url);
			if (domain.equals(Constants.PUSH_DOMAIN)) {
				// 符合域名.内部网页
				String accessToken = Util.getAccessToken();
				String query = Util.getUrlQuery(url);
				if (!TextUtils.isEmpty(query)) {
					intent.putExtra(AppWebActivity.KEY_URL, url + "&token="
							+ accessToken);
				} else {
					intent.putExtra(AppWebActivity.KEY_URL, url + "?token="
							+ accessToken);
				}
			} else {
				// 外部网页
				intent.putExtra(AppWebActivity.KEY_URL, url);

			}
		} catch (Exception e) {
		}

		intent.putExtra(AppWebActivity.KEY_TITLE, AppWebActivity.TITLE_VIEW);
		context.startActivity(intent);

	}

	/**
	 * 
	 * @param context
	 * @param url
	 * @param cache
	 *            是否支持cache
	 */
	public static void startWebActivityWithYYToken(Context context, String url,
			boolean cache) {
		Intent intent = new Intent(context, AppWebActivity.class);
		UserInitRsp rsp = Preference.getInstance().getInitRsp();
		String token = "";
		if (rsp != null) {
			token = rsp.extraInfo
					.get(LoginActionFlag._LOGIN_ACTION_FLAG_YY_TOKEN);
		}
		String query = "";
		try {
			query = Util.getUrlQuery(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(query)) {
			url = url + "&token=" + token;
		} else {
			url = url + "?token=" + token;
		}
		intent.putExtra(AppWebActivity.KEY_URL, url);
		intent.putExtra(AppWebActivity.KEY_ENABLE_CACHE, cache);
		intent.putExtra(AppWebActivity.KEY_TITLE, AppWebActivity.TITLE_VIEW);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @param context
	 * @param url
	 * @param accessToken
	 * @param cache
	 *            是否支持cache
	 */
	public static void startWebActivityFromNotice(Context context, String url,
			String accessToken, boolean cache) {
		Intent intent = new Intent(context, AppWebActivity.class);
		if (url.contains("token=")) {
			url = url.replace("token=", "token=" + accessToken);
		} else {
			url = url + "&token=" + accessToken;
		}
		intent.putExtra(AppWebActivity.KEY_URL, url);
		intent.putExtra(AppWebActivity.KEY_ENABLE_CACHE, cache);
		intent.putExtra(AppWebActivity.KEY_TITLE, AppWebActivity.TITLE_VIEW);
		context.startActivity(intent);

	}

	private class MyWebChromeClient extends WebChromeClient {
		private View mVideoProgressView;

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
			LocalLog.d(TAG, "[AppWebActivity][onShowCustomView]");
			onShowCustomView(view, callback);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			LocalLog.d(TAG,
					"[AppWebActivity][onShowCustomView], mCustomView = "
							+ mCustomView);
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			mCustomView = view;
			mState = STATE_VIDEO;
			updateView();
			mCustomViewContainer.addView(view);
			mCustomViewCallback = callback;
		}

		@Override
		public View getVideoLoadingProgressView() {

			LocalLog.d(TAG, "[AppWebActivity][getVideoLoadingProgressView]");
			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(AppWebActivity.this);
				mVideoProgressView = inflater.inflate(R.layout.video_progress,
						null);
			}
			return mVideoProgressView;
		}

		@Override
		public void onHideCustomView() {
			LocalLog.d(TAG,
					"[AppWebActivity][getVideoLoadingProgressView], mCustomView = "
							+ mCustomView);
			super.onHideCustomView();
			if (mCustomView == null)
				return;

			mState = STATE_WEB;
			updateView();

			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);

			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomViewCallback.onCustomViewHidden();

			mCustomView = null;
		}
	}

	private void updateView() {
		mCustomViewContainer.setVisibility(mState == STATE_VIDEO ? View.VISIBLE
				: View.GONE);
		toggleFullScreen(mState == STATE_VIDEO);

		if (mState == STATE_VIDEO) {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				/* 若目前为竖排，则更改为横排呈现 */
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				/* 若目前为竖排，则更改为横排呈现 */
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

		}
	}

	@SuppressLint("NewApi")
	private void toggleFullScreen(boolean fullscreen) {
		if (fullscreen) {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			getWindow().setAttributes(attrs);
			if (android.os.Build.VERSION.SDK_INT >= 14) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LOW_PROFILE);
			}
		} else {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			getWindow().setAttributes(attrs);
			if (android.os.Build.VERSION.SDK_INT >= 14) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_VISIBLE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Constants.REQUEST_LOGIN == requestCode) {
			if (resultCode == Constants.RESULT_LOGIN) {
				UserInitRsp rsp = Preference.getInstance().getInitRsp();
				accessToken = rsp.extraInfo
						.get(LoginActionFlag._LOGIN_ACTION_FLAG_YY_TOKEN);
				startWebActivityFromNotice(this, mUrl, accessToken, true);
				finish();
			}
			return;
		}
		if (resultCode == RESULT_OK) {
			Fragment fs = getSupportFragmentManager().findFragmentByTag(
					TAG_SOCIAL_DIALOG);
			if (fs != null && fs.isAdded() && fs instanceof ArticleSocialDialog) {
				((ArticleSocialDialog) fs).onActivityResult(requestCode,
						resultCode, data);
			}
		}
	}

	@Override
	protected void onStop() {
		mIsAllowEntryLogin = false;
		super.onStop();
		if (inCustomView()) {
			hideCustomView();
		}
	}

	private boolean inCustomView() {
		return mCustomView != null;
	}

	private void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		LocalLog.d(TAG, "[AppWebActivity][onCreate]");
		setContentView(R.layout.activity_app_web);
		mPref = Preference.getInstance();
		mMenu = findViewById(R.id.menu);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mUrl = getIntent().getStringExtra(KEY_URL);
		if (TextUtils.isEmpty(mUrl)
				|| (!mUrl.startsWith("http") && !mUrl.startsWith("file"))) {
			mUrl = "http://" + mUrl;
		}
		mTitle = getIntent().getStringExtra(KEY_TITLE);
		if (TextUtils.isEmpty(mTitle)) {
			mTitle = TITLE_VIEW;
		} else {
			needAgent = true;
		}
		mCustomViewContainer = (FrameLayout) findViewById(R.id.custom_view_container);
		mWebView = (WebView) findViewById(R.id.app_webview);
		mWebReload = (ImageView) findViewById(R.id.web_reload);
		mWebBack = (ImageView) findViewById(R.id.web_back);
		mWebForward = (ImageView) findViewById(R.id.web_go);
		mProgressBar = mActionBar.findViewById(R.id.actionbar_right);
		mLoadingAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.article_detail_loading);
		mLoadingAnimation.setInterpolator(new LinearInterpolator());
		mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.getSettings().setPluginState(PluginState.ON);

		mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.setHorizontalScrollbarOverlay(true);
		// mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);//默认缩放模式
		mWebView.setInitialScale(150);
		mState = STATE_INIT;
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new JsInterface(this), "client"); // JS交互
		mWebView.getSettings().setSupportZoom(false);
		// 来自于特权，支持缓存
		if (getIntent().getExtras().getBoolean(KEY_ENABLE_CACHE)) {
			WebViewCacheUtil.startWebViewCache(mWebView, this);
		}
		if (needAgent) {
			String userAgent = Constants.USER_AGENT_PREFIX
					+ GameNewsApplication.getInstance().getPackageInfo().versionName;
			String userAgentString = webSettings.getUserAgentString();
			webSettings.setUserAgentString(userAgentString + userAgent);
		}
		mActionBar.setTitle(mTitle);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		// 扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		// 自适应屏幕
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webSettings.setLoadWithOverviewMode(true);

		mWebChromeClient = new MyWebChromeClient();
		mWebView.setWebChromeClient(mWebChromeClient);

		// mUrl = "http://v.youku.com/v_show/id_XNzIwODUzOTg4.html";
		// mUrl = "http://m.iqiyi.com";
		// mUrl = "file:///android_asset/1.htm";

		mWebReload.setOnClickListener(this);
		mWebBack.setOnClickListener(this);
		mWebForward.setOnClickListener(this);
		findViewById(R.id.web_more).setOnClickListener(this);
		setDataView(mWebView);
		mWebView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!v.hasFocus()) {
						v.requestFocus();
					}
				}
				return false;
			}
		});
		super.onCreate(savedInstanceState);
		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});

		Util.ensureAccesstokenForCookie(AppWebActivity.this);
		mWebView.loadUrl(mUrl);
		// showView(VIEW_TYPE_LOADING);
		// mProgressBar.setVisibility(View.VISIBLE);
		// mProgressBar.startAnimation(mLoadingAnimation);
		setEmptyViewClickable(false);
		StatsUtil.statsReport(this, "stats_web_article", "web_article_name",
				mTitle + "  " + mUrl);
		StatsUtil.statsReportByMta(this, "stats_web_article",
				"web_article_name", mTitle + "  " + mUrl);
		StatsUtil.statsReportByHiido("", "web_article_name:" + mTitle + "  "
				+ mUrl);

		LocalLog.d(TAG, "[AppWebActivity][onCreate]+");
	}

	@Override
	protected void onStart() {
		super.onStart();
		mIsAllowEntryLogin = true;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= 11) {
			mWebView.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT >= 11) {
			mWebView.onPause();
		}
	}

	public void setButtons() {
		if (mWebView.canGoBack()) {
			mWebBack.setImageResource(R.drawable.app_web_back_selector);
		} else {
			mWebBack.setImageResource(R.drawable.app_web_back_disable);
		}

		if (mWebView.canGoForward()) {
			mWebForward.setImageResource(R.drawable.app_web_go_selector);
		} else {
			mWebForward.setImageResource(R.drawable.app_web_go_disable);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.web_reload:
			mWebView.reload();
			break;
		case R.id.web_back:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			break;
		case R.id.web_go:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;
		case R.id.web_more:
			AppWebOperatorDialog fw = AppWebOperatorDialog.newInstance(mUrl);
			showDialog(fw, TAG_OPERATOR_DIALOG);

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (inCustomView()) {
				hideCustomView();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LocalLog.d(TAG, "[MyWebViewClient] onPageFinished, url  = " + url);

			showView(VIEW_TYPE_DATA);
			mProgressBar.setVisibility(View.INVISIBLE);
			mProgressBar.clearAnimation();
			setEmptyViewClickable(false);
			setButtons();
			// mWebView.getSettings().setBlockNetworkImage(false);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// mWebView.getSettings().setBlockNetworkImage(true);
			LocalLog.d(TAG, "[MyWebViewClient] onPageStarted, url = " + url);
			setButtons();
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.startAnimation(mLoadingAnimation);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			LocalLog.d(TAG, "[MyWebViewClient] onReceivedError, errorCode = "
					+ errorCode + ", description = " + description
					+ ", failingUrl = " + failingUrl);
			showView(VIEW_TYPE_EMPTY);
			mProgressBar.setVisibility(View.INVISIBLE);
			mProgressBar.clearAnimation();
			setEmptyViewClickable(true);
			setButtons();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// return super.shouldOverrideUrlLoading(view, url);
		// }
	}

	@Override
	protected void onEmptyViewClicked() {
		// TODO Auto-generated method stub
		mWebView.reload();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			mMenu.setVisibility(View.GONE);
			mActionBar.setVisibility(View.GONE);
		} else {
			mMenu.setVisibility(View.VISIBLE);
			mActionBar.setVisibility(View.VISIBLE);
		}

		super.onConfigurationChanged(newConfig);
	}

	private void showDialog(DialogFragment f, String tag) {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = fm.findFragmentByTag(tag);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		f.show(ft, tag);
	}

	class JsInterface {
		Context context;
		final static int TYPR_MIX = 0;
		final static int TYPR_PIC = 1;

		public JsInterface(Context context) {
			this.context = context;
		}

		// 点击图片标签
		@JavascriptInterface
		public void onShareImg(String info) {
			try {
				JSONObject jInfo = new JSONObject(info);
				int type = TYPR_PIC;
				if (jInfo.has("type")) {
					type = jInfo.getInt("type");
				}
				String title = "";
				if (type == TYPR_MIX) {
					title = jInfo.getString("title");
				}
				String url = jInfo.getString("url");
				String image = jInfo.getString("content");

				DialogFragment fs = ArticleSocialDialog.newInstance(image,
						title, url, ArticleSocialDialog.SHARED_FROM_HD);
				showDialog(fs, TAG_SOCIAL_DIALOG);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.i("test", "error");
				e.printStackTrace();
			}
		}

		// 复制链接
		@JavascriptInterface
		public void copyToClipboard(final String url) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					Util.copyText(url);
					Toast.makeText(AppWebActivity.this, "已复制到粘贴板",
							Toast.LENGTH_SHORT).show();
				}
			});

		}

		// 打开链接
		@JavascriptInterface
		public void openInBrowser(final String url) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					startWebActivityFromNotice(AppWebActivity.this, url);
				}
			});

		}

		@JavascriptInterface
		public void openNativePage(final int type, final int id) {

			mHandler.post(new Runnable() {
				public void run() {
					if (!mIsStopped) {
						PageCaller.open(AppWebActivity.this, type, id);
					}
				}
			});
		}

		// 点击登录
		@JavascriptInterface
		public void yyLogin(final String url) {
			if (mIsAllowEntryLogin) {
				mHandler.post(new Runnable() {
					public void run() {
						Intent intent = new Intent(AppWebActivity.this,
								LoginYYActivity.class);
						intent.putExtra(FROM, TAG);
						intent.putExtra(FROM_KEY_RUL, url);
						mUrl = url;
						startActivityForResult(intent, Constants.REQUEST_LOGIN);
					}
				});
				mIsAllowEntryLogin = false;
			}

		}

		@JavascriptInterface
		public void checkIn() {

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					StatsUtil.statsReport(AppWebActivity.this,
							"stats_click_checkin");
					StatsUtil.statsReportByMta(AppWebActivity.this,
							"stats_click_checkin", "点击签到");
					StatsUtil.statsReportByHiido("stats_click_checkin", "点击签到");
					new SignUtil(AppWebActivity.this).requestSign();
				}
			});
		}
	}
}