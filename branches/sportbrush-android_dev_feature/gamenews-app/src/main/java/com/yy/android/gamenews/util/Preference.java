package com.yy.android.gamenews.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import com.duowan.android.base.Tea;
import com.duowan.gamenews.ActiveInfo;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetStoreAppListRsp;
import com.duowan.gamenews.GetTeamListRsp;
import com.duowan.gamenews.GetUnionListRsp;
import com.duowan.gamenews.MeRsp;
import com.duowan.gamenews.PlatType;
import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.SportRaceListRsp;
import com.duowan.gamenews.StoreAppInfo;
import com.duowan.gamenews.Team;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.WelcomeChannel;
import com.duowan.show.AllowEmptyContent;
import com.duowan.show.GetTagListRsp;
import com.duowan.show.GetTopicListRsp;
import com.duowan.show.NotificationRsp;
import com.duowan.taf.jce.JceInputStream;
import com.duowan.taf.jce.JceOutputStream;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.util.maintab.MainTab1;

public class Preference {
	// Preference
	public static final String PREF_NAME = "gamenews_pref";

	// public static final String PREF_SCHED_NAME = "sched_pref";

	private SharedPreferences mPref;
	private Tea mTea = new Tea();
	// private SharedPreferences mSchedPref;

	/**
	 * mCacheMap 把preference的数据保存到内存中
	 */
	private Map<String, Object> mCacheMap = new HashMap<String, Object>();
	private static final String LOG_TAG = Preference.class.getSimpleName();
	private static Preference mInstance = new Preference();
	private boolean isInited;

	public void init(Context context) {
		if (mPref == null) {
			mPref = context.getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE);
		}
		// if(mSchedPref == null){
		// mSchedPref = context.getSharedPreferences(PREF_SCHED_NAME,
		// Context.MODE_PRIVATE);
		// }
		isInited = true;
	}

	public boolean isInited() {
		return isInited;
	}

	public static Preference getInstance() {
		return mInstance;
	}

	public SharedPreferences getPreference() {

		return mPref;
	}

	// 是否选过频道
	private static final String KEY_CHANNEL_SELECTED = "channel_selected";

	// 是否是首次启动app
	private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
	// 是否每天首次启动app
	private static final String KEY_IS_FIRST_LAUNCH_DAILY = "is_first_launch_daily";
	private static final String KEY_LAST_LAUNCH_TIME = "last_launch_time";
	// 频道
	private static final String KEY_TOP_LIST = "top_list";
	private static final String KEY_CHANNEL_LIST = "channel_list";
	private static final String KEY_SEARCH_SUGGESTION = "channel_search_suggestion";
	private static final String KEY_LAST_GET_SUGGESTION = "last_get_suggestion";
	// 用户登录后拿到的id
	private static final String KEY_USER_INIT_LOGIN = "user_init_login";
	// 未登录时生成的id
	private static final String KEY_USER_INIT_DEFAULT = "user_init_default";
	// 是否仅在wifi下自动加载图片
	private static final String KEY_USER_ONLY_WIFI = "only_wifi";
	// 喜欢的评论
	private static final String KEY_COMMENTS_LIKE = "comments_like";
	// 赞的文章
	private static final String KEY_ARTICLES_LIKE = "articles_like";
	// 踩的文章
	private static final String KEY_ARTICLES_DISLIKE = "articles_dislike";
	// 是否仅在wifi下自动加载图片
	private static final String KEY_PUSH_MSG_ENABLED = "push_msg_enabled";

	// 用户收藏数
	private static final String KEY_MY_FAV_COUNT = "my_fav_count";

	// 引导页步骤
	private static final String KEY_GUIDE_STEP = "guide_step";
	// 活动频道
	private static final String KEY_ACTIVE_CHANNEL_LIST = "active_channel_list";

	/**
	 * @deprecated use {@link #KEY_LAST_TAB_INDEX} instead
	 */
	private static final String KEY_LAST_TAB_NAME = "last_tab_name";
	private static final String KEY_LAST_TAB_INDEX = "last_tab_index";

	// 上一次检查更新的时间
	private static final String KEY_LAST_CHECK_TIME = "last_check_time";

	// 重试的状态
	public static final String RETRY_STATE = "retry_state";

	// 测试用的url
	private static final String KEY_TEST_URL = "test_url";
	// 获取测试url的ip地址
	private static final String KEY_TEST_IP = "test_ip";
	// 获取app type
	private static final String KEY_TEST_APP_TYPE = "test_app_type";

	private static final String KEY_ME_RSP = "me_rsp";

	private static final String KEY_LOGIN_TYPE = "login_type";

	private static final String KEY_SAVED_ALARM = "alarm_race";
	private static final String KEY_SCHED_SAVED_TEAM = "sched_alarm_race"; // 在赛事表添加的提醒赛事；区分通过添加球队添加的赛事表
	private static final String KEY_FOLLOWED_TEAM = "follow_team";

	private static final String KEY_SPORT_RACE = "sport_race";
	private static final String KEY_TEAM = "team";

	private static final String KEY_HOST_IP_MAP = "bs2_host_ip_map"; // bs2 ip
																		// map

	private static final String KEY_HOST_MAP_UPDATE_TIME = "bs2_host_map_update_time";

	// 发表话题是否允许为空
	public static final String ALLOW_EMPTY_TOPIC_CONTENT = "allow_empty_topic_content";
	// 个人消息
	private static final String KEY_PERSON_MESSAGE = "preson_message";

	private static final String STORE_APP_LIST = "store_app_list";
	// 已安装的app 列表，用于与后台同步，同步后清空
	private static final String INSTALLED_APP_LIST = "installed_app_list";
	// 已下载的app 列表，用于与后台同步，同步后清空
	private static final String DOWNLOADED_APP_LIST = "downloaded_app_list";
	/**
	 * 保存的app版本，如果是升级，则该值为升级前的app版本
	 */
	private static final String KEY_VERSION_CODE = "version_code";
	private static final String KEY_NEED_SHOW_LOG = "need_show_log";

	public static final String KEY_PUSH_NEW_EVENT = "push_new_event";
	public static final String KEY_PUSH_NEW_TDOU = "push_new_tdou";

	// 礼包详情页url
	private static final String GIFT_ADDRESS_CONTENT = "gift_address_content";

	public void setHasNewTdou(boolean hasNewTdou) {
		mPref.edit().putBoolean(KEY_PUSH_NEW_TDOU, hasNewTdou).commit();
	}

	public boolean hasNewTdou() {
		return mPref.getBoolean(KEY_PUSH_NEW_TDOU, true);
	}

	public void setHasNewEvent(boolean hasNewEvent) {
		mPref.edit().putBoolean(KEY_PUSH_NEW_EVENT, hasNewEvent).commit();
	}

	public boolean hasNewEvent() {
		return mPref.getBoolean(KEY_PUSH_NEW_EVENT, true);
	}

	// 欢迎频道
	private static final String KEY_WELCOME_CHANNEL = "welcome_channel";

	public void saveGiftAddressContent(String giftUrl) {
		if (giftUrl != null) {
			mPref.edit().putString(GIFT_ADDRESS_CONTENT, giftUrl).commit();
		}
	}

	public String getGiftAddressContent() {
		return mPref.getString(GIFT_ADDRESS_CONTENT, Constants.GIFT_URL);
	}

	public void saveVersionCode(int versionCode) {
		mPref.edit().putInt(KEY_VERSION_CODE, versionCode).commit();
	}

	public int getVersionCode() {
		return mPref.getInt(KEY_VERSION_CODE, 0);
	}

	public boolean isAppUpdated() {
		int savedVersionCode = Preference.getInstance().getVersionCode();
		int currentVersionCode = Util.getVersionCode();
		return savedVersionCode < currentVersionCode;
	}

	public void finishAppUpdate() {
		saveVersionCode(Util.getVersionCode());
	}

	public void saveInitRsp(UserInitRsp rsp) {
		if (rsp == null) {
			return;
		}

		saveJceObject(KEY_USER_INIT_LOGIN, rsp);
	}

	/**
	 * 清除登录信息
	 */
	public void clearLoginInfo() {
		saveJceObject(KEY_USER_INIT_LOGIN, null);
	}

	public UserInitRsp getInitRsp() {
		UserInitRsp tempRsp = new UserInitRsp();
		UserInitRsp rsp = (UserInitRsp) getJceObject(KEY_USER_INIT_LOGIN,
				tempRsp);
		if (rsp == null) {
			rsp = (UserInitRsp) getJceObject(KEY_USER_INIT_DEFAULT, tempRsp);
		}

		return rsp;
	}

	public void setXinGeListData(String key, List<Channel> channels) {
		if (channels != null && channels.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < channels.size(); j++) {
				Channel channel = channels.get(j);
				if (channel != null) {
					sb.append(channel.getName() + "_" + channel.getId());
					sb.append(",");
				}
			}
			String value = sb.toString().substring(0,
					sb.toString().length() - 1);
			mPref.edit().putString(key, value).commit();
		}
	}

	public void setXinGeData(String key, String value) {
		mPref.edit().putString(key, value).commit();
	}

	public String getXinGeData(String key) {
		return mPref.getString(key, "");
	}

	public boolean isUserLogin() {
		UserInitRsp rsp = (UserInitRsp) getJceObject(KEY_USER_INIT_LOGIN,
				new UserInitRsp());
		return rsp != null && rsp.getAccessToken() != null;
	}

	public void saveDefaultInitRsp(UserInitRsp rsp) {

		if (rsp != null) {
			saveJceObject(KEY_USER_INIT_DEFAULT, rsp);
		}
	}

	public void saveMyFavCount(int count) {
		saveObject(KEY_MY_FAV_COUNT, count);
	}

	public int getMyFavCount() {
		Integer count = getObject(KEY_MY_FAV_COUNT);
		if (count == null) {
			return 0;
		}
		return count;
	}

	/**
	 * 设置是否仅在wifi下自动加载图片
	 */
	public void setOnlyWifi(boolean isOnlyWifi) {
		mPref.edit().putBoolean(KEY_USER_ONLY_WIFI, isOnlyWifi).commit();
		mCacheMap.put(KEY_USER_ONLY_WIFI, isOnlyWifi);
	}

	/**
	 * 是否仅在wifi下自动加载图片
	 * 
	 * @return
	 */
	public boolean isOnlyWifi() {
		if (mCacheMap.containsKey(KEY_USER_ONLY_WIFI)) {
			return (Boolean) mCacheMap.get(KEY_USER_ONLY_WIFI);
		}

		boolean isOnlyWifi = mPref.getBoolean(KEY_USER_ONLY_WIFI, false);
		mCacheMap.put(KEY_USER_ONLY_WIFI, isOnlyWifi);
		return isOnlyWifi;
	}

	public void saveActiveChannelList(List<ActiveInfo> list) {
		saveJceObject(KEY_ACTIVE_CHANNEL_LIST, list);
	}

	public List<ActiveInfo> getActiveChannelList() {
		List<ActiveInfo> list = new ArrayList<ActiveInfo>();
		list.add(new ActiveInfo());

		return getJceObject(KEY_ACTIVE_CHANNEL_LIST, list);
	}

	/**
	 * 设置频道仓库是否需要重试
	 * 
	 * @param enabled
	 */
	public void setRetryState(boolean enabled) {
		mPref.edit().putBoolean(RETRY_STATE, enabled).commit();
	}

	/**
	 * 获取频道仓库是否需要重试
	 * 
	 * @return
	 */
	public boolean getRetryState() {
		return mPref.getBoolean(RETRY_STATE, false);
	}

	/**
	 * 设置发表话题内容是否允许为空
	 * 
	 * @param enabled
	 */
	public void setAllowEmptyTopicContent(int allowEmptyTopicContent) {
		mPref.edit().putInt(ALLOW_EMPTY_TOPIC_CONTENT, allowEmptyTopicContent)
				.commit();
	}

	/**
	 * 获取发表话题内容是否允许为空
	 * 
	 * @return
	 */
	public int getAllowEmptyTopicContent() {
		return mPref.getInt(ALLOW_EMPTY_TOPIC_CONTENT,
				AllowEmptyContent._NO_ALLOW_EMPTY);
	}

	/**
	 * 设置是否推送通知
	 * 
	 * @param enabled
	 */
	public void setPushMsgEnabled(boolean enabled) {
		mPref.edit().putBoolean(KEY_PUSH_MSG_ENABLED, enabled).commit();
	}

	/**
	 * 设置webView的cache
	 * 
	 * @param enabled
	 */
	public void setWebViewCacheState(boolean enabled) {
		mPref.edit().putBoolean(WebViewCacheUtil.WEBVIEW_CACHE_STATE, enabled)
				.commit();
	}

	/**
	 * 获取webView的cache
	 * 
	 * @param enabled
	 */
	public boolean getWebViewCacheState() {
		return mPref.getBoolean(WebViewCacheUtil.WEBVIEW_CACHE_STATE, false);
	}

	public void setWelcomeChannelList(List<WelcomeChannel> list) {
		saveJceObject(KEY_WELCOME_CHANNEL, list);
	}

	public List<WelcomeChannel> getWelcomeChannelList() {

		List<WelcomeChannel> list = new ArrayList<WelcomeChannel>();
		list.add(new WelcomeChannel());
		return getJceObject(KEY_WELCOME_CHANNEL, list);
	}

	/**
	 * 设置上次选中的tab名
	 * 
	 * @return
	 */
	public int getLastTabIndex() {
		int defaultTab = MainTab1.INDEX;
		return mPref.getInt(KEY_LAST_TAB_INDEX, defaultTab);
	}

	/**
	 * 获取上次选中的tab名
	 * 
	 * @param tabIndex
	 */
	public void setLastTabIndex(int tabIndex) {

		// 把key_last_tab_name清空
		mPref.edit().putString(KEY_LAST_TAB_NAME, "")
				.putInt(KEY_LAST_TAB_INDEX, tabIndex).commit();
	}

	/**
	 * 设置上一次检查更新的时间
	 * 
	 * @param time
	 */
	public void setLastCheckTime(long time) {
		mPref.edit().putLong(KEY_LAST_CHECK_TIME, time).commit();
	}

	/**
	 * 获取上一次检查更新的时间
	 * 
	 * @return
	 */
	public long getLastCheckTime() {
		return mPref.getLong(KEY_LAST_CHECK_TIME, 0);
	}

	/**
	 * 是否推送通知
	 * 
	 * @return
	 */
	public boolean isPushMsgEnabled() {
		return mPref.getBoolean(KEY_PUSH_MSG_ENABLED, true);
	}

	public UserInitRsp getDefaultInitRsp() {
		UserInitRsp rsp = getJceObject(KEY_USER_INIT_DEFAULT, new UserInitRsp());// new
		// UserInitRsp();
		return rsp;
	}

	/**
	 * 判断是否是第一次启动，该方法只有在第一次被调用时返回true
	 * 
	 * @return true if this method is called at the first time. false otherwise
	 */
	public boolean isFirstLaunch() {
		boolean isFirstLaunch = mPref.getBoolean(KEY_IS_FIRST_LAUNCH, true);
		return isFirstLaunch;
	}

	public void finishFirstLaunch() {
		mPref.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).commit();
	}

	/**
	 * 获取头条频道
	 * 
	 * @return
	 */
	public List<Channel> getTopChannelList() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		list.add(new Channel());
		List<Channel> channelList = (List<Channel>) getJceObject(KEY_TOP_LIST,
				list);// new
						// ArrayList<Channel>();
		return channelList;
	}

	/**
	 * 保存头条频道
	 * 
	 * @return
	 */
	public void saveTopChannelList(List<Channel> channelList) {
		if (channelList == null) {
			Log.w(LOG_TAG, "[saveMyFavorChannelList] channelList is null");
			return;
		}

		Set<Channel> tempSet = new LinkedHashSet<Channel>();
		tempSet.addAll(channelList);
		channelList.clear();
		channelList.addAll(tempSet);
		saveJceObject(KEY_TOP_LIST, channelList);
	}

	public List<Channel> getMyFavorChannelList() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		list.add(new Channel());
		List<Channel> channelList = (List<Channel>) getJceObject(
				KEY_CHANNEL_LIST, list);// new
										// ArrayList<Channel>();
		return channelList;
	}

	public void saveLastGetSuggestionTime(long timeV) {
		mPref.edit().putLong(KEY_LAST_GET_SUGGESTION, timeV).commit();
	}

	public long getLastGetSuggestionTime() {
		return mPref.getLong(KEY_LAST_GET_SUGGESTION, 0);
	}

	private void saveJceObject(String key, Object object) {
		JceOutputStream os = new JceOutputStream();

		try {
			os.write(object, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String productBase64 = new String(Base64.encode(
				mTea.encrypt2(null, os.toByteArray()), Base64.DEFAULT));
		mPref.edit().putString(key, productBase64).commit();
	}

	@SuppressWarnings("unchecked")
	private <T> T getJceObject(String key, T t) {

		String productBase64 = mPref.getString(key, "");
		if (productBase64 == null || "".equals(productBase64)) {
			return null;
		}

		byte[] savedData = Base64.decode(productBase64.getBytes(),
				Base64.DEFAULT);
		byte[] data = mTea.decrypt2(null, savedData);
		if (data == null) {
			data = savedData;
		}
		JceInputStream is = new JceInputStream(data);
		T returnValue = null;
		try {
			returnValue = (T) is.read(t, 0, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	private void saveObject(String key, Object object) {
		mCacheMap.put(key, object);
		if (object == null) {
			mPref.edit().putString(key, "").commit();
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);

			String productBase64 = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			mPref.edit().putString(key, productBase64).commit();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getObject(String key) {
		if (mCacheMap.containsKey(key)) {
			return (T) mCacheMap.get(key);
		}
		String productBase64 = mPref.getString(key, "");

		byte[] data = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		T obj = null;
		try {
			ois = new ObjectInputStream(bais);
			obj = (T) ois.readObject();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		mCacheMap.put(key, obj);

		return obj;
	}

	// private Object getObject(String key) {
	// if (mCacheMap.containsKey(key)) {
	// return mCacheMap.get(key);
	// }
	// String productBase64 = mPref.getString(key, "");
	//
	// byte[] data = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);
	//
	// ByteArrayInputStream bais = new ByteArrayInputStream(data);
	// ObjectInputStream ois = null;
	// Object obj = null;
	// try {
	// ois = new ObjectInputStream(bais);
	// obj = ois.readObject();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// mCacheMap.put(key, obj);
	//
	// return obj;
	// }

	public void saveMyFavorChannelList(List<Channel> channelList) {
		if (channelList == null) {
			Log.w(LOG_TAG, "[saveMyFavorChannelList] channelList is null");
			return;
		}

		Set<Channel> tempSet = new LinkedHashSet<Channel>();
		tempSet.addAll(channelList);
		channelList.clear();
		channelList.addAll(tempSet);
		saveJceObject(KEY_CHANNEL_LIST, channelList);
	}

	public void saveMyCommentsLike(Set<String> commentList) {
		if (commentList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(KEY_COMMENTS_LIKE, commentList);
	}

	public Set<String> getMyCommentsLike() {
		Set<String> commentList = (Set<String>) getObject(KEY_COMMENTS_LIKE);
		return commentList;
	}

	public void saveMyArticlesLike(Set<Long> articleList) {
		if (articleList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(KEY_ARTICLES_LIKE, articleList);
	}

	public Set<Long> getMyArticlesLike() {
		Set<Long> articleList = (Set<Long>) getObject(KEY_ARTICLES_LIKE);
		return articleList;
	}

	public void saveMyArticlesDislike(Set<Long> articleList) {
		if (articleList == null) {
			Log.w(LOG_TAG, "[saveMyCommentsLike] commentList is null");
			return;
		}
		saveObject(KEY_ARTICLES_DISLIKE, articleList);
	}

	public Set<Long> getMyArticlesDislike() {
		Set<Long> articleList = (Set<Long>) getObject(KEY_ARTICLES_DISLIKE);
		return articleList;
	}

	public void saveSearchSuggestion(Map<String, ArrayList<String>> map) {
		if (map == null) {
			return;
		}
		saveObject(KEY_SEARCH_SUGGESTION, map);
	}

	public Map<String, ArrayList<String>> getSearchSuggestion() {
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map = getObject(KEY_SEARCH_SUGGESTION);
		return map;
	}

	public void setTestUrl(String url) {
		mPref.edit().putString(KEY_TEST_URL, url).commit();
	}

	public String getTestUrl() {
		return mPref.getString(KEY_TEST_URL, "");
	}

	public void setTestIp(String ip) {

		mPref.edit().putString(KEY_TEST_IP, ip).commit();
	}

	public String getTestIp() {
		return mPref.getString(KEY_TEST_IP, "");
	}

	public void setTestAppType(int type) {
		mPref.edit().putInt(KEY_TEST_APP_TYPE, type).commit();
	}

	public int getTestAppType() {
		return mPref.getInt(KEY_TEST_APP_TYPE, Constants.ECOMM_APP_TYPE);
	}

	public static final int STEP_0 = 0;
	public static final int STEP_1 = 1;
	public static final int STEP_2 = 2;
	public static final int STEP_3 = 3;
	public static final int STEP_DONE = 1;

	public int getCurrentGuideStep() {
		return mPref.getInt(KEY_GUIDE_STEP, STEP_0);
	}

	public void setGuideStep(int step) {
		mPref.edit().putInt(KEY_GUIDE_STEP, step).commit();
	}

	public void setMeRsp(MeRsp rsp) {
		saveJceObject(KEY_ME_RSP, rsp);
	}

	public MeRsp getMeRsp() {
		return getJceObject(KEY_ME_RSP, new MeRsp());
	}

	public void setLoginType(int type) {
		mPref.edit().putInt(KEY_LOGIN_TYPE, type).commit();
	}

	public int getLoginType() {
		return mPref.getInt(KEY_LOGIN_TYPE, PlatType._PLAT_TYPE_DEFAULT);
	}

	/**
	 * 获取赛事表数据
	 * 
	 * @return
	 */
	public SportRaceListRsp getSportRaceRsp() {
		SportRaceListRsp rsp = (SportRaceListRsp) getJceObject(KEY_SPORT_RACE,
				new SportRaceListRsp());
		return rsp;
	}

	public void saveSportRaceRsp(SportRaceListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(KEY_SPORT_RACE, rsp);
	}

	/**
	 * 获取球队数据
	 * 
	 * @return
	 */
	public GetTeamListRsp getTeamListRsp() {
		GetTeamListRsp rsp = (GetTeamListRsp) getJceObject(KEY_TEAM,
				new GetTeamListRsp());
		return rsp;
	}

	public void saveTeamListRsp(GetTeamListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(KEY_TEAM, rsp);
	}

	/**
	 * 获取需要提醒的所有赛事列表
	 */
	public List<RaceInfo> getAlarmRaceList() {

		List<RaceInfo> list = new ArrayList<RaceInfo>();
		list.add(new RaceInfo());

		List<RaceInfo> retList = getJceObject(KEY_SAVED_ALARM, list);
		if (retList == null) {
			retList = new ArrayList<RaceInfo>();
		}
		return retList;
	}

	public void saveAlarmRaceList(List<RaceInfo> list) {
		saveJceObject(KEY_SAVED_ALARM, list);
	}

	/**
	 * 获取在赛事表添加提醒的赛事列表
	 */
	public List<RaceInfo> getSchedAlarmRaceList() {

		List<RaceInfo> list = new ArrayList<RaceInfo>();
		list.add(new RaceInfo());

		List<RaceInfo> retList = getJceObject(KEY_SCHED_SAVED_TEAM, list);
		if (retList == null) {
			retList = new ArrayList<RaceInfo>();
		}
		return retList;
	}

	public void saveSchedAlarmRaceList(List<RaceInfo> list) {
		saveJceObject(KEY_SCHED_SAVED_TEAM, list);
	}

	/**
	 * 获取提醒的球队列表
	 */
	public List<Team> getFollowTeamList() {

		List<Team> list = new ArrayList<Team>();
		list.add(new Team());

		List<Team> retList = getJceObject(KEY_FOLLOWED_TEAM, list);
		if (retList == null) {
			retList = new ArrayList<Team>();
		}
		return retList;
	}

	public void saveFollowTeamList(List<Team> list) {
		saveJceObject(KEY_FOLLOWED_TEAM, list);
	}

	/**
	 * 获取公会数据
	 * 
	 * @return
	 */
	public GetUnionListRsp getUnionListRsp(String key) {
		GetUnionListRsp rsp = (GetUnionListRsp) getJceObject(key,
				new GetUnionListRsp());
		return rsp;
	}

	public void saveUnionListRsp(String key, GetUnionListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(key, rsp);
	}

	/**
	 * 获取post数据
	 * 
	 * @return
	 */
	public GetTopicListRsp getTopicListRsp(String key) {
		GetTopicListRsp rsp = (GetTopicListRsp) getJceObject(key,
				new GetTopicListRsp());
		return rsp;
	}

	public void saveTopicListRsp(String key, GetTopicListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(key, rsp);
	}

	/**
	 * 获取post分类数据
	 * 
	 * @return
	 */
	public GetTagListRsp getTagListRsp(String key) {
		GetTagListRsp rsp = (GetTagListRsp) getJceObject(key,
				new GetTagListRsp());
		return rsp;
	}

	public void saveTagListRsp(String key, GetTagListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(key, rsp);
	}

	/**
	 * 获取分发app列表
	 * 
	 * @return
	 */
	public GetStoreAppListRsp getStoreAppListRsp() {
		GetStoreAppListRsp rsp = (GetStoreAppListRsp) getJceObject(
				STORE_APP_LIST, new GetStoreAppListRsp());
		return rsp;
	}

	public void saveStoreAppListRsp(GetStoreAppListRsp rsp) {
		if (rsp == null) {
			return;
		}
		saveJceObject(STORE_APP_LIST, rsp);
	}

	public ArrayList<StoreAppInfo> getInstalledAppList() {

		ArrayList<StoreAppInfo> list = new ArrayList<StoreAppInfo>();
		list.add(new StoreAppInfo());
		list = getJceObject(INSTALLED_APP_LIST, list);
		return list;
	}

	public void saveInstalledAppList(ArrayList<StoreAppInfo> list) {
		if (list == null) {
			return;
		}
		saveJceObject(INSTALLED_APP_LIST, list);
	}

	/**
	 * 获取分发app列表
	 * 
	 * @return
	 */
	public ArrayList<StoreAppInfo> getDownloadedAppList() {

		ArrayList<StoreAppInfo> list = new ArrayList<StoreAppInfo>();
		list.add(new StoreAppInfo());
		list = getJceObject(DOWNLOADED_APP_LIST, list);
		return list;
	}

	public void saveDownloadededAppList(ArrayList<StoreAppInfo> list) {
		if (list == null) {
			return;
		}
		saveJceObject(DOWNLOADED_APP_LIST, list);
	}

	/**
	 * 获取个人消息记录
	 * 
	 * @return
	 */
	public void setNotifacation(NotificationRsp rsp) {
		saveJceObject(KEY_PERSON_MESSAGE, rsp);
	}

	public NotificationRsp getNotifacation() {
		return getJceObject(KEY_PERSON_MESSAGE, new NotificationRsp());
	}

	public void setNeedShowLog(boolean needShowLog) {
		mPref.edit().putBoolean(KEY_NEED_SHOW_LOG, needShowLog).commit();
	}

	public boolean getNeedShowLog() {
		return mPref.getBoolean(KEY_NEED_SHOW_LOG, false);
	}

	public void setChannelSelected(boolean selected) {
		mPref.edit().putBoolean(KEY_CHANNEL_SELECTED, selected).commit();
	}

	public boolean isChannelSelected() {
		return mPref.getBoolean(KEY_CHANNEL_SELECTED, false);
	}

	public boolean isFirstLaunchDaily() {

		return mPref.getBoolean(KEY_IS_FIRST_LAUNCH_DAILY, true);
	}

	public void finishFirstLaunchDaily() {
		mPref.edit().putBoolean(KEY_IS_FIRST_LAUNCH_DAILY, false).commit();
	}

	/**
	 * 记录启动时间
	 */
	public void recordLaunchTime() {
		long lastLaunchTime = mPref.getLong(KEY_LAST_LAUNCH_TIME,
				System.currentTimeMillis());

		long now = System.currentTimeMillis();

		Calendar cLast = Calendar.getInstance();
		cLast.setTimeInMillis(lastLaunchTime);

		Calendar cNow = Calendar.getInstance();
		cNow.setTimeInMillis(now);

		Editor editor = mPref.edit();
		if (cLast.get(Calendar.DATE) != cNow.get(Calendar.DATE)) {
			editor.putBoolean(KEY_IS_FIRST_LAUNCH_DAILY, true);
		}
		editor.putLong(KEY_LAST_LAUNCH_TIME, now).commit();
	}

	public Map<String, String> getIpMap() {
		return getJceObject(KEY_HOST_IP_MAP, new HashMap<String, String>());
	}

	public void setIpMap(Map<String, String> map) {
		saveJceObject(KEY_HOST_IP_MAP, map);
		mPref.edit()
				.putLong(KEY_HOST_MAP_UPDATE_TIME, System.currentTimeMillis())
				.commit();
	}

	public long getLastHostMapUpdateTime() {
		return mPref.getLong(KEY_HOST_MAP_UPDATE_TIME, 0);
	}
}
