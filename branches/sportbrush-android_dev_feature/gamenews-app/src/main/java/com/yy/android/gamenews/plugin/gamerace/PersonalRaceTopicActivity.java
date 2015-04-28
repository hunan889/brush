package com.yy.android.gamenews.plugin.gamerace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.plugin.gamerace.PersonalRaceTopicFragment.OnDataLoadedListener;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public class PersonalRaceTopicActivity extends BaseActivity {

	private static final String TAG = PersonalRaceTopicActivity.class
			.getSimpleName();
	public static final String RACE_TOPIC = "personalRaceTopic";
	private ActionBar mActionBar;
	private PersonalRaceTopicFragment personalRaceTopicFragment;

	public static void startActivity(Context context, long raceTopicId) {
		Intent intent = new Intent(context, PersonalRaceTopicActivity.class);
		intent.putExtra(RACE_TOPIC, raceTopicId);
		context.startActivity(intent);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.union_info_view);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		long raceTopicId = getIntent().getLongExtra(RACE_TOPIC, -1L);

		if (savedInstanceState != null) {
			personalRaceTopicFragment = (PersonalRaceTopicFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG);
		} else {
			personalRaceTopicFragment = PersonalRaceTopicFragment
					.newInstance(raceTopicId);
		}
		personalRaceTopicFragment
				.setOnDataLoadedListener(new OnDataLoadedListener() {

					@Override
					public void onDataLoaded(String title) {
						mActionBar.setTitle(title);
					}
				});
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, personalRaceTopicFragment, TAG)
				.commit();
	}
}
