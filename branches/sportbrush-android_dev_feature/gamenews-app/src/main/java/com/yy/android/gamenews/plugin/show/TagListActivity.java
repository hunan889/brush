package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.TagSuccessEvent;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TagListActivity extends BaseActivity {

	private static final String TAG = TagListActivity.class.getSimpleName();
	private ActionBar mActionBar;
	private TagListFragment tagListFragment;

	public static void startTagListActivity(Context context) {
		Intent intent = new Intent(context, TagListActivity.class);
		context.startActivity(intent);
		StatsUtil.statsReport(context, "into_tag_list", "desc",
				"into_tag_list");
		StatsUtil.statsReportByHiido("into_tag_list", "into_tag_list");
		StatsUtil.statsReportByMta(context, "into_tag_list", "into_tag_list");
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		EventBus.getDefault().register(this);
		setContentView(R.layout.show_topic_list_activity);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mActionBar.setTitle(getResources().getString(R.string.select_tag));
		if (bundle != null) {
			tagListFragment = (TagListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG);
		} else {
			tagListFragment = new TagListFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, tagListFragment, TAG)
					.commit();
		}
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
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
