package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class ExpandableHeightGridView extends GridView {

	boolean mExpanded = true;

	public ExpandableHeightGridView(Context context) {
		super(context);
	}

	public ExpandableHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandableHeightGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isExpanded() {
		return mExpanded;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (isExpanded()) {
			int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			ViewGroup.LayoutParams params = getLayoutParams();
			params.height = getMeasuredHeight();
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setExpanded(boolean expanded) {
		this.mExpanded = expanded;
	}
}