package com.yy.android.gamenews.plugin.message;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.show.BaseComment;
import com.duowan.show.Message;
import com.duowan.show.NoteType;
import com.yy.android.gamenews.plugin.show.TopicDetailActivity;
import com.yy.android.gamenews.plugin.show.TopicDetailCommentActivity;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

public class MessageAdapter extends ImageAdapter<Message> {
	private Context mContext;

	public MessageAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message item = getItem(position);
		ViewHolder mViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_person_message,
					null);
			mViewHolder = new ViewHolder();
			mViewHolder.messageItem = (LinearLayout) convertView
					.findViewById(R.id.message_item);
			mViewHolder.headPhotoItem = (FrameLayout) convertView
					.findViewById(R.id.flt_message_headPhoto);
			mViewHolder.headPhoto = (ImageView) convertView
					.findViewById(R.id.message_headPhoto);
			mViewHolder.messageTxt = (LinearLayout) convertView
					.findViewById(R.id.llt_message_text);
			mViewHolder.name = (TextView) convertView
					.findViewById(R.id.message_name);
			mViewHolder.time = (TextView) convertView
					.findViewById(R.id.message_time);
			mViewHolder.title = (TextView) convertView
					.findViewById(R.id.message_title);
			mViewHolder.img = (ImageView) convertView
					.findViewById(R.id.message_image);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		displayImage(item.getAuthor().getIcon(), mViewHolder.headPhoto);
		displayImage(item.getImg(), mViewHolder.img);
		mViewHolder.messageItem.setOnClickListener(new OnItemClick(item));
		mViewHolder.img.setOnClickListener(new OnItemClick(item));
		mViewHolder.name.setText(item.getAuthor().getName());
		mViewHolder.time.setText(TimeUtil.parseTime(getContext(),
				item.getTime()));
		if (item.getNoteType() == NoteType._NOTE_COMMENT_TYPE) {
			mViewHolder.title.setText(item.getNote());
			mViewHolder.title.setTextColor(mContext.getResources().getColor(
					R.color.global_text_info_color));
			mViewHolder.title.setTextSize(10);
		} else if (item.getNoteType() == NoteType._NOTE_LIKE_TYPE) {
			mViewHolder.title.setText(mContext.getResources().getString(
					R.string.praise_message));
			mViewHolder.title.setTextColor(mContext.getResources().getColor(
					R.color.global_lv_primary_text));
			mViewHolder.title.setTextSize(15);
		} else if (item.getNoteType() == NoteType._NOTE_REPPLY_TYPE) {
			mViewHolder.title.setText(item.getNote());
			mViewHolder.title.setTextColor(mContext.getResources().getColor(
					R.color.global_text_info_color));
			mViewHolder.title.setTextSize(10);
		} else {
			mViewHolder.title.setText(item.getNote());
			mViewHolder.title.setTextColor(mContext.getResources().getColor(
					R.color.global_text_info_color));
			mViewHolder.title.setTextSize(10);
		}
		return convertView;
	}

	class OnItemClick implements OnClickListener {
		Message item;

		public OnItemClick(Message item) {
			this.item = item;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.message_item:
				if (item != null) {
					if (item.getNoteType() == NoteType._NOTE_COMMENT_TYPE||item.getNoteType() == NoteType._NOTE_REPPLY_TYPE) {
						BaseComment comment = new BaseComment();
						comment.setId(item.getCommentId());
						comment.setAuthor(item.getAuthor());
						TopicDetailCommentActivity
								.startActivityForResultFromFragment(mContext,
										item.getTopicId(), comment, 1);
					} else {
						TopicDetailActivity.startTopicDetailActivity(mContext,
								item.getTopicId());
					}
				}
				break;
			case R.id.message_image:
				if (item != null) {
					TopicDetailActivity.startTopicDetailActivity(mContext,
							item.getTopicId());
				}
				break;
			default:
				break;
			}

		}

	}

	static class ViewHolder {
		LinearLayout messageItem;

		FrameLayout headPhotoItem;

		ImageView headPhoto;

		LinearLayout messageTxt;

		TextView name;

		TextView time;

		TextView title;

		ImageView img;

	}

}
