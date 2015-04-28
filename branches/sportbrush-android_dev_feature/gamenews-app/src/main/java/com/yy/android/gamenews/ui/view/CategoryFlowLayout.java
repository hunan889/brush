package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.duowan.gamenews.Channel;
import com.yy.android.sportbrush.R;

public class CategoryFlowLayout extends GridFlowLayout implements
		View.OnTouchListener, View.OnLongClickListener {
	private static final int MODE_NORMAL = 0;
	private static final int MODE_EDIT = 1;
	private int mMode = MODE_NORMAL;
	private int mLastX = -1;
	private int mLastY = -1;
	private int mDragged = -1;
	private int mLastTarget = -1;
	private int mReserved = 0;
	public static int animTime = 300;
	private boolean mTouching = false;
	private boolean isFloatingViewReady = false;
	private int mLocation[] = new int[2];
	private GridItemViewCache mDummyImage;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();

	protected OnRearrangeListener mOnRearrangeListener;
	protected OnLongClickListener mOnLongClickListener;
	private AnimationListener mAnimationListener;

	public interface OnRearrangeListener {
		public abstract void onRearrange(int oldIndex, int newIndex);
	}

	public CategoryFlowLayout(Context context) {
		super(context);
		init();
	}

	public CategoryFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CategoryFlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setListeners();
		setChildrenDrawingOrderEnabled(true);
		mAnimationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		};
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@SuppressLint("WrongCall")
	protected void arrangeLayout(int l, int t, int r, int b) {
		onLayout(true, l, t, r, b);
	}

	protected int getColumnIndex(int x) {
		x -= getPaddingLeft();
		for (int i = 0; x > 0; i++) {
			if (x < mColumnWidth)
				return i;
			x -= (mColumnWidth + mHorizontalSpace);
		}
		return -1;
	}

	protected int getRowIndex(int y) {
		y -= getPaddingTop();
		for (int i = 0; y > 0; i++) {
			if (y < mColumnHeight)
				return i;
			y -= (mColumnHeight + mVerticalSpace);
		}
		return -1;
	}

	public int getItemIndexByCoordinate(int x, int y) {
		int col = getColumnIndex(x);
		int row = getRowIndex(y);
		if (col == -1 || row == -1)
			return -1;
		int index = row * mNumColumns + col;
		if (index >= getChildCount())
			return -1;
		return index;
	}

	public int getSelectedItemIndex() {
		return getItemIndexByCoordinate(mLastX, mLastY);
	}

	protected int getTargetByCoordinate(int x, int y) {
		if (getRowIndex(y) == -1)
			return -1;
		int leftPos = getItemIndexByCoordinate(x - (mColumnWidth / 4), y);
		int rightPos = getItemIndexByCoordinate(x + (mColumnWidth / 4), y);
		if (leftPos == -1 && rightPos == -1)
			return -1;
		if (leftPos == rightPos)
			return -1;
		int target = -1;
		if (rightPos > -1)
			target = rightPos;
		else if (leftPos > -1)
			target = leftPos + 1;
		if (mDragged < target) {
			// log("target", String.format("mDragged-adjust-target(%d,%d)",
			// mDragged, target - 1));
			return target - 1;
		}
		return target;
	}

	protected Point getCoordinateByIndex(int index) {
		int col = index % mNumColumns;
		int row = index / mNumColumns;
		return new Point(getPaddingLeft()
				+ (mColumnWidth + getHorizontalSpace()) * col, getPaddingTop()
				+ (mColumnHeight + getVerticalSpace()) * row);
	}

	public int getIndexOfItem(View child) {
		for (int i = 0; i < getChildCount(); i++)
			if (getChildAt(i) == child)
				return i;
		return -1;
	}

	public int getIndexOfItemByDelBtn(View delBtn) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child != null && child.findViewById(R.id.channel_del) == delBtn)
				return i;
		}
		return -1;
	}

	public int getIndexOfItemByChannelFrame(int id) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child != null) {
				Channel channel = (Channel) child
						.findViewById(R.id.channel_del).getTag();
				if (channel.getId() == id)
					return i;
			}
		}
		return -1;
	}

	public ArrayList<Channel> getSubscribeChannelList() {
		ArrayList<Channel> channels = new ArrayList<Channel>();
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child != null) {
				View v = child.findViewById(R.id.channel_del);
				if (v != null) {
					channels.add((Channel) v.getTag());
				}
			}
		}
		return channels;
	}

	public int getSubscribeChannelCount() {
		return getChildCount();
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mOnLongClickListener = l;
	}

	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.mOnRearrangeListener = l;
	}

	public void setEditMode() {
		mMode = MODE_EDIT;
		final int num = getChildCount();
		if (num >= 1) {
			for (int i = mReserved; i < num; i++) {
				View child = getChildAt(i);
				child.clearAnimation();
				child.startAnimation(AnimationUtils.loadAnimation(getContext(),
						R.anim.shake));
				View mask = child.findViewById(R.id.channel_del_mask);
				mask.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setNormalMode() {
		mMode = MODE_NORMAL;
		if (mDummyImage != null) {
			mDummyImage.removeSelf();
			mDummyImage = null;
			isFloatingViewReady = false;
		}
		final int num = getChildCount();
		if (num >= 1) {
			for (int i = 0; i < num; i++) {
				View child = getChildAt(i);
				child.clearAnimation();
				if (child.getVisibility() == View.INVISIBLE) {
					child.setVisibility(View.VISIBLE);
				}
				View mask = child.findViewById(R.id.channel_del_mask);
				mask.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void updateViewsByMode() {
		if (mMode == MODE_EDIT) {
			setEditMode();
		} else if (mMode == MODE_NORMAL) {
			setNormalMode();
		}
	}

	public boolean isFloatingViewReady() {
		return isFloatingViewReady;
	}

	private void prepareDrag(View view) {
		if (!mTouching) {
			return;
		}
		Bitmap bitmap = GridItemViewCache.getBitmapByView(view);
		if (bitmap != null) {
			int location[] = new int[2];
			int imgWidth = bitmap.getWidth();
			int imgHeight = bitmap.getHeight();
			view.clearAnimation();
			view.getLocationOnScreen(location);
			int toLeft = mLastX - location[0] + mLocation[0];
			int toTop = mLastY - location[1] + mLocation[1];
			mDummyImage = new GridItemViewCache(getContext(), bitmap, toLeft,
					toTop, 0, 0, imgWidth, imgHeight);
			mDummyImage.bindToWindow(getApplicationWindowToken(), mLastX
					+ mLocation[0], mLastY + mLocation[1]);
			isFloatingViewReady = true;
		}

		view.setVisibility(View.INVISIBLE);
		invalidate();
	}

	protected void setListeners() {
		setOnTouchListener(this);
		super.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View view) {
		if (mOnLongClickListener != null) {
			mOnLongClickListener.onLongClick(view);
		}
		if (mMode == MODE_NORMAL) {
			setEditMode();
			return true;
		}
		return false;
	}
	
	public void editChannel(){
		if (mMode == MODE_NORMAL) {
			setEditMode();
		}
	}

	public boolean onTouch(View view, MotionEvent event) {
		if (mMode == MODE_NORMAL) {
			return false;
		} else {
			getLocationOnScreen(mLocation);
			int action = event.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mLastX = (int) event.getX();
				mLastY = (int) event.getY();
				mTouching = true;

				int index = getSelectedItemIndex();
				if (index != -1) {
					mDragged = index;
					prepareDrag(getChildAt(index));
				}
				// log("event", String.format("onTouch -ACTION_DOWN(%d,%d)",
				// mLastX, mLastY));
				break;
			case MotionEvent.ACTION_MOVE:
				requestDisallowInterceptTouchEvent(true);
				if (mDragged != -1) {
					int x = (int) event.getX(), y = (int) event.getY();
					if (mDummyImage != null) {
						mDummyImage.moveToCoordinate(mLastX + mLocation[0],
								mLastY + mLocation[1]);
					}

					int target = getTargetByCoordinate(x, y);
					if (mLastTarget != target) {
						if (target != -1 && target >= mReserved) {
							itemSwapAnimation(target);
							mLastTarget = target;
						}
					}
				}
				mLastX = (int) event.getX();
				mLastY = (int) event.getY();
				// log("event", String.format("onTouch -ACTION_MOVE(%d,%d)",
				// mLastX, mLastY));
				break;
			case MotionEvent.ACTION_UP:
				if (mDragged != -1) {
					View v = getChildAt(mDragged);
					if (v.getVisibility() == View.INVISIBLE) {
						v.setVisibility(View.VISIBLE);
					}
					if (mLastTarget != -1)
						reorderChildren();
					else {
						v.invalidate();
					}
					// log("event", String.format("onTouch -ACTION_UP(%d,%d)",
					// mLastX, mLastY));
					if (mDummyImage != null) {
						mDummyImage.removeSelf();
						mDummyImage = null;
						isFloatingViewReady = false;
					}
					v.clearAnimation();
					mLastTarget = -1;
					mDragged = -1;
					updateViewsByMode();
				}
				mTouching = false;
				invalidate();
				break;
			case MotionEvent.ACTION_CANCEL:
				if (mDummyImage != null) {
					mDummyImage.removeSelf();
					mDummyImage = null;
					isFloatingViewReady = false;
					updateViewsByMode();
				}
				break;
			}
			if (mDragged != -1)
				return true;
			requestDisallowInterceptTouchEvent(false);
			return false;
		}
	}

	protected void itemSwapAnimation(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (i == mDragged)
				continue;
			int newPos = i;
			if (mDragged < target && i >= mDragged + 1 && i <= target)
				newPos--;
			else if (target < mDragged && i >= target && i < mDragged)
				newPos++;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoordinateByIndex(oldPos);
			Point newXY = getCoordinateByIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y
					- v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y
					- v.getTop());

			TranslateAnimation translate = new TranslateAnimation(
					Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
					newOffset.x, Animation.ABSOLUTE, oldOffset.y,
					Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animTime);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			translate.setAnimationListener(mAnimationListener);
			v.clearAnimation();
			v.startAnimation(translate);
			newPositions.set(i, newPos);
		}
	}

	protected void reorderChildren() {
		if (mOnRearrangeListener != null)
			mOnRearrangeListener.onRearrange(mDragged, mLastTarget);
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		removeAllViews();
		while (mDragged != mLastTarget)
			if (mLastTarget == children.size()) {
				children.add(children.remove(mDragged));
				mDragged = mLastTarget;
			} else if (mDragged < mLastTarget) {
				Collections.swap(children, mDragged, mDragged + 1);
				mDragged++;
			} else if (mDragged > mLastTarget) {
				Collections.swap(children, mDragged, mDragged - 1);
				mDragged--;
			}
		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i), false);
		}
		invalidate();
	}

	private void log(String tag, String msg) {
		Log.e(tag, msg);
	}

	@Override
	public void addView(View child) {
		addView(child, true);
	};

	public void addView(View child, boolean flag) {
		super.addView(child);
		newPositions.add(-1);
		if (flag) {
			updateViewsByMode();
		}
	};

	@Override
	public void removeViewAt(int index) {
		View child = getChildAt(index);
		child.clearAnimation();
		super.removeViewAt(index);
		newPositions.remove(index);
	};
}
