package com.yy.android.gamenews;

import com.duowan.Comm.ECommAppType;
import com.yy.android.gamenews.util.Util;

public final class Constants {

	public static long UID = 0;// 用户id
	// APP 版本
	public static final String APP_VER_NAME_1_2_0 = "1.2.0";
	public static final String APP_VER_NAME_1_2_0_SSHOT = "1.2.0-SNAPSHOT";
	public static final String APP_VER_NAME_1_7_0 = "1.7.0";
	public static final String APP_VER_NAME_1_7_0_SSHOT = "1.7.0-SNAPSHOT";

	// APP KEY
	public static final String WEIBO_APP_KEY = "2872789820";
	public static final String QQ_APP_ID = "1101502802";
	public static final String QQ_APP_KEY = "FE4EmndXTC0H2Wgs";
	public static final String WEIXIN_APP_KEY = "wxcd4e8e4f32e4c1e2";

	public static final String YY_APP_ID = "5173";

	public static final String URL_DUOWAN = "shua.duowan.com";
	// Cache +
	// 数据库版本
	public static final String SD_DATABASE_VERSION_NAME = "sdcard_database_version";
	public static final int SD_DATABASE_VERSION = 1;

	public static final int INNER_DATABASE_VERSION = 1;
	public static final String INNER_DATABASE_NAME = "gamenews.db";

	public static final String SD_DATABASE_NAME = "gamenews.db";
	public static final String TMPDIRNAME = "gamenews";
	// cache -
	/** Default maximum velley disk usage in bytes. */
	public static final int DEFAULT_DISK_USAGE_BYTES = 15 * 1024 * 1024;

	// 首页
	public static final String TITLE_MY_FAVOR = "我的最爱";
	public static final String TITLE_RECMD = "刷子";
	public static final String TITLE_LEADERBOARD = "排行榜";
	public static final String FILE_NAME_GAME_LIST = "gamelist.txt";
	public static final String FILE_NAME_WELCOME_CHANNEL = "welcome_channel.txt";

	public static final int MY_FAVOR_CHANNEL_ID = 99; // 我的最爱
	public static final int RECOMMD_ID = 100; // 资讯墙

	// 个人中心

	public final static String MY_EVENT_URL = "http://shua.duowan.com/sport/index.php?m=active";
	public final static String UDB_FORGET_PASSWORD_URL = "https://aq.yy.com/p/pwd/fgt/m/index.do";
	public final static String UDB_REGIST_URL = "https://zc.yy.com/reg/wap/reg4Wap.do?appid="
			+ YY_APP_ID + "&mode=wap&action=2&busiurl=https://aq.yy.com";

	// Cache Key
	public static final String CACHE_KEY_MYFAVOR_LIST = "my_favor_list";
	public static final String CACHE_KEY_HOME_LIST = "home_list";
	public static final String CACHE_KEY_SPECIAL_LIST = "special_list";
	public static final String CACHE_KEY_UNION_LIST = "union_list";
	public static final String CACHE_KEY_PERSONAL_RACETOPIC_LIST = "personal_race_topic_list";
	public static final String CACHE_KEY_VIEWED_ARTICLE_LIST = "viewed_article_list";
	public static final String CACHE_KEY_BRAND_CHOOSE_LIST = "brand_choose_list";
	public static final String CACHE_KEY_HOT_CART_LIST = "hot_cart_list";
	public static final String CACHE_KEY_BRAND_CHOOSE_SECOND_LIST = "brand_choose_second_list";
	public static final int CACHE_SIZE_VIEWED_ARTI_LIST = 500;
	public static final String CACHE_KEY_ARTICLE_LIST_DEFAULT = "article_list_default";
	public static final String CACHE_KEY_LAST_REFRSH_TIME = "last_refresh_time";
	public static final String CACHE_KEY_LAST_REFRSH_TIME_ARTICLE = "last_refresh_article";
	public static final String CACHE_KEY_LAST_REFRSH_TIME_SPECIAL = "last_refresh_special";
	public static final String CACHE_KEY_LAST_REFRSH_TIME_MYFAVOR = "last_refresh_myfavor";
	public static final String CACHE_KEY_SCHETABLE = "schedule_table";
	public static final String CACHE_KEY_TEAM_LIST = "team_list";
	public static final String WONDERFUL_RACE_LIST = "wondful_race_list";
	public static final String UNION_RACE_TOPIC = "union_race_topic";
	public static final String CACHE_KEY_LIKE_TOPIC_LIST = "like_topic_list";
	public static final String CACHE_PERSON_MESSAGE_LIST = "cache_person_message_list";
	/**
	 * 文章列表缓存的大小
	 */
	public static final int CACH_SIZE_HOME_HEAD_LIST = 30;
	public static final int CACH_SIZE_HOME_HOT_LIST = 20;
	public static final int CACH_SIZE_HOME_ARTI_LIST = 30;

	public static final int CACHE_DURATION_FOREVER = Integer.MAX_VALUE;
	public static final int CACHE_MYFAVOR_DURATION = Integer.MAX_VALUE; // 保存12小时

	/**
	 * 一小时
	 */
	public static final int CACHE_DURATION_HOMELIST = 60 * 60;

	// 频道仓库
	public static final int SUBSCRIBE_MOST_LIMIT = 20;
	public static final String EXTRA_GRID_FG_TYPE = "EXTRA_GRID_FG_TYPE";
	public static final String EXTRA_GRID_FG_TYPE_SEARCH = "EXTRA_GRID_FG_TYPE_SEARCH";
	public static final String EXTRA_GRID_FG_TYPE_MORE = "EXTRA_GRID_FG_TYPE_MORE";
	public static final String EXTRA_GRID_FG_KEY_WORD = "EXTRA_GRID_FG_KEY_WORD";
	public static final String EXTRA_GRID_FG_CHANNELS = "EXTRA_GRID_FG_CHANNELS";
	public static final String EXTRA_SEARCH_FG_COLUMN = "EXTRA_SEARCH_FG_COLUMN";

	public static final String EXTRA_HAS_MORE = "EXTRA_HAS_MORE";
	public static final String EXTRA_ATTACHINFO = "EXTRA_ATTACHINFO";
	public static final String EXTRA_COLUMN_ID = "EXTRA_COLUMN_ID";
	public static final String EXTRA_COLUMN_NAME = "EXTRA_COLUMN_NAME";

	// 详情
	public static final String USER_AGENT_PREFIX = "android_jjww_";
	public static final String ARTICLE_URL_FORMATTER = "http://shua.duowan.com/sport/index.php?m=share&id=%d";

	// 个人中心
	public static final String DOWNLOAD_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.yy.android.sportbrush";

	// 礼包
	public static final String GIFT_URL = "http://mtq.yy.com/utl/shuazilogin?url=http://mtq.yy.com&from=shuazi&token=";
	// 通用
	public static final int LIST_DEFAULT_LOADING_COUNT = 10;

	// push
	public static final String PUSH_TYPE = "type";
	public static final String PUSH_ID = "id";
	public static final String PUSH_URL = "url";
	public static final String PUSH_DOMAIN = "shua.duowan.com"; // 详情页web更新
	public static final String MANIFEST_URL = "http://shua.duowan.com/static/client/version.json";
	public static final String KEY_UPDATE_GLOBAL = "update_global";
	public static final String KEY_COPY_DETAIL_WEB = "copy_detail_web";
	public static final String NEW_VERSION_READY = "new_version_ready";
	public static final String CURR_DIR = "curr_dir";
	public static final String MANIFEST_FILE = "version.json";
	public static final String NEWS_IMAGE_LOADING = "image_bg_loading.png";
	public static final String NEWS_IMAGE_FAIL = "image_bg_failed.png";
	public static final String SPORTS_HTML = "sportsdetail.html";
	public static final String NEWS_HTML = "newsdetail.html";
	public static final String NEWS_JS = "js";
	public static final String NEWS_CSS = "css";
	// 推送-小红点消息提示
	public static final String PUSH_MESSAGE_NEW_TDOU = "new_tdou";
	public static final String PUSH_MESSAGE_NEW_EVENT = "new_event";
	// 网络状态
	public static final String CHECK_NETWORK_STATE = "check_network_state";

	// app环境
	public static final String APP_DEV_IP = "http://shua.duowan.com/ip.php?type=dev";
	public static final String APP_PRE_IP = "http://shua.duowan.com/ip.php?type=pre";
	public static final String APP_IDC_IP = "http://shua.duowan.com/ip.php?type=idc";

	// 访问服务器需要用到的servant name
	public static String APP_SERVANT_NAME = "gamenews";

	// 更新时用到的app type
	public static int ECOMM_APP_TYPE = ECommAppType._Comm_APP_GAMENEWS;

	public static boolean isFunctionEnabled(int enabledAppType) {
		return ECOMM_APP_TYPE == enabledAppType;
	}

	public static boolean isFunctionEnabledInVersion(String versionName) {
		if (versionName == null) {
			return false;
		}
		return versionName.equals(Util.getVersionName());
	}

	public static final String EXTRA_USER_INIT_RSP = "user_init_rsp";
	public static final int REQUEST_LOGIN = 1001;
	public static final int REQUEST_LOGIN_REDIRECT = 1002; // 登录完成跳转
	public static final int RESULT_LOGOUT = 1003;
	public static final int RESULT_LOGIN = 1004;

	// bs2 server info
	public static final String BS2_ACCESS_KEY = "ak_uzk";
	public static final String BS2_ACCESS_SECRET = "c07e90035de645c8c90f171272960724c87e0b88";
	public static final String BS2_BUCKET = "brushimg";

	public static final String BS2_IMG_HOST = "image.yy.com";

	// 秀场share地址
	public static final String SHOW_TOPIC_DETAIL_FORMATTER = "http://shua.duowan.com/show/index.php?m=share&id=%d";
	public static final long CACHE_DURATION_BS2_IP_MAP = 24 * 3600 * 1000;

}