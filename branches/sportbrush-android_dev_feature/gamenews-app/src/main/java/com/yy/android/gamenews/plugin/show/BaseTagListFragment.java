package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.RefreshType;
import com.duowan.show.GetTagListRsp;
import com.duowan.show.Tag;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

public abstract class BaseTagListFragment<E> extends BaseListFragment<Tag> {

	private Preference mPref;
	private GetTagListRsp mRsp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);

		new LoadCacheTagTask().execute();

		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDataView().setBackgroundResource(R.color.list_bg);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void requestData(final int refreType) {
		if (getDataSource() != null) {
			ArrayList<Tag> dataSource = getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}

		ShowModel
				.getTagList(new ResponseListener<GetTagListRsp>(getActivity()) {

					@Override
					public void onResponse(GetTagListRsp param) {
						mRsp = param;
						if (param != null && param.getTagList() != null
								&& !param.getTagList().isEmpty()) {
							requestFinish(refreType, param.getTagList(), false,
									true, false);
							if (refreType == RefreshType._REFRESH_TYPE_REFRESH) {
								cacheTagTopicList(param);
							}
						} else {
							requestFinish(refreType, null,false, false, false);
						}
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType, null, false, false, true);
					}

				});
	}

	@Override
	protected void requestFinish(int refresh, ArrayList<Tag> sourceList,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null && sourceList.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<Tag> dataSource = getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	protected void cacheTagTopicList(GetTagListRsp param) {
		cacheTagListTask.execute(param);
	}

	protected GetTagListRsp loadCacheTagList() {
		GetTagListRsp rsp = mPref.getTagListRsp(getKey());
		return rsp;
	}

	protected CacheTagListTask cacheTagListTask = new CacheTagListTask();

	class CacheTagListTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			Object value = params[0];
			mPref.saveTagListRsp(getKey(), (GetTagListRsp) value);
			return null;
		}
	}

	private class LoadCacheTagTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = loadCacheTagList();
			if (mRsp != null && mRsp.getTagList() != null) {
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

			requestFinish(RefreshType._REFRESH_TYPE_REFRESH, mRsp.getTagList(),
					false, true, false);

			super.onPostExecute(needReload);
		}
	}

	protected abstract String getKey();

	@Override
	protected boolean isRefreshableLoad() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

}
