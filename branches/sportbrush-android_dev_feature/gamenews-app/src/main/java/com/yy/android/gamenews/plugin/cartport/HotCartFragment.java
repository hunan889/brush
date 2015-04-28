package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.autonews.CarListInfo;
import com.duowan.autonews.GetHotCarListRsp;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

public class HotCartFragment extends BaseListFragment<CarListInfo> {
	private static final int COUNT = 20;
	protected IPageCache mPageCache;
	protected Preference mPref;
	private FragmentActivity mActivity;
	private GridView mGridView;
	private HotCartAdapter mHotCartAdapter;
	private GetHotCarListRsp mRspCache;
	private GetHotCarListRsp mRspNet;
	private boolean mFirstLoad;

	public HotCartFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_GRIDVIEW);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		new BgTask().execute();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected boolean isRefreshableHead() {
		return false;
	}

	@Override
	protected boolean isRefreshableLoad() {
		return true;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView) getDataView();
		if (mGridView != null) {
			mGridView.setBackgroundColor(0xff161719);
			mGridView.setNumColumns(2);
			mGridView.setHorizontalSpacing(5);
			mGridView.setVerticalSpacing(5);
			mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			mGridView.setSelector(R.drawable.hot_cart_bg);
		}
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mRspCache != null) {
			mRspCache = null;
		}
		if (mRspNet != null) {
			mRspNet = null;
		}
	}

	@Override
	protected void requestData(final int refreType) {
	
		if (mHotCartAdapter != null && mHotCartAdapter.getDataSource() != null) {
			ArrayList<CarListInfo> dataSource = mHotCartAdapter.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}

		String attachInfo = "";

		if (mRspNet != null) {
			attachInfo = mRspNet.getAttachInfo();
		}
		if (Util.isNetworkConnected() && mFirstLoad) {
			if (mRspNet != null && mRspNet.getHasMore() == 0) {
				ToastUtil.showToast(R.string.global_last_pager);
				return;
			} else {
				ToastUtil.showToast(R.string.global_list_loading);
			}
		}
		HotCartModel.getHotcartList(new ResponseListener<GetHotCarListRsp>(
				mActivity) {

			@Override
			public void onResponse(GetHotCarListRsp paramT) {
				mRspNet = paramT;
				if (paramT != null && paramT.getHotCarList() != null) {
					saveListToDisk(paramT);
					mFirstLoad = true;
					ArrayList<CarListInfo> hotCarList = paramT.getHotCarList();
					boolean replace = false;
					boolean hasMore = true;
					if (refreType == RefreshType._REFRESH_TYPE_REFRESH) {
						replace = true;
					}
					requestFinish(refreType, hotCarList, hasMore, replace, false);
				}

			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				requestFinish(refreType, null, false, false, false);
			}
		}, COUNT, attachInfo);

	}

	@Override
	protected synchronized void requestFinish(int refresh,
			ArrayList<CarListInfo> data, boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
		if (data != null && data.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<CarListInfo> dataSource = mHotCartAdapter.getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	@Override
	protected ImageAdapter<CarListInfo> initAdapter() {
		mHotCartAdapter = new HotCartAdapter(getActivity());
		return mHotCartAdapter;
	}

	protected void saveListToDisk(GetHotCarListRsp param) {
		String key = Constants.CACHE_KEY_HOT_CART_LIST;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION,
				true);
	}

	protected GetHotCarListRsp getResponseFromDisk() {
		GetHotCarListRsp rsp = null;
		synchronized (mPageCache) {
			rsp = mPageCache.getJceObject(Constants.CACHE_KEY_HOT_CART_LIST,
					new GetHotCarListRsp());
		}
		return rsp;
	}

	protected SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {

			String key = (String) params[0];
			Object value = params[1];
			int duration = (Integer) params[2];
			boolean isJceObject = (Boolean) params[3];
			synchronized (mPageCache) {
				if (isJceObject) {
					mPageCache.setJceObject(key, value, duration);
				} else {
					mPageCache.setObject(key, value, duration);
				}
			}
			return null;
		}
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {
		@Override
		public void execute(Void... params) {
			super.execute(params);
			showView(VIEW_TYPE_LOADING);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			mRspCache = getResponseFromDisk();
			if (mRspCache != null) {
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {

			if (!needReload) {
				if (Util.isNetworkConnected()) {
					showView(VIEW_TYPE_LOADING);
				} else {
					showView(VIEW_TYPE_EMPTY);
				}
				return;
			}

			showView(VIEW_TYPE_DATA);
			requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
					mRspCache.getHotCarList(), false, false, false);

			super.onPostExecute(needReload);
		}
	}

}
