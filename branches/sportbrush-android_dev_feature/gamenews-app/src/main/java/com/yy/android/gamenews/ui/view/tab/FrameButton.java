package com.yy.android.gamenews.ui.view.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public abstract class FrameButton extends FrameLayout {
	public FrameButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FrameButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FrameButton(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 */
	private void init(Context context) {
		inflateView(LayoutInflater.from(getContext()));
	}

	protected abstract void inflateView(LayoutInflater inflater);

	public abstract void setTitle(CharSequence title);
}
