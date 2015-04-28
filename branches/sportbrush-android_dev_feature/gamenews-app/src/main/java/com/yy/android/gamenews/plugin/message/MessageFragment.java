package com.yy.android.gamenews.plugin.message;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.RefreshType;
import com.duowan.show.Message;
import com.duowan.show.NoteCallType;
import com.duowan.show.NotificationRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.model.MessageModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class MessageFragment extends BaseListFragment<Message> {

	private boolean isFirstEnter;
	private String mAttachInfo;
	protected IPageCache mPageCache;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageCache = new IPageCache();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new BgTask().execute();
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected void requestData(final int refreType) {
		MessageModel.getPersonMessage(new ResponseListener<NotificationRsp>(
				getActivity()) {

			@Override
			public void onResponse(NotificationRsp arg0) {
				if (arg0 != null) {
					if (arg0.getAttachInfo() != null) {
						mAttachInfo = arg0.getAttachInfo();
					}
					if (!isFirstEnter) {
						isFirstEnter = true;
						requestFinish(refreType, arg0.getNoteList(),
								arg0.getHasMore(), true, false);
					} else {
						requestFinish(refreType, arg0.getNoteList(),
								arg0.getHasMore(), false, false);
					}
				} else {
					requestFinish(refreType, null, true, false, false);
				}

			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				requestFinish(refreType, null, true, false, false);
			}
		}, refreType, NoteCallType._NOTE_CALL_LIST_TYPE, mAttachInfo);

	}

	@Override
	protected synchronized void requestFinish(int refresh, ArrayList<Message> data,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
	}

	protected boolean isRefreshableHead() {
		return false;
	}

	protected boolean isRefreshableLoad() {
		return true;
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected ImageAdapter<Message> initAdapter() {
		return new MessageAdapter(getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ArrayList<Message> dataSource = getAdapter().getDataSource();
		if (dataSource.size() <= 20) {
			NotificationRsp param = new NotificationRsp();
			param.setNoteList(dataSource);
			saveListToDisk(param);
		} else {
			NotificationRsp param = new NotificationRsp();
			for (int i = dataSource.size() - 1; i >= 20; i--) {
				dataSource.remove(i);
			}
			param.setNoteList(dataSource);
			saveListToDisk(param);
		}
	}

	protected void saveListToDisk(NotificationRsp param) {
		String key = Constants.CACHE_PERSON_MESSAGE_LIST;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION,
				true);
	}

	protected NotificationRsp getResponseFromDisk() {
		NotificationRsp rsp = null;
		synchronized (mPageCache) {
			rsp = mPageCache.getJceObject(Constants.CACHE_PERSON_MESSAGE_LIST,
					new NotificationRsp());
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

		NotificationRsp mRspCache = null;

		@Override
		public void execute(Void... params) {
			super.execute(params);
			showView(VIEW_TYPE_LOADING);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			mRspCache = getResponseFromDisk();

			if (mRspCache != null && mRspCache.getNoteList() != null
					&& mRspCache.getNoteList().size() > 0) {
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {
			if (!isFirstEnter) {
				if (!needReload) {
					if (Util.isNetworkConnected()) {
						showView(VIEW_TYPE_LOADING);
					} else {
						showView(VIEW_TYPE_EMPTY);
					}
					return;
				}
				requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
						mRspCache.getNoteList(), false, false, false);
			}
			showView(VIEW_TYPE_DATA);
			super.onPostExecute(needReload);
		}
	}

}
