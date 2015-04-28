package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.show.BaseComment;
import com.duowan.show.Comment;
import com.duowan.show.User;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

public class TopicCommentAdapter extends ImageAdapter<Comment> {

	private String mStrReply;

	public TopicCommentAdapter(Context context) {
		super(context);
		mStrReply = context.getString(R.string.show_reply_comment);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.show_topic_detail_comment_list_item, null);

			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.mContent = (TextView) convertView.findViewById(R.id.content);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.comment_time);
			holder.mUserLogo = (ImageView) convertView
					.findViewById(R.id.user_logo);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Comment item = getItem(position);
		if (item != null) {
			BaseComment selfComment = item.getComment();
			BaseComment replyComment = item.getReplyComment();

			SpannableStringBuilder content = new SpannableStringBuilder();
			if (replyComment != null) {
				User user = replyComment.getAuthor();
				if (user != null) {
					content.append(mStrReply);
					content.append(" ");
					if (user.getName() != null) {
						content.append(user.getName());
					}
					content.append("：");
				}

				TextView userNameTv = holder.mUserName;
				int defaultColor = userNameTv.getTextColors().getDefaultColor();
				float fontSize = userNameTv.getTextSize();

				content.setSpan(new ForegroundColorSpan(defaultColor), 0,
						content.length(),
						SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);

				content.setSpan(new AbsoluteSizeSpan((int) fontSize), 0,
						content.length(),
						SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
			}

			if (selfComment != null) {
				content.append(String.valueOf(selfComment.getContent()));
				holder.mContent.setText(content);

				User me = selfComment.getAuthor();
				holder.mUserName.setText(me.getName());

				SwitchImageLoader.getInstance().displayImage(me.icon,
						holder.mUserLogo,
						SwitchImageLoader.DEFAULT_USER_DISPLAYER);

				holder.mTime.setText(TimeUtil.parseTime(getContext(),
						selfComment.getCreateTime()));
			}

		}
		return convertView;
	}

	private static class ViewHolder {
		TextView mUserName;
		TextView mTime;
		TextView mContent;
		ImageView mUserLogo;
	}

}
