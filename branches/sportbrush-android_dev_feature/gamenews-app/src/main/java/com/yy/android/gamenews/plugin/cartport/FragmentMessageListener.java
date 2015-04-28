package com.yy.android.gamenews.plugin.cartport;

public interface FragmentMessageListener {

	public static final int MSG_UPDATE_CART_PARAMS = 1001;
	public static final int MSG_SHOW_CART_PARAMS = 1002;
	public static final int MSG_SHOW_CART_COLUMN_ENTER = 1003;
	public static final int MSG_SHOW_CART_COLUMN_EXIT = 1004;
	public void onMessage(int msg, Object value);
}
