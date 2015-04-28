package com.yy.android.gamenews;

import com.duowan.Comm.ECommAppType;

public final class Constants {

	public static long UID = 0;// 用户id
	// APP KEY
	public static final String WEIBO_APP_KEY = "2872789820";
	public static final String QQ_APP_ID = "1101502802";
	public static final String QQ_APP_KEY = "FE4EmndXTC0H2Wgs";
	public static final String WEIXIN_APP_KEY = "wxcd4e8e4f32e4c1e2";

	public static final String YY_APP_ID = "5173";
	public static final String YY_APP_KEY = "01985E49FE47A6A41F2B28B305E6780C";
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
	public static final String TITLE_RECMD = "推荐";
	public static final String FILE_NAME_GAME_LIST = "gamelist.txt";

	public static final int MY_FAVOR_CHANNEL_ID = 99; // 我的最爱
	public static final int RECOMMD_ID = 100; // 资讯墙

	// 个人中心

	public final static String MY_EVENT_URL = "http://shua.duowan.com/sport/index.php?m=active";
	public final static String UDB_FORGET_PASSWORD_URL = "https://udb.yy.com/account/forgetPassword2.do";

	// Cache Key
	public static final String CACHE_KEY_MYFAVOR_LIST = "my_favor_list";
	public static final String CACHE_KEY_HOME_LIST = "home_list";
	public static final String CACHE_KEY_SPECIAL_LIST = "special_list";
	public static final String CACHE_KEY_VIEWED_ARTICLE_LIST = "viewed_article_list";
	public static final int CACHE_SIZE_VIEWED_ARTI_LIST = 500;

	public static final int CACH_SIZE_HOME_ARTI_LIST = 60;

	public static final int CACHE_DURATION_FOREVER = Integer.MAX_VALUE;
	public static final int CACHE_MYFAVOR_DURATION = Integer.MAX_VALUE; // 保存12小时
	public static final int CACHE_DURATION_HOMELIST = 60 * 60; // cache一小时过期

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
	public static final String ARTICLE_URL_FORMATTER = "http://shua.duowan.com/index.php?m=share&id=%d&app_name=sport";

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

	// app环境
	public static final String APP_DEV_IP = "http://shua.duowan.com/ip.php?type=dev";
	public static final String APP_PRE_IP = "http://shua.duowan.com/ip.php?type=pre";
	public static final String APP_IDC_IP = "http://shua.duowan.com/ip.php?type=idc";

	// 访问服务器需要用到的servant name
	public static final String APP_SERVANT_NAME = "sportnews";
	
	// 支持的app type
	public static final int APPTYPE_GAMENEWS = ECommAppType._Comm_APP_GAMENEWS;
	public static final int APPTYPE_SPORTBRUSH = ECommAppType._Comm_APP_SPORTBRUSH;

	// 更新时用到的app type
	public static final int ECOMM_APP_TYPE = APPTYPE_SPORTBRUSH;

	public static boolean isFunctionEnabled(int enabledAppType) {
		return ECOMM_APP_TYPE == enabledAppType;
	}
}