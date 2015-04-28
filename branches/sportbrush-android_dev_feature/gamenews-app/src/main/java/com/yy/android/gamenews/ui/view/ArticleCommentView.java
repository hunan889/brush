package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.model.CommentModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ArticleCommentView extends CommentView {
	public ArticleCommentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ArticleCommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArticleCommentView(Context context) {
		super(context);
	}

	private long mArticleId;

	public void setArticleId(long articleId) {
		mArticleId = articleId;
	}

	@Override
	protected void init(Context context) {
		setOnCommentListener(new OnCommentListener() {

			@Override
			public void onComment(String text) {
				CommentModel.addComment(new ResponseListener<Boolean>(
						(FragmentActivity) getContext()) {

					@Override
					public void onResponse(Boolean arg0) {
						ToastUtil.showToast(R.string.commented);

						clearInputBoxText();

						CommentEvent event = new CommentEvent();
						event.id = mArticleId;
						event.commentCount = CommentEvent.CMT_CNT_ADD;
						EventBus.getDefault().post(event);
					}

				}, mArticleId, text, true);
			}
		});
		super.init(context);
	}
}
