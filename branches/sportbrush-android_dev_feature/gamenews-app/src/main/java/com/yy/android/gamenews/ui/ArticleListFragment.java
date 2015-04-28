package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;

import com.duowan.Comm.ECommAppType;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.RefreshType;
import com.duowan.taf.jce.JceStruct;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.CheckExpireEvent;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.LikeEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.jcewrapper.GetArticleListRspLocal;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.ArticleDetailSwitcher;
import com.yy.android.gamenews.util.AsyncIPageCache;
import com.yy.android.gamenews.util.AsyncIPageCache.OnCacheListener;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

/**
 * 文章列表Fragment，此fragment的数据视图展示ArticleInfo的列表
 * 
 * 对以下事件进行了封装： 1. 数据视图点击事件 2. 检查数据过期 3. 对列表中已查看过的数据进行区别化（变灰） 4. 缓存数据的读取及保存
 * 
 * @author Administrator
 * 
 * @param <RSP>
 */
@SuppressLint("ValidFragment")
public abstract class ArticleListFragment<RSP extends JceStruct, WRAPPER extends GetArticleListRspLocal<RSP>>
		extends BaseListFragment<ArticleInfo> {
	// private static final String TAG =
	// ArticleListFragment.class.getSimpleName();
	private static final String KEY_VIEWED_LIST = "viewed_list";
	private static final String KEY_RESPONSE = "response";
	protected static final String KEY_CACHE_SIZE = "cache_size";

	protected IPageCache mPageCache;
	private ArticleListAdapter mAdapter;
	private boolean mIsDarkTheme;

	private ArrayList<Long> mViewedList = new ArrayList<Long>();
	private WRAPPER mRspWrapper;
	private boolean mIsLoading;
	private int mCacheSize;
	protected Preference mPref;

	public static final String LOG_TAG = "ArticleListFragment";

	public ArticleListFragment() {
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		EventBus.getDefault().register(this);
		mRspWrapper = initRspWrapper();
		if (bundle != null) {
			mViewedList = (ArrayList<Long>) bundle
					.getSerializable(KEY_VIEWED_LIST);
			if (mViewedList == null) {
				mViewedList = new ArrayList<Long>();
			}

			mRspWrapper.setObject((RSP) bundle.getSerializable(KEY_RESPONSE));
			mCacheSize = bundle.getInt(KEY_CACHE_SIZE);
		} else {
			Bundle args = getArguments();
			if (args != null) {
				mCacheSize = args.getInt(KEY_CACHE_SIZE);
			}
		}
		if (mCacheSize == 0) {
			mCacheSize = Constants.CACH_SIZE_HOME_ARTI_LIST;
		}

	}

	protected abstract WRAPPER initRspWrapper();

	protected RSP getRspInstance() {
		if (mRspWrapper != null) {
			return mRspWrapper.getObject();
		}
		return null;
	}

	public void setIsDarkTheme(boolean isDark) {
		mIsDarkTheme = isDark;
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected ImageAdapter<ArticleInfo> initAdapter() {
		if (mAdapter == null) {
			mAdapter = new ArticleListAdapter(getActivity());
			// mAdapter.setChannel(mChannel);
			if (needShowViewedArticle()) {
				mAdapter.setViewedArticleList(mViewedList);
			}
			mAdapter.setIsDarkTheme(mIsDarkTheme);
		}
		return mAdapter;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_VIEWED_LIST, mViewedList);
		if (mRspWrapper != null) {
			outState.putSerializable(KEY_RESPONSE, mRspWrapper.getObject());
		}
		outState.putInt(KEY_CACHE_SIZE, mCacheSize);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mClickListener = new ArticleClickHandler(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		if (mRspWrapper.getObject() == null && needLoadData()) { // 只有在第一次进入界面加载，避免因被系统杀掉而再次加载
			showView(VIEW_TYPE_LOADING);
			new BgTask().execute();
		}
		if (mIsDarkTheme) {
			getDataView().setBackgroundResource(
					R.color.global_list_item_bg_dark);
		} else if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
			getDataView().setBackgroundResource(
					R.color.global_waterfall_list_bg);
		}
		super.onViewCreated(view, savedInstanceState);
		Log.d(LOG_TAG, "onViewCreated");
	}

	@Override
	public void onDestroyView() {
		setAdapter(null);
		mClickListener = null;
		super.onDestroyView();
	}

	@Override
	protected ImageAdapter<ArticleInfo> initBannerAdapter() {
		return new ArticleListBannerAdapter(getActivity());
	}

	@Override
	public void onResume() {

		// checkInfoUpdate();
		checkViewedListChanged();
		super.onResume();
	}

	private void checkViewedListChanged() {
		AsyncIPageCache.getInstance().readAsync(
				Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, null, false,
				new OnCacheListener<List<Long>>() {

					@Override
					public void onRead(List<Long> idList) {
						if (idList != null && !idList.equals(mViewedList)) {
							mViewedList.clear();
							mViewedList.addAll(idList);
							mAdapter.setViewedArticleList(mViewedList);
							mAdapter.notifyDataSetChanged();
						}
					}

					@Override
					public void onWrite() {

					}
				});
	}

	/**
	 * 评论更新
	 * 
	 * @param mCommentEvent
	 */
	private void commentCountChange(CommentEvent mCommentEvent) {
		if (mCommentEvent == null) {
			return;
		}
		Adapter adapter = getDataViewAdapter();
		if (mSelectedPos == -1) {
			long id = mCommentEvent.id;
			for (int i = 0; i < adapter.getCount(); i++) {
				ArticleInfo model = (ArticleInfo) adapter.getItem(i);
				if (model != null && model.getId() == id) {
					mSelectedPos = i;
					break;
				}
			}
		}

		if (mSelectedPos != -1) {
			if (adapter != null) {
				ArticleInfo model = (ArticleInfo) adapter.getItem(mSelectedPos);

				boolean hasChanged = false;
				int commentCount = mCommentEvent.commentCount;
				if (commentCount == CommentEvent.CMT_CNT_ADD) {
					commentCount = model.getCommentCount() + 1;
				}

				if (commentCount != model.commentCount) {

					model.setCommentCount(commentCount);
					hasChanged = true;
				}

				if (hasChanged) {
					mAdapter.notifyDataSetChanged();
					saveListToDisk(getDataSource());
				}
			}
			mSelectedPos = -1;
		}
	}

	/**
	 * 点赞更新
	 * 
	 * @param mLikeEvent
	 */
	private void likeCountChange(LikeEvent mLikeEvent) {
		if (mLikeEvent == null) {
			return;
		}
		Adapter adapter = getDataViewAdapter();
		if (mSelectedPos == -1) {
			long id = mLikeEvent.id;
			for (int i = 0; i < adapter.getCount(); i++) {
				ArticleInfo model = (ArticleInfo) adapter.getItem(i);
				if (model != null && model.getId() == id) {
					mSelectedPos = i;
					break;
				}
			}
		}

		if (mSelectedPos != -1) {
			if (adapter != null) {
				ArticleInfo model = (ArticleInfo) adapter.getItem(mSelectedPos);

				boolean hasChanged = false;
				int likeCount = mLikeEvent.likeCount;

				if (likeCount != model.praiseCount) {
					model.setPraiseCount(likeCount);
					hasChanged = true;
				}

				if (hasChanged) {
					mAdapter.notifyDataSetChanged();
					saveListToDisk(getDataSource());
				}
			}
			mSelectedPos = -1;
		}
	}

	/**
	 * 检查评论数，点赞数等是否有更新，若有更新，则刷新adapter private void checkInfoUpdate() {
	 * 
	 * Adapter adapter = getDataViewAdapter(); if (mSelectedPos == -1 &&
	 * (mCommentEvent != null || mLikeEvent != null)) { long id = 0; if
	 * (mCommentEvent != null) { id = mCommentEvent.id; } else if (mLikeEvent !=
	 * null) {
	 * 
	 * id = mLikeEvent.id; } for (int i = 0; i < adapter.getCount(); i++) {
	 * ArticleInfo model = (ArticleInfo) adapter.getItem(i); if (model != null
	 * && model.getId() == id) { mSelectedPos = i; break; } } }
	 * 
	 * if (mSelectedPos != -1) { if (adapter != null) { ArticleInfo model =
	 * (ArticleInfo) adapter.getItem(mSelectedPos);
	 * 
	 * boolean hasChanged = false; int commentCount = model.commentCount; int
	 * likeCount = model.praiseCount;
	 * 
	 * if (mCommentEvent != null) { commentCount = mCommentEvent.commentCount;
	 * if (commentCount == CommentEvent.CMT_CNT_ADD) { commentCount =
	 * model.getCommentCount() + 1; } }
	 * 
	 * if (mLikeEvent != null) { likeCount = mLikeEvent.likeCount; }
	 * 
	 * if (commentCount != model.commentCount) {
	 * 
	 * model.setCommentCount(commentCount); hasChanged = true; }
	 * 
	 * if (likeCount != model.praiseCount) {
	 * 
	 * model.setPraiseCount(likeCount); hasChanged = true; }
	 * 
	 * if (hasChanged) { mAdapter.notifyDataSetChanged();
	 * saveListToDisk(getDataSource()); } }
	 * 
	 * mCommentEvent = null; mLikeEvent = null; mSelectedPos = -1; } }
	 */

	public void onEvent(CommentEvent event) {
		commentCountChange(event);
	}

	public void onEvent(LikeEvent event) {
		likeCountChange(event);
	}

	public void onEvent(RefreshEvent event) {
		if (event == null) {
			return;
		}
		if (needCheckRefresh(event)) {
			callRefresh();
		}
	}

	public void onEvent(CheckExpireEvent event) {
		if (event == null) {
			return;
		}

		if (needCheckExpire(event)) {
			checkExpire();
		}
	}

	@Override
	public void onScrollStateChanged(View view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);

	}

	private int mSelectedPos = -1;

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		ArticleDetailSwitcher.getInstance().setArticleInfos(getDataSource());
		ArticleInfo model = (ArticleInfo) adapter.getItem(position);
		onItemClick(model, position, TYPE_LIST);
		super.onItemClick(parent, adapter, view, position, id);
	}

	private static final int TYPE_BANNER = 1;
	private static final int TYPE_LIST = 2;

	private ArticleClickHandler mClickListener;

	protected void onItemClick(final ArticleInfo model, int position, int type) {

		if (model != null) {
			if (TYPE_LIST == type) {
				mSelectedPos = position;
			}
			if (mClickListener != null) {
				mClickListener.onArticleItemClick(model);
			}
		}
	}

	@Override
	public void onBannerItemClick(View view, ListAdapter adapter, int position) {
		ArticleListBannerAdapter bannerAdapter = (ArticleListBannerAdapter) adapter;
		if (bannerAdapter != null) {
			ArticleInfo info = bannerAdapter.getItem(position);
			onItemClick(info, position, TYPE_BANNER);
		}

		super.onBannerItemClick(view, bannerAdapter, position);
	}

	protected abstract void requestDataImpl(int refresh, Object attachInfo);

	protected final void requestData(final int refresh) {
		if (mIsLoading) {
			return;
		}
		if (refresh == RefreshType._REFRESH_TYPE_REFRESH) {
			notifyListener(FragmentCallbackEvent.FRGMT_LIST_REFRESHING, null);
		}

		mIsLoading = true;

		Object attachInfo = null;
		if (mRspWrapper != null) {
			attachInfo = mRspWrapper.getAttachInfo();
		}
		requestDataImpl(refresh, attachInfo);
	}

	private void prepareEmptyText(boolean error) {
		String emptyText = getEmptyText(error);

		if (TextUtils.isEmpty(emptyText)) {
			if (error) {
				emptyText = strEmptyReload;
			} else {
				emptyText = strEmptyNoData;
			}
		}
		setEmptyText(emptyText);
	}

	protected String getEmptyText(boolean error) {
		return "";
	}

	protected boolean needShowViewedArticle() {
		return true;
	}

	protected void requestFinishImpl(int refresh, RSP data, boolean error) {
		ArrayList<ArticleInfo> dataList = null;
		ArrayList<ArticleInfo> localList = getDataSource(); // 本地列表
		boolean hasMore = false;
		if (data != null) {

			if (mRspWrapper != null) {
				hasMore = mRspWrapper.hasMore();
				dataList = mRspWrapper.getArticleList();
			}

			/**
			 * 去重
			 */
			if (dataList != null && localList != null) {
				for (int i = 0; i < dataList.size(); i++) {
					ArticleInfo dataInfo = dataList.get(i);
					for (int j = 0; j < localList.size(); j++) {
						ArticleInfo localInfo = localList.get(j);
						if (localInfo.getId() == dataInfo.getId()) {

							// 如果有重复，用新拉下来的替换老数据，位置不变
							localList.remove(j);
							localList.add(j, dataInfo);
							dataList.remove(i); // 从新列表中删除
							i--;
							break;
						}
					}
				}
			}

			// 只有下拉刷新的时候需要更新banner
			if (refresh == RefreshType._REFRESH_TYPE_LOAD_MORE) {

				if (mRspWrapper != null) {
					mRspWrapper.setPictopList(getBannerDataSource());
				}
			} else {
				updateBanner(mRspWrapper == null ? null : mRspWrapper
						.getPictopList());
				// updateHotChannelView(mRsp.getHotChannel(), dataList);
			}
		}

		requestFinish(refresh, dataList, hasMore, false, error);

		Log.d(LOG_TAG, "requestFinish");

		/**
		 * 需求：下拉刷新后，列表中展示最新的CACH_SIZE_HOME_ARTI_LIST篇文章
		 */
		if (refresh == RefreshType._REFRESH_TYPE_REFRESH) {
			ArrayList<ArticleInfo> dataSource = getDataSource();
			if (dataSource != null) {
				int size = dataSource.size();
				if (size > mCacheSize) {
					ArrayList<ArticleInfo> replaceList = new ArrayList<ArticleInfo>();
					replaceList.addAll(dataSource);

					dataSource.clear();
					dataSource.addAll(replaceList.subList(0, mCacheSize));

					getAdapter().notifyDataSetChanged();
				}
			}
		}
		// 保存到本地
		saveListToDisk(getDataSource());
	}

	protected final void requestFinish(int refresh, RSP data, boolean error) {

		prepareEmptyText(error);

		mIsLoading = false;

		if (!error) {
			mRspWrapper.setObject(data);
		}
		requestFinishImpl(refresh, data, error);
	}

	protected SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	public class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {

			String key = (String) params[0];
			Object value = params[1];
			int duration = (Integer) params[2];
			boolean isJceObject = (Boolean) params[3];

			if (isJceObject) {
				mPageCache.setJceObject(key, value, duration);
			} else {
				mPageCache.setObject(key, value, duration);
			}
			return null;
		}
	}

	protected boolean needSaveToDisk() {
		return true;
	}

	private void saveListToDisk(ArrayList<ArticleInfo> list) {

		if (!needSaveToDisk()) {
			return;
		}
		ArrayList<ArticleInfo> savedList = new ArrayList<ArticleInfo>();
		ArrayList<ArticleInfo> totalList = list;
		if (totalList == null || mRspWrapper == null
				|| mRspWrapper.getObject() == null) {
			return;
		}
		if (totalList.size() > mCacheSize && mCacheSize > 0) {
			savedList.addAll(totalList.subList(0, mCacheSize));
		} else {
			savedList.addAll(totalList);
		}
		if (savedList.size() > 0) {
			mRspWrapper.setArticleList(savedList);
		}

		saveResponseToDisk();
	}

	protected void saveResponseToDisk() {
		if (mRspWrapper == null) {
			return;
		}
		RSP savedRsp = mRspWrapper.clone();
		String key = getCacheKey();
		mSaveCacheTask.execute(key, savedRsp, getDuration(), true);
	}

	protected abstract RSP newRspObject();

	protected RSP getResponseFromDisk() {
		RSP rsp = mPageCache.getJceObject(getCacheKey(), newRspObject());
		return rsp;
	}

	protected boolean needLoadData() {
		return true;
	}

	protected boolean needCheckRefresh(RefreshEvent event) {
		return false;
	}

	protected boolean needCheckExpire(CheckExpireEvent event) {
		return false;
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(LOG_TAG, "doInBackground+");
			mRspWrapper.setObject(getResponseFromDisk());
			List<Long> idList = mPageCache
					.getObject(Constants.CACHE_KEY_VIEWED_ARTICLE_LIST);
			if (idList != null) {

				if (mViewedList != null) {
					mViewedList.addAll(idList);
				}
			}
			Log.d(LOG_TAG, "doInBackground-");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {

			Log.d(LOG_TAG, "onPostExecute+");
			if (isDetached()) {
				return;
			}
			ArrayList<ArticleInfo> list = null;

			if (mRspWrapper != null) {
				list = mRspWrapper.getArticleList();// getDataFromRsp(mRsp.getObject());
			}

			if ((list == null || list.size() == 0) && needReload) {
				refreshData();
			} else {
				requestFinish(0, mRspWrapper.getObject(), false);
			}

			checkExpire();
			super.onPostExecute(needReload);
			Log.d(LOG_TAG, "onPostExecute-");
		}
	}

	/**
	 * @return 将数据保存至本地cache的关键字，默认返回
	 *         {@link Constants#CACHE_KEY_ARTICLE_LIST_DEFAULT} + "_" +
	 *         FULL_CLASS_NAME
	 */
	protected String getCacheKey() {
		return Constants.CACHE_KEY_ARTICLE_LIST_DEFAULT + "_"
				+ getClass().getName();
	}

	/**
	 * 
	 * @return 将数据保存至本地cache的过期时长，默认返回 {@link Constants#CACHE_DURATION_HOMELIST}
	 */
	protected int getDuration() {
		return Constants.CACHE_DURATION_HOMELIST;
	}
}
