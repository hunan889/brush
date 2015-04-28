package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.RefreshType;
import com.duowan.show.GetTopicListRsp;
import com.duowan.show.Topic;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.event.LikeEvent;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.SendTopicEvent;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public abstract class BaseTopicListFragment<E> extends BaseListFragment<Topic> {

	// private Preference mPref;
	private IPageCache mPageCache;
	private GetTopicListRsp mRsp;
	private TopicListAdapter topicListAdapter;
	private boolean isFirstEnter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		// mPref = Preference.getInstance();
		mPageCache = new IPageCache();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);

		new LoadCacheTagTopicTask().execute();

		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDataView().setBackgroundResource(R.color.list_bg);
		int padding = Util.dip2px(getActivity(), 5);
		getDataView().setPadding(padding, 0, padding, padding);
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
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		Topic topic = (Topic) adapter.getItem(position);
		TopicDetailActivity.startTopicDetailActivity(getActivity(),
				topic.getId());
		MainTabStatsUtil.statistics(getActivity(), MainTabEvent.TAB_COMMUNITY,
				MainTabEvent.CLICK_TOPIC_IN_SQUARE, TextUtils.isEmpty(topic
						.getDesc()) ? MainTabEvent.CLICK_TOPIC_IN_SQUARE_NAME
						: topic.getDesc());
		super.onItemClick(parent, adapter, view, position, id);
	}

	public void onEvent(LikeEvent likeEvent) {
		if (likeEvent != null) {
			int topicId = (int) likeEvent.getId();
			ArrayList<Topic> dataSource = getDataSource();
			for (Topic topic : dataSource) {
				if (topic.getId() == topicId) {
					topic.setLikeNum(topic.getLikeNum() + 1);
					topicListAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	public void onEvent(CommentEvent commentEvent) {
		if (commentEvent != null) {
			int topicId = (int) commentEvent.getId();
			ArrayList<Topic> dataSource = getDataSource();
			for (Topic topic : dataSource) {
				if (topic.getId() == topicId) {
					topic.setCommentNum(topic.getCommentNum() + 1);
					topicListAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	public void onEvent(SendTopicEvent sendTopicEvent) {
		if (sendTopicEvent != null && sendTopicEvent.getTopic() != null) {
			Topic topic = sendTopicEvent.getTopic();
			ArrayList<Topic> dataSource = new ArrayList<Topic>();
			if (topicListAdapter != null
					&& topicListAdapter.getDataSource() != null) {
				dataSource = topicListAdapter.getDataSource();
			}
			dataSource.add(0, topic);
			topicListAdapter.setDataSource(dataSource);
		}
	}

	protected void requestData(final int refreType,
			final ArrayList<Integer> tags) {
		if (topicListAdapter != null
				&& topicListAdapter.getDataSource() != null) {
			ArrayList<Topic> dataSource = topicListAdapter.getDataSource();
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
		ShowModel.getTopicList(new ResponseListener<GetTopicListRsp>(
				getActivity()) {

			@Override
			public void onResponse(GetTopicListRsp param) {
				mRsp = param;
				if (param != null && param.getTopicList() != null
						&& !param.getTopicList().isEmpty()) {
					if (!isFirstEnter) {
						isFirstEnter = true;
					}
					ArrayList<Topic> dataList = param.getTopicList();
					ArrayList<Topic> localList = getDataSource();
					if (dataList != null && localList != null) {
						for (Topic topic : dataList) {
							for (int i = 0; i < localList.size(); i++) {
								Topic localTopic = localList.get(i);
								if (localTopic.getId() == topic.getId()) {
									localList.remove(i);
									i--;
								}
							}
						}
					}
					requestFinish(refreType, param.getTopicList(),
							param.hasMore, true, false);
					if (refreType == RefreshType._REFRESH_TYPE_REFRESH) {
						cacheTagTopicList(param);
					}
				} else {
					requestFinish(refreType, null, false, false, false);
				}
			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				requestFinish(refreType, null, false, false, true);
			}

		}, tags, attachInfo, refreType);
	}

	@Override
	protected synchronized void requestFinish(int refresh, ArrayList<Topic> sourceList,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null && sourceList.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<Topic> dataSource = topicListAdapter.getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	protected ImageAdapter<Topic> initAdapter(int type) {
		topicListAdapter = new TopicListAdapter(getActivity());
		topicListAdapter.setType(type);
		return topicListAdapter;
	}

	protected void cacheTagTopicList(GetTopicListRsp param) {
		cacheTagTopicListTask.execute(param);
	}

	protected GetTopicListRsp loadCacheTagTopicList() {
		// GetTopicListRsp rsp = mPref.getTopicListRsp(getKey());
		GetTopicListRsp rsp = mPageCache.getJceObject(getKey(),
				new GetTopicListRsp());
		return rsp;
	}

	protected CacheTagTopicListTask cacheTagTopicListTask = new CacheTagTopicListTask();

	class CacheTagTopicListTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			Object value = params[0];
			// mPref.saveTopicListRsp(getKey(), (GetTopicListRsp) value);

			mPageCache.setJceObject(getKey(), value,
					Constants.CACHE_DURATION_HOMELIST);
			return null;
		}
	}

	private class LoadCacheTagTopicTask extends
			BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = loadCacheTagTopicList();
			if (mRsp != null && mRsp.getTopicList() != null) {
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
						mRsp.getTopicList(), false, true, false);
			}

			showView(VIEW_TYPE_DATA);
			super.onPostExecute(needReload);
		}
	}

	protected abstract String getKey();

	@Override
	protected boolean isRefreshableLoad() {
		return super.isRefreshableLoad();
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
