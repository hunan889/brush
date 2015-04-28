package com.yy.android.gamenews.ui.view;

import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yy.android.sportbrush.R;

public class ViewPagerRadioHeader extends ViewPagerHeader {

	protected RadioGroup mTitles;
	private LayoutInflater mInflater;
	public HorizontalScrollView mTitleContainer;

	public ViewPagerRadioHeader(Context context) {
		super(context);
	}

	public ViewPagerRadioHeader(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ViewPagerRadioHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mInflater = LayoutInflater.from(context);

		mInflater.inflate(R.layout.viewpager_header_radio_group, this);
		mTitleContainer = (HorizontalScrollView) findViewById(R.id.title_container);

		mTitles = (RadioGroup) findViewById(R.id.titles);

		mTitles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				animateToTitle(checkedId);
				OnCheckedChangeListener listener = getOnCheckedChangeListener();
				if (listener != null) {
					listener.onCheckedChanged(group, checkedId);
				}
			}
		});

	}

	@Override
	public void check(int pos) {

		mTitles.check(pos);
	}

	@Override
	public void update(List<String> ds) {
		mTitles.removeAllViews();

		if (ds == null || ds.size() <= 1) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < ds.size(); i++) {
			RadioButton btn = (RadioButton) mInflater.inflate(
					R.layout.my_favor_news_title, mTitles, false);
			btn.setId(i);
			btn.setText(ds.get(i));
			mTitles.addView(btn);
		}

		mTitles.check(-1);

		super.update(ds);
	}

	@Override
	protected void checkEqually() {
		int orientation = getResources().getConfiguration().orientation;
		if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
			return;
		}
		int width = mTitles.getWidth();
		View parent = (View) mTitles.getParent();
		int parentWidth = parent.getWidth();
		if (width < parentWidth) {
			int childCount = mTitles.getChildCount();

			// 总父视图宽度减去所有字的宽度（即child width的总和），再平分得出每个视图的margin
			int totalChildWidth = 0;
			for (int i = 0; i < childCount; i++) {
				View child = mTitles.getChildAt(i);
				int childWidth = child.getWidth();
				totalChildWidth += childWidth;
			}

			int remainWidth = parentWidth - totalChildWidth;
			int margin = remainWidth / childCount / 2;

			for (int i = 0; i < childCount; i++) {
				View child = mTitles.getChildAt(i);

				RadioGroup.LayoutParams childParams = (RadioGroup.LayoutParams) child
						.getLayoutParams();
				childParams.leftMargin = margin;
				childParams.rightMargin = margin;
			}
			mTitles.requestLayout();
		}
	}

	private Runnable mTitleSelector;

	private void animateToTitle(final int id) {
		final View titleView = mTitles.findViewById(id);
		if (mTitleSelector != null) {
			mTitleContainer.removeCallbacks(mTitleSelector);
		}
		if (titleView == null) {
			return;
		}
		mTitleSelector = new Runnable() {
			@Override
			public void run() {
				int x = titleView.getLeft()
						- (mTitleContainer.getWidth() - titleView.getWidth())
						/ 2;
				mTitleContainer.smoothScrollTo(x, 0);
				mTitleSelector = null;
			}
		};
		mTitleContainer.post(mTitleSelector);
	}
}
