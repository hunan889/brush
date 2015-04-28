package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yy.android.sportbrush.R;

public class GridFlowLayout extends ViewGroup {
	protected int mColumnWidth;
	protected int mColumnHeight;
	protected int mVerticalSpace;
	protected int mMinHorizontalSpace;
	protected int mNumColumns;
	protected int mHorizontalSpace;
	protected int mItemToColumnOffset;

	public GridFlowLayout(Context context) {
		this(context, null);
	}

	public GridFlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GridFlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.GridFlowLayout, defStyle, 0);

		mColumnWidth = a.getDimensionPixelOffset(
				R.styleable.GridFlowLayout_gfColumnWidth, 0);
		mVerticalSpace = a.getDimensionPixelOffset(
				R.styleable.GridFlowLayout_gfVSpacing, 0);
		mMinHorizontalSpace = a.getDimensionPixelOffset(
				R.styleable.GridFlowLayout_gfMinHSpacing, 0);
		a.recycle();
	}

	public int getColumnWidth() {
		return mColumnWidth;
	}

	public int getVerticalSpace() {
		return mVerticalSpace;
	}

	public int getNumColumns() {
		return mNumColumns;
	}

	public int getHorizontalSpace() {
		return mHorizontalSpace;
	}
	
	public int getItemToColumnOffset() {
		return mItemToColumnOffset;
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof GridFlowLayoutParams;
	}

	@Override
	protected GridFlowLayoutParams generateDefaultLayoutParams() {
		return new GridFlowLayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	public GridFlowLayoutParams generateLayoutParams(AttributeSet attrs) {
		return new GridFlowLayoutParams(getContext(), attrs);
	}

	@Override
	public GridFlowLayoutParams generateLayoutParams(LayoutParams params) {
		return new GridFlowLayoutParams(params.width, params.height);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		mNumColumns = determineColumns(widthSize - getPaddingLeft()
				- getPaddingRight());

		int childHeight = 0;
		int childWidth = 0;
		int xPos = getPaddingLeft();
		int yPos = getPaddingTop();
		final int num = getChildCount();
		for (int i = 0; i < num; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			GridFlowLayoutParams params = (GridFlowLayoutParams) child
					.getLayoutParams();
			int childWidthSpec = getChildMeasureSpec(
					MeasureSpec.makeMeasureSpec(mColumnWidth,
							MeasureSpec.EXACTLY), 0, params.width);
			int childHeightSpec = getChildMeasureSpec(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0,
					params.height);
			child.measure(childWidthSpec, childHeightSpec);
			childHeight = child.getMeasuredHeight();
			childWidth = child.getMeasuredWidth();

			if (mColumnWidth > childWidth) {
				mItemToColumnOffset = (mColumnWidth - childWidth) / 2;
			} else {
				mItemToColumnOffset = 0;
			}

			if (xPos + mColumnWidth > widthSize) {
				xPos = getPaddingLeft();
				yPos += mVerticalSpace + childHeight;
			}
			params.x = xPos + mItemToColumnOffset;
			params.y = yPos;
			xPos += mColumnWidth + mHorizontalSpace;

		}

		// int rows = (int) Math.ceil(1D * num / mNumColumns);
		// int tempHeight = rows * childHeight + (rows - 1) * mVerticalSpace;
		// if (tempHeight < 0) {
		// tempHeight = 0;
		// }
		mColumnHeight = childHeight;
		int finalWidth = resolveSize(getPaddingLeft() + getPaddingRight()
				+ getSuggestedMinimumWidth(), widthMeasureSpec);
		int finalHeight = resolveSize(yPos + childHeight, heightMeasureSpec);
		setMeasuredDimension(finalWidth, finalHeight);
	}

	private int determineColumns(int availableSpace) {

		int columns = 0;
		if (mColumnWidth > 0) {
			columns = (availableSpace + mMinHorizontalSpace)
					/ (mColumnWidth + mMinHorizontalSpace);
		} else {
			columns = 2;
		}

		int spaceLeft = availableSpace - columns * mColumnWidth - (columns - 1)
				* mMinHorizontalSpace;
		if (columns > 1) {
			mHorizontalSpace = mMinHorizontalSpace + spaceLeft / (columns - 1);
		} else {
			mHorizontalSpace = mMinHorizontalSpace + spaceLeft;
		}
		if (columns <= 0) {
			columns = 1;
		}
		return columns;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int num = getChildCount();
		int childWidth = 0;
		int childHeight = 0;
		for (int i = 0; i < num; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}

			childWidth = child.getMeasuredWidth();
			childHeight = child.getMeasuredHeight();
			GridFlowLayoutParams params = (GridFlowLayoutParams) child
					.getLayoutParams();
			child.layout(params.x, params.y, params.x + childWidth, params.y
					+ childHeight);
		}
	}

	public static class GridFlowLayoutParams extends LayoutParams {
		public int x;
		public int y;

		public GridFlowLayoutParams(int width, int height) {
			super(width, height);
		}

		public GridFlowLayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
	}

	public Point getNewLocationOnScreen() {
		Point point = new Point();
		int[] location = new int[2];
		getLocationOnScreen(location);

		final int num = getChildCount();
		int cols = num % mNumColumns;
		int rows = num / mNumColumns;

		int childHeight = 0;
		if (num > 0) {
			View child = getChildAt(0);
			childHeight = child.getMeasuredHeight();
		}

		int xPos = getPaddingLeft() + (mColumnWidth + mHorizontalSpace) * cols
				+ mItemToColumnOffset;
		int yPos = getPaddingTop() + rows * (childHeight + mVerticalSpace);
		point.x = location[0] + xPos;
		point.y = location[1] + yPos;
		return point;
	}

}
