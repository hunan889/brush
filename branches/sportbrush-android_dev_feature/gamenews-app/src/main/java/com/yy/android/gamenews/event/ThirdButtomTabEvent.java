package com.yy.android.gamenews.event;

public class ThirdButtomTabEvent {
	public static final String THIRD_TAB_INFO = "into_third_tab_info";
	public static final int _INTO_MY_HOME_CENTER = 1;
	public static final int _INTO_HAND_INFO = 2;
	public static final int _INTO_ORDER = 3;
	public static final String INTO_MY_HOME_CENTER = "into_my_home_center";
	public static final String INTO_HAND_INFO = "into_head_info";
	public static final String INTO_ORDER = "info_order";
	public static final String INTO_MY_HOME_CENTER_NAME = "个人中心";
	public static final String INTO_HAND_INFO_NAME = "头条";
	public static final String INTO_ORDER_NAME = "订阅";

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
