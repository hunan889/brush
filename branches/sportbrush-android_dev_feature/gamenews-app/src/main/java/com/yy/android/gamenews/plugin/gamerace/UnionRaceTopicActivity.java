package com.yy.android.gamenews.plugin.gamerace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.gamenews.RaceTopicInfo;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public class UnionRaceTopicActivity extends BaseActivity {
	private static final String TAG = UnionRaceTopicActivity.class
			.getSimpleName();
	public static final int _RACE_TOPIC = 1;
	public static final int _RACE_TOPIC_ID = 2;
	protected static final String RACE_TOPIC = "race_topic";
	protected static final String RACE_TOPIC_ID = "race_topic_id";
	protected static final String RACE_TOPIC_TYPE = "race_topic_type";
	protected int mTpye;
	protected long mTopicId;
	protected ActionBar mActionBar;
	private RaceTopicInfo raceTopicInfo;
	private UnionRaceTopicFragment fragment;

	public static void startRaceTopicActivity(Context context,
			RaceTopicInfo raceTopicInfo, int type) {
		Intent intent = new Intent(context, UnionRaceTopicActivity.class);
		intent.putExtra(UnionRaceTopicActivity.RACE_TOPIC, raceTopicInfo);
		intent.putExtra(UnionRaceTopicActivity.RACE_TOPIC_TYPE, type);
		context.startActivity(intent);
	}

	public static void startRaceTopicActivity(Context context, long topicId,
			int type) {
		Intent intent = new Intent(context, UnionRaceTopicActivity.class);
		intent.putExtra(UnionRaceTopicActivity.RACE_TOPIC_ID, topicId);
		intent.putExtra(UnionRaceTopicActivity.RACE_TOPIC_TYPE, type);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_race_topic);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mTpye = getIntent().getExtras().getInt(RACE_TOPIC_TYPE, 0);
		if (mTpye == _RACE_TOPIC) {
			raceTopicInfo = (RaceTopicInfo) getIntent().getExtras()
					.getSerializable(RACE_TOPIC);
			if (raceTopicInfo != null) {
				mActionBar.setTitle(raceTopicInfo.getName());
			}
		} else if (mTpye == _RACE_TOPIC_ID) {
			mTopicId = getIntent().getExtras().getLong(RACE_TOPIC_ID, -1);
		} else {
			return;
		}
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (savedInstanceState != null) {
			fragment = (UnionRaceTopicFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG);
		} else {
			if (mTpye == _RACE_TOPIC) {
				fragment = UnionRaceTopicFragment.newInstance();
				fragment.setData(raceTopicInfo);
			} else if (mTpye == _RACE_TOPIC_ID) {
				fragment = UnionRaceTopicFragment.newInstance();
				fragment.setData(mTopicId);
			} else {
				return;
			}
			transaction.add(R.id.container, fragment, TAG);
		}
		transaction.show(fragment);
		transaction.commitAllowingStateLoss();
	}
}
