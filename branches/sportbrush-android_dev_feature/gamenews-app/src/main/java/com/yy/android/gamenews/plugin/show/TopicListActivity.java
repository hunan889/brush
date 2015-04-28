package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.show.Tag;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.TagSuccessEvent;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TopicListActivity extends BaseActivity {

	private static final String TAG = TopicListActivity.class.getSimpleName();
	protected static final String KEY_TAG = "tag_id";
	private ActionBar mActionBar;
	private TopicListFragment topicListFragment;

	public static void startTopicListActivity(Context context, Tag tag) {
		Intent intent = new Intent(context, TopicListActivity.class);
		intent.putExtra(KEY_TAG, tag);
		context.startActivity(intent);
		StatsUtil.statsReport(context, "into_tag_detail", "tagName",
				tag.getName());
		StatsUtil.statsReportByHiido("into_tag_detail", tag.getName());
		StatsUtil.statsReportByMta(context, "into_tag_detail", tag.getName());
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.show_topic_list_activity);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.getRightImageView().setImageResource(
				R.drawable.show_new_post_selector);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mActionBar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UiUtils.sendTopicCheckLogin(TopicListActivity.this);
			}
		});

		Tag tag = (Tag) getIntent().getSerializableExtra(KEY_TAG);

		mActionBar.setTitle(tag.getName());
		if (bundle != null) {
			topicListFragment = (TopicListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG);
		} else {
			topicListFragment = TopicListFragment.newInstance(tag.getId());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, topicListFragment, TAG)
					.commit();
		}
		EventBus.getDefault().register(this);
	}

	public void onEvent(TagSuccessEvent event) {
		if (event != null) {
			boolean state = event.isSuccess();
			if (state) {
				finish();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 登录完成开始发表话题
		if (requestCode == Constants.REQUEST_LOGIN_REDIRECT
				&& resultCode == RESULT_OK) {
			TagListActivity.startTagListActivity(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
