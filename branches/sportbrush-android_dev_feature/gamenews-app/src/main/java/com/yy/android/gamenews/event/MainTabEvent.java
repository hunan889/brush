package com.yy.android.gamenews.event;

public class MainTabEvent {

	/**
	 * 公共事件 
	 */
	public static final String INTO_MY_HOME_CENTER = "into_my_home_center";  //进入个人中心
	public static final String INTO_MY_HOME_CENTER_NAME = "个人中心";
	
	public static final String ARTICLE_INFO = "article_info"; //点击某篇文章进入详情
	public static final String ARTICLE_INFO_NAME = "文章详情";
	
	public static final String INTO_HAND_INFO = "into_head_info"; //进入头条tab
	public static final String INTO_HAND_INFO_NAME = "头条";
	
	public static final String INTO_ORDER_CHANNEL = "into_order_channel";  //进入频道tab
	public static final String INTO_ORDER_CHANNEL_NAME = "频道";
	
	public static final String INTO_SCHETABLE = "into_schetable"; //进入体育刷子的赛事tab
	public static final String INTO_SCHETABLE_NAME = "赛事";
	
	public static final String INTO_GAMERACE = "into_gamerace"; //进入游戏刷子的赛事tab
	public static final String INTO_GAMERACE_NAME = "赛事";
	
	public static final String INTO_COMMUNITY = "into_community"; //进入社区tab
	public static final String INTO_COMMUNITY_NAME = "社区";
	
	public static final String INTO_GIFT = "into_gift"; //进入礼包tab
	public static final String INTO_GIFT_NAME = "礼包";
	
	
	/**
	 * 在头条tab
	 */
	public static final String TAB_HEAD_INFO = "into_head_info"; //当前在头条tab
	
	public static final String ONCLICK_GATE_HEAD_TOP = "onclick_gate_head_top";  //######在头条tab点击顶部ViewPagerHeader频道
	public static final String ONCLICK_GATE_HEAD_TOP_NAME = "门户头条";
	
	
	/**
	 * 在频道tab
	 */
	public static final String TAB_ORDER_INFO = "into_order_info";//当前在频道tab
	
	public static final String ONCLICK_ARTICLE_TOP = "onclick_article_top";//######在频道tab点击顶部ViewPagerHeader频道
	public static final String ONCLICK_ARTICLE_TOP_NAME = "频道";
	
	public static final String INTO_CHANNEL_STORAGE ="into_channel_storage"; //进入频道仓库
	public static final String INTO_CHANNEL_STORAGE_NAME = "频道仓库";
	
	/**
	 * 在体育刷子的赛事tab（仅针对体育刷子）
	 */
	public static final String TAB_SPORTRACE_INFO = "into_third_tab_info"; //当前在赛事tab（体育刷子的赛事表）
	public static final String CLICK_FILTER = "click_filter"; //在赛事广场点击筛选
	public static final String CLICK_FILTER_NAME = "在赛事点筛选";
	
	/**
	 * 在游戏刷子的赛事tab（仅针对游戏育刷子）
	 */
	public static final String TAB_GAMERACE_INFO = "in_gamerace_tab"; //当前在赛事tab（游戏刷子的赛事表）
	
	public static final String INTO_GAMERACE_SQUARE = "into_gamerace_square"; //进入赛事广场 
	public static final String INTO_GAMERACE_SQUARE_NAME = "话赛事广场 ";
	
	public static final String CLICK_RACEPORTAL = "click_race_portal"; //在赛事广场点击文章
	public static final String CLICK_RACEPORTAL_NAME = "在赛事广场点击文章";
	
	public static final String INTO_UNION_TAB = "into_union_tab"; //进入公会tab
	public static final String INTO_UNION_TAB_NAME = "进入公会tab";
	
	public static final String INTO_TOP_UNION_TAB = "into_top_union_tab"; //进入top10公会tab
	public static final String INTO_TOP_UNION_TAB_NAME = "进入top10公会tab";
	
	public static final String CLICK_TOP_UNION = "click_top_union"; //在top10公会点击union
	public static final String CLICK_TOP_UNION_NAME = "在top10公会点击union";
	
	public static final String INTO_OTHER_UNION_TAB = "into_other_union_tab"; //进入其它公会tab
	public static final String INTO_OTHER_UNION_TAB_NAME = "进入其它公会tab";
	
	public static final String CLICK_OTHER_UNION = "click_other_union"; //在其它公会点击union
	public static final String CLICK_OTHER_UNION_NAME = "在其它公会点击union";
	
	public static final String INTO_RACE_TOPIC = "into_race_topic"; //进入精彩赛事tab
	public static final String INTO_RACE_TOPIC_NAME = "进入精彩赛事tab";
	 
	public static final String CLICK_RACE_TOPIC = "click_race_topic"; //在精彩赛事tab点击赛事
	public static final String CLICK_RACE_TOPIC_NAME = "在精彩赛事tab点击赛事";
	
	/**
	 * 游戏刷子赛事相关事件
	 */
	public static final String INTO_UNION_DETAIL = "into_union_detail"; //进入公会详情	
	public static final String SUPPORT_UNION = "support_union"; //支持公会	
	
	/**
	 * 在游戏刷子社区tab
	 */
	public static final String TAB_COMMUNITY = "in_community_tab"; //当前在社区tab
	
	public static final String INTO_SELECT_TOPIC_TAG = "into_select_topic_tag"; //进入选择话题分类 
	public static final String INTO_SELECT_TOPIC_TAG_NAME = "选择话题分类";
	
	public static final String INTO_TOPIC_SQUARE = "into_topic_square"; //进入话题广场 
	public static final String INTO_TOPIC_SQUARE_NAME = "话题广场";
	 
	public static final String CLICK_TOPIC_IN_SQUARE = "click_topic_square"; //在话题广场点击话题 
	public static final String CLICK_TOPIC_IN_SQUARE_NAME = "点击话题进详情";
	
	public static final String INTO_TAG_BOARD = "into_tag_board"; //进入风云榜 
	public static final String INTO_TAG_BOARD_NAME = "风云榜";
	
	public static final String CLICK_HOT_TAG = "click_hot_tag"; //在风云榜点击tag分类 
	public static final String CLICK_TAG_NAME = "点击tag分类进入话题列表";
	
	/**
	 * 在礼包tab
	 */
	
	public static final String TAB_GIFT = "in_gift_tab";

	private String eventId;
	private String key;
	private String value;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
