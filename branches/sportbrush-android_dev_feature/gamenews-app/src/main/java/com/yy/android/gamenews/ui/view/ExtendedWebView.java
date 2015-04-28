package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created with IntelliJ IDEA. User: sash0k Date: 07.06.13 Time: 11:15 Для
 * возможности вертикального скролла на Android 2.x see
 * http://stackoverflow.com/a/9925980
 */
public class ExtendedWebView extends WebView {

	public ExtendedWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ExtendedWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ExtendedWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public int getScrollRange() {
		return computeVerticalScrollRange();
	}

	public boolean canScrollVerticallyex(int direction) {
		final int offset = computeVerticalScrollOffset();
		final int range = computeVerticalScrollRange()
				- computeVerticalScrollExtent();
		if (range == 0)
			return false;
		if (direction < 0) {
			return offset > 0;
		} else {
			return offset < range - 1;
		}
	}

}
