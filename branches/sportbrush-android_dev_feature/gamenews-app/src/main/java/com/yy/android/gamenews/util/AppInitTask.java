package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.util.LocalLog;
import com.duowan.gamenews.ActiveInfo;
import com.duowan.gamenews.AppInitReq;
import com.duowan.gamenews.AppInitRsp;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.ServiceType;
import com.duowan.gamenews.UpdateMyFavChannelListRsp;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.WelcomeChannel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.model.InitModel;
import com.yy.android.gamenews.util.UpdateManager.OnUpdateInfoListener;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class AppInitTask extends BackgroundTask<Void, Void, Boolean> {
	// private IPageCache mPageCache;
	private Preference mPref;

	private boolean mAppInitRunning;
	private boolean mUserInitRunning;
	private boolean mArticleListRunning;
	private boolean mLaunchRunning;
	private boolean mIsAsyncTaskRunning; // doInbackground最多执行5秒，之后该标志位被置为false
	private boolean mIsTaskEnd;
	private boolean mIsUpdateChannelRunning;

	private static final int TASK_APP_INIT = 1001;
	private static final int TASK_USER_INIT = 1002;
	private static final int TASK_GET_ARTICLE_LIST = 1003;
	private static final int TASK_LAUNCH = 1004;
	private static final int TASK_UPDATE_CHANNEL = 1005;

	private static final int STAY_MAX_TIME = 5000; // 最多停留5秒

	private static final int DURATION_CHECK_UPDATE = 24 * 3600 * 1000; // 24小时自动检查一次

	private FragmentActivity mActivity;

	private static final String LOG_TAG = "AppInitTask";
	private DomainIpTransformatter mIpFormatter;

	public AppInitTask(FragmentActivity activity) {
		mPref = Preference.getInstance();
		mActivity = activity;
	}

	private void checkMyFavorChannel() {
		final ArrayList<Channel> channels = (ArrayList<Channel>) mPref
				.getMyFavorChannelList();
		checkRemoveChannel(channels, "世界杯");

		if (mPref.isFirstLaunch()) { // 如果本地列表为空，则从服务器上读取，否则使用本地的同步服务器
			loadDefaultChannel();
		} else {
			sendAppInit();
			syncMyFavorChannelList(channels);
		}
	}

	private void syncMyFavorChannelList(final ArrayList<Channel> channels) {
		setTaskStatus(TASK_UPDATE_CHANNEL, true);
		saveToXinGe(channels);
		ChannelModel.updateMyFavChannelList(mActivity,
				new ResponseListener<UpdateMyFavChannelListRsp>(mActivity) {

					@Override
					public void onResponse(UpdateMyFavChannelListRsp rsp) {
						setTaskStatus(TASK_UPDATE_CHANNEL, false);
						if (rsp != null) {
							saveChannel(rsp.getChannelList());
						} else {
							saveChannel(channels);
						}
						onTaskFinish();
					}

					@Override
					public void onError(Exception e) {
						setTaskStatus(TASK_UPDATE_CHANNEL, false);
						saveChannel(channels);
						onTaskFinish();
						super.onError(e);
					}
				}, channels, false);
	}

	private void saveChannel(List<Channel> channels) {
		mPref.saveMyFavorChannelList(channels);
	}

	private void saveToXinGe(List<Channel> channels) {
		if (Util.isNetworkConnected()) {
			PushUtil.addChannelTag(mActivity, channels);// 每次进入同步服务器频道列表，同时和信鸽同步推送的Tag
		} else {
			mPref.setXinGeListData(PushUtil.ADD_XINGE_PUSH_DATA, channels);
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		long time = System.currentTimeMillis();
		mIsAsyncTaskRunning = true;
		Log.d(LOG_TAG, "[doInBackground] executed");

		LocalLog.FLAG_WRITE_TO_FILE = mPref.getNeedShowLog();
		/**
		 * 判断是否是第一次启动app 如果是第一次启动，加载： 1. 欢迎页面 2. 推荐频道 3. 首页内容
		 * 
		 * 如果不是第一次启动，那么每次启动时检查： 首页内容是否有更新，如果有更新，下载并更新缓存
		 */

		if (!sendUserInit()) {
			checkMyFavorChannel();
		}
		checkUpdate();
		prepareLocalData();

		long delay = System.currentTimeMillis() - time; // 计算以上过程使用了多长时间

		// 计算还需停留多久
		if (delay < STAY_MAX_TIME) {
			delay = STAY_MAX_TIME - delay;
		}

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void prepareLocalData() {
		AlarmUtil.ensureAlarms(mActivity);
		// mIpFormatter = DomainIpTransformatter.getInstance();
		// mIpFormatter.prepareDefault();
	}

	private void checkAndDeleteChannel(List<Channel> channels, String key) {
		if (key == null || channels == null) {
			return;
		}
		for (Channel channel : channels) {
			if (key.equals(channel.getName())) {
				channels.remove(channel);
				mPref.saveMyFavorChannelList(channels);
				PushUtil.deleteChannelTag(mActivity, channel);
				break;
			}
		}
	}

	@Override
	protected void onPostExecute(Boolean startNow) {
		Log.d(LOG_TAG, "[onPostExecute] executed");
		mIsAsyncTaskRunning = false;
		onTaskFinish();
		super.onPostExecute(startNow);
	}

	private synchronized boolean isTaskEnd() {

		Log.d(LOG_TAG, "mAppInitRunning = " + mAppInitRunning
				+ "\n mUserInitRunning = " + mUserInitRunning
				+ "\n mArticleListRunning = " + mArticleListRunning
				// + "\n mIsAsyncTaskRunning = " + mIsAsyncTaskRunning
				+ "\n mIsUpdateChannelRunning = " + mIsUpdateChannelRunning
				+ "\n mIsAppExit = " + mIsTaskEnd);
		return !(mAppInitRunning || mUserInitRunning || mArticleListRunning || mIsUpdateChannelRunning);
	}

	private synchronized void setTaskStatus(int taskId, boolean status) {

		Log.d(LOG_TAG, "[setTaskStatus]");
		switch (taskId) {
		case TASK_APP_INIT: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mAppInitRunning = status;
			break;
		}
		case TASK_GET_ARTICLE_LIST: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mArticleListRunning = status;
			break;
		}
		case TASK_LAUNCH: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mLaunchRunning = status;
			break;
		}
		case TASK_USER_INIT: {
			Log.d(LOG_TAG, "[setTaskStatus], taskId = TASK_APP_INIT, status = "
					+ status);
			mUserInitRunning = status;
			break;
		}
		case TASK_UPDATE_CHANNEL: {
			Log.d(LOG_TAG,
					"[setTaskStatus], taskId = TASK_UPDATE_CHANNEL, status = "
							+ status);
			mIsUpdateChannelRunning = status;
			break;

		}
		}
	}

	private boolean sendUserInit() {

		if (mPref.getInitRsp() == null) {
			setTaskStatus(TASK_USER_INIT, true);
			InitModel.sendUserInitReq(mActivity, mUserInitRspListener, null,
					false);
			return true;
		}
		return false;
	}

	private void loadDefaultChannel() {
		ArrayList<String> channelList = null;
		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
			channelList = Util.getInitChannelList();
		}
		AppInitReq req = new AppInitReq();
		req.setServiceType(ServiceType._ST_GET_RECOMMEND_LIST
				| ServiceType._ST_GET_SUGGEST_LIST
				| ServiceType._ST_GET_ACTIVE_LIST
				| ServiceType._ST_GET_WELCOME_CHANNEL_lIST
				| ServiceType._ST_GET_TOP_lIST);

		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
			req.setGameName(channelList);
		}

		setTaskStatus(TASK_APP_INIT, true);
		InitModel.sendAppInitReq(mActivity, req, mAppInitRspListener);
	}

	private void sendAppInit() {
		AppInitReq req = new AppInitReq();

		int flag = ServiceType._ST_GET_SUGGEST_LIST
				| ServiceType._ST_GET_ACTIVE_LIST
				| ServiceType._ST_GET_TOP_lIST;

		/*
		 * 如果没有选欢迎频道，则拉取一次欢迎频道
		 */
		if (!mPref.isChannelSelected()) {
			flag |= ServiceType._ST_GET_WELCOME_CHANNEL_lIST;
		}
		req.setServiceType(flag);

		setTaskStatus(TASK_APP_INIT, true);
		InitModel.sendAppInitReq(mActivity, req, mAppInitRspListener);
	}

	private void checkUpdate() {
		long lastCheckDate = mPref.getLastCheckTime();
		final long current = System.currentTimeMillis();
		if (current - lastCheckDate < DURATION_CHECK_UPDATE) {
			return;
		}
		setTaskStatus(TASK_LAUNCH, true);
		UpdateManager manager = new UpdateManager(mActivity);

		manager.setOnUpdateInfoListener(new OnUpdateInfoListener() {

			@Override
			public void onClick(int button, boolean isForceUpdate) {
				setTaskStatus(TASK_LAUNCH, false);
				if (isForceUpdate) {
					mActivity.finish();
				} else {
					onTaskFinish();
				}
			}

			@Override
			public void onCheckFinish(boolean needUpdate, boolean isForceUpdate) {

				if (!isForceUpdate) {
					mPref.setLastCheckTime(current);
				}
				if (!needUpdate) {
					setTaskStatus(TASK_LAUNCH, false);
					onTaskFinish();
				}
			}
		});
		manager.doLaunch();
	}

	// private void loadMyFavorList() {
	//
	// Map<Integer, String> attachInfo = null;
	//
	// setTaskStatus(TASK_GET_ARTICLE_LIST, true);
	// ArticleModel.getArticleList(
	// mArticleListRspListener, // Listener
	// RefrshType._REFRESH_TYPE_REFRESH, Constants.RECOMMD_ID,
	// attachInfo, false);
	// }

	private ResponseListener<UserInitRsp> mUserInitRspListener = new ResponseListener<UserInitRsp>(
			mActivity) {
		public void onResponse(UserInitRsp rsp) {
			setTaskStatus(TASK_USER_INIT, false);
			mPref.saveDefaultInitRsp(rsp);
			checkMyFavorChannel();
			onTaskFinish();
		};

		public void onError(Exception e) {
			setTaskStatus(TASK_USER_INIT, false);
			checkMyFavorChannel();
			onTaskFinish();
		};
	};

	private ResponseListener<AppInitRsp> mAppInitRspListener = new ResponseListener<AppInitRsp>(
			mActivity) {

		@Override
		public void onResponse(AppInitRsp rsp) {
			if (rsp != null) {

				/*
				 * 欢迎频道
				 */
				List<WelcomeChannel> welcomeChannelList = rsp.getChannelList();
				mPref.setWelcomeChannelList(welcomeChannelList);

				Map<Integer, String> options = rsp.getOptions();
				if (options != null && !options.isEmpty()) {
					mPref.saveGiftAddressContent(options.get(0));
				}

				ArrayList<Channel> topList = rsp.getTopList();
				if (topList != null) {
					mPref.saveTopChannelList(topList);
				}

				ArrayList<Channel> channelList = rsp.getRecommendList();
				checkRemoveChannel(channelList, "世界杯");
				if (channelList != null && channelList.size() != 0) {
					saveChannel(channelList);
					saveToXinGe(channelList);
				}

				List<ActiveInfo> activeChannelList = rsp.getNewActiveList();
				if (activeChannelList != null) {
					mPref.saveActiveChannelList(activeChannelList);
				}

			}

			setTaskStatus(TASK_APP_INIT, false);
			onTaskFinish();
		}

		@Override
		public void onError(Exception e) {

			setTaskStatus(TASK_APP_INIT, false);
			onTaskFinish();
			super.onError(e);
		}
	};

	/**
	 * 如果任务结束，或者停留超过{@link #STAY_MAX_TIME} 秒，则任务结束
	 */
	private void onTaskFinish() {
		Log.d(LOG_TAG, "[startMainApp] executed");

		if ((isTaskEnd() || !mIsAsyncTaskRunning) && !mIsTaskEnd
				&& !mLaunchRunning) {
			if (mOnAppInitTaskListener != null) {
				mOnAppInitTaskListener.onTaskFinished();
			}
			mIsTaskEnd = true;
		}
	}

	public void endTask() {
		mIsTaskEnd = true;
	}

	private OnAppInitTaskListener mOnAppInitTaskListener;

	public void setOnAppInitTaskListener(OnAppInitTaskListener listener) {
		mOnAppInitTaskListener = listener;
	}

	public interface OnAppInitTaskListener {
		public void onTaskFinished();
	}

	private void checkRemoveChannel(List<Channel> channelList,
			String channelName) {
		/**
		 * 体育刷子1.2.0版本中，当用户更新时，删除世界杯频道
		 */
		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
			if (Constants
					.isFunctionEnabledInVersion(Constants.APP_VER_NAME_1_2_0)
					|| Constants
							.isFunctionEnabledInVersion(Constants.APP_VER_NAME_1_2_0_SSHOT)) {
				checkAndDeleteChannel(channelList, channelName);
			}
		}
	}
}