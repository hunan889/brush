package com.yy.android.gamenews.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.util.LocalLog;
import com.duowan.gamenews.ArticleDetail;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.ExtraInfoType;
import com.duowan.gamenews.FavType;
import com.duowan.gamenews.GetArticleDetailRsp;
import com.duowan.gamenews.Image;
import com.duowan.gamenews.ImageType;
import com.duowan.gamenews.Like;
import com.duowan.gamenews.LikeType;
import com.duowan.gamenews.PicInfo;
import com.duowan.gamenews.User;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.Video;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.event.LikeEvent;
import com.yy.android.gamenews.exception.UserException;
import com.yy.android.gamenews.model.ArticleDetailModel;
import com.yy.android.gamenews.model.ReportModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.ArticleCommentView;
import com.yy.android.gamenews.ui.view.ExtendedScrollView;
import com.yy.android.gamenews.ui.view.ExtendedScrollView.OnFlipListener;
import com.yy.android.gamenews.ui.view.ExtendedWebView;
import com.yy.android.gamenews.util.ArticleDetailSwitcher;
import com.yy.android.gamenews.util.ArticleUtil;
import com.yy.android.gamenews.util.DropDownHelper;
import com.yy.android.gamenews.util.DropDownHelper.DropDownItem;
import com.yy.android.gamenews.util.DropDownHelper.OnDropDownClickListener;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.WebViewMonitor;
import com.yy.android.gamenews.util.maintab.MainTab2;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ArticleDetailActivity extends BaseActivity implements
		OnClickListener, OnFlipListener, OnGestureListener {
	public static String CURRENT_ARTICLE_TAB = "我的最爱";// 来自于哪个tab
	public static int CURRENT_BUTTON_TAB = MainTab2.INDEX;// 来自于哪个tab
	public static final String KEY_ARTICLE_INFO = "article_info";
	public static final String KEY_ARTICLE_LIST = "article_list";
	public static final String KEY_ARTICLE_ID = "article_id";
	public static final String KEY_COMMENT = "article_comment";
	private static final int ARTICLE_UNCERTAIN = -1;
	private static final String TAG = "ArticleDetailActivity";
	private ExtendedWebView mBody;
	private ExtendedScrollView mScrollView;
	private SharedPreferences msharedPre;
	private ArticleDetail mArticleDetail = null;
	private ArrayList<ArticleInfo> recommendArticleList = null;
	private ActionBar mActionBar;
	private ArticleCommentView mCommentView;
	private View commentCountView;
	private TextView mLikeCount;
	private TextView mDislikeCount;
	private ImageView mLikeArticle;
	private ImageView mDislikeArticle;
	private TextView[] mTag;
	private TextView mCommentCount;
	private Set<Long> mMyArticlesLike;
	private Set<Long> mMyArticlesDislike;
	private ArticleInfo mArticleInfo;
	private boolean mKept = false;
	private View mTitleCommentNewest;
	private View mTitleCommentEmpty;
	private User me;
	private Handler mHandler = new Handler();
	private String mSocialImgUrl;

	private View[] mArticleViews;
	private ImageView[] mArticleImageViews;
	private TextView[] mArticleTitleTextViews;

	// for progress
	private View mProgressBar;
	private View mProgressBarInner;
	private View mRetryArticle;
	private Animation mLoadingAnimation;

	// for video
	private View mCustomView;
	private int mState;
	private static final int STATE_INIT = 1;
	private static final int STATE_WEB = 5;
	private static final int STATE_VIDEO = 6;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private MyWebChromeClient mWebChromeClient;
	private static final int UNDEFINE = 0;
	private static final int HORIZIONTAL = 1;
	private static final int VERTICAL = 2;

	// for
	private Map<Integer, String> mExtraInfo = null;

	public static void startArticleDetailActivityFromNotice(Context context,
			long articleId) {
		Intent intent = new Intent(context, ArticleDetailActivity.class);
		ArticleInfo info = new ArticleInfo();
		info.setId(articleId);
		info.setArticleType(ARTICLE_UNCERTAIN);
		intent.putExtra(ArticleDetailActivity.KEY_ARTICLE_INFO, info);
		context.startActivity(intent);
	}

	public static void startArticleDetailActivity(Context context,
			ArticleInfo articleInfo) {
		startArticleDetailActivityWithList(context, null, articleInfo);
	}

	public static void startArticleDetailActivityWithList(Context context,
			ArrayList<ArticleInfo> list, ArticleInfo info) {
		Intent intent = new Intent(context, ArticleDetailActivity.class);
		intent.putExtra(ArticleDetailActivity.KEY_ARTICLE_INFO, info);
		intent.putExtra(ArticleDetailActivity.KEY_ARTICLE_LIST, list);
		context.startActivity(intent);
	}

	private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			loadWebImageFailed(imageUri);

		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {

			File cache = ImageLoader.getInstance().getDiscCache().get(imageUri);
			if (cache.exists()) {
				String destFileName = String.format("file:///%s", cache);
				replaceWebImage(imageUri, destFileName);
			} else {
				replaceWebImage(imageUri, imageUri);
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {

		}

	};

	private void replaceWebImage(final String srcUri, final String destUrl) {

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				int id = getSrcImage(srcUri);
				if (id >= 0) {
					mBody.loadUrl(String
							.format("javascript:webview.fillImageById(\"image\",%d,\"%s\")",
									id, destUrl));
				} else {
					id = getSrcVideo(srcUri);
					if (id >= 0) {
						mBody.loadUrl(String
								.format("javascript:webview.fillImageById(\"video\",%d,\"%s\")",
										id, destUrl));
					} else {
					}
				}
			}
		});
	}

	private void loadWebImageFailed(final String srcUri) {

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				int id = getSrcImage(srcUri);
				if (id >= 0) {
					mBody.loadUrl(String
							.format("javascript:webview.reloadFail(\"image\",%d,\"image_bg_failed.png\")",
									id));
				} else {
					id = getSrcVideo(srcUri);
					if (id >= 0) {
						mBody.loadUrl(String
								.format("javascript:webview.reloadFail(\"video\",%d,\"image_bg_failed.png\")",
										id));
					} else {
					}
				}
			}
		});
	}

	private int getSrcImage(String url) {
		if (TextUtils.isEmpty(url)) {
			return -1;
		}
		List<Image> images = mArticleDetail.getImageList();
		if (images == null) {
			return -1;
		}
		int size = images.size();
		for (int i = 0; i < size; ++i) {
			PicInfo picInfo = images.get(i).getUrls()
					.get(ImageType._IMAGE_TYPE_BIG);
			if (picInfo == null) {
				continue;
			}
			if (url.equals(picInfo.getUrl())) {
				return i;
			}
		}
		return -1;
	}

	private int getSrcVideo(String url) {
		if (TextUtils.isEmpty(url)) {
			return -1;
		}
		List<Video> videos = mArticleDetail.getVideoList();
		if (videos == null) {
			return -1;
		}
		int size = videos.size();
		for (int i = 0; i < size; ++i) {
			PicInfo picInfo = videos.get(i).getUrls()
					.get(ImageType._IMAGE_TYPE_BIG);
			if (picInfo == null) {
				continue;
			}
			if (url.equals(picInfo.getUrl())) {
				return i;
			}
		}
		return -1;
	}

	private void setArticleLoadingView() {
		mProgressBar.setVisibility(View.VISIBLE);
		mRetryArticle.setVisibility(View.INVISIBLE);
		mCommentCount.setVisibility(View.INVISIBLE);
		mProgressBarInner.startAnimation(mLoadingAnimation);

		mActionBar.setRightVisibility(View.GONE);
		mCommentView.setVisibility(View.GONE);
		commentCountView.setVisibility(View.GONE);
	}

	private void setArticleLoadedView() {
		mProgressBar.setVisibility(View.INVISIBLE);
		mRetryArticle.setVisibility(View.INVISIBLE);
		mCommentCount.setVisibility(View.VISIBLE);
		mProgressBarInner.clearAnimation();

		mActionBar.setRightVisibility(View.VISIBLE);
		mCommentView.setVisibility(View.VISIBLE);
		commentCountView.setVisibility(View.VISIBLE);
	}

	private void setArticleLoadFailedView() {
		LocalLog.d(TAG, "[getArticleDetail][setArticleLoadFailedView]");
		mProgressBar.setVisibility(View.INVISIBLE);
		mRetryArticle.setVisibility(View.VISIBLE);
		mCommentCount.setVisibility(View.INVISIBLE);
		mProgressBarInner.clearAnimation();

		mActionBar.setRightVisibility(View.GONE);
		mCommentView.setVisibility(View.GONE);
		commentCountView.setVisibility(View.GONE);
	}

	private class MyWebChromeClient extends WebChromeClient {
		private View mVideoProgressView;

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
			onShowCustomView(view, callback);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
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
			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(ArticleDetailActivity.this);
				mVideoProgressView = inflater.inflate(R.layout.video_progress,
						null);
			}
			return mVideoProgressView;
		}

		@Override
		public void onHideCustomView() {
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

	private void setCommentTitleView() {
		if (mArticleDetail.getCommentCount() == 0) {
			mTitleCommentNewest.setVisibility(View.GONE);
			mTitleCommentEmpty.setVisibility(View.VISIBLE);

			mTitleCommentEmpty.setOnClickListener(this);
		} else {
			mTitleCommentNewest.setVisibility(View.VISIBLE);
			mTitleCommentEmpty.setVisibility(View.GONE);
			mTitleCommentEmpty.setOnClickListener(null);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		LocalLog.d(TAG, "[onCreate]");
		super.onResume();
		if (Build.VERSION.SDK_INT >= 11) {
			mBody.onResume();
		}
		startActivity = false;
	}

	@SuppressLint("NewApi")
	@Override
	public void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT >= 11) {
			mBody.onPause();
		}
		mMonitor.onCancelMonitor();
	}

	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		LocalLog.d(TAG, "[onCreate]");
		EventBus.getDefault().register(this);
		Intent intent = getIntent();
		msharedPre = getSharedPreferences(Constants.KEY_UPDATE_GLOBAL,
				Context.MODE_PRIVATE);
		if (intent != null
				&& msharedPre.getBoolean(Constants.KEY_COPY_DETAIL_WEB, false)) {
			mArticleInfo = (ArticleInfo) intent
					.getSerializableExtra(KEY_ARTICLE_INFO);
			if (mArticleInfo == null) {
				finish();
				return;
			}

			@SuppressWarnings("unchecked")
			ArrayList<ArticleInfo> list = (ArrayList<ArticleInfo>) intent
					.getSerializableExtra(KEY_ARTICLE_LIST);
			if (list != null) {
				ArticleDetailSwitcher.getInstance().setArticleInfos(list);
			}

		} else {
			finish();
			return;
		}

		setContentView(R.layout.activity_article_detail);
		mGestureDetector = new GestureDetector((OnGestureListener) this);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setRightVisibility(View.GONE);
		commentCountView = LayoutInflater.from(this).inflate(
				R.layout.global_actionbar_comment, null);
		commentCountView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mArticleDetail != null) {
					CommentListActivity.startActivity(
							ArticleDetailActivity.this, mArticleDetail.getId(),
							mArticleDetail.getTitle());
				}
			}
		});
		((TextView) commentCountView.findViewById(R.id.comment_count))
				.setTextColor(getResources().getColor(R.color.actionbar_bg));
		commentCountView.findViewById(R.id.comment_count)
				.setBackgroundResource(R.drawable.comment_count_white);
		commentCountView.setVisibility(View.GONE);
		mActionBar.setCustomizeView(commentCountView);
		mActionBar.setRightImageResource(R.drawable.more_menu);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mActionBar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMoreMenu();
			}
		});

		if (mArticleInfo.getSourceUrl() != null) {
			mActionBar.setTitle(mArticleInfo.getSourceUrl());
		}

		mCommentView = (ArticleCommentView) findViewById(R.id.comment_view);
		mCommentView
				.setViewBackground(R.drawable.global_inputbox_parent_bg_light);
		mCommentView.setInputBoxBackground(R.drawable.global_inputbox_bg_light);
		mCommentView.setInputBoxTextColor(getResources().getColor(
				R.color.article_detail_comment_primary_text));
		mCommentView.setArticleId(mArticleInfo.getId());
		mCommentView.setVisibility(View.GONE);
		mScrollView = (ExtendedScrollView) findViewById(R.id.scroll);
		initWebView();
		initRecommandView();
		initExtraView();
		loadArticle();
		loadMyData();

		mState = STATE_INIT;
		if (ARTICLE_UNCERTAIN != mArticleInfo.getArticleType()) {
			sendArticleReadReport(mArticleInfo.getId(), mArticleInfo.getTitle());
		}
	}

	private void sendArticleReadReport(long id, String title) {
		if (title == null) {
			title = "";
		}
		StatsUtil
				.statsReport(this, "stats_read_article", "article_name", title);
		StatsUtil.statsReportByMta(this, "stats_read_article", "article_name",
				"(" + mArticleInfo.getId() + ")" + title);
		StatsUtil.statsReportByHiido("stats_read_article",
				"(" + mArticleInfo.getId() + ")" + title);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Fragment fs = getSupportFragmentManager().findFragmentByTag(
				ArticleSocialDialog.TAG_SOCIAL_DIALOG);
		if (fs != null && fs.isAdded() && fs instanceof ArticleSocialDialog) {
			((ArticleSocialDialog) fs).onActivityResult(requestCode,
					resultCode, data);
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				// String comment = data.getStringExtra(KEY_COMMENT);
				// Comment myComment = new Comment();
				// myComment.setId(COMMENT_MINE);
				// myComment.setContent(comment);
				// myComment.setCount(new Count(0, 0));
				// myComment.setUser(me);
				// myComment.setTime((int) (System.currentTimeMillis() / 1000));
				// mListAdapter.insert(myComment, 0);
				// mJump.performClick();
				// mArticleDetail
				// .setCommentCount(mArticleDetail.getCommentCount() + 1);
				// mCommentCount.setText(Integer.toString(mArticleDetail
				// .getCommentCount()));
				//
				// notifyCommentCountChanged();
				// setCommentTitleView();
			}
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

	@Override
	protected void onStop() {
		super.onStop();
		if (inCustomView()) {
			hideCustomView();
		}
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private boolean inCustomView() {
		return mCustomView != null;
	}

	private void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}

	private void initRecommandView() {
		mArticleViews = new View[3];
		mArticleImageViews = new ImageView[3];
		mArticleTitleTextViews = new TextView[3];
		mArticleViews[0] = findViewById(R.id.ll_article_view1);
		mArticleViews[0].setOnClickListener(this);
		mArticleViews[0].setTag(0);
		mArticleViews[1] = findViewById(R.id.ll_article_view2);
		mArticleViews[1].setOnClickListener(this);
		mArticleViews[1].setTag(1);
		mArticleViews[2] = findViewById(R.id.ll_article_view3);
		mArticleViews[2].setOnClickListener(this);
		mArticleViews[2].setTag(2);
		mArticleImageViews[0] = (ImageView) findViewById(R.id.iv_article_view1);
		mArticleImageViews[1] = (ImageView) findViewById(R.id.iv_article_view2);
		mArticleImageViews[2] = (ImageView) findViewById(R.id.iv_article_view3);
		mArticleTitleTextViews[0] = (TextView) findViewById(R.id.tv_article_view1);
		mArticleTitleTextViews[1] = (TextView) findViewById(R.id.tv_article_view2);
		mArticleTitleTextViews[2] = (TextView) findViewById(R.id.tv_article_view3);
	}

	private void initExtraView() {
		mProgressBarInner = findViewById(R.id.progressbar_load_article_inner);
		mProgressBar = findViewById(R.id.progressbar_load_article);
		mRetryArticle = findViewById(R.id.retry_article);
		mRetryArticle.setOnClickListener(this);
		mLikeArticle = (ImageView) findViewById(R.id.like_article);
		mDislikeArticle = (ImageView) findViewById(R.id.dislike_article);
		mLikeCount = (TextView) findViewById(R.id.like_count);
		mDislikeCount = (TextView) findViewById(R.id.dislike_count);
		mTag = new TextView[3];
		mTag[0] = (TextView) findViewById(R.id.tag_a);
		mTag[1] = (TextView) findViewById(R.id.tag_b);
		mTag[2] = (TextView) findViewById(R.id.tag_c);
		mCommentCount = (TextView) findViewById(R.id.comment_count);
		mCommentCount.setVisibility(View.INVISIBLE);
		findViewById(R.id.like_article_container).setOnClickListener(this);
		findViewById(R.id.dislike_article_container).setOnClickListener(this);

		mScrollView.setOnFlipListener(this);
		mLoadingAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.article_detail_loading);
		mLoadingAnimation.setInterpolator(new LinearInterpolator());
		mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		mTitleCommentNewest = findViewById(R.id.title_comment_newest);
		mTitleCommentEmpty = findViewById(R.id.title_comment_empty);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mBody = (ExtendedWebView) findViewById(R.id.article_body);
		mCustomViewContainer = (FrameLayout) findViewById(R.id.custom_view_container);
		// mScrollView.webView = mBody;
		mBody.getSettings().setJavaScriptEnabled(true);
		mBody.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mBody.getSettings().setDomStorageEnabled(true);
		mBody.addJavascriptInterface(new JsInterface(this), "client"); // JS交互
		mBody.getSettings().setSupportZoom(false);
		String userAgent = Constants.USER_AGENT_PREFIX
				+ GameNewsApplication.getInstance().getPackageInfo().versionName;
		mBody.getSettings().setUserAgentString(userAgent);
		mWebChromeClient = new MyWebChromeClient();
		mBody.setWebChromeClient(mWebChromeClient);
		mBody.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mMonitor.onCancelMonitor();
				LocalLog.d(TAG,
						"[WebViewClient][shouldOverrideUrlLoading], url = "
								+ url);
				Intent intent = new Intent(ArticleDetailActivity.this,
						AppWebActivity.class);
				intent.putExtra(AppWebActivity.KEY_URL, url);
				startActivity(intent);
				return true;
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				LocalLog.d(TAG, "[WebViewClient][onLoadResource], url = " + url);
				mMonitor.onCancelMonitor();
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				LocalLog.d(TAG, "[WebViewClient][onPageStarted], url = " + url);
				mMonitor.onCancelMonitor();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				LocalLog.d(TAG, "[WebViewClient][onPageFinished], url = " + url);
				mMonitor.onCancelMonitor();
				setUpRecommendArticle();
				maskAsViewed();
				super.onPageFinished(view, url);

			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				mMonitor.onCancelMonitor();
				LocalLog.d(TAG, "[WebViewClient][onReceivedError], url = "
						+ failingUrl + ", errorCode = " + errorCode);
				Toast.makeText(ArticleDetailActivity.this, description,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showMoreMenu() {
		if (mArticleDetail == null) {
			return;
		}
		ArrayList<DropDownItem> itemList = new ArrayList<DropDownHelper.DropDownItem>();
		itemList.add(new DropDownItem(getResources().getString(R.string.share),
				R.drawable.btn_show_topic_detail_share_selector, false, false));
		boolean hasFav = mArticleDetail == null ? false : mArticleDetail.hasFav;
		itemList.add(new DropDownItem(getResources().getString(
				hasFav ? R.string.cancel_collect : R.string.collect),
				R.drawable.btn_article_collect_selector, hasFav, false));
		DropDownHelper.showDropDownList(this, mActionBar.getRightImageView(),
				itemList, new OnDropDownClickListener() {

					@Override
					public void onClick(int position, String text) {
						if (position == 0) {
							if (mArticleDetail == null) {
								return;
							}
							DialogFragment fs = ArticleSocialDialog.newInstance(
									mArticleDetail.getId(),
									mArticleDetail.getTitle(), mSocialImgUrl,
									ArticleSocialDialog.SHARED_FROM_ARTICLE);
							Util.showDialog(ArticleDetailActivity.this, fs,
									ArticleSocialDialog.TAG_SOCIAL_DIALOG);
						} else if (position == 1) {
							collectPost();
						} else {
							if (mArticleDetail == null) {
								return;
							}
							DialogFragment fr = ArticleReportDialog
									.newInstance(mArticleInfo.getId(),
											mArticleDetail.getTitle());
							Util.showDialog(ArticleDetailActivity.this, fr,
									ArticleSocialDialog.TAG_REPORT_DIALOG);
						}
					}
				});
	}

	private void loadMyData() {

		UserInitRsp rsp = Preference.getInstance().getInitRsp();
		me = null;
		if (rsp != null) {
			me = rsp.getUser();
		}
		if (me == null) {
			me = new User();
			me.setId("-1");
			me.setName(getResources()
					.getString(R.string.global_my_default_name));
		}

		IPageCache pageCache = new IPageCache();

		mMyArticlesLike = Preference.getInstance().getMyArticlesLike();
		if (mMyArticlesLike == null) {
			mMyArticlesLike = new HashSet<Long>();
		}
		mMyArticlesDislike = Preference.getInstance().getMyArticlesDislike();
		if (mMyArticlesDislike == null) {
			mMyArticlesDislike = new HashSet<Long>();
		}

	}

	private void setUpRecommendArticle() {
		if (recommendArticleList == null || recommendArticleList.size() <= 0) {
			findViewById(R.id.ll_recommend_article_view).setVisibility(
					View.GONE);
		} else {
			int articleCount = Math.min(recommendArticleList.size(), 3);
			if (articleCount > 1) {
				findViewById(R.id.recommand_divider1).setVisibility(
						View.VISIBLE);
			}
			if (articleCount > 2) {
				findViewById(R.id.recommand_divider2).setVisibility(
						View.VISIBLE);
			}

			for (int i = 0; i < articleCount; ++i) {
				mArticleViews[i].setVisibility(View.VISIBLE);
				mArticleTitleTextViews[i].setText(recommendArticleList.get(i)
						.getTitle());
				ArrayList<String> imageList = recommendArticleList.get(i)
						.getImageList();
				if (imageList != null && imageList.size() > 0) {
					SwitchImageLoader.getInstance().displayImage(
							imageList.get(0), mArticleImageViews[i],
							SwitchImageLoader.DEFAULT_ARTICLE_ITEM_DISPLAYER);
				}
			}
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.ll_detail_footer_view).setVisibility(
						View.VISIBLE);
			}
		}, 100);
	}

	private void maskAsViewed() {
		if (mArticleDetail == null) {
			return;
		}
		ArticleUtil.maskAsViewed(mArticleDetail.getId());
	}

	private void setArticleDetail(GetArticleDetailRsp data) {

		LocalLog.d(TAG, "[setArticleDetail] enter");
		boolean afterCache = false;
		if (mArticleDetail != null) {
			afterCache = true;
			if (data.getArticleDetail() != null) {
				mArticleDetail = data.getArticleDetail();
				mExtraInfo = data.getExtraInfo();
			}
		} else {
			mArticleDetail = data.getArticleDetail();
			mExtraInfo = data.getExtraInfo();
		}
		if (mArticleDetail == null) {
			Toast.makeText(
					ArticleDetailActivity.this,
					getResources().getString(
							R.string.article_detail_load_failed),
					Toast.LENGTH_SHORT).show();
			setArticleLoadFailedView();
			return;
		}
		recommendArticleList = data.getRecommendList();

		/**
		 * 如果是推送过来的数据，在拉取到数据之后发送报告
		 */
		if (ARTICLE_UNCERTAIN == mArticleInfo.getArticleType()) {
			sendArticleReadReport(mArticleDetail.getId(),
					mArticleDetail.getTitle());
		}

		LocalLog.d(TAG, "[setArticleDetail]afterCache = " + afterCache);
		if (!afterCache) {
			SharedPreferences mPref = GameNewsApplication.getInstance()
					.getSharedPreferences(Constants.KEY_UPDATE_GLOBAL,
							MODE_PRIVATE);
			File dir = null;
			if (mPref.getBoolean(Constants.NEW_VERSION_READY, false)) {
				int currDir = mPref.getInt(Constants.CURR_DIR, 0);
				int newDir = 1 - currDir;
				dir = this.getDir(String.valueOf(newDir), Context.MODE_PRIVATE);
				mPref.edit().putBoolean(Constants.NEW_VERSION_READY, false)
						.putInt(Constants.CURR_DIR, newDir).commit();
			} else {
				int currDir = mPref.getInt(Constants.CURR_DIR, 0);
				dir = this
						.getDir(String.valueOf(currDir), Context.MODE_PRIVATE);
			}
			String filepath = null;
			if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
				filepath = dir.getAbsolutePath() + "/" + Constants.NEWS_HTML;
			} else if (Constants
					.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
				filepath = dir.getAbsolutePath() + "/" + Constants.SPORTS_HTML;
			} else {
				filepath = dir.getAbsolutePath() + "/" + Constants.NEWS_HTML;
			}
			LocalLog.d(TAG, "[setArticleDetail][mBody.loadUrl], filepath = "
					+ filepath);
			mBody.loadUrl("file://" + filepath);
			mDataDisplayed = true;
			mMonitor.startMonitor();
		}
		mLikeCount.setText(Integer.toString(mArticleDetail.getCount()
				.getLikeCount()));

		LikeEvent event = new LikeEvent();
		event.id = mArticleDetail.getId();
		event.likeCount = Integer.parseInt(mLikeCount.getText().toString());
		EventBus.getDefault().post(event);

		mDislikeCount.setText(Integer.toString(mArticleDetail.getCount()
				.getDislikeCount()));
		if (mArticleDetail.getHasLike() == Like._HAS_PRAISE) {
			if (!mMyArticlesLike.contains(mArticleInfo.getId())) {
				mMyArticlesLike.add(mArticleInfo.getId());
			}
			mLikeArticle
					.setImageResource(R.drawable.article_detail_like_pressed);
			mDislikeArticle
					.setImageResource(R.drawable.article_detail_dislike_normal);
		} else if (mArticleDetail.getHasLike() == Like._HAS_STAMP) {
			if (!mMyArticlesDislike.contains(mArticleInfo.getId())) {
				mMyArticlesDislike.add(mArticleInfo.getId());
			}
			mDislikeArticle
					.setImageResource(R.drawable.article_detail_dislike_pressed);
			mLikeArticle
					.setImageResource(R.drawable.article_detail_like_normal);
		} else {
			if (mMyArticlesLike.contains(mArticleInfo.getId())) {
				mMyArticlesLike.remove(mArticleInfo.getId());
			}
			if (mMyArticlesDislike.contains(mArticleInfo.getId())) {
				mMyArticlesDislike.remove(mArticleInfo.getId());
			}
			mLikeArticle
					.setImageResource(R.drawable.article_detail_like_normal);
			mDislikeArticle
					.setImageResource(R.drawable.article_detail_dislike_normal);
		}
		// if (mMyArticlesLike.contains(mArticleInfo.getId())) {
		// mLikeArticle
		// .setImageResource(R.drawable.article_detail_like_pressed);
		// } else {
		// mLikeArticle
		// .setImageResource(R.drawable.article_detail_like_normal);
		// }
		// if (mMyArticlesDislike.contains(mArticleInfo.getId())) {
		// mDislikeArticle
		// .setImageResource(R.drawable.article_detail_dislike_pressed);
		// } else {
		// mDislikeArticle
		// .setImageResource(R.drawable.article_detail_dislike_normal);
		// }

		List<Channel> channels = mArticleDetail.getChannelList();
		if (channels != null && channels.size() > 0) {
			int tagCount = Math.min(channels.size(), 3);
			int maxTagWidth = (getWindowManager().getDefaultDisplay()
					.getWidth() - 170) / tagCount;
			for (int i = 0; i < tagCount; ++i) {
				mTag[i].setMaxWidth(maxTagWidth);
				mTag[i].setText(channels.get(i).getName());
				mTag[i].setOnClickListener(ArticleDetailActivity.this);
			}
		}

		mActionBar.setTitle(mArticleDetail.getSourceUrl());

		if (mArticleDetail.hasFav) {
			mKept = true;
		} else {
			mKept = false;
		}
		mCommentCount
				.setText(Integer.toString(mArticleDetail.getCommentCount()));

		mSocialImgUrl = "";
		List<Image> images = mArticleDetail.getImageList();
		if (images != null) {
			for (Image image : images) {
				if (image.getUrls() == null) {
					continue;
				}
				// PicInfo picInfoS = image.getUrls().get(
				// ImageType._IMAGE_TYPE_SMALL);
				// if (picInfoS != null
				// && !TextUtils.isEmpty(picInfoS.getUrl())) {
				// imgUrl = picInfoS.getUrl();
				// break;
				// }

				PicInfo picInfoB = image.getUrls().get(
						ImageType._IMAGE_TYPE_BIG);
				if (picInfoB != null && !TextUtils.isEmpty(picInfoB.getUrl())) {
					mSocialImgUrl = picInfoB.getUrl();
					break;
				}
			}
		}
		notifyCommentCountChanged();

	}

	private void loadArticle() {
		LocalLog.d(TAG, "[loadArticle]");
		setArticleLoadingView();
		ArticleDetailModel.getArticleDetail(
				new ResponseListener<GetArticleDetailRsp>(
						ArticleDetailActivity.this) {
					@Override
					public void onError(Exception e) {
						LocalLog.d(TAG, "[getArticleDetail][onError]");
						if (mArticleDetail != null) {
							return;
						}
						if (e instanceof UserException) {
							String msg = e.getMessage();
							if (!TextUtils.isEmpty(msg)) {
								ToastUtil.showToast(msg);
							}
						} else {
							if (!Util.isNetworkConnected()) {
								ToastUtil
										.showToast(R.string.http_not_connected);
								return;
							} else {
								ToastUtil.showToast(R.string.http_error);
							}
						}
						setArticleLoadFailedView();
					}

					@Override
					public void onResponse(GetArticleDetailRsp data) {
						LocalLog.d(TAG,
								"[getArticleDetail][onResponse], data = "
										+ data);
						setArticleDetail(data);
					}
				}, // Listener
				mArticleInfo.getId());
	}

	class JsInterface {
		Context context;

		public JsInterface(Context context) {
			this.context = context;
		}

		// 查看图片url
		@JavascriptInterface
		public void openVideo(String url) {
			LocalLog.d(TAG, "[JsInterface][openVideo], url = " + url);
			// Intent intent = new Intent(ArticleDetailActivity.this,
			// VideoPlayerActivity.class);
			// intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, url);
			// startActivity(intent);
			//

			VideoPlayerActivity.startVideoPlayerActivity(
					ArticleDetailActivity.this, "", url);
		}

		// 查看图片url
		@JavascriptInterface
		public void openImage(String img) {
			LocalLog.d(TAG, "[JsInterface][openImage], img = " + img);
			// Intent intent = new Intent(ArticleDetailActivity.this,
			// ImageDetailViewerActivity.class);
			// intent.putExtra(ImageDetailViewerActivity.EXTRA_IMAGE_LIST,
			// (Serializable) mSrcImages);
			// intent.putExtra(ImageDetailViewerActivity.EXTRA_CURRENT_IMAGE,
			// 0);
			// startActivity(intent);
			//
			ArrayList<String> imageList = new ArrayList<String>();
			if (!TextUtils.isEmpty(img)) {
				imageList.add(img);
				Intent intent = new Intent(ArticleDetailActivity.this,
						ImageZoomDetailViewerActivity.class);
				intent.putExtra(
						ImageZoomDetailViewerActivity.EXTRA_IMAGE_LIST_ZOOM,
						imageList);
				intent.putExtra(
						ImageZoomDetailViewerActivity.EXTRA_CURRENT_IMAGE, 0);
				intent.putExtra(ImageZoomDetailViewerActivity.EXTRA_TITLE,
						mArticleDetail.getTitle());
				startActivity(intent);
			}

		}

		// 获取文章信息
		@JavascriptInterface
		public String getArticleDetail() {
			// Toast.makeText(context, "getArticleDetail()", Toast.LENGTH_SHORT)
			// .show();
			LocalLog.d(TAG, "[JsInterface][getArticleDetail]");
			try {
				JSONObject info = new JSONObject();
				info.put("content", mArticleDetail.getContent());
				info.put("timeStamp",
						TimeUtil.parseTime(mArticleDetail.getTimeStamp()));
				info.put("title", mArticleDetail.getTitle());
				info.put("refer", mArticleDetail.getRefer());
				info.put("sourceUrl", mArticleDetail.getSourceUrl());
				if (mExtraInfo != null
						&& mExtraInfo
								.containsKey(ExtraInfoType.EATRAINFO_TYPE_SUPRISE_AWARD
										.value())) {
					info.put("extraInfo", mExtraInfo
							.get(ExtraInfoType.EATRAINFO_TYPE_SUPRISE_AWARD
									.value()));
				}
				return info.toString();
			} catch (Exception e) {
				return null;
			}
		}

		// 获取文章信息
		@JavascriptInterface
		public void toast(String msg) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}

		// 获取文章信息
		@JavascriptInterface
		public void longtoast(String msg) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}

		// 获取文章信息
		@JavascriptInterface
		public String getArticleDetail(String key) {
			// Toast.makeText(context, "getArticleDetail " + key,
			// Toast.LENGTH_SHORT).show();

			LocalLog.d(TAG, "[JsInterface][getArticleDetail(key) = ]" + key);
			if ("content".equals(key)) {
				return mArticleDetail.getContent();
			}
			if ("timeStamp".equals(key)) {
				return TimeUtil.parseTime(mArticleDetail.getTimeStamp());
			}
			if ("title".equals(key)) {
				return mArticleDetail.getTitle();
			}
			if ("refer".equals(key)) {
				return mArticleDetail.getRefer();
			}
			if ("sourceUrl".equals(key)) {
				return mArticleDetail.getSourceUrl();
			}
			return "";
		}

		// 获取加载中本地图片地址
		@JavascriptInterface
		public String getLoadingPicInfo() {
			return "image_bg_loading.png";
		}

		// 获取所有的图片尺寸信息
		@JavascriptInterface
		public String getPicInfo() {
			// Toast.makeText(context, "getPicInfo()",
			// Toast.LENGTH_SHORT).show();
			JSONArray infos = new JSONArray();
			// 图片信息
			{
				List<Image> images = mArticleDetail.getImageList();
				if (images != null) {
					int size = images.size();
					for (int i = 0; i < size; ++i) {
						try {
							PicInfo picInfo = images.get(i).getUrls()
									.get(ImageType._IMAGE_TYPE_BIG);
							JSONObject info = new JSONObject();
							info.put("id", i);
							info.put("type", "image");
							info.put("width", picInfo.getWidth());
							info.put("height", picInfo.getHeight());
							File cache = ImageLoader.getInstance()
									.getDiscCache().get(picInfo.getUrl());
							if (cache.exists()) {
								String destFileName = String.format(
										"file:///%s", cache);
								info.put("src", destFileName);
							} else {
								info.put("src", "");
							}

							infos.put(info);
							// return new JSONObject(bean.ManeuverInfo);
						} catch (Exception e) {
							return infos.toString();
						}
					}
				}
			}
			// 视频cover信息
			{

				List<Video> videos = mArticleDetail.getVideoList();
				if (videos != null) {
					int size = videos.size();
					for (int i = 0; i < size; ++i) {
						try {
							PicInfo picInfo = videos.get(i).getUrls()
									.get(ImageType._IMAGE_TYPE_BIG);
							JSONObject info = new JSONObject();
							info.put("id", i);
							info.put("type", "video");
							info.put("width", picInfo.getWidth());
							info.put("height", picInfo.getHeight());
							File cache = ImageLoader.getInstance()
									.getDiscCache().get(picInfo.getUrl());
							if (cache.exists()) {
								String destFileName = String.format(
										"file:///%s", cache);
								info.put("src", destFileName);
							} else {
								info.put("src", "");
							}
							infos.put(info);
							// return new JSONObject(bean.ManeuverInfo);
						} catch (Exception e) {
							return infos.toString();
						}
					}
				}
			}
			return infos.toString();
		}

		// 加载图片
		@JavascriptInterface
		public void loadImage(final int id) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (mArticleDetail.getImageList() == null) {
						return;
					}
					Image image = mArticleDetail.getImageList().get(id);
					if (image == null) {
						return;
					}
					PicInfo picInfo = image.getUrls().get(
							ImageType._IMAGE_TYPE_BIG);
					if (picInfo == null) {
						return;
					}
					if (ImageLoader.getInstance().getDiscCache()
							.get(picInfo.getUrl()).exists()
							|| SwitchImageLoader.getInstance().needLoadImage()) {
						ImageLoader.getInstance().loadImage(picInfo.url,
								mImageLoadingListener);
					}
				}
			});
		}

		// 加载视频cover
		@JavascriptInterface
		public void loadVideoCover(final int id) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mArticleDetail.getVideoList() == null) {
						return;
					}
					Video video = mArticleDetail.getVideoList().get(id);
					if (video == null) {
						return;
					}
					PicInfo picInfo = video.getUrls().get(
							ImageType._IMAGE_TYPE_BIG);
					if (picInfo == null) {
						return;
					}
					ImageLoader.getInstance().loadImage(picInfo.url,
							mImageLoadingListener);

				}
			});
		}

		// content渲染结束
		@JavascriptInterface
		public void onPageFinished() {
			LocalLog.d(TAG, "[JsInterface][onPageFinished]");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					setArticleLoadedView();
				}
			});
		}

		// 点击图片标签
		@JavascriptInterface
		public void onClickImage(int pos) {
			ArrayList<Image> imageList = mArticleDetail.getImageList();
			if (imageList == null || imageList.size() == 0) {
				return;
			}
			ArrayList<String> arrayList = new ArrayList<String>();
			for (Image image : imageList) {
				arrayList.add(image.getUrls().get(ImageType._IMAGE_TYPE_BIG)
						.getUrl());
			}
			ImageZoomDetailViewerActivity.startZoomDetailActivity(
					ArticleDetailActivity.this, arrayList, pos,
					mArticleDetail.getTitle());
			// ArticleGalleryActivity.startActivity(ArticleDetailActivity.this,
			// null, mArticleDetail.getId(), mArticleDetail.getTitle(),
			// pos, ArticleGalleryActivity.SOURCE_QUERY,
			// mArticleDetail.commentCount,
			// ArticleType._ARTICLE_TYPE_ARTICLE, false);

			// Intent intent = new Intent(ArticleDetailActivity.this,
			// ImageZoomDetailViewerActivity.class);
			// intent.putExtra(
			// ImageZoomDetailViewerActivity.EXTRA_IMAGE_LIST_ZOOM,
			// arrayList);
			// intent.putExtra(ImageZoomDetailViewerActivity.EXTRA_CURRENT_IMAGE,
			// id);
			// intent.putExtra(ImageZoomDetailViewerActivity.EXTRA_TITLE,
			// mArticleDetail.getTitle());
			// startActivity(intent);

		}

		// 点击视频标签
		@JavascriptInterface
		public void onClickVideo(int id) {
			// Toast.makeText(context, "onClickVideo " + Integer.toString(id),
			// Toast.LENGTH_SHORT).show();
			Video video = mArticleDetail.getVideoList().get(id);
			// Intent intent = new Intent(ArticleDetailActivity.this,
			// VideoPlayerActivity.class);
			// intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL,
			// video.getMp4Url());
			// startActivity(intent);
			//

			VideoPlayerActivity.startVideoPlayerActivity(
					ArticleDetailActivity.this, "", video.getMp4Url());
		}

		// 滚动到指定y
		@JavascriptInterface
		public void scrollTo(final int y) {
			Toast.makeText(context, "loadVideoCover " + Integer.toString(y),
					Toast.LENGTH_SHORT).show();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mBody.scrollTo(0, y);

				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_comment_empty:
			postComment();
			break;
		case R.id.tag_a: {
			Channel channel = mArticleDetail.getChannelList().get(0);
			ArticleListActivity.startChannelListActivity(
					ArticleDetailActivity.this, channel);
		}
			break;
		case R.id.tag_b: {
			Channel channel = mArticleDetail.getChannelList().get(1);
			ArticleListActivity.startChannelListActivity(
					ArticleDetailActivity.this, channel);
		}
			break;
		case R.id.tag_c: {
			Channel channel = mArticleDetail.getChannelList().get(2);
			ArticleListActivity.startChannelListActivity(
					ArticleDetailActivity.this, channel);
		}
			break;
		case R.id.dislike_article_container:
			if (mArticleDetail.getHasLike() == Like._HAS_PRAISE) {
				Toast.makeText(this,
						getResources().getString(R.string.liked_hint),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (mArticleDetail.getHasLike() == Like._HAS_STAMP) {
				Toast.makeText(this,
						getResources().getString(R.string.disliked_hint),
						Toast.LENGTH_SHORT).show();
				return;
			}

			ReportModel.LikeArticle(new ResponseListener<Boolean>(
					ArticleDetailActivity.this) {

				@Override
				public void onResponse(Boolean arg0) {
					mArticleDetail.setHasLike(Like._HAS_STAMP);
					mMyArticlesDislike.add(mArticleInfo.getId());
					Preference.getInstance().saveMyArticlesDislike(
							mMyArticlesDislike);
					mDislikeArticle
							.setImageResource(R.drawable.article_detail_dislike_pressed);
					mDislikeCount.setText(Integer.toString(Integer
							.parseInt(mDislikeCount.getText().toString()) + 1));

				}

			}, mArticleInfo.getId(), LikeType.LIKE_TYPE_DISLIKE);
			break;
		case R.id.like_article_container:
			if (mArticleDetail.getHasLike() == Like._HAS_PRAISE) {
				Toast.makeText(this,
						getResources().getString(R.string.liked_hint),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (mArticleDetail.getHasLike() == Like._HAS_STAMP) {
				Toast.makeText(this,
						getResources().getString(R.string.disliked_hint),
						Toast.LENGTH_SHORT).show();
				return;
			}
			ReportModel.LikeArticle(new ResponseListener<Boolean>(
					ArticleDetailActivity.this) {

				@Override
				public void onResponse(Boolean arg0) {
					mArticleDetail.setHasLike(Like._HAS_PRAISE);
					mMyArticlesLike.add(mArticleInfo.getId());
					Preference.getInstance()
							.saveMyArticlesLike(mMyArticlesLike);
					mLikeArticle
							.setImageResource(R.drawable.article_detail_like_pressed);
					mLikeCount.setText(Integer.toString(Integer
							.parseInt(mLikeCount.getText().toString()) + 1));

					LikeEvent event = new LikeEvent();
					event.id = mArticleInfo.getId();
					event.likeCount = Integer.parseInt(mLikeCount.getText()
							.toString());
					EventBus.getDefault().post(event);
				}

			}, mArticleInfo.getId(), LikeType.LIKE_TYPE_LIKE);
			break;
		case R.id.retry_article:
			loadArticle();
			break;
		case R.id.ll_article_view1:
		case R.id.ll_article_view2:
		case R.id.ll_article_view3:
			ArticleInfo articleInfo = recommendArticleList.get((Integer) v
					.getTag());
			ArticleDetailActivity.startArticleDetailActivity(
					ArticleDetailActivity.this, articleInfo);
			String eventId = "stats_click_recomm";
			String key = "article_title";
			String value = String.valueOf(articleInfo.getTitle() + "("
					+ articleInfo.getId() + ")");
			StatsUtil.statsReport(this, eventId, key, value);
			StatsUtil.statsReportByMta(this, eventId, key, value);
			StatsUtil.statsReportByHiido(eventId, key + value);
			break;
		default:
			break;
		}
	}

	private void collectPost() {
		if (mArticleDetail == null) {
			return;
		}
		if (mKept) {

			ReportModel.AddFavArticle(new ResponseListener<Boolean>(
					ArticleDetailActivity.this) {
				@Override
				public void onError(Exception e) {
					super.onError(e);
					ToastUtil.showToast(R.string.load_failed);
				}

				@Override
				public void onResponse(Boolean arg0) {
					mKept = false;
					mArticleDetail.setHasFav(false);
					Toast.makeText(
							ArticleDetailActivity.this,
							getResources().getString(
									R.string.article_keep_cancel),
							Toast.LENGTH_SHORT).show();
					Preference.getInstance().saveMyFavCount(
							Preference.getInstance().getMyFavCount() - 1);
				}
			}, mArticleInfo.getId(), FavType.FAV_TYPE_DEL);
		} else {

			ReportModel.AddFavArticle(new ResponseListener<Boolean>(
					ArticleDetailActivity.this) {
				@Override
				public void onError(Exception e) {
					super.onError(e);
					ToastUtil.showToast(R.string.load_failed);
				}

				@Override
				public void onResponse(Boolean arg0) {
					mKept = true;
					mArticleDetail.setHasFav(true);
					Toast.makeText(
							ArticleDetailActivity.this,
							getResources().getString(
									R.string.article_keep_success),
							Toast.LENGTH_SHORT).show();
					Preference.getInstance().saveMyFavCount(
							Preference.getInstance().getMyFavCount() + 1);
				}

			}, mArticleInfo.getId(), FavType.FAV_TYPE_ADD);
		}

		StatsUtil.statsReport(this, "stats_collect", "article_name",
				mArticleDetail.getTitle());
		StatsUtil.statsReportByMta(this, "stats_collect", "article_name",
				mArticleInfo.getTitle());
		StatsUtil.statsReportByHiido("stats_collect", "article_name"
				+ mArticleDetail.getTitle());
	}

	public void onEvent(CommentEvent commentEvent) {

		if (commentEvent != null && mArticleDetail != null
				&& mArticleDetail.getId() == commentEvent.getId()) {

			int count = commentEvent.commentCount;
			if (count == CommentEvent.CMT_CNT_ADD) {
				count = mArticleDetail.getCommentCount() + 1;
				mArticleDetail.setCommentCount(count);
			}

			mCommentCount.setText(Integer.toString(count));
		}
	}

	private void postComment() {

		if (mArticleDetail == null) {
			return;
		}

		mCommentView.showInputMethod();

		StatsUtil.statsReport(this, "stats_comment", "article_name",
				mArticleDetail.getTitle());
		StatsUtil.statsReportByMta(this, "stats_comment", "article_name",
				mArticleInfo.getTitle());
		StatsUtil.statsReportByHiido("stats_comment", "article_name"
				+ mArticleDetail.getTitle());
	}

	private void notifyCommentCountChanged() {

		CommentEvent event = new CommentEvent();
		event.id = mArticleDetail.getId();
		event.commentCount = mArticleDetail.getCommentCount();
		EventBus.getDefault().post(event);
	}

	@Override
	public void onFlipRight() {
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public boolean onUp() {
		return ArticleDetailSwitcher.getInstance().switchToNextArticle(
				ArticleDetailActivity.this, mArticleInfo.getId(),
				ArticleType._ARTICLE_TYPE_ARTICLE, null);
	}

	@Override
	public boolean onDown() {
		return ArticleDetailSwitcher.getInstance().switchToPreArticle(
				ArticleDetailActivity.this, mArticleInfo.getId(),
				ArticleType._ARTICLE_TYPE_ARTICLE, null);
	}

	private boolean startActivity = false;

	private int orientation = UNDEFINE;

	@Override
	public void onFlipLeft() {
		CommentListActivity.startActivity(ArticleDetailActivity.this,
				mArticleInfo.getId(), mArticleInfo.getTitle());
	}

	private GestureDetector mGestureDetector;
	private int verticalMinDistance = Util.getAppWidth() / 5;
	private int minVelocity = 100;

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		// orientation = UNDEFINE;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean mDataDisplayed;

	/**
	 * 捕获上下左右滑动手势
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		int distanceX = (int) (e1.getX() - e2.getX());
		int distanceY = (int) (e1.getY() - e2.getY());
		if (orientation == HORIZIONTAL) {
			if (distanceX > verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				onFlipLeft();
			} else if (distanceX < -verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				onFlipRight();
			}
		} else if (orientation == VERTICAL) {
			if (startActivity) {
				return false;
			}

			if (!mDataDisplayed
					|| Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
				// for KITKAT webview load video will cause deadlock issue
				return false;
			}
			if (!mScrollView.canScrollVerticallyex(1)
					&& distanceY > verticalMinDistance
					&& Math.abs(velocityY) > minVelocity) {
				startActivity = onUp();
			} else if (!mScrollView.canScrollVerticallyex(-1)
					&& distanceY < -verticalMinDistance
					&& Math.abs(velocityY) > minVelocity) {
				startActivity = onDown();
			}
		}

		return true;
	}

	float downX;
	float downY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = ev.getX();
			downY = ev.getY();
			orientation = UNDEFINE;
		}
		mGestureDetector.onTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_MOVE
				&& (Math.abs(ev.getX() - downX) > verticalMinDistance / 4 || Math
						.abs(ev.getY() - downY) > verticalMinDistance / 4)) {
			if (orientation == HORIZIONTAL) {
				return true;
			} else if (orientation == VERTICAL) {
				return super.dispatchTouchEvent(ev);
			} else if (Math.abs(ev.getX() - downX) > Math
					.abs(ev.getY() - downY)) {
				orientation = HORIZIONTAL;
				return true;
			} else {
				orientation = VERTICAL;
				return super.dispatchTouchEvent(ev);
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	private WebViewMonitor mMonitor = new WebViewMonitor(this);

}
