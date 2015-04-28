package com.yy.android.gamenews.plugin.distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetStoreAppListRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.StoreAppInfo;
import com.duowan.gamenews.StoreAppStatus;
import com.duowan.gamenews.StoreServiceType;
import com.yy.android.gamenews.event.DistributionAppEvent;
import com.yy.android.gamenews.model.StoreAppModel;
import com.yy.android.gamenews.plugin.distribution.DistributionListAdapter.DownloadListener;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class DistributionListFragment extends BaseListFragment<StoreAppInfo> {

	private Preference mPref;
	private GetStoreAppListRsp mRsp;
	private List<StoreAppInfo> queueStoreAppInfos;
	private View headerView;
	private DistributionListAdapter distributionListAdapter;

	public DistributionListFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}

	private DownloadListener downloadListener = new DownloadListener() {

		@Override
		public void startDownload(int key, String title, String url,
				String fileName) {
			((DistributionListActivity) getActivity()).startDownload(key,
					title, url, fileName);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);

		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDataView().setBackgroundResource(R.color.global_waterfall_list_bg);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
	}

	public void setDownloadQueue(List<StoreAppInfo> storeAppInfos) {
		this.queueStoreAppInfos = storeAppInfos;
		new LoadCacheStoreAppTask().execute();
	}

	public void onEventMainThread(DistributionAppEvent distributionAppEvent) {
		if (distributionAppEvent != null) {
			Log.d("onEventMainThread", "install app:"
					+ distributionAppEvent.getInstallAppMap().toString()
					+ " needRefresh:" + distributionAppEvent.isNeedRefresh());
			boolean needRefresh = distributionAppEvent.isNeedRefresh();
			if (needRefresh) {
				refreshData();
			} else {
				Map<Integer, Integer> instassedAppMap = distributionAppEvent
						.getInstallAppMap();
				if (instassedAppMap != null) {
					for (Entry<Integer, Integer> entry : instassedAppMap
							.entrySet()) {
						setStoreAppStatusByKey(entry.getKey(),
								StoreAppStatus._STORE_APP_HAS_REWARD);
						updateQueueStoreAppStatus(entry.getKey());
					}
				}
			}
		}
	}

	public void prepareDownload(int key, boolean isPending) {
		Log.d("prepareDownload", "key:" + key + "isPending:" + isPending);
		if (isPending) {
			setStoreAppStatusByKey(key, StoreAppStatus._STORE_APP_PENDING);
		}
	}

	public void startDownload(int key) {
		setStoreAppStatusByKey(key, StoreAppStatus._STORE_APP_DOWNLOADING);
	}

	public void finishDownload(int key, boolean isSuccess) {
		setStoreAppStatusByKey(key,
				isSuccess ? StoreAppStatus._STORE_APP_HAS_DOWNLOAD
						: StoreAppStatus._STORE_APP_NOT_INSTALL);
		updateQueueStoreAppStatus(key);
	}

	private void setStoreAppStatusByKey(int key, int status) {
		Log.d("setStoreAppStatusByKey", "key:" + key + " set status:" + status);
		ArrayList<StoreAppInfo> dataSource = getDataSource();
		if (dataSource != null) {
			for (StoreAppInfo storeAppInfo : dataSource) {
				if (storeAppInfo.getId() == key) {
					storeAppInfo.setStatus(status);
					getAdapter().notifyDataSetChanged();
					mRsp.setAppList(getDataSource());
					cacheStoreAppList(mRsp);
					break;
				}
			}
		}
	}

	private void updateQueueStoreAppStatus(int key) {
		if (queueStoreAppInfos != null) {
			for (StoreAppInfo storeAppInfo : queueStoreAppInfos) {
				if (storeAppInfo.getId() == key) {
					queueStoreAppInfos.remove(storeAppInfo);
					break;
				}
			}
		}
	}

	@Override
	protected RefreshableViewWrapper<?> getViewWrapper() {
		headerView = mInflater.inflate(R.layout.store_app_header_view, null);
		return mDataViewConverter.getViewWrapper(headerView);
	}

	@Override
	protected void requestData(final int refreType) {
		if (getDataSource() != null) {
			ArrayList<StoreAppInfo> dataSource = getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}

		String attachInfo = null;
		if (mRsp != null && refreType == RefreshType._REFRESH_TYPE_LOAD_MORE) {
			attachInfo = mRsp.getAttachInfo();
		}
		StoreAppModel.getStoreAppList(new ResponseListener<GetStoreAppListRsp>(
				getActivity()) {

			@Override
			public void onResponse(GetStoreAppListRsp param) {
				mRsp = param;
				if (param != null && param.getAppList() != null
						&& !param.getAppList().isEmpty()) {

					String msg = param.getAlertMsg();
					boolean isYYLogin = (msg == null || msg.equals(""));
					distributionListAdapter.setYYLogin(isYYLogin);

					syncAppStatus(param.getAppList());
					requestFinish(refreType, param.getAppList(), param.hasMore,
							true, false);

					if (refreType != RefreshType._REFRESH_TYPE_LOAD_MORE) {
						distributionListAdapter.cleanUpInstalledMap();
						initHeaderView(mRsp.getMyTdou());
					}
					if (!isYYLogin) {
						ToastUtil.showToast(msg);
					}

					mRsp.setAppList(getDataSource());
					cacheStoreAppList(mRsp);
				} else {
					requestFinish(refreType, null, false, false, false);
				}
			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				requestFinish(refreType, null, false, false, true);
			}

		}, attachInfo, StoreServiceType._STORE_SERVICE_GETTDOU_TYPE);
	}

	@Override
	protected void requestFinish(int refresh,
			ArrayList<StoreAppInfo> sourceList, boolean hasMore,
			boolean replace, boolean error) {

		super.requestFinish(refresh, sourceList, hasMore, replace, error);

		if (sourceList != null && sourceList.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<StoreAppInfo> dataSource = getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	private void syncAppStatus(ArrayList<StoreAppInfo> sourceList) {

		// 根据下载队列的数据修改下载状态
		if (sourceList != null) {

			ArrayList<StoreAppInfo> localList = getDataSource();
			if (localList != null) {
				for (StoreAppInfo storeAppInfo : sourceList) {
					for (StoreAppInfo localStoreAppInfo : localList) {
						if (localStoreAppInfo.getId() == storeAppInfo.getId()) {
							storeAppInfo.setStatus(localStoreAppInfo
									.getStatus());
							break;
						}
					}
				}
			}

			if (queueStoreAppInfos != null && queueStoreAppInfos.size() > 0) {
				for (StoreAppInfo storeAppInfo : sourceList) {
					boolean isInQueue = false;
					for (StoreAppInfo queueStoreAppInfo : queueStoreAppInfos) {
						if (queueStoreAppInfo.getId() == storeAppInfo.getId()) {
							storeAppInfo.setStatus(queueStoreAppInfo
									.getStatus());
							isInQueue = true;
							break;
						}
					}
					if (!isInQueue
							&& (storeAppInfo.getStatus() == StoreAppStatus._STORE_APP_PENDING || storeAppInfo
									.getStatus() == StoreAppStatus._STORE_APP_DOWNLOADING)) {
						storeAppInfo
								.setStatus(StoreAppStatus._STORE_APP_NOT_INSTALL);
					}
				}
			} else {
				for (StoreAppInfo storeAppInfo : sourceList) {
					if (storeAppInfo.getStatus() == StoreAppStatus._STORE_APP_PENDING
							|| storeAppInfo.getStatus() == StoreAppStatus._STORE_APP_DOWNLOADING) {
						storeAppInfo
								.setStatus(StoreAppStatus._STORE_APP_NOT_INSTALL);
					}
				}
			}
		}
	}

	private void initHeaderView(int tdou) {
		((TextView) headerView.findViewById(R.id.tv_my_tdou)).setText(String
				.valueOf(tdou));
	}

	@Override
	protected ImageAdapter<StoreAppInfo> initAdapter() {
		distributionListAdapter = new DistributionListAdapter(getActivity());
		distributionListAdapter.setDownloadListener(downloadListener);
		return distributionListAdapter;
	}

	protected void cacheStoreAppList(GetStoreAppListRsp param) {
		cacheStoreAppListTask.execute(param);
	}

	protected GetStoreAppListRsp loadStoreAppList() {
		GetStoreAppListRsp rsp = mPref.getStoreAppListRsp();
		return rsp;
	}

	protected CacheStoreAppListTask cacheStoreAppListTask = new CacheStoreAppListTask();

	class CacheStoreAppListTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			Object value = params[0];
			mPref.saveStoreAppListRsp((GetStoreAppListRsp) value);
			return null;
		}
	}

	private class LoadCacheStoreAppTask extends
			BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = loadStoreAppList();
			if (mRsp != null && mRsp.getAppList() != null) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean hasData) {

			if (!hasData) {
				if (Util.isNetworkConnected()) {
					showView(VIEW_TYPE_LOADING);
				} else {
					showView(VIEW_TYPE_EMPTY);
				}
			} else {
				requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
						mRsp.getAppList(), false, true, false);

				if (mRsp != null) {
					initHeaderView(mRsp.getMyTdou());
				}
			}
			requestData(RefreshType._REFRESH_TYPE_REFRESH);
			super.onPostExecute(hasData);
		}
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	public void setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
		if (distributionListAdapter != null) {
			distributionListAdapter.setDownloadListener(downloadListener);
		}
	}
}
