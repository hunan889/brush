package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.show.GetTopicDetailRsp;
import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.duowan.show.Topic;
import com.duowan.show.User;
import com.yy.android.gamenews.ui.ImageZoomDetailViewerActivity;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.AutoAdjustHelper;
import com.yy.android.gamenews.ui.view.AutoAdjustImageView;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

public class TopicDetailView extends FrameLayout {

	private TextView mUserNameView;
	private AutoAdjustImageView mImageView;
	private TextView mTimeView;
	private ImageView mUserImgView;
	private TextView mContentView;
	private View mCommentView;
	private View mUpView;
	private View mShareView;
	private TextView mTagView;

	private GetTopicDetailRsp mRsp;

	private SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();

	public TopicDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TopicDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TopicDetailView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);

		inflater.inflate(R.layout.show_topic_detail, this);
		mUserImgView = (ImageView) findViewById(R.id.show_topic_detail_head_img);
		mUserNameView = (TextView) findViewById(R.id.show_topic_detail_username);
		mTimeView = (TextView) findViewById(R.id.show_topic_detail_time);
		mImageView = (AutoAdjustImageView) findViewById(R.id.show_topic_detail_img);

		mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Topic topic = mRsp.getTopicInfo();
				PicInfo info = TopicUtils.getImageFromTopic(topic,
						ImageType._IMAGE_TYPE_BIG);
				if (info != null) {
					ArrayList<String> arrayList = new ArrayList<String>();
					arrayList.add(info.getUrl());
					ImageZoomDetailViewerActivity.startZoomDetailActivity(
							getContext(), arrayList, 0, topic.getDesc());
				}

			}
		});
		mContentView = (TextView) findViewById(R.id.show_topic_detail_content);
		mCommentView = findViewById(R.id.show_topic_detail_comment);
		mTagView = (TextView) findViewById(R.id.show_topic_detail_tag);
		mShareView = findViewById(R.id.show_topic_detail_more);
		mUpView = findViewById(R.id.show_topic_detail_up);
	}

	public void refresh(GetTopicDetailRsp rsp) {
		if (rsp == null) {
			return;
		}
		mRsp = rsp;
		Topic topic = rsp.getTopicInfo();

		User user = topic.getAuthor();
		if (user != null) {
			mUserNameView.setText(user.getName());
			mImageLoader.displayImage(user.getIcon(), mUserImgView,
					SwitchImageLoader.DEFAULT_USER_DISPLAYER);
		}
		if (TextUtils.isEmpty(topic.getDesc())) {
			mContentView.setVisibility(View.GONE);
		} else {
			mContentView.setVisibility(View.VISIBLE);
			mContentView.setText(topic.getDesc());
		}
		mTimeView.setText(TimeUtil.parseTime(getContext(),
				topic.getCreateTime()));

		List<Tag> tagList = topic.getTagList();
		if (tagList != null && tagList.size() > 0) {

			mTagView.setText(tagList.get(0).getName());
		}

		if (topic.isLike) {
			mUpView.setEnabled(false);
		} else {
			mUpView.setEnabled(true);
		}

		PicInfo info = TopicUtils.getImageFromTopic(topic,
				ImageType._IMAGE_TYPE_NORMAL);
		if (info != null && !TextUtils.isEmpty(info.url)) {

			int width = info.width;
			int height = info.height;

			ViewGroup.LayoutParams params = mImageView.getLayoutParams();
			if (width == 0 || height == 0) {
				params.width = ViewGroup.LayoutParams.MATCH_PARENT;
				mImageView
						.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_SCALE_HEIGHT);
			} else {
				mImageView.setCustHeight(height);
				mImageView.setCustWidth(width);
				// 图片拉伸处理
				if (width > height) {
					params.width = ViewGroup.LayoutParams.MATCH_PARENT;
					mImageView
							.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_HEIGHT);
				} else {

					params.height = ((View) mImageView.getParent()).getWidth();
					mImageView
							.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_WIDTH);
				}
			}

			mImageView.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(info.url, mImageView);
			mImageView.invalidate();
			mImageView.requestLayout();
		} else {
			mImageView.setVisibility(View.GONE);
		}
	}

	public void setOnCommentViewClickListener(OnClickListener listener) {
		if (mCommentView != null) {
			mCommentView.setOnClickListener(listener);
		}
	}

	public void setOnLikeViewClickListener(OnClickListener listener) {
		if (mUpView != null) {
			mUpView.setOnClickListener(listener);
		}
	}

	public void setShareViewClickListener(OnClickListener listener) {
		if (mShareView != null) {
			mShareView.setOnClickListener(listener);
		}
	}
}
