package com.yy.android.gamenews.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.CommentView;
import com.yy.android.gamenews.ui.view.CommentView.OnCommentListener;
import com.yy.android.sportbrush.R;

public class CommentGalleryFragment extends GalleryFragment implements
		OnCommentListener {

	protected View mCommentCountParent;
	protected TextView mCommentCountView;
	protected CommentView mCommentView;

	@Override
	protected void initActionBar(ActionBar actionbar) {
		actionbar.setTitle("");

		if (mCommentCountParent == null) {
			mCommentCountParent = mInflater.inflate(
					R.layout.global_actionbar_comment, null);
			mCommentCountView = (TextView) mCommentCountParent
					.findViewById(R.id.comment_count);

			mCommentCountParent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onCommentCountViewClicked();
				}
			});
		}

		actionbar.setCustomizeView(mCommentCountParent);
		super.initActionBar(actionbar);
	}

	@Override
	protected void initFooter(ViewGroup footer) {
		mCommentView = initCommentView();

		mCommentView.showDivider(false);
		footer.addView(mCommentView);
		super.initFooter(footer);
	}

	protected CommentView initCommentView() {
		mCommentView = new CommentView(getActivity());
		mCommentView.setOnCommentListener(this);

		return mCommentView;
	}

	@Override
	public void onComment(String text) {

	}

	protected void onCommentCountViewClicked() {

	}

	@Override
	protected void onHeaderFooterHide() {
		mCommentCountParent.setEnabled(false);
		mCommentView.setEnabled(false);
		super.onHeaderFooterHide();
	}

	@Override
	protected void onHeaderFooterShow() {
		mCommentCountParent.setEnabled(true);
		mCommentView.setEnabled(true);
		super.onHeaderFooterShow();
	}
}
