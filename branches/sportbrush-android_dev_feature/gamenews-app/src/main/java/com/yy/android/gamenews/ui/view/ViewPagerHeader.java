package com.yy.android.gamenews.ui.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public abstract class ViewPagerHeader extends FrameLayout {

	public ViewPagerHeader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ViewPagerHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ViewPagerHeader(Context context) {
		super(context);
		init(context);
	}

	protected void init(Context context) {
		getViewTreeObserver()
				.addOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}

	public abstract void check(int pos);

	private List<String> mTitleList;

	public void update(List<String> titles) {
		mTitleList = titles;
	}

	private boolean mNeedCheckEqually;

	public boolean isNeedCheckEqually() {
		return mNeedCheckEqually;
	}

	public void setNeedCheckEqually(boolean needCheckEqually) {
		this.mNeedCheckEqually = needCheckEqually;
	}

	private OnCheckedChangeListener mOnCheckedChangeListener;

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	public OnCheckedChangeListener getOnCheckedChangeListener() {
		return mOnCheckedChangeListener;
	}

	public interface OnCheckedChangeListener {
		public void onCheckedChanged(ViewParent parent, int checkedId);
	}

	private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			if (mNeedCheckEqually) {
				checkEqually();
			}
		}
	};

	public int getTitleCount() {
		return mTitleList == null ? 0 : mTitleList.size();
	}

	/**
	 * 检查是否自动平分
	 */
	protected void checkEqually() {
		// for child implementation
	}
}
