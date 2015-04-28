package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.ui.common.DataViewConverter;
import com.yy.android.gamenews.ui.common.DataViewConverter.OnItemClickListener;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.RefreshListWrapper;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper.OnListViewEventListener;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper.OnRefreshCompleteListener;
import com.yy.android.gamenews.ui.view.BaseBannerView.OnBannerItemClickListener;
import com.yy.android.gamenews.ui.view.InfiniteBannerView;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

/**
 * 对数据视图进行抽象，获取用户调用setType方法传递过来的视图类型 通过{@link DataViewConverter} 类得到数据视图，并将该视图
 * 使用{@link RefreshableViewWrapper} 进行封装。 <br/>
 * <br/>
 * 对以下事件进行了封装：<br/>
 * 1. 无数据时的空视图 <br/>
 * 2. 数据视图的回调事件处理 <br/>
 * 3. 刷新数量显示<br/>
 * 
 * @param <DATA>
 *            列表数据类型
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class BaseListFragment<DATA> extends BaseFragment implements
		OnListViewEventListener, OnItemClickListener,
		OnBannerItemClickListener, OnRefreshCompleteListener {

	public static final String LOG_TAG = "BaseListFragment";

	protected DataViewConverter<?> mDataViewConverter;

	private View mUpdateCountLayout;
	private TextView mUpdateCountBubble;
	private TextView mUpdateCountTv;

	private ImageAdapter<DATA> mAdapter;

	protected RefreshableViewWrapper<?> mViewWrapper;
	private ArrayList<DATA> mDataSource = new ArrayList<DATA>();
	private ArrayList<DATA> mBannerDataSource = new ArrayList<DATA>();
	// private LinkedHashMap<String,DATA> mMapDataSource = new
	// LinkedHashMap<String,DATA>();

	/**
	 * 置顶banner view
	 */
	private InfiniteBannerView mBannerView;
	private View mBannerLayout;

	/**
	 * 顶端置顶banner的adapter
	 */
	private ImageAdapter<DATA> mBannerAdapter;

	private static final String KEY_HIDE_LOADING_BAR = "is_hide_loading_bar";
	private static final String KEY_DATA_SOURCE = "list_datasource";
	private static final String KEY_BANNER_DATA_SOURCE = "list_banner_datasource";
	private static final String KEY_VIEW_TYPE = "view_type";

	private IPageCache mPageCache;

	protected Resources mRes;

	public ArrayList<DATA> getDataSource() {
		return mDataSource;
	}

	public ArrayList<DATA> getBannerDataSource() {
		return mBannerDataSource;
	}

	private int mType = DataViewConverterFactory.TYPE_LIST_NORMAL;

	public BaseListFragment() {
		mPageCache = new IPageCache();
	}

	public void setType(int type) {
		this.mType = type;
		mDataViewConverter = DataViewConverterFactory.getDataViewWrapper(
				mContext, mType);
	}

	public int getType() {
		return mType;
	}

	@Override
	public void onAttach(Activity activity) {
		mRes = activity.getResources();
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		mRes = null;
		super.onDetach();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			mType = savedInstanceState.getInt(KEY_VIEW_TYPE);
		}

		if (mDataViewConverter == null) {
			setType(mType);
		}

		ViewGroup parentView = (ViewGroup) createView(inflater, container,
				savedInstanceState);

		customizeView(parentView);

		if (needShowUpdatedCount()) {
			mUpdateCountLayout = mInflater.inflate(
					R.layout.article_list_updated_count, null);
			mUpdateCountTv = (TextView) mUpdateCountLayout
					.findViewById(R.id.update_count_tv);
			mUpdateCountBubble = (TextView) mUpdateCountLayout
					.findViewById(R.id.update_count_bubble);
			parentView.addView(mUpdateCountLayout,
					new FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
		}

		mViewWrapper = getViewWrapper();

		if (mViewWrapper != null) {
			mViewWrapper.setOnRefreshCompleteListener(this);
			mViewWrapper.setOnListViewEventListener(this);
		}
		mBannerAdapter = initBannerAdapter();
		if (mBannerAdapter != null) {
			View layout = inflater.inflate(R.layout.article_list_bannerview,
					null);
			mBannerLayout = layout.findViewById(R.id.root);
			mBannerView = (InfiniteBannerView) mBannerLayout
					.findViewById(R.id.article_list_banner);
			mBannerLayout.setVisibility(View.GONE);
			mBannerView.setListAdapter(mBannerAdapter);
			mBannerView.startScroll();
			mBannerView.setOnItemClickListener(this);
			mDataViewConverter.addHeader(layout);
		}

		boolean hasLoadingBar = true;
		if (savedInstanceState != null) {

			hasLoadingBar = savedInstanceState.getBoolean(KEY_HIDE_LOADING_BAR,
					true);
			mDataSource.clear();

			// 多个fragment可能共用同一个saveinstance，拿出来的datasource可能为同一个
			// 当其它的fragment里面的datasource有改变时，会影响到这个类的datasource
			mDataSource.addAll((ArrayList<DATA>) savedInstanceState
					.getSerializable(KEY_DATA_SOURCE));

			updateBannerIfNotEmpty((ArrayList<DATA>) savedInstanceState
					.getSerializable(KEY_BANNER_DATA_SOURCE));
		}
		if (mViewWrapper != null) {
			if (hasLoadingBar) {
				mViewWrapper.showLoadingBar();
			} else {
				mViewWrapper.showNoMoreLoadingBar();
			}
		}

		super.onCreateView(inflater, container, savedInstanceState);
		return parentView;
	}

	protected void customizeView(ViewGroup viewGroup) {

	}

	private String strUpdatedCount;
	private String strUpdatedCountZero;

	/**
	 * 获取数据显示的视图
	 * 
	 * @return
	 */
	protected View getDataView() {
		return mDataViewConverter.getDataView();
	}

	/**
	 * 对data view添加上拉刷新和下拉加载属性
	 * 
	 * @return
	 */
	protected RefreshableViewWrapper<?> getViewWrapper() {
		if (!isRefreshable()) {
			return null;
		}
		return mDataViewConverter.getViewWrapper(isRefreshableHead(),
				isRefreshableLoad());
	}

	/**
	 * 为视图设置adapter
	 * 
	 * @param adapter
	 */
	protected void setAdapter(ImageAdapter<DATA> adapter) {
		mDataViewConverter.setAdapter(adapter);
	}

	/**
	 * 创建数据视图，此方法在onCreateView时调用
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	private View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = mDataViewConverter.createView(inflater, container,
				savedInstanceState);
		mDataViewConverter.setOnItemClickListener(this);
		return view;
	}

	protected InfiniteBannerView getBannerView() {
		return mBannerView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		View dataView = getDataView();

		strUpdatedCount = getString(R.string.global_update_count);
		strUpdatedCountZero = getString(R.string.global_update_count_zero);
		setContainer((ViewGroup) dataView.getParent());

		if (dataView != null) {
			mAdapter = initAdapter();
			if (mAdapter != null) {
				mAdapter.setDataSource(mDataSource);
				setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
			}
			new ReadLastRefreshTimeTask().execute();
		}
		super.onViewCreated(view, savedInstanceState);
	}

	public void setStrUpdatedCount(String strUpdatedCount) {
		this.strUpdatedCount = strUpdatedCount;
	}

	public void setStrUpdatedCountZero(String strUpdatedCountZero) {
		this.strUpdatedCountZero = strUpdatedCountZero;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mViewWrapper != null) {
			outState.putBoolean(KEY_HIDE_LOADING_BAR,
					mViewWrapper.hasLoadingBar());
		}
		outState.putSerializable(KEY_DATA_SOURCE, mDataSource);
		outState.putSerializable(KEY_BANNER_DATA_SOURCE, mBannerDataSource);
		outState.putInt(KEY_VIEW_TYPE, mType);

		super.onSaveInstanceState(outState);
	}

	/**
	 * 数据视图进行了下拉刷新操作
	 */
	@Override
	public void onRefresh() {
		refreshData();
	}

	/**
	 * 数据视图进行了上拉加载操作
	 */
	@Override
	public void onLoading() {
		loadData();
	}

	/**
	 * 数据视图的滚动状态进行了改变
	 */
	@Override
	public void onScrollStateChanged(View view, int scrollState) {
		if ((scrollState == OnScrollListener.SCROLL_STATE_FLING || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)) {
			mAdapter.pause();
		} else {
			mAdapter.resume();
		}
		if ((scrollState == SCROLL_STATE_IDLE)) {
			if (mDataViewConverter.getFirstVisiblePosition() != 0) {
				// notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_END,
				// null);
			}
		}
	}

	/**
	 * 数据视图的onScroll回调
	 */
	@Override
	public void onScroll(View view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount, int direction) {
		int event = 0;
		Object params = null;

		switch (direction) {
		case RefreshListWrapper.DIRECTION_DOWN: {
			if (firstVisibleItem == 0) {
				// Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD;
			} else {
				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN;
			}
			break;
		}
		case RefreshListWrapper.DIRECTION_UP: {

			event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP;
			break;
		}
		}
		notifyListener(event, params);
	}

	/**
	 * empty view点击操作，默认行为加载数据
	 */
	protected void onEmptyViewClicked() {
		refreshData();
	}

	protected boolean hasData() {
		return mDataSource != null && mDataSource.size() != 0;
	}

	// 加载数据，将新数据加到list尾
	protected void loadData() {
		if (!hasData()) {
			showView(VIEW_TYPE_LOADING);
		}

		requestData(RefreshType._REFRESH_TYPE_LOAD_MORE);
	}

	/**
	 * 调用该方法进行刷新操作 1. 滚动到数据视图顶端，并展示“正在刷新”视图 2. 调用子类的刷新方法
	 */
	public void callRefresh() {
		if (getDataView() == null) {
			return;
		}
		mDataViewConverter.stopScroll();
		mDataViewConverter.setSelection(0);
		// mDataView.setSelection(0);
		// notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD,
		// null);
		if (mViewWrapper != null) {
			mViewWrapper.onRefreshing();
		}
		// refreshData();
	}

	/**
	 * 是否是用户请求刷新，该标记位用于显示刷新时间。如果是自动加载历史数据，则不更新刷新时间
	 */
	private boolean mIsUserRequest;

	protected void refreshData() {
		mIsUserRequest = true;
		if (!hasData()) {
			showView(VIEW_TYPE_LOADING);
		}
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
		StatsUtil.statsReport(mContext, "stats_refresh");
		StatsUtil.statsReportByMta(mContext, "stats_refresh", "刷新");
		StatsUtil.statsReportByHiido("stats_refresh", "");
	}

	protected abstract void requestData(int refreType);

	/**
	 * 初始化数据视图的adapter
	 * 
	 * @return
	 */
	protected abstract ImageAdapter<DATA> initAdapter();

	protected boolean isRefreshable() {
		return true;
	}

	protected boolean isRefreshableHead() {
		if (isRefreshable()) {
			return true;
		}
		return false;
	}

	protected boolean isRefreshableLoad() {
		if (isRefreshable()) {
			return true;
		}
		return false;
	}

	protected ImageAdapter<DATA> getAdapter() {
		return mAdapter;
	}

	/**
	 * 获取数据视图的adapter，对于某些实现，可能返回封装的adapter
	 * 如，dataview是listview，对listview添加了header，则返回的是HeaderFooterAdapter
	 * 如果需要通过onItemClick返回过来的position，从adapter里面获取数据，则必须使用该方法获取adapter
	 * 
	 * @return 数据视图真实的adapter
	 */
	protected Adapter getDataViewAdapter() {
		return mDataViewConverter.getAdapter();
	}

	/**
	 * 初始化banner的adapter
	 * 
	 * @return
	 */
	protected ImageAdapter<DATA> initBannerAdapter() {
		return null;
	}

	/**
	 * 当dataSource不为空时更新banner，如果datasource为空，会显示更新之前的数据
	 * 
	 * @param dataSource
	 */
	public void updateBannerIfNotEmpty(ArrayList<DATA> dataSource) {

		if (dataSource != null && dataSource.size() > 0) {
			if (mBannerView != null) {

				mBannerLayout.setVisibility(View.VISIBLE);
				updateBanner(dataSource);
			}
		}
	}

	/**
	 * 更新banner，如果数据为空，会隐藏banner
	 * 
	 * @param dataSource
	 */
	public void updateBanner(ArrayList<DATA> dataSource) {
		if (mBannerDataSource == null) {
			return;
		}
		if (mBannerView == null) {
			return;
		}
		if (mBannerAdapter == null) {
			return;
		}
		mBannerDataSource.clear();
		if (dataSource == null || dataSource.size() <= 0) {
			mBannerLayout.setVisibility(View.GONE);
		} else {
			mBannerLayout.setVisibility(View.VISIBLE);
			mBannerDataSource.addAll(dataSource);
		}
		if (mBannerAdapter != null) {
			mBannerAdapter.setDataSource(mBannerDataSource);
		}
	}

	/**
	 * 获取上次更新时间的key，视图会根据当前的key来获取上次更新的时间并展示 默认实现为
	 * {@link Constants#CACHE_KEY_LAST_REFRSH_TIME} + "_" + FULL_CLASS_NAME
	 * 
	 * @return
	 */
	protected String getLastRefreshTimeKey() {
		return Constants.CACHE_KEY_LAST_REFRSH_TIME + "_"
				+ getClass().getName(); // 每个页面有不同的key
	}

	private int mLastEvent; // 防止多余的刷新

	private Handler mUIHandler = new Handler();
	private static final int CLEAR_EVENT_DURATION = 2000;
	private Runnable mClearEventRunnable = new Runnable() {

		@Override
		public void run() {
			mLastEvent = -1;
		}
	};

	protected void notifyListener(int eventType, Object params) {

		// Log.d(LOG_TAG, "eventType = " + eventType + ", params = " + params);
		if (mLastEvent != eventType) {
			mLastEvent = eventType;

			// 自动清除event type，防止同一事件只能发送一次
			mUIHandler.postDelayed(mClearEventRunnable, CLEAR_EVENT_DURATION);
			FragmentCallbackEvent event = new FragmentCallbackEvent();
			event.mEventType = eventType;
			event.mParams = params;
			event.mTarget = getActivity();
			event.mFragment = this;
			EventBus.getDefault().post(event);
		}
	}

	protected boolean needShowUpdatedCount() {
		return true;
	}

	protected boolean needShowUpdatedBubble() {
		return true;
	}

	/**
	 * 获取数据完成，更新数据视图 </br> 一般情况下，如果是刷新，则将数据添加到原列表的头部，如果是加载，则添加到尾部 </br> </br>
	 * 另一种情况，如果设置了replace为true，则会用data代替原来的数据 </br> </br>
	 * 
	 * @param refresh
	 *            数据加载的方法，参考{@link RefreshType}
	 * @param data
	 *            新获取到的数据，要展示在数据视图（如listview）中的数据列表
	 * @param hasMore
	 *            是否还有数据，如果是false，则上拉加载功能被禁用
	 * @param replace
	 *            是否替换之前的数据
	 * @param error
	 *            是否加载出错
	 */
	protected void requestFinish(int refresh, ArrayList<DATA> data,
			boolean hasMore, boolean replace, boolean error) {

		BaseAdapter adapter = mAdapter;
		if (adapter == null) {
			return;
		}

		int updateCount = -1; // -1表示数据请求失败
		if (data != null) {
			// 如果是刷新，则添加到顶端

			updateCount = data.size();
			if (refresh == RefreshType._REFRESH_TYPE_REFRESH) {
				updateRefreshTime();
				if (replace) {
					mDataSource.clear();
				}
				mDataSource.addAll(0, data);
			} else {
				mDataSource.addAll(data);
			}
			adapter.notifyDataSetChanged();
		}

		if (mViewWrapper != null) {
			if (refresh == RefreshType._REFRESH_TYPE_REFRESH) {
				mViewWrapper.onRefreshComplete(updateCount);
			}
			if (error) {
				mViewWrapper.showErrorLoadingBar();
			} else {
				if (!hasMore) {
					mViewWrapper.showNoMoreLoadingBar();
				} else {
					mViewWrapper.showLoadingBar();
				}
			}

		} else {
			if (refresh == RefreshType._REFRESH_TYPE_REFRESH
					&& needShowUpdatedCount()) {

				showUpdatedToast(updateCount);
			}
		}

		checkShowEmptyView();
	}

	// private Animation mAlphaAnimOut;
	private Animation mAnimationBubble;

	protected void showUpdatedToast(int count) {

		if (count < 0) {
			return;
		}
		showUpdateTv(count);
		if (needShowUpdatedBubble()) {
			if (count != 0) {
				showUpdateBubble(count);
			}
		}
	}

	private void showUpdateBubble(int count) {
		String toastString = "+" + count;
		mUpdateCountBubble.setText(toastString);
		if (mAnimationBubble == null) {
			mAnimationBubble = getBubbleAnimation();
			mAnimationBubble.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					mUpdateCountBubble.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mUpdateCountBubble.setVisibility(View.INVISIBLE);
				}
			});
		}

		mUpdateCountBubble.startAnimation(mAnimationBubble);
	}

	private Animation getBubbleAnimation() {
		AnimationSet animSet = new AnimationSet(false);

		int screenWidth = Util.getAppWidth();
		int screenHeight = Util.getAppHeight();
		int viewHeight = mUpdateCountBubble.getHeight();

		int duration = 2000;

		Animation anim = new TranslateAnimation(0, 0, 0,
				-(screenHeight / 3 - viewHeight / 2));
		anim.setFillAfter(true);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(duration);
		anim.setRepeatCount(0);

		animSet.addAnimation(anim);

		anim = new TranslateAnimation(0, (float) (screenWidth / 5), 0, 0);
		anim.setFillAfter(true);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(duration / 2);
		anim.setRepeatCount(0);

		animSet.addAnimation(anim);

		anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(duration / 4);
		anim.setRepeatCount(0);
		anim.setFillAfter(true);
		animSet.addAnimation(anim);

		int alphaAnimDuration = duration / 5;
		anim = new AlphaAnimation(0.0f, 1f);
		anim.setDuration(alphaAnimDuration);
		anim.setInterpolator(new LinearInterpolator());
		anim.setFillAfter(true);

		anim = new AlphaAnimation(1f, 0.0f);
		anim.setDuration(alphaAnimDuration);
		anim.setInterpolator(new LinearInterpolator());
		anim.setStartOffset(duration - alphaAnimDuration);
		anim.setFillAfter(true);

		animSet.addAnimation(anim);

		return animSet;
	}

	// private Animation mAlphaAnimOut;
	private Animation mAlphaAnimIn;

	protected void showUpdateTv(int count) {

		String toastString = "";

		if (count > 0) {
			toastString = String.format(strUpdatedCount, count);
		} else {
			toastString = strUpdatedCountZero;
		}

		mUpdateCountTv.setText(toastString);
		// mUpdateCountTv.setAlpha(1.0f);
		if (mAlphaAnimIn == null) {
			mAlphaAnimIn = AnimationUtils.loadAnimation(mContext,
					R.anim.article_list_updated_fadein);
		}

		mUpdateCountTv.setVisibility(View.VISIBLE);
		mUpdateCountTv.setAnimation(mAlphaAnimIn);

		mAlphaAnimIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mUpdateCountTv.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mUpdateCountTv.setVisibility(View.INVISIBLE);
			}
		});
		mAlphaAnimIn.startNow();
	}

	protected void checkShowEmptyView() {
		if (hasData()) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
	}

	private class ReadLastRefreshTimeTask extends
			BackgroundTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... params) {
			String timeString = mPageCache.get(getLastRefreshTimeKey());
			Long time = 0l;
			if (timeString != null && !"".equals(timeString)) {
				time = Long.parseLong(timeString);
			} else {
				time = System.currentTimeMillis();
			}
			return time;
		}

		@Override
		protected void onPostExecute(Long time) {
			setRefreshTime(time);
			super.onPostExecute(time);
		}
	}

	private void setRefreshTime(long time) {
		if (mViewWrapper == null) {
			return;
		}
		mViewWrapper.setRefreshTime(time);
	}

	private void updateRefreshTime() {
		if (!mIsUserRequest) {
			return;
		}
		mIsUserRequest = false;
		long time = System.currentTimeMillis();
		new WriteLastRefreshTimeTask().execute(time);
		setRefreshTime(time);
	}

	private class WriteLastRefreshTimeTask extends
			BackgroundTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			Long time = params[0];
			String timeString = String.valueOf(time);
			mPageCache.set(getLastRefreshTimeKey(), timeString,
					Constants.CACHE_DURATION_FOREVER);
			return null;
		}
	}

	/**
	 * 数据视图点击事件，默认不处理
	 */
	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		// For child implementation
	}

	/**
	 * Banner点击事件，默认不处理
	 */
	@Override
	public void onBannerItemClick(View view, ListAdapter adapter, int position) {
		// For child implementation

	}

	@Override
	public void onComplete(int updateCount) {
		if (needShowUpdatedCount()) {
			showUpdatedToast(updateCount);
		}

		notifyListener(FragmentCallbackEvent.FRGMT_LIST_REFRESH_DONE, null);
	}

	protected boolean isExpire() {
		return false;
	}

	public final void checkExpire() {
		if (isExpire()) {
			callRefresh();
		}
	}

}