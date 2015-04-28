package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.show.BaseComment;
import com.duowan.show.Comment;
import com.duowan.show.GetCommentListRsp;
import com.duowan.show.GetTopicDetailRsp;
import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.RefreshType;
import com.duowan.show.Tag;
import com.duowan.show.Topic;
import com.duowan.show.User;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.LikeEvent;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.ArticleSocialDialog;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TopicDetailFragment extends BaseListFragment<Comment> {

	private TopicDetailView mDetailView;
	private int mTopicId;
	private GetTopicDetailRsp mRsp;
	private GetCommentListRsp mCommentRsp;

//	private static final int DETAIL_VIEW_ID = 1001;

	private static final String LOG_TAG = "TopicDetailFragment";
	public static final String TAG_DIALOG = "topic_social_dialog";

	private ArrayList<Integer> mLikeTopicList;
	private IPageCache mPageCache = new IPageCache();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle data = null;
		if (savedInstanceState != null) {
			data = savedInstanceState;
		} else {
			data = getArguments();
		}

		if (data != null) {
			mTopicId = data.getInt(TopicDetailActivity.KEY_ID);
		}
		mLikeTopicList = mPageCache
				.getObject(Constants.CACHE_KEY_LIKE_TOPIC_LIST);
		if (mLikeTopicList == null) {
			mLikeTopicList = new ArrayList<Integer>();
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(TopicDetailActivity.KEY_ID, mTopicId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parentView = super.onCreateView(inflater, container,
				savedInstanceState);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		ActionBar actionbar = (ActionBar) inflater.inflate(
				R.layout.actionbar_default, layout)
				.findViewById(R.id.actionbar);

		actionbar.setTitle(getString(R.string.show_topic_detail));
		actionbar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		layout.addView(parentView);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestTopicDetail();
	}

	@Override
	protected void requestData(final int refresh) {

		if (mRsp == null) {
			requestTopicDetail();
			return;
		}
		String attachInfo = "";
		if (mCommentRsp != null) {
			attachInfo = mCommentRsp.getAttachInfo();
		}
		ShowModel.getCommentList(new ResponseListener<GetCommentListRsp>(
				getActivity()) {

			@Override
			public void onResponse(GetCommentListRsp response) {
				updateCommentList(response, refresh);
			}

			@Override
			public void onError(Exception e) {
				requestFinish(refresh, null, false, false, true);
				super.onError(e);
			}
		}, mTopicId, attachInfo);
	}

	private void updateCommentList(GetCommentListRsp response, int refresh) {
		if (response != null) {
			mCommentRsp = response;
			ArrayList<Comment> commentList = response.getCommentList();
			requestFinish(refresh, commentList, response.hasMore,
					refresh == RefreshType._REFRESH_TYPE_REFRESH, false);
		}
	}

	private void updateTopicDetail(GetTopicDetailRsp response) {
		if (response == null && mRsp == null) {
			Log.w(LOG_TAG, "[updateTopicDetail] response is null!");

			showView(VIEW_TYPE_EMPTY);
			return;
		}
		mRsp = response;
		showView(VIEW_TYPE_DATA);

		if (mCommentRsp == null) {
			refreshData();
		}

		if (mRsp != null) {
			Topic topic = mRsp.getTopicInfo();
			Integer id = Integer.valueOf(topic.getId());
			if (mLikeTopicList.contains(id)) {
				topic.setIsLike(true);
			}

			mDetailView.refresh(response);
		}
	}
	
	private void requestTopicDetail() {
		showView(VIEW_TYPE_LOADING);
		ShowModel.getTopicDetail(new ResponseListener<GetTopicDetailRsp>(
				getActivity()) {

			@Override
			public void onResponse(GetTopicDetailRsp response) {
				updateTopicDetail(response);
			}

			@Override
			public void onError(Exception e) {
				updateTopicDetail(null);
			}
		}, mTopicId);
	}

	@Override
	protected void customizeView(ViewGroup viewGroup) {
		super.customizeView(viewGroup);

//		mDetailView = (TopicDetailView) viewGroup.findViewById(DETAIL_VIEW_ID);
		if (mDetailView == null) {
			mDetailView = new TopicDetailView(getActivity());
			mDataViewConverter.addHeader(mDetailView);

			mDetailView.setOnCommentViewClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					TopicDetailCommentActivity
							.startActivityForResultFromFragment(
									TopicDetailFragment.this, mTopicId, null,
									REQUEST_ADD_COMMENT,0);
				}
			});

			mDetailView.setShareViewClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Topic info = null;
					if (mRsp != null) {
						info = mRsp.getTopicInfo();
					}
					String imageUrl = "";
					StringBuilder content = new StringBuilder();
					if (info != null) {
						PicInfo pic = TopicUtils.getImageFromTopic(info,
								ImageType._IMAGE_TYPE_NORMAL);
						if (pic != null) {
							imageUrl = pic.url;
						}

						List<Tag> tagList = info.getTagList();
						if (tagList != null && tagList.size() > 0) {
							Tag tag = tagList.get(0);
							String tagStr = String.format("#%s#", tag.name);
							content.append(tagStr);
						}
						content.append(info.getDesc());
					}

					String detailUrl = String.format(
							Constants.SHOW_TOPIC_DETAIL_FORMATTER, mTopicId);
					DialogFragment fs = ArticleSocialDialog.newInstance(
							imageUrl, content.toString(), detailUrl,
							ArticleSocialDialog.SHARED_FROM_ARTICLE);
					Util.showDialog(getActivity(), fs, TAG_DIALOG);

					String eventId = "stats_share_show_topic";
					String key = "topic_id";
					String value = String.valueOf(mTopicId);
					StatsUtil.statsReport(getActivity(), eventId, key, value);
					StatsUtil.statsReportByMta(getActivity(), eventId, key,
							value);
					StatsUtil.statsReportByHiido(eventId, key + value);
				}
			});

			mDetailView.setOnLikeViewClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ShowModel.sendTopicLikeReq(new ResponseListener<Object>(
							getActivity()) {

						@Override
						public void onResponse(Object response) {
							if (mRsp == null) {
								return;
							}
							Topic info = mRsp.topicInfo;
							info.setIsLike(true);
							mDetailView.refresh(mRsp);

							Integer id = Integer.valueOf(info.getId());
							if (!mLikeTopicList.contains(id)) {
								mLikeTopicList.add(id);
							}

							mPageCache.setObject(
									Constants.CACHE_KEY_LIKE_TOPIC_LIST,
									mLikeTopicList,
									Constants.CACHE_DURATION_FOREVER);

							LikeEvent likeEvent = new LikeEvent();
							likeEvent.setId(mTopicId);
							EventBus.getDefault().post(likeEvent);

							String eventId = "stats_like_show_topic";
							String key = "topic_id";
							String value = String.valueOf(mTopicId);
							StatsUtil.statsReport(getActivity(), eventId, key,
									value);
							StatsUtil.statsReportByMta(getActivity(), eventId,
									key, value);
							StatsUtil.statsReportByHiido(eventId, key + value);
						}

					}, mTopicId);
				}
			});
		}
	}

	public static final int REQUEST_ADD_COMMENT = 1001;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ADD_COMMENT: {
			if (resultCode == Activity.RESULT_OK) {
				Comment comment = (Comment) data
						.getSerializableExtra(TopicDetailCommentActivity.KEY_COMMENT_OBJ);
				if (mCommentRsp != null) {
					List<Comment> commentList = null;
					if (mCommentRsp != null) {
						commentList = mCommentRsp.getCommentList();
					}

					if (commentList != null) {
						commentList.add(0, comment);
						updateCommentList(mCommentRsp,
								RefreshType._REFRESH_TYPE_REFRESH);
					}

					String eventId = "stats_comment_show_topic";
					String key = "topic_id";
					String value = String.valueOf(mTopicId);
					StatsUtil.statsReport(getActivity(), eventId, key, value);
					StatsUtil.statsReportByMta(getActivity(), eventId, key,
							value);
					StatsUtil.statsReportByHiido(eventId, key + value);
				}
			}
			break;
		}
		}
	}

	@Override
	protected ImageAdapter<Comment> initAdapter() {
		return new TopicCommentAdapter(getActivity());
	}

	@Override
	protected boolean hasData() {
		return mRsp != null;
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
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {

		Comment comment = (Comment) adapter.getItem(position);

		if (comment == null) {
			Log.w(LOG_TAG, "[onItemClick], comment is null!");
			return;
		}
		BaseComment bc = comment.getComment();

		User user = bc.getAuthor();

		UserInitRsp rsp = Preference.getInstance().getInitRsp();
		com.duowan.gamenews.User localUser = null;// rsp.getUser();
		if (rsp != null) {
			localUser = rsp.getUser();
		}

		if (user != null && localUser != null) {
			if (user.getId().equals(localUser.getId())) {

				// TODO:用户回复自己发表的评论
				return;
			}
		}

		TopicDetailCommentActivity.startActivityForResultFromFragment(this,
				mTopicId, comment.getComment(), REQUEST_ADD_COMMENT,0);

		super.onItemClick(parent, adapter, view, position, id);
	}
}
