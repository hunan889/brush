package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Comment;
import com.duowan.gamenews.GetCommentListRsp;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.model.CommentModel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.ArticleCommentView;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class CommentListFragment extends BaseListFragment<Comment> {

	private GetCommentListRsp mCommentListRsp;
	private long mArticleId;

	@Override
	protected void readDataFromBundle(Bundle bundle) {
		if (bundle != null) {
			mArticleId = bundle.getLong(CommentListActivity.KEY_ARTICLE_ID);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parentView = super.onCreateView(inflater, container,
				savedInstanceState);
		RelativeLayout layout = new RelativeLayout(getActivity());

		ActionBar actionbar = (ActionBar) inflater.inflate(
				R.layout.actionbar_default, layout)
				.findViewById(R.id.actionbar);

		actionbar.setTitle(R.string.article_comment_list_title);
		actionbar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		View view = new View(getActivity());
		AbsListView.LayoutParams viewParams = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, Util.dip2px(getActivity(), 44));
		view.setLayoutParams(viewParams);
		((ListView) getDataView()).addFooterView(view);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, actionbar.getId());
		layout.addView(parentView, params);

		ArticleCommentView commentView = new ArticleCommentView(getActivity());
		commentView.setArticleId(mArticleId);

		commentView
				.setViewBackground(R.drawable.global_inputbox_parent_bg_light);
		commentView.setInputBoxBackground(R.drawable.global_inputbox_bg_light);
		commentView.setInputBoxTextColor(getResources().getColor(
				R.color.article_detail_comment_primary_text));
		LayoutParams commentParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		commentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView(commentView, commentParams);

		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshData();
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected void requestData(final int refreshType) {
		String attachInfo = null;

		if (refreshType == RefreshType._REFRESH_TYPE_REFRESH) {
			if (mCommentListRsp != null) {
				mCommentListRsp.setAttachInfo(null);
			}
		} else {
			if (mCommentListRsp != null) {
				attachInfo = mCommentListRsp.getAttachInfo();
			}
		}

		CommentModel.getCommentList(new ResponseListener<GetCommentListRsp>(
				getActivity()) {
			@Override
			public void onError(Exception e) {
				super.onError(e);
				if (!TextUtils.isEmpty(e.getMessage())) {

					ToastUtil.showToast(R.string.load_failed);
				}
				requestFinish(refreshType, null, false, false, true);
			}

			@Override
			public void onResponse(GetCommentListRsp data) {
				mCommentListRsp = data;
				ArrayList<Comment> commentList = null;
				if (data != null) {
					commentList = data.commentList;
				}

				requestFinish(refreshType, commentList, data.hasMore,
						refreshType == RefreshType._REFRESH_TYPE_REFRESH, false);
			}
		}, mArticleId, attachInfo);
	}

	@Override
	protected ImageAdapter<Comment> initAdapter() {

		ArticleCommentAdapter adapter = new ArticleCommentAdapter(getActivity());

		adapter.setArticleId(mArticleId);

		return adapter;
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	public void onEvent(CommentEvent event) {
		refreshData();
	}

}
