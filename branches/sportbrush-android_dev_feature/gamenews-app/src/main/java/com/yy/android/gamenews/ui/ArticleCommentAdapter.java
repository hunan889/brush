package com.yy.android.gamenews.ui;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Comment;
import com.duowan.gamenews.LikeType;
import com.yy.android.gamenews.model.ReportModel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class ArticleCommentAdapter extends ImageAdapter<Comment> {
	private Set<String> mMyCommentsLike;
	private long mArticleId;

	public ArticleCommentAdapter(Context context) {
		super(context);
		mMyCommentsLike = Preference.getInstance().getMyCommentsLike();
		if (mMyCommentsLike == null) {
			mMyCommentsLike = new HashSet<String>();
		}
	}

	public void setArticleId(long id) {
		mArticleId = id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.article_detail_comment_list_item, null);
			holder.mContent = (TextView) convertView.findViewById(R.id.content);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.mLikeCount = (TextView) convertView
					.findViewById(R.id.like_count);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.comment_time);
			holder.mUserLogo = (ImageView) convertView
					.findViewById(R.id.user_logo);
			holder.mLike = (ImageView) convertView
					.findViewById(R.id.like_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Comment item = getItem(position);
		holder.mContent.setText(item.getContent());
		if (mMyCommentsLike.contains(item.getId())) {
			holder.mLike
					.setImageResource(R.drawable.article_detail_comment_like_pressed);
		} else {
			holder.mLike
					.setImageResource(R.drawable.article_detail_comment_like_normal);
		}
		holder.mComment = item;
		holder.mLike.setTag(holder);
		holder.mLike.setOnClickListener(mOnClickListener);
		holder.mLikeCount.setText(Integer.toString(item.getCount()
				.getLikeCount()));
		holder.mUserName.setText(item.getUser().getName());
		SwitchImageLoader.getInstance().displayImage(item.getUser().icon,
				holder.mUserLogo, SwitchImageLoader.DEFAULT_USER_DISPLAYER);
		// holder.mTime.setText(TimeUtil.parseTime(ArticleDetailActivity.this,
		// item.getTime()));
		holder.mTime.setText(TimeUtil.parseTime(item.getTime()));
		return convertView;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			final ViewHolder holder = (ViewHolder) v.getTag();
			if (mMyCommentsLike.contains(holder.mComment.getId())) {
				Toast.makeText(getContext(),
						getResources().getString(R.string.liked_hint),
						Toast.LENGTH_SHORT).show();
			} else {

				ReportModel.LikeComment(new ResponseListener<Boolean>(
						(FragmentActivity) getContext()) {

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						super.onError(e);
						ToastUtil.showToast(R.string.load_failed);
					}

					@Override
					public void onResponse(Boolean arg0) {
						// TODO Auto-generated method stub
						// Toast.makeText(ArticleDetailActivity.this,
						// "success", Toast.LENGTH_SHORT).show();
						mMyCommentsLike.add(holder.mComment.getId());
						Preference.getInstance().saveMyCommentsLike(
								mMyCommentsLike);
						holder.mLike
								.setImageResource(R.drawable.article_detail_comment_like_pressed);
						int likeCount = Integer.parseInt(holder.mLikeCount
								.getText().toString()) + 1;
						holder.mLikeCount.setText(Integer.toString(likeCount));
						holder.mComment.getCount().setLikeCount(likeCount);

					}

				}, mArticleId, holder.mComment.getId(), LikeType.LIKE_TYPE_LIKE);
			}
		}
	};

	private static class ViewHolder {
		TextView mUserName;
		TextView mLikeCount;
		TextView mTime;
		TextView mContent;
		ImageView mUserLogo;
		ImageView mLike;
		Comment mComment;
	}

}
