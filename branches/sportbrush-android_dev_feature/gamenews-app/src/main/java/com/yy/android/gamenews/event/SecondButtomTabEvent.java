package com.yy.android.gamenews.event;

public class SecondButtomTabEvent {
	public static final String ORDER_INFO = "into_order_info";
	public static final int _INTO_ORDER_TOP = 1;
	public static final int _INTO_MY_HOME_CENTER = 2;
	public static final int _ONCLICK_ARTICLE_TOP = 3;
	public static final int _INTO_HAND_INFO = 4;
	public static final int _INTO_SCHETABLE = 5;
	public static final int _INTO_CHANNEL_STORAGE = 6;
	public static final int _ARTICLE_INFO = 7;
	public static final String INTO_ORDER_TOP = "info_order_top";
	public static final String INTO_MY_HOME_CENTER = "into_my_home_center";
	public static final String ONCLICK_ARTICLE_TOP = "onclick_article_top";
	public static final String INTO_HAND_INFO = "into_head_info";
	public static final String INTO_SCHETABLE = "into_schetable";
	public static final String INTO_CHANNEL_STORAGE ="into_channel_storage";
	public static final String ARTICLE_INFO = "article_info";
	public static final String INTO_ORDER_TOP_NAME = "订阅";
	public static final String INTO_MY_HOME_CENTER_NAME = "个人中心";
	public static final String ONCLICK_ARTICLE_TOP_NAME = "频道";
	public static final String INTO_HAND_INFO_NAME = "头条";
	public static final String INTO_SCHETABLE_NAME = "赛事表";
	public static final String INTO_CHANNEL_STORAGE_NAME = "频道仓库";
	public static final String ARTICLE_INFO_NAME = "文章详情";

	private int type;
	private String eventId;
	private String key;
	private String value;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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
