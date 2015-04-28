package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.show.BaseComment;
import com.duowan.show.Comment;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.CommentActivity;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TopicDetailCommentActivity extends CommentActivity {

	public static final String KEY_TYPE = "key_type";
	public static final String KEY_TOPIC_ID = "topic_id";
	public static final String KEY_REPLY_COMMENT = "reply_comment";
	public static final String KEY_COMMENT_OBJ = "comment_obj";

	private BaseComment mReplyComment;
	private int mType = 0;// 0:来自于正常回复 1：来自于个人消息回复
	private int mTopicId;

	/**
	 * 
	 * @param context
	 * @param topicId
	 * @param comment
	 * @param requestCode
	 * @param type // 0:来自于正常回复 1：来自于个人消息回复
	 *            
	 */
	public static void startActivityForResultFromFragment(Fragment context,
			int topicId, BaseComment comment, int requestCode, int type) {
		Intent intent = new Intent(context.getActivity(),
				TopicDetailCommentActivity.class);

		intent.putExtra(KEY_TOPIC_ID, topicId);
		intent.putExtra(KEY_REPLY_COMMENT, comment);
		intent.putExtra(KEY_TYPE, type);
		String hint = "";
		if (comment != null) {
			hint = "回复" + comment.getAuthor().name;
			intent.putExtra(KEY_HINT, hint);
		}

		context.startActivityForResult(intent, requestCode);
	}

	public static void startActivityForResultFromFragment(Context context,
			int topicId, BaseComment comment, int type) {
		Intent intent = new Intent(context,
				TopicDetailCommentActivity.class);

		intent.putExtra(KEY_TOPIC_ID, topicId);
		intent.putExtra(KEY_REPLY_COMMENT, comment);
		intent.putExtra(KEY_TYPE, type);
		String hint = "";
		if (comment != null) {
			hint = "回复" + comment.getAuthor().name;
			intent.putExtra(KEY_HINT, hint);
		}

		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mType = getIntent().getIntExtra(KEY_TYPE, 0);
		if (mType == 1) {
			mReplyComment = (BaseComment) getIntent().getSerializableExtra(
					KEY_REPLY_COMMENT);
			mTopicId = getIntent().getIntExtra(KEY_TOPIC_ID, -1);
		} else if (mType == 0) {
			mReplyComment = (BaseComment) getIntent().getSerializableExtra(
					KEY_REPLY_COMMENT);
			mTopicId = getIntent().getIntExtra(KEY_TOPIC_ID, -1);
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void sendComment(final String comment) {
		String replyId = "";
		if (mReplyComment != null) {
			replyId = mReplyComment.getId();
		}
		ShowModel.sendComment(new ResponseListener<Object>(this) {

			@Override
			public void onResponse(Object arg0) {

				Toast.makeText(TopicDetailCommentActivity.this,
						R.string.show_reply_comment_succ, Toast.LENGTH_SHORT)
						.show();
				if (mType == 1) {
					fromPersonMessageSuccess();
				} else if (mType == 0) {
					CommentEvent commentEvent = new CommentEvent();
					commentEvent.setId(mTopicId);
					EventBus.getDefault().post(commentEvent);

					Intent intent = new Intent();
					intent.putExtra(KEY_COMMENT_OBJ, getCommentObj(comment));

					setResult(RESULT_OK, intent);
					finish();
				}

			}
		}, comment, replyId, mTopicId);
	}

	private void fromPersonMessageSuccess() {
		if (mTopicId > 0) {
			TopicDetailActivity.startTopicDetailActivity(
					TopicDetailCommentActivity.this, mTopicId);
			finish();
		}
	}

	private Comment getCommentObj(String comment) {
		Comment commentObject = new Comment();
		commentObject.setReplyComment(mReplyComment);

		BaseComment baseComment = new BaseComment();
		baseComment.setCreateTime((int) (System.currentTimeMillis() / 1000));
		baseComment.setContent(comment);
		com.duowan.show.User showUser = new com.duowan.show.User();
		UserInitRsp rsp = Preference.getInstance().getInitRsp();
		com.duowan.gamenews.User localUser = null;
		if (rsp != null) {
			localUser = rsp.getUser();
		}

		if (localUser != null) {
			showUser.setIcon(localUser.getIcon());
			showUser.setId(localUser.getId());
			showUser.setName(localUser.getName());
		}

		baseComment.setAuthor(showUser);
		baseComment.setId("");

		commentObject.setComment(baseComment);

		return commentObject;
	}
}
