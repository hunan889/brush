package com.yy.android.gamenews.plugin.schetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public class TeamMoreActivity extends BaseActivity {
	private static final String TAG = TeamListFragment.class.getSimpleName();
	public static final String LEAGUE_ID = "leagueId";
	public static final String LEAGUE_NAME = "leagueName";
	private ActionBar mActionBar;
	private TeamMoreFragment mFragment;
	
	public static void startActivity(Context context, String leagueId, String leagueName) {
		Intent intent = new Intent(context, TeamMoreActivity.class);
		intent.putExtra(LEAGUE_ID, leagueId);
		intent.putExtra(LEAGUE_NAME, leagueName);
		context.startActivity(intent);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_more);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		String leagueId = getIntent().getStringExtra(LEAGUE_ID);
		String leagueName = getIntent().getStringExtra(LEAGUE_NAME);

		mActionBar.setTitle(leagueName);
		if (savedInstanceState != null) {
			mFragment = (TeamMoreFragment) getSupportFragmentManager().findFragmentByTag(TAG);
		} else {
			mFragment = TeamMoreFragment.newInstance(leagueId);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, mFragment, TAG).commit();
		}
	}
}
