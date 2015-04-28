package com.yy.android.gamenews.ui.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class HorizontalListView extends AdapterView<ListAdapter> {

	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			requestLayout();
		}
	};;
	
	private int SCROLL_DRUATION = 500;

	private static String TAG = "HorizontalListView";

	private float xDistance, yDistance, lastX, lastY;

	public HorizontalListView(Context context) {
		super(context);
		init();
	}

	public HorizontalListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {

		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
		initView();
	}

	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
	}

	@Override
	public void requestLayout() {
//		Log.d(TAG, "requestLayout");
		super.requestLayout();
	}

	@Override
	public void setOnItemSelectedListener(
			AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(
			AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			Log.d(TAG, "onChanged");
			synchronized (HorizontalListView.this) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}

	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {

		int positionX = 0;
		for (int i = 0; i < getChildCount() && i <= position;) {
			View child = getChildAt(i);
			positionX = child.getWidth() * position;
			break;
		}
		// int maxWidth = this.getChildCount() * this.getWidth();
		// if (positionX <= 0) {
		// positionX = 0;
		// }
		// if (positionX > maxWidth) {
		// positionX = maxWidth;
		// }
		scrollTo(positionX);
	}

	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(
				MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top,
			int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null) {
			return;
		}

		mIsScroll = mIsScroll || !mScroller.isFinished();
//		if (!mIsScroll) {
			if (mDataChanged) {
				int oldCurrentX = mCurrentX;
				initView();
				removeAllViewsInLayout();
				mNextX = oldCurrentX;
				mDataChanged = false;
			}
//		}

		if (mScroller.computeScrollOffset()) {
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}

		if (mNextX <= 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if (mNextX >= mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;
		if (mIsScroll) {
			mIsScroll = false;
			requestLayoutDelayed();
		}
	}

	public int getFirstVisible() {
		int first = 0;
		for (int i = 0; i < getChildCount();) {
			View child = getChildAt(i);
			first = mCurrentX / child.getWidth();
			break;
		}
		return first;
	}
	
	public int getVisibleCount() {
		int visibleCount = 0;
		int width = getWidth();
		for (int i = 0; i < getChildCount();) {
			View child = getChildAt(i);
			visibleCount = width / child.getWidth();
			break;
		}
		return visibleCount;
	}

	public int getLastVisible() {
		return getFirstVisible() + getVisibleCount() - 1;
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);

	}

	private void fillListRight(int rightEdge, final int dx) {
		while (rightEdge + dx < getWidth()
				&& mRightViewIndex < mAdapter.getCount()) {

			View child = mAdapter.getView(mRightViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if (mRightViewIndex == mAdapter.getCount() - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}

			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightViewIndex++;
		}

	}

	private void fillListLeft(int leftEdge, final int dx) {
		while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex,
					mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);

		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth,
						child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}

	private boolean mIsScroll;

	public synchronized void scrollTo(int x) {
		mIsScroll = true;
		mScroller.startScroll(mNextX, 0, x - mNextX, 0, SCROLL_DRUATION);
		requestLayoutDelayed();
	}
	
	private void requestLayoutDelayed() {
		post(mRunnable);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		getParent().getParent().requestDisallowInterceptTouchEvent(true);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - lastX);
			yDistance += Math.abs(curY - lastY);
			lastX = curX;
			lastY = curY;
			// 横向移动距离>竖直距离，要横向滑动
			if (xDistance > yDistance && getParent() != null)
				getParent().requestDisallowInterceptTouchEvent(true);
			else
				getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}

		return super.onTouchEvent(ev);
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		synchronized (HorizontalListView.this) {
			mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontalListView.this
					.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			synchronized (HorizontalListView.this) {
				mNextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this,
								child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}

			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(
								HorizontalListView.this, child, mLeftViewIndex
										+ 1 + i,
								mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}

			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Rect viewRect = new Rect();
			int[] childPosition = new int[2];
			child.getLocationOnScreen(childPosition);
			int left = childPosition[0];
			int right = left + child.getWidth();
			int top = childPosition[1];
			int bottom = top + child.getHeight();
			viewRect.set(left, top, right, bottom);
			return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
		}
	};

}
