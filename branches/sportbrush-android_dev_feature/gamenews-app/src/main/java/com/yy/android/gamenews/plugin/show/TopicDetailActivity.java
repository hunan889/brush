package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yy.android.gamenews.ui.ArticleSocialDialog;
import com.yy.android.gamenews.ui.SingleFragmentActivity;
import com.yy.android.gamenews.util.StatsUtil;

public class TopicDetailActivity extends SingleFragmentActivity {

	public static final String KEY_ID = "topic_id";

	public static void startTopicDetailActivity(Context context, int topicId) {
		Intent intent = new Intent(context, TopicDetailActivity.class);
		intent.putExtra(KEY_ID, topicId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		StatsUtil.statsReport(context, "into_topic_detail", "desc",
				"into_topic_detail");
		StatsUtil.statsReportByHiido("into_topic_detail", "into_topic_detail");
		StatsUtil.statsReportByMta(context, "into_topic_detail",
				"into_topic_detail");
	}

	@Override
	protected Fragment initFragment() {
		// TODO Auto-generated method stub
		return new TopicDetailFragment();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Fragment fs = getSupportFragmentManager().findFragmentByTag(
				TopicDetailFragment.TAG_DIALOG);
		if (fs != null && fs.isAdded() && fs instanceof ArticleSocialDialog) {
			((ArticleSocialDialog) fs).onActivityResult(requestCode,
					resultCode, data);
		}
	}
}
