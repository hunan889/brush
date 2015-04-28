package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CustomViewPager extends ViewPager {

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean isIntercept = false;
		if (isChildIntercept(ev)) {
			return false;
		}
		try {
			isIntercept = super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isIntercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean ret = false;
		try {
			ret = super.onTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private boolean isChildIntercept(MotionEvent ev) {
		View view = getAdapter().getPrimaryItem();

		return isIntercept(ev, view);
	}

	@Override
	public CustomPagerAdapter getAdapter() {
		return (CustomPagerAdapter) super.getAdapter();
	}

	private boolean isIntercept(MotionEvent ev, View view) {
		boolean isIntercept = false;

		if (view instanceof ChildTouchIntercepter) {
			ChildTouchIntercepter intercept = (ChildTouchIntercepter) view;
			if (intercept.isIntercept(ev)) {
				return true;
			}
		}
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				View child = group.getChildAt(i);
				isIntercept = isIntercept || isIntercept(ev, child);
				if (isIntercept) {
					break;
				}
			}
		}

		return isIntercept;
	}

}
