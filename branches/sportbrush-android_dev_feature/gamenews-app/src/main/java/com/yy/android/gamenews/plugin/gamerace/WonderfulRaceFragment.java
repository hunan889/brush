package com.yy.android.gamenews.plugin.gamerace;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetWonderfulRaceRsp;
import com.duowan.gamenews.RaceTopicInfo;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class WonderfulRaceFragment extends BaseListFragment<RaceTopicInfo> {

	private WonderfulRaceAdapter mWonderfulRaceAdapter;
	private static final int COUNT = 10;
	private FragmentActivity mActivity;
	protected IPageCache mPageCache;
	private Map<Integer, String> mAttachInfo = null;
	private GetWonderfulRaceRsp mRsp;
	private boolean IsFirstEnter = true;
	private boolean mQuit = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (FragmentActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageCache = new IPageCache();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (IsFirstEnter) {
			showView(VIEW_TYPE_LOADING);
		}
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected void requestData(final int refreType) {
		Map<Integer, String> attachInfo = null;
		if (refreType == RefreshType._REFRESH_TYPE_LOAD_MORE) {
			attachInfo = mAttachInfo;
		}
		WonderfulRaceModel.getWonderfulRaceList(
				new ResponseListener<GetWonderfulRaceRsp>(mActivity) {

					@Override
					public void onResponse(GetWonderfulRaceRsp arg0) {
						if (arg0 != null && IsFirstEnter) {
							saveListToDisk(arg0);
						}
						if (arg0 != null) {
							mAttachInfo = arg0.getAttachInfo();
						}
						if (refreType == RefreshType._REFRESH_TYPE_REFRESH) {
							requestFinish(refreType, arg0.getRaceList(),
									arg0.getHasMore(), true, false);
						} else {
							requestFinish(refreType, arg0.getRaceList(),
									arg0.getHasMore(), false, false);
						}
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType, null, false, false, false);

					}
				}, COUNT, attachInfo, refreType);
	}

	@Override
	protected void requestFinish(int refresh, ArrayList<RaceTopicInfo> data,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
		if (IsFirstEnter && data != null && data.size() > 0) {
			showView(VIEW_TYPE_DATA);
			IsFirstEnter = false;
		} else if (IsFirstEnter) {
			new BgTask().execute();
		}
	}

	@Override
	protected ImageAdapter<RaceTopicInfo> initAdapter() {
		mWonderfulRaceAdapter = new WonderfulRaceAdapter(getActivity());
		return mWonderfulRaceAdapter;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mQuit = true;
	}

	protected SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
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

	protected void saveListToDisk(GetWonderfulRaceRsp param) {
		String key = Constants.WONDERFUL_RACE_LIST;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION,
				true);
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = getResponseFromDisk();
			if (mRsp != null && mRsp.getRaceList() != null) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {
			if (needReload && !mQuit) {
				requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
						mRsp.getRaceList(), false, true, false);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}

			super.onPostExecute(needReload);
		}
	}

	protected GetWonderfulRaceRsp getResponseFromDisk() {
		GetWonderfulRaceRsp rsp = mPageCache.getJceObject(
				Constants.WONDERFUL_RACE_LIST, new GetWonderfulRaceRsp());
		return rsp;
	}

}
