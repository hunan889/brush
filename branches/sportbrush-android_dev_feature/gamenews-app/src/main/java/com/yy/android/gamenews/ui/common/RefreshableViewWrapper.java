package com.yy.android.gamenews.ui.common;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huewu.pla.lib.MultiColumnListView;
import com.yy.android.gamenews.ui.view.Counter;
import com.yy.android.gamenews.ui.view.Counter.OnCounterCallback;
import com.yy.android.gamenews.ui.view.CounterItem;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

/**
 * 为ListView添加属性，使得该listview支持下拉刷新和上拉加载
 * 
 * 需要注册OnListViewEventListener 来监听ListView的事件
 * 
 * 使用该类后，不要再设置listview的 onTouchListener和onScrollListener
 * 
 * @author carlosliu
 * 
 */
public abstract class RefreshableViewWrapper<E extends View> {

	protected boolean mAddHeader;
	protected boolean mAddFooter;

	// private static final String TAG =
	// RefreshListWrapper.class.getSimpleName();
	private E mMainView;
	private final static int RELEASE_TO_REFRESH = 0;// 下拉过程的状态值
	private final static int PULL_TO_REFRESH = 1; // 从下拉返回到不刷新的状态值
	private final static int REFRESHING = 2;// 正在刷新的状态值
	private final static int DONE = 3;
	private final static int LOADING = 4;
	private Counter mShowCounter;
	private OnCounterCallback mOnShowCallback = new OnCounterCallback() {

		@Override
		public void onStart() {
		}

		@Override
		public void onStop() {
			onLvRefresh();
		}

		@Override
		public void onPause() {

		}

		@Override
		public void onUpdate(List<CounterItem> list) {
			mHeaderView.setPadding(0, (int) list.get(0).getValue(), 0, 0);
		}

	};

	private Counter mHideCounter;
	private OnCounterCallback mOnHideCallback = new OnCounterCallback() {

		@Override
		public void onStart() {

		}

		@Override
		public void onStop() {
			mHeaderTipsTv.setText(R.string.global_list_pull);
			mHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			if (mOnRefreshCompleteListener != null && showUpdateTip) {
				mOnRefreshCompleteListener.onComplete(mUpdateCount);
			}
		}

		@Override
		public void onPause() {

		}

		@Override
		public void onUpdate(List<CounterItem> list) {
			mHeaderView.setPadding(0, (int) list.get(0).getValue(), 0, 0);
		}

	};

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;
	private final static int OFFSET_SCROLL = 0;
	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private View mFooterView;
	private LinearLayout mHeaderView;
	private TextView mHeaderTipsTv;
	private TextView mHeaderLastUpdatedTv;
	// private ImageView mHeaderArrowIv;
	private ImageView mHeaderProgressBar;
	private ImageView mFooterProgressBar;
	private TextView mFooterTipsTv;
	private View mFooterContainer;
	private Animation mLoadingAnimation;

	// 定义头部下拉刷新的布局的高度
	private int headerContentHeight;

	// private int mHeaderShowPos;
	// private int mHeaderHidePos;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int mStartY; // 用户手指按下时的位置
	private int mLastY; // 上次event时的位置，用于记录用户是向上还是向下滑
	private int mState;
	private boolean isBack;
	private boolean mHasLoadingBar;
	private boolean showUpdateTip; // 是否需要show 更新提示

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecorded;

	protected OnListViewEventListener mListener;

	private boolean isRefreshable;
	private Context mContext;

	private int mDirection = DIRECTION_NONE;
	public static final int DIRECTION_UP = -1;
	public static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_DOWN = 1;

	public RefreshableViewWrapper(Context context, E view) {
		mMainView = view;
		init(context, null, true, true);
	}

	public RefreshableViewWrapper(Context context, E view, boolean addHeader,
			boolean addFooter) {
		mMainView = view;
		init(context, null, addHeader, addFooter);
	}

	public RefreshableViewWrapper(Context context, E view, View headerView) {
		mMainView = view;
		init(context, headerView, true, true);
	}

	private void init(Context context, View headerView, boolean addHeader,
			boolean addFooter) {
		this.mAddHeader = addHeader;
		this.mAddFooter = addFooter;
		mContext = context;

		mShowCounter = new Counter();
		mShowCounter.setOnTimerCallback(mOnShowCallback);

		mHideCounter = new Counter();
		mHideCounter.setOnTimerCallback(mOnHideCallback);

		initView(mMainView);

		if (mLoadingAnimation == null) {
			mLoadingAnimation = AnimationUtils.loadAnimation(mContext,
					R.anim.article_detail_loading);
			mLoadingAnimation.setInterpolator(new LinearInterpolator());
			mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		}

		// mMainView.setCacheColorHint(Color.TRANSPARENT);
		inflater = LayoutInflater.from(context);
		mHeaderView = (LinearLayout) inflater.inflate(
				R.layout.article_list_header_refreshing, null);
		mHeaderTipsTv = (TextView) mHeaderView
				.findViewById(R.id.lvHeaderTipsTv);
		mHeaderLastUpdatedTv = (TextView) mHeaderView
				.findViewById(R.id.lvHeaderLastUpdatedTv);
		// mHeaderArrowIv = (ImageView) mHeaderView
		// .findViewById(R.id.lvHeaderArrowIv);
		// 设置下拉刷新图标的最小高度和宽度
		// mHeaderArrowIv.setMinimumWidth(70);
		// mHeaderArrowIv.setMinimumHeight(50);

		mHeaderProgressBar = (ImageView) mHeaderView
				.findViewById(R.id.lvHeaderProgressBar);
		mHeaderProgressBar.setAnimation(mLoadingAnimation);
		measureView(mHeaderView);
		headerContentHeight = mHeaderView.getMeasuredHeight();
		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		mHeaderView.setPadding(0, -1 * headerContentHeight, 0, 0);
		// 重绘一下
		mHeaderView.invalidate();

		// 将下拉刷新的布局加入ListView的顶部
		// mListView.addHeaderView(mHeaderView, null, false);
		if (addHeader) {
			addHeader(mMainView, mHeaderView);
		}
		if (headerView != null) {
			addHeader(mMainView, headerView);
			Log.d("test", "headerView = " + (headerView != null));
			// mListView.addHeaderView(headerView, null, false);
		}

		// 上拉加载布局
		mFooterView = inflater.inflate(R.layout.article_list_footer_loading,
				null);
		mFooterProgressBar = (ImageView) mFooterView
				.findViewById(R.id.progressBar1);
		mFooterTipsTv = (TextView) mFooterView
				.findViewById(R.id.global_loading_text);
		mFooterContainer = mFooterView
				.findViewById(R.id.global_footer_container);

		mFooterContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					showLoadingBar();
					mListener.onLoading();
				}
			}
		});
		if (addFooter) {
			addFooter(mMainView, mFooterView);
		}
		// mListView.addFooterView(mFooterView, null, false);
		showLoadingBar();

		// 设置滚动监听事件
		// mMainView.setOnScrollListener(mOnScrollListener);
		mMainView.setOnTouchListener(mOnTouchListener);

		// 设置旋转动画事件
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		mState = DONE;
		// 是否正在刷新
		isRefreshable = false;
	}

	protected void goIdle() {
		mDirection = DIRECTION_NONE;
		mLastY = 0;
	}

	//
	// public void onScrollStateChanged(E view, int scrollState) {
	//
	// if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE
	// && (mHasLoadingBar)
	// && (view.getLastVisiblePosition() >= view.getCount() - 1)) {
	// if (mListener != null) {
	// mListener.onLoading();
	// }
	// }
	//
	// if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE) {
	// mDirection = DIRECTION_NONE;
	// mLastY = 0;
	// }
	//
	// if (mListener != null) {
	// mListener.onScrollStateChanged(view, scrollState);
	// }
	// }
	//
	// public void onScroll(E view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// // Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
	// if (firstVisibleItem == 0) {
	// isRefreshable = true;
	// } else {
	// isRefreshable = false;
	// }
	// if (mListener != null) {
	// mListener.onScroll(view, firstVisibleItem, visibleItemCount,
	// totalItemCount, mDirection);
	// }
	// }

	// private OnScrollListener mOnScrollListener = new OnScrollListener() {
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	//
	// if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE
	// && (mHasLoadingBar)
	// && (view.getLastVisiblePosition() >= view.getCount() - 1)) {
	// if (mListener != null) {
	// mListener.onLoading();
	// }
	// }
	//
	// if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE) {
	// mDirection = OnListViewEventListener.SCROLL_STATE_IDLE;
	// mLastY = 0;
	// }
	//
	// if (mListener != null) {
	// mListener.onScrollStateChanged(view, scrollState);
	// }
	// }
	//
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// // Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
	// if (firstVisibleItem == 0) {
	// isRefreshable = true;
	// } else {
	// isRefreshable = false;
	// }
	// if (mListener != null) {
	// mListener.onScroll(view, firstVisibleItem, visibleItemCount,
	// totalItemCount, mDirection);
	// }
	// }
	// };

	public boolean isRefreshable() {
		return isRefreshable;
	}

	public void setRefreshable(boolean isRefreshable) {
		this.isRefreshable = isRefreshable;
	}

	public int getDirection() {
		return mDirection;
	}

	public void setDirection(int direction) {
		this.mDirection = direction;
	}

	private void handleRefreshEvent(View v, MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!mIsRecorded) {
				mIsRecorded = true;
				mStartY = (int) ev.getY();// 手指按下时记录当前位置
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int currentY = (int) ev.getY();
			if (!mIsRecorded) {
				mIsRecorded = true;
				mStartY = currentY;
			}
			if (mState != REFRESHING && mIsRecorded && mState != LOADING) {
				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				// 可以松手去刷新了
				if (mState == RELEASE_TO_REFRESH) {
					// setSelection(mMainView, 0);
					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((currentY - mStartY) / RATIO < headerContentHeight)// 由松开刷新状态转变到下拉刷新状态
							&& (currentY - mStartY) > 0) {
						mState = PULL_TO_REFRESH;
						changeHeaderViewByState(true);
					}
					// 一下子推到顶了
					else if (currentY - mStartY <= 0) {// 由松开刷新状态转变到done状态
						mState = DONE;
						changeHeaderViewByState(true);
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (mState == PULL_TO_REFRESH) {
					// setSelection(mMainView, 0);
					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((currentY - mStartY) / RATIO >= headerContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
						mState = RELEASE_TO_REFRESH;
						isBack = true;
						changeHeaderViewByState(true);
					}
					// 上推到顶了
					else if (currentY - mStartY <= 0) {// 由 或者下拉刷新状态转变到done状态
						mState = DONE;
						// changeHeaderViewByState();
					}
				}
				// done状态下
				if (mState == DONE) {
					if (currentY - mStartY > 0) {
						mState = PULL_TO_REFRESH;
						changeHeaderViewByState(true);
					}
				}
				// 更新headView的size
				if (mState == PULL_TO_REFRESH) {
					updateRefreshTime();
					mHeaderView.setPadding(0, -1 * headerContentHeight
							+ (currentY - mStartY) / RATIO, 0, 0);

				}
				// 更新headView的paddingTop
				if (mState == RELEASE_TO_REFRESH) {
					mHeaderView.setPadding(0, (currentY - mStartY) / RATIO
							- headerContentHeight, 0, 0);
				}

			}
			break;

		default:
			break;
		}
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent ev) {

			int currentY = (int) ev.getY();
			int action = ev.getAction();

			switch (action) {
			case MotionEvent.ACTION_DOWN: {
				mLastY = mStartY;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mLastY - currentY > OFFSET_SCROLL) {
					// Log.d(TAG, "mDirection = DIRECTION_UP");
					mDirection = DIRECTION_UP;
				} else if (mLastY - currentY < -OFFSET_SCROLL) {
					// Log.d(TAG, "mDirection = DIRECTION_DOWN");
					mDirection = DIRECTION_DOWN;
				}
				mLastY = currentY;

				if (mMainView instanceof ListView) {
					onScroll(((ListView) mMainView).getFirstVisiblePosition(),
							0, 0);
				} else if (mMainView instanceof GridView) {
					onScroll(((GridView) mMainView).getFirstVisiblePosition(),
							0, 0);
				} else if (mMainView instanceof MultiColumnListView) {
					onScroll(
							((MultiColumnListView) mMainView)
									.getFirstVisiblePosition(),
							0, 0);
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (mState != REFRESHING && mState != LOADING) {
					if (mState == PULL_TO_REFRESH) {
						mState = DONE;
						changeHeaderViewByState(false, false);
					}
					if (mState == RELEASE_TO_REFRESH && mAddHeader) {
						mState = REFRESHING;
						changeHeaderViewByState(true, false);
					}
				}
				mIsRecorded = false;
				isBack = false;

				break;
			}
			if (isRefreshable && mAddHeader) {
				handleRefreshEvent(v, ev);
			}
			if (mMainView != null) {
				return mMainView.onTouchEvent(ev);
			}
			return true;
		}
	};

	private void showHeaderView(boolean anim) {
		mHeaderTipsTv.setText(R.string.global_list_refreshing);
		mHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

		if (anim) {
			CounterItem item = new CounterItem(mHeaderView.getPaddingTop(), 0);
			mShowCounter.setValue(item);
			mShowCounter.start();
		}

		else {

			mHeaderView.setPadding(0, 0, 0, 0);
			onLvRefresh();
		}
	}

	private void hideHeaderView(boolean showUpdateTip, boolean delay) {
		this.showUpdateTip = showUpdateTip;
		if (showUpdateTip) {
			int res = 0;
			if (-1 == mUpdateCount) { // -1表示数据请求失败
				res = R.string.global_list_refresh_fail;
			} else {
				res = R.string.global_list_refresh_done;
			}
			mHeaderTipsTv.setText(res);
		}
		mHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

		int delayDuration = 0;
		if (delay) {
			delayDuration = 500;
		}

		mHandler.postDelayed(mHideHeaderRunnable, delayDuration);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState(boolean showUpdateTip, boolean delay) {
		switch (mState) {
		case RELEASE_TO_REFRESH:
			// mHeaderArrowIv.setVisibility(View.VISIBLE);
			// mHeaderProgressBar.setVisibility(View.GONE);
			mHeaderTipsTv.setVisibility(View.VISIBLE);
			mHeaderLastUpdatedTv.setVisibility(View.VISIBLE);
			// mHeaderArrowIv.clearAnimation();// 清除动画
			// mHeaderArrowIv.startAnimation(animation);// 开始动画效果

			mHeaderTipsTv.setText(R.string.global_list_release);
			break;
		case PULL_TO_REFRESH:
			// mHeaderProgressBar.setVisibility(View.GONE);
			mHeaderTipsTv.setVisibility(View.VISIBLE);
			mHeaderLastUpdatedTv.setVisibility(View.VISIBLE);

			// mHeaderArrowIv.clearAnimation();
			// mHeaderArrowIv.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				// mHeaderArrowIv.clearAnimation();
				// mHeaderArrowIv.startAnimation(reverseAnimation);

				mHeaderTipsTv.setText(R.string.global_list_pull);
			} else {
				mHeaderTipsTv.setText(R.string.global_list_pull);
			}
			break;

		case REFRESHING:

			showHeaderView(true);
			break;
		case DONE:
			hideHeaderView(showUpdateTip, delay);
			break;
		}
	}

	private void changeHeaderViewByState(boolean showUpdateTip) {
		changeHeaderViewByState(showUpdateTip, true);
	}

	// 此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
				params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnListViewEventListener(
			OnListViewEventListener refreshListener) {
		this.mListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnListViewEventListener {
		public static int SCROLL_STATE_IDLE = 0;
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;
		public static int SCROLL_STATE_FLING = 2;

		public void onRefresh();

		public void onLoading();

		public void onScrollStateChanged(View view, int scrollState);

		public void onScroll(View view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount, int direction);
	}

	public void onRefreshing() {
		mState = REFRESHING;
		changeHeaderViewByState(true);
		updateRefreshTime();
	}

	private int mUpdateCount;

	public void onRefreshComplete(int updateCount) {
		mState = DONE;
		mUpdateCount = updateCount;
		changeHeaderViewByState(true);
	}

	private Handler mHandler = new Handler() {
	};

	private Runnable mHideHeaderRunnable = new Runnable() {
		public void run() {
			CounterItem item = new CounterItem(mHeaderView.getPaddingTop(), -1
					* headerContentHeight);
			mHideCounter.setValue(item);
			mHideCounter.start();
		};
	};

	private void updateRefreshTime() {
		mHeaderLastUpdatedTv.setText(mContext.getString(
				R.string.global_list_last_get,
				TimeUtil.parseTimeForRefresh(mContext, mLastRefreshTime)));
	}

	private long mLastRefreshTime;

	public void setRefreshTime(long time) {
		mLastRefreshTime = time;
	}

	public void showNoMoreLoadingBar() {
		mHasLoadingBar = false;
		mFooterTipsTv.setText(R.string.global_list_no_more);
		if (mFooterProgressBar != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterProgressBar.clearAnimation();
		}

		mFooterContainer.setClickable(false);
	}

	public void showErrorLoadingBar() {
		mHasLoadingBar = false;

		mFooterTipsTv.setText(R.string.global_retry);

		if (mFooterProgressBar != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterProgressBar.clearAnimation();
		}
		mFooterContainer.setClickable(true);
	}

	public void showLoadingBar() {
		if (mAddFooter) {
			mHasLoadingBar = true;
		}
		mFooterTipsTv.setText(R.string.global_list_loading);

		if (mFooterProgressBar != null) {
			mFooterProgressBar.setVisibility(View.VISIBLE);
			mFooterProgressBar.startAnimation(mLoadingAnimation);
		}
		mFooterContainer.setClickable(false);
	}

	public boolean hasLoadingBar() {
		return mHasLoadingBar;
	}

	private void onLvRefresh() {
		if (mListener != null) {
			mListener.onRefresh();
		}
	}

	public void onScroll(int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		if (firstVisibleItem == 0) {
			setRefreshable(true);
		} else {
			setRefreshable(false);
		}
		if (mListener != null) {
			mListener.onScroll(mMainView, firstVisibleItem, visibleItemCount,
					totalItemCount, mDirection);
		}
	}

	/**
	 * Called from constructor method, to init the view by subclass
	 * 
	 * @param view
	 */
	protected abstract void initView(E view);

	protected abstract void addHeader(E view, View header);

	protected abstract void addFooter(E view, View footer);

	protected abstract void setSelection(E view, int pos);

	private OnRefreshCompleteListener mOnRefreshCompleteListener;

	public void setOnRefreshCompleteListener(OnRefreshCompleteListener listener) {
		mOnRefreshCompleteListener = listener;
	}

	public interface OnRefreshCompleteListener {
		public void onComplete(int updateCount);
	}
}
