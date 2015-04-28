package com.yy.android.gamenews.event;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class FragmentCallbackEvent {
	public static final int FRGMT_LIST_SCROLL_TO_HEAD = 1001; // list view滑动到头部了
	public static final int FRGMT_LIST_SCROLL_DOWN = 1002; // listview向下滑
	public static final int FRGMT_LIST_SCROLL_UP = 1003; // listview 向上滑
	public static final int FRGMT_LIST_SCROLL_END = 1004;

	public static final int FRGMT_LIST_REFRESH_DONE = 1005;
	public static final int FRGMT_LIST_REFRESHING = 1006;

	public static final int FRGMT_TAB_CHANGED = 1007; // 新闻tab切换

	public int mEventType;
	public Fragment mFragment;
	public Object mParams;
	public Activity mTarget;

	public Object getParams() {
		return mParams;
	}

	public void setParams(Object params) {
		mParams = params;
	}

	public int getEventType() {
		return mEventType;
	}

	public void setEventType(int eventType) {
		mEventType = eventType;
	}

	public Fragment getFragment() {
		return mFragment;
	}

	public void setFragment(Fragment fragment) {
		mFragment = fragment;
	}

	public void setTarget(Activity target) {
		mTarget = target;
	}

	public Activity getTarget() {
		return mTarget;
	}
}
