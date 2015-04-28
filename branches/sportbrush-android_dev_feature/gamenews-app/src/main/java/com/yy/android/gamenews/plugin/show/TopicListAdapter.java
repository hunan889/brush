package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.Topic;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.AutoAdjustHelper;
import com.yy.android.gamenews.ui.view.AutoAdjustImageView;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

public class TopicListAdapter extends ImageAdapter<Topic> {

	protected final static int TYPE_LIST = 0;
	protected final static int TYPE_WATERFALL = 1;

	private Context context;
	private int type;

	public TopicListAdapter(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		Topic topic = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.show_topic_list_item_view,
					null);
			convertView.setBackgroundResource(R.color.global_transparent);
			viewHolder.iconImageView = (AutoAdjustImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.contenTextView = (TextView) convertView
					.findViewById(R.id.tv_content);
			viewHolder.avatarImageView = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.commentCountTextView = (TextView) convertView
					.findViewById(R.id.tv_comment_count);
			viewHolder.likeCountTextView = (TextView) convertView
					.findViewById(R.id.tv_like_coount);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		PicInfo picInfo = TopicUtils.getImageFromTopic(topic,
				type == TYPE_WATERFALL ? ImageType._IMAGE_TYPE_SMALL
						: ImageType._IMAGE_TYPE_NORMAL);
		if (picInfo != null) {
			int width = picInfo.getWidth();
			int height = picInfo.getHeight();
			if (width > 0 && height > 0) {

				viewHolder.iconImageView.setCustWidth(width);
				viewHolder.iconImageView.setCustHeight(height);
				viewHolder.iconImageView
						.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_HEIGHT);
				viewHolder.iconImageView.invalidate();

			} else {
				viewHolder.iconImageView
						.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_SCALE_HEIGHT);
				if (type == TYPE_WATERFALL) {
					viewHolder.iconImageView.setScaleRate(0.87f);
				} else {
					viewHolder.iconImageView.setScaleRate(1.80f);
				}
				viewHolder.iconImageView.invalidate();
			}
			displayImage(picInfo.getUrl(), viewHolder.iconImageView,
					SwitchImageLoader.DEFAULT_ARTICLE_ITEM_WATERFALL_DISPLAYER);
		} else {
			viewHolder.iconImageView
					.setAdjustType(AutoAdjustHelper.AUTO_ADJUST_SCALE_HEIGHT);
			if (type == TYPE_WATERFALL) {
				viewHolder.iconImageView.setScaleRate(0.87f);
			} else {
				viewHolder.iconImageView.setScaleRate(1.80f);
			}
			viewHolder.iconImageView.invalidate();
		}

		if(TextUtils.isEmpty(topic.getDesc())){
			viewHolder.contenTextView.setVisibility(View.GONE);
		}else{
			viewHolder.contenTextView.setVisibility(View.VISIBLE);
			viewHolder.contenTextView.setText(topic.getDesc());
		}
		
		SwitchImageLoader.getInstance().displayImage(
				topic.getAuthor().getIcon(), viewHolder.avatarImageView,
				SwitchImageLoader.DEFAULT_USER_DISPLAYER);
		viewHolder.nameTextView.setText(topic.getAuthor().getName());
		if (type == TYPE_WATERFALL) {
			viewHolder.timeTextView.setVisibility(View.GONE);
		} else {
			viewHolder.timeTextView.setVisibility(View.VISIBLE);
			viewHolder.timeTextView.setText(TimeUtil.parseTime(context,
					topic.getCreateTime()));
		}
		viewHolder.commentCountTextView.setText(String.valueOf(topic
				.getCommentNum()));
		viewHolder.likeCountTextView
				.setText(String.valueOf(topic.getLikeNum()));

		return convertView;
	}

	static class ViewHolder {
		AutoAdjustImageView iconImageView;
		TextView contenTextView;
		ImageView avatarImageView;
		TextView nameTextView;
		TextView timeTextView;
		TextView commentCountTextView;
		TextView likeCountTextView;
	}

	public void setType(int type) {
		this.type = type;
	}

}
