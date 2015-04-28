package com.yy.android.gamenews.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.WelcomeChannel;
import com.duowan.show.NotificationRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.LoginStatusEvent;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.MessageEvent;
import com.yy.android.gamenews.event.SchedTabChangedEvent;
import com.yy.android.gamenews.event.XGPushEvent;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.plugin.schetable.SchedFragment;
import com.yy.android.gamenews.plugin.show.TagListActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.ImagePagerAdapter;
import com.yy.android.gamenews.ui.view.WelcomeChannelView;
import com.yy.android.gamenews.ui.view.tab.FrameFragmentItem;
import com.yy.android.gamenews.ui.view.tab.FrameFragmentLayout;
import com.yy.android.gamenews.ui.view.tab.FrameFragmentLayout.OnFrameChangeListener;
import com.yy.android.gamenews.util.AnimationHelper;
import com.yy.android.gamenews.util.ClassUtils;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.MessageAsyncTask;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.SignUtil;
import com.yy.android.gamenews.util.SignUtil.OnSignEndListener;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.WebViewCacheUtil;
import com.yy.android.gamenews.util.maintab.MainTab;
import com.yy.android.gamenews.util.maintab.MainTab1;
import com.yy.android.gamenews.util.maintab.MainTab2;
import com.yy.android.gamenews.util.maintab.MainTab3SportBrush;
import com.yy.android.gamenews.util.maintab.MainTab5Gamenews;
import com.yy.android.gamenews.util.maintab.MainTabFactory;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	// public static final String TAG_NAME_INFO = "info"; // 头条，第一个tab
	// public static final String TAG_NAME_NEWS = "news"; // 频道，第二个tab
	// public static final String TAG_NAME_BRUSH = "brush";
	// public static final String TAG_NAME_EXTRA1 = "extra1"; // 额外的tab1
	// public static final String TAG_NAME_EXTRA2 = "extra2"; // 额外的tab2
	// public static final String TAG_NAME_EXTRA3 = "extra3"; // 额外的tab3

	public static final String ACTION_BRUSH_CLICKED = "action_brush_clicked";

	private SignUtil mSignUtil;
	private View mWelcome_main;

	private Animation mAnimRadioIn;
	private Animation mAnimRadioOut;
	// 游戏刷子
	private ImageView imageViewOne;
	private ImageView imageViewTwo;
	private ImageView imageViewThree;
	private ImageView imageView1;
	private ImageView imageView3;

	private ActionBar mActionBar;
	private FrameFragmentLayout mFragmentFrame;

	private Preference mPref;
	private boolean mShowWelcomeView;
	private boolean mIsShowWelcomeGuide;

	// private List<ActiveInfo> mActiveChannelList;

	private static final int DURATION_EXIT_APP = 2000; // 在该间隔内按两次返回键退出应用
	// 要隐藏时，layout最少要显示的时间
	private static final int FILT_DURATION = 200; // 在该时间内被发送过来的消息会覆盖之前的消息
	private static final int MSG_SHOW_RADIO = 1001;
	private static final int MSG_HIDE_RADIO = 1002;
	private static final int MSG_FAKE_EXIT_APP = 1003;

	private Handler mHandler = new UIHandler(this);

	private static class UIHandler extends Handler {
		private WeakReference<MainActivity> mRef;

		public UIHandler(MainActivity activity) {
			mRef = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity activity = mRef.get();
			if (activity == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_RADIO: {
				activity.showMainRadioNow();
				break;
			}
			case MSG_HIDE_RADIO: {
				activity.hideMainRadioNow();
				break;
			}
			}
		}
	}

	private boolean isFirstLaunch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		EventBus.getDefault().register(this);

		setContentView(R.layout.activity_main);

		handleIntent(getIntent());
		mSignUtil = new SignUtil(this);

		mSignUtil.setOnSignEndListener(new OnSignEndListener() {

			@Override
			public void onEnd() {
				checkHint();
			}
		});
		mPref = Preference.getInstance();
		// 当用户更新到版本1.2.0，显示新手引导
		boolean appUpdated = mPref.isAppUpdated();
		isFirstLaunch = mPref.isFirstLaunch();
		mShowWelcomeView = isFirstLaunch || appUpdated;

		if (mShowWelcomeView) {
			// 初始化
			mPref.setGuideStep(Preference.STEP_0);
			initViewPager();
		} else {
			// checkHint();
		}
		if (mPref.isPushMsgEnabled()) {
			PushUtil.start(getApplicationContext());
		}

		mAnimRadioOut = AnimationUtils.loadAnimation(this,
				R.anim.main_radio_tans_out);
		mAnimRadioOut.setAnimationListener(mAnimListener);
		mAnimRadioIn = AnimationUtils.loadAnimation(this,
				R.anim.main_radio_tans_in);
		mAnimRadioIn.setAnimationListener(mAnimListener);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);

		mCurrentTabIndex = mPref.getLastTabIndex();// .getString(KEY_CURRENT_TAB);

		if (Constants.isFunctionEnabledInVersion(Constants.APP_VER_NAME_1_7_0)
				|| Constants
						.isFunctionEnabledInVersion(Constants.APP_VER_NAME_1_7_0_SSHOT)) {
			if (appUpdated
					&& Constants
							.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
				mCurrentTabIndex = MainTab1.INDEX;
			}
		}

		mFragmentFrame = (FrameFragmentLayout) findViewById(R.id.container);
		mFragmentFrame.setOnFrameChangeListener(new OnFrameChangeListener() {

			@Override
			public void onChange(int index, FrameFragmentItem item) {
				changeTab(index, item);
			}

		});

		List<FrameFragmentItem> itemList = new ArrayList<FrameFragmentItem>();
		for (int i = 0; i < MainTabFactory.getTabCount(); i++) {
			FrameFragmentItem item = MainTabFactory.getTab(i, this, mActionBar,
					savedInstanceState);
			itemList.add(item);
		}

		if (savedInstanceState != null) {
			mFragmentFrame.restore(itemList);
		} else {
			mFragmentFrame.add(itemList);
		}
	}

	public void onEvent(MessageEvent event) {
		if (event != null && event.isNeedUpdate()) {
			showPersonMessage();
		}
		if (event != null && event.isNetworkChangeStatus()) {
			new MessageAsyncTask(this).execute();
		}
	}

	public void onEvent(XGPushEvent event) {
		if (event == null) {
			return;
		}
		String msg = event.content;
		if (Constants.PUSH_MESSAGE_NEW_TDOU.equals(msg)
				|| Constants.PUSH_MESSAGE_NEW_EVENT.equals(msg)) {
			showPersonMessage();
		}
	}

	public void showPersonMessage() {
//		NotificationRsp Notification = mPref.getNotifacation();
//		if (Notification != null && Notification.getUnreadCount() > 0) {
//			mActionBar.setLeftMsgCountVisibility(View.VISIBLE);
//			mActionBar.setLeftMsgCount(Notification.getUnreadCount());
//			mActionBar.setLeftMsgHintVisibility(View.GONE);
//		} else {
			mActionBar.setLeftMsgCountVisibility(View.GONE);
			mActionBar.setLeftMsgCount(0);

			boolean hasNewEvent = mPref.hasNewEvent();
//			boolean hasNewTdou = mPref.hasNewTdou();
//			boolean showHint = hasNewEvent || hasNewTdou;
			boolean showHint = hasNewEvent;
			if (showHint) {
				mActionBar.setLeftMsgHintVisibility(View.VISIBLE);
			} else {

				mActionBar.setLeftMsgHintVisibility(View.GONE);
			}
//		}
	}

	public void startWebWithYYToken(String url) {
		AppWebActivity.startWebActivityWithYYToken(this, url, true);
	}

	public void handleIntent(Intent intent) {
		if (null == intent || intent.getBooleanExtra(ACTION_EXIT_APP, false)) {
			finish();
			return;
		}

		int type = intent.getIntExtra(Constants.PUSH_TYPE, -1);
		long id = intent.getLongExtra(Constants.PUSH_ID, -1);
		String url = intent.getStringExtra(Constants.PUSH_URL);

		if (type == 2) {
			// 网页
			if (url != null) {
				AppWebActivity.startWebActivityFromNotice(this, url);
			}
		} else if (type == 1) {
			// 专题
			if (id != -1) {
				ArticleListActivity.startSpecialListActivity(this, id);
			}
		} else if (type == 0) {
			// 文章
			if (id != -1) {
				ArticleDetailActivity.startArticleDetailActivityFromNotice(
						this, id);
			}
		}
	}

	/**
	 * 统计
	 * 
	 * @param tabName
	 */
	public void addchangeTabStatistics(int index) {
		int lastTabIndex = mPref.getLastTabIndex();
		MainTabStatsUtil.addchangeTabStatistics(this, lastTabIndex, index);
	}

	private void checkDailyLaunch() {
		if (!mShowWelcomeView) {
			mSignUtil.requestSignDaily();
		}
	}

	private void checkBehaviorFromLauncher() {
		if (mFromOtherActivity) {
			mFromOtherActivity = false;
			return;
		}

		checkExpireRefresh();
		checkDailyLaunch();
	}

	private void checkExpireRefresh() {

		// if (mTab1 == null || mTab2 == null) {
		// return;
		// }
		// if (mTab1.isVisible()) {
		// mTab1.checkExpire();
		// }
		// if (mTab2.isVisible()) {
		// mTab2.checkExpire();
		// }

		List<FrameFragmentItem> itemList = mFragmentFrame.getItemList();
		for (int i = 0; i < itemList.size(); i++) {
			MainTab tab = (MainTab) itemList.get(i);
			tab.checkExpire();
		}
	}

	private boolean isAnimating;
	private boolean mIsRadioVisible = true; // 初始化时为显示状态
	private AnimationListener mAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnimating = true;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == mAnimRadioIn) {
				mIsRadioVisible = true;
			} else {
				mIsRadioVisible = false;
			}
			isAnimating = false;
		}
	};

	// private long mLastDisplayTime;

	private void showMainRadio(int delay) {
		Log.d(TAG, "[showMainRadio]");

		if (!mHandler.hasMessages(MSG_SHOW_RADIO)) {
			mHandler.removeMessages(MSG_HIDE_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_RADIO, delay);
		}
	}

	private void hideMainRadio(int delay) {
		if (!mHandler.hasMessages(MSG_HIDE_RADIO)) {
			Log.d(TAG, "[hideMainRadio], delay = " + delay);
			mHandler.removeMessages(MSG_SHOW_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_RADIO, delay);
		}
	}

	private void showMainRadioNow() {
		if (isAnimating) {
			showMainRadio(10);
			return;
		}
		if (!mIsRadioVisible) {
			mAnimRadioOut.cancel();
		}
	}

	private void hideMainRadioNow() {
		if (isAnimating) {
			hideMainRadio(10);
			return;
		}
		if (mIsRadioVisible) {
			mAnimRadioIn.cancel();
		}
	}

	private int mCurrentTabIndex;

	private void changeTab(int index, FrameFragmentItem item) {
		addchangeTabStatistics(index);
		if (isFirstLaunch && !mPref.isChannelSelected()
				&& item instanceof MainTab2) {
			showWelcomeChannelView((MainTab) item);
		}
		mCurrentTabIndex = index;
		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)
				&& mCurrentTabIndex != MainTab3SportBrush.INDEX
				&& index != MainTab3SportBrush.INDEX) {
			reloadSchedData();
		}

		ArticleDetailActivity.CURRENT_BUTTON_TAB = index;
		mCurrentTabIndex = index;
		mPref.setLastTabIndex(index);
	}

	@Override
	public void onResume() {
		super.onResume();

		mFragmentFrame.show(mCurrentTabIndex);
		if (mPref.getInitRsp() == null) {
			InitModel.sendUserInitReq(this, mUserInitRspListener, null, false);
		}

		mPref.recordLaunchTime();
		checkBehaviorFromLauncher();
		checkWebviewPreload();
	}

	private void checkWebviewPreload() {
		if (mPref.getWebViewCacheState() == false) {
			View view = getLayoutInflater().inflate(
					R.layout.webview_cache_layout, null);
			final WebView webView = (WebView) view
					.findViewById(R.id.cache_webview);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					WebViewCacheUtil
							.startPreWebView(MainActivity.this, webView,
									WebViewCacheUtil.webView_url_one, mHandler);
				}
			}, WebViewCacheUtil.DELAY_MILLIS);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					WebViewCacheUtil
							.startPreWebView(MainActivity.this, webView,
									WebViewCacheUtil.webView_url_two, mHandler);
				}
			}, WebViewCacheUtil.DELAY_MILLIS);

		}
	}

	@SuppressWarnings("unused")
	private void startGameWelcomeAinamin() {
		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)
				&& mShowWelcomeView && mIsShowWelcomeGuide) {
			Animation createAnimRightToLeftIn = AnimationHelper
					.createAnimRightToLeftIn(getBaseContext(),
							new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									imageViewTwo.startAnimation(AnimationHelper
											.createAnimRightToLeftSecondLongIn(
													getBaseContext(), null));
									imageViewThree.startAnimation(AnimationHelper
											.createAnimRightToLeftLongIn(
													getBaseContext(), null));
									imageViewTwo.setVisibility(View.VISIBLE);
									imageViewThree.setVisibility(View.VISIBLE);
								}
							});
			imageViewOne.startAnimation(createAnimRightToLeftIn);
			imageViewOne.setVisibility(View.VISIBLE);

		}

	}

	public void setTitleContainerWidget(View view) {
		// if (mTab3 != null && mTab3.getFragment() instanceof SchedFragment) {
		// mTab3.setTitleContainerWidget(view);
		// }
	}

	/**
	 * 体育刷子切换到赛事tab，重新请求赛事和球队数据
	 * 
	 * @author yuelai.ye
	 */
	public void reloadSchedData() {
		for (FrameFragmentItem item : mFragmentFrame.getItemList()) {
			Fragment fragment = item.getFragment();
			if (fragment instanceof SchedFragment) {

				((SchedFragment) fragment).reloadSchedData();
			}
		}
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/*
	 * 判断是否有启动过其它的activity, 此flag用于判断是否要检查列表数据过期
	 * 现在的逻辑是，只有当activity处于列表页时，如果列表内容上次加载距当前时间 超过一定时间，则在下次onResume时自动刷新
	 */
	private boolean mFromOtherActivity; //

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		mFromOtherActivity = true;
		super.startActivityForResult(intent, requestCode, options);
	}

	private ResponseListener<UserInitRsp> mUserInitRspListener = new ResponseListener<UserInitRsp>(
			this) {
		public void onResponse(UserInitRsp rsp) {
			mPref.saveDefaultInitRsp(rsp);
		};

		public void onError(Exception e) {
		};
	};

	public static final String ACTION_EXIT_APP = "exit_app";

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressedAfterFragment() {
		if (mHandler.hasMessages(MSG_FAKE_EXIT_APP)) {
			super.onBackPressedAfterFragment();
		} else {
			Toast.makeText(this, R.string.main_exit_app, DURATION_EXIT_APP)
					.show();
			mHandler.sendEmptyMessageDelayed(MSG_FAKE_EXIT_APP,
					DURATION_EXIT_APP);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// ToastUtil.showToast("onMenuOpened");
		return false; // 返回为true则显示系统menu
	}

	public static void exitApp(Activity from) {
		Intent intent = new Intent(from, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(ACTION_EXIT_APP, true);
		from.startActivityForResult(intent, -1);
	}

	public void onEvent(SchedTabChangedEvent event) {

		MainTab3SportBrush tab = (MainTab3SportBrush) getItemByClass(MainTab3SportBrush.class);
		if (tab != null) {
			int visibility = event.getVisibility();
			tab.setRightImageViewVisibility(visibility);
		}
	}

	public void onEvent(LoginStatusEvent event) {
		MainTab5Gamenews item = getItemByClass(MainTab5Gamenews.class);
		if (item != null) {
			item.refreshAfterLoginChanged();
		}
	}

	public void onEvent(FragmentCallbackEvent event) {
		if (event == null
				|| (event.mTarget != MainActivity.this && event.mTarget != null)) {
			Log.d(TAG, "[onEvent]" + ", event = " + event.getEventType()
					+ ", activity = " + event.mTarget);
			return;
		}
		if (event.getEventType() == 1005 || event.getEventType() == 1006) {

			Log.d(TAG, "[onEvent]" + ", eventId = " + event.getEventType()
					+ ", activity = " + event.mTarget);

		}

		int eventType = event.mEventType;
		switch (eventType) {
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_DOWN");
			// showMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_UP");
			// hideMainRadio(FILT_DURATION);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD: {
			// Log.d(TAG, "[onEvent]" +
			// ", eventId = FRGMT_LIST_SCROLL_TO_HEAD");
			// showMainRadio(0);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_SCROLL_END: {
			// Log.d(TAG, "[onEvent]" + ", eventId = FRGMT_LIST_SCROLL_END");
			// hideMainRadio(DURATION_RADIO_SHOW_MIN);
			break;
		}
		case FragmentCallbackEvent.FRGMT_LIST_REFRESHING: {
			mActionBar.startLoading();
			// setIndicatorRefreshing(true);
			break;
		}

		case FragmentCallbackEvent.FRGMT_LIST_REFRESH_DONE: {
			mActionBar.stopLoading();
			// setIndicatorRefreshing(false);
			break;
		}
		case FragmentCallbackEvent.FRGMT_TAB_CHANGED: {
			// showMainRadio(0);
			break;
		}
		}
	}

	public void onEvent(MainTabEvent event) {
		MainTabStatsUtil.statistics(this, event);
	}

	private ViewPager mViewPager;
	private ImagePagerAdapter mAdapter;

	private void initViewPager() {
		ViewStub stub = (ViewStub) findViewById(R.id.welcome_pager_main);
		stub.inflate();

		mWelcome_main = findViewById(R.id.welcome_pager_main_layout);
		mViewPager = (ViewPager) findViewById(R.id.welcome_pager);
		mAdapter = new ImagePagerAdapter(this);
		mWelcome_main.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.VISIBLE);

		final List<View> resList = new ArrayList<View>();

		View layout1 = getLayoutInflater().inflate(
				R.layout.welcome_pager_layout, null);
		ImageView view1 = (ImageView) layout1
				.findViewById(R.id.welcome_pager_img);
		view1.setBackgroundResource(R.drawable.welcome_1);
		resList.add(layout1);

		View layout2 = getLayoutInflater().inflate(
				R.layout.welcome_pager_layout, null);
		ImageView view2 = (ImageView) layout2
				.findViewById(R.id.welcome_pager_img);
		view2.setBackgroundResource(R.drawable.welcome_2);
		resList.add(layout2);

		if (!Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)){
			View layout3 = getLayoutInflater().inflate(
					R.layout.welcome_pager_layout, null);
			ImageView view3 = (ImageView) layout3
					.findViewById(R.id.welcome_pager_img);
			view3.setBackgroundResource(R.drawable.welcome_3);
			resList.add(layout3);

			View layout4 = getLayoutInflater().inflate(
					R.layout.welcome_pager_layout, null);
			ImageView view4 = (ImageView) layout4
					.findViewById(R.id.welcome_pager_img);
			view4.setBackgroundResource(R.drawable.welcome_4);
			resList.add(layout4);
		}
		
		resList.add(new View(this));
		mAdapter.updateDatasource(resList);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				if (position == resList.size() - 1) {
					mViewPager.postDelayed(new Runnable() {
						@Override
						public void run() {
							mPref.finishFirstLaunch();
							mPref.finishAppUpdate();
							// checkAndShowTips();
							mSignUtil.requestSignDaily();
							if (mWelcomeChannelView != null) {
								mWelcomeChannelView.checkHint();
							} else {
								mShowWelcomeView = false;
							}

							ViewGroup parent = (ViewGroup) mWelcome_main
									.getParent();

							parent.removeView(mWelcome_main);
							// mWelcome_main.setVisibility(View.GONE);
							// mViewPager.setVisibility(View.GONE);
							mViewPager.setAdapter(null);
							mAdapter.updateDatasource(null);
							mViewPager = null;
							mAdapter = null;
							resList.clear();
						}
					}, 200);
				}
			}
		});
	}

	private WelcomeChannelView mWelcomeChannelView;

	/**
	 * 显示欢迎页选频道的列表
	 * 
	 * @param item
	 *            在该item叫起showWelcomeChannelView,点击后会刷新该item
	 */
	private void showWelcomeChannelView(final MainTab item) {

		ViewStub stub = (ViewStub) findViewById(R.id.welcome_channel_view);
		stub.inflate();

		mPref.setChannelSelected(true);
		mWelcomeChannelView = (WelcomeChannelView) findViewById(R.id.welcome_channel_view_object);

		List<WelcomeChannel> welcomeChannelList = Util.getWelcomeChannelList();
		mWelcomeChannelView.setChannelList(welcomeChannelList);
		mWelcomeChannelView.setVisibility(View.VISIBLE);
		mWelcomeChannelView.setOnSaveClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewGroup parent = (ViewGroup) mWelcomeChannelView.getParent();
				parent.removeView(mWelcomeChannelView);

				if (item != null) {
					item.refresh();
				}
				mShowWelcomeView = false;
			}
		});
	}

	/**
	 * 游戏刷子欢迎页Viewpager动画
	 */
	public void addGameWelcomeAnimation(ViewPager mViewPager, List<View> resList) {
		mWelcome_main
				.setBackgroundResource(R.color.main_welcome_guide_bg_color);
		findViewById(R.id.welcome_image_one).setVisibility(View.VISIBLE);
		findViewById(R.id.welcome_image_two).setVisibility(View.VISIBLE);
		View layoutOne = getLayoutInflater().inflate(
				R.layout.welcome_pager_gamenews_layout, null);
		imageViewOne = (ImageView) layoutOne
				.findViewById(R.id.welcome_pager_img_one);
		imageViewTwo = (ImageView) layoutOne
				.findViewById(R.id.welcome_pager_img_two);
		imageViewThree = (ImageView) layoutOne
				.findViewById(R.id.welcome_pager_img_three);
		imageViewOne
				.setImageResource(R.drawable.game_news_welcome_one_pager_one);
		imageViewTwo
				.setImageResource(R.drawable.game_news_welcome_one_pager_two);
		imageViewThree
				.setImageResource(R.drawable.game_news_welcome_one_pager_three);
		imageViewOne.setVisibility(View.GONE);
		imageViewTwo.setVisibility(View.GONE);
		imageViewThree.setVisibility(View.GONE);
		resList.add(layoutOne);
		View layoutTwo = getLayoutInflater().inflate(
				R.layout.welcome_pager_gamenews_layout, null);
		imageView1 = (ImageView) layoutTwo
				.findViewById(R.id.welcome_pager_img_one);
		ImageView imageView2 = (ImageView) layoutTwo
				.findViewById(R.id.welcome_pager_img_two);
		imageView3 = (ImageView) layoutTwo
				.findViewById(R.id.welcome_pager_img_three);
		imageView1.setImageResource(R.drawable.game_news_welcome_two_pager_one);
		imageView3.setImageResource(R.drawable.game_news_welcome_two_pager_two);
		imageView1.setVisibility(View.GONE);
		imageView2.setVisibility(View.GONE);
		imageView3.setVisibility(View.GONE);
		resList.add(layoutTwo);
		mIsShowWelcomeGuide = true;// 展示欢迎引导页
	}

	public void checkHint() {

		if (mPref.getCurrentGuideStep() == Preference.STEP_0) {

			MainTab item = (MainTab) getItemByClass(MainTab5Gamenews.class);
			Util.showMainHelpTips(MainActivity.this, item, null);
		}
	}

	private List<FrameFragmentItem> getFragmentItemList() {
		if (mFragmentFrame == null) {
			return null;
		}
		return mFragmentFrame.getItemList();
	}

	private FrameFragmentItem getFragmentItem(int index) {

		List<FrameFragmentItem> itemList = getFragmentItemList();
		if (itemList == null || index >= itemList.size() || index < 0) {
			return null;
		}

		return itemList.get(index);
	}

	@SuppressWarnings("unchecked")
	private <T> T getItemByClass(Class<T> clazz) {
		List<FrameFragmentItem> itemList = getFragmentItemList();
		if (itemList == null) {
			return null;
		}
		for (FrameFragmentItem item : itemList) {
			if (ClassUtils.isInstanceOf(item, clazz)) {
				return (T) item;
			}
		}

		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 登录完成开始发表话题
		if (requestCode == Constants.REQUEST_LOGIN_REDIRECT
				&& resultCode == Constants.REQUEST_LOGIN) {
			TagListActivity.startTagListActivity(this);
		} else if (requestCode == Constants.REQUEST_LOGIN) {
			if (Constants.RESULT_LOGIN == resultCode
					|| Constants.RESULT_LOGOUT == resultCode) {
				// MainTab5Gamenews item =
				// getItemByClass(MainTab5Gamenews.class);
				// if (item != null) {
				// item.refreshAfterLoginChanged();
				// }
			}
		} else {
			Fragment fs = getSupportFragmentManager().findFragmentByTag(
					ArticleSocialDialog.TAG_SOCIAL_DIALOG);
			if (fs != null && fs.isAdded() && fs instanceof ArticleSocialDialog) {
				((ArticleSocialDialog) fs).onActivityResult(requestCode,
						resultCode, data);
			}
		}
	}
}
