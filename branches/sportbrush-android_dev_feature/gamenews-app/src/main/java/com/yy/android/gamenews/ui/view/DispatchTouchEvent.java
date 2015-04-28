package com.yy.android.gamenews.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.plugin.schetable.SchedFragment;
import com.yy.android.gamenews.ui.MyHomeActivity;
import com.yy.android.gamenews.ui.NewsFragment;
import com.yy.android.gamenews.util.MainTabStatsUtil;

public class DispatchTouchEvent {

	private Context mContext;
	private Fragment mNewsFragment;
	private View mTitleContainer;
	private int[] mAttribute = new int[2];
	private boolean intercept = false;
	private boolean mIsSliding = false;
	// for slip out
	// 手指向右滑动时的最小速度
	private static final int YDISTANCE_MAX = 50;
	// 手指向右滑动时的最小距离
	private int XDISTANCE_MIN = 0;
	// 记录手指按下时的横坐标。
	private float xDown;
	private float yDown;
	// 记录手指移动时的横坐标。
	private float xMove;
	private float yMove;

	public DispatchTouchEvent(Context context, int[] attribute) {
		this.mContext = context;
		this.mAttribute = attribute;
	}

	public boolean dispatchTouchEvent(MotionEvent ev, final String fromTab) {
		// Log.d(TAG, "intercept = " + intercept);
		if (mNewsFragment != null) {
			if (mNewsFragment.getView() == null
					|| (!mNewsFragment.getView().isShown())) {
				return true;
			}
			if (mNewsFragment instanceof NewsFragment) {
				int currentItem = ((NewsFragment) mNewsFragment)
						.getCurrentItem();
				if (currentItem != 0) {
					return true;
				}
			}
			if (mNewsFragment instanceof SchedFragment) {
				int currentItem = ((SchedFragment) mNewsFragment)
						.getCurrentItem();
				if (currentItem != 0) {
					return true;
				}
			}
		}
		if (intercept) {
			return true;
		}
		if (XDISTANCE_MIN == 0) {
			if (mAttribute.length > 0 && mAttribute[0] != 0) {
				XDISTANCE_MIN = mAttribute[0] / 6;
			} else {
				XDISTANCE_MIN = 40;
			}
			if (XDISTANCE_MIN == 0) {
				return false;
			}
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			if (mTitleContainer != null) {
				if (mIsSliding) {
					return true;
				}
			}
			xMove = ev.getRawX();
			yMove = ev.getRawY();
			// Log.d(TAG, "yMove = " + yMove + "   xMove = " + xMove);
			// 活动的距离
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) Math.abs(yDown - yMove);
			// 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
			if (distanceX > XDISTANCE_MIN && distanceY < YDISTANCE_MAX) {
				infoToHomeActivity(fromTab);
				intercept = true;

				return false;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			xDown = ev.getRawX();
			yDown = ev.getRawY();
			if (mTitleContainer != null) {
				int height = mTitleContainer.getHeight();
				int y = getLocations(mTitleContainer);
				if (yDown > y && yDown < y + height) {
					mIsSliding = true;
				} else {
					mIsSliding = false;
				}
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* Release the drag */
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		}

		return true;
	}

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	public void setNewsFragment(Fragment newsFragment) {
		this.mNewsFragment = newsFragment;
	}

	public void setTitleContainerWidget(View TitleContainer) {
		this.mTitleContainer = TitleContainer;
	}

	private void infoToHomeActivity(String fromTab) {
		MyHomeActivity.startMyHomeActivityForResult((Activity) mContext);

		MainTabEvent event = new MainTabEvent();
		event.setEventId(fromTab);
		event.setKey(MainTabEvent.INTO_MY_HOME_CENTER);
		event.setValue(MainTabEvent.INTO_MY_HOME_CENTER_NAME);
		MainTabStatsUtil.statistics(mContext, event);
	}

	/*
	 * 单击按钮事件
	 */
	private int getLocations(View view) {
		if (view == null) {
			return 0;
		}
		// 数组长度必须为2
		int[] locations = new int[2];
		view.getLocationOnScreen(locations);
		// int x = locations[0];//获取组件当前位置的横坐标
		int y = locations[1];// 获取组件当前位置的纵坐标
		return y;
	}
}
