package com.yy.android.gamenews.plugin.schetable;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class GameBroadcastActivity extends BaseActivity {
private static final String TAG = GameBroadcastActivity.class.getSimpleName();
	
	private ActionBar mActionBar;
	private GameBroadcastFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.game_broadcast);

		setDataView(findViewById(R.id.fragment_container));

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.getRightImageView().setVisibility(View.GONE);
		mActionBar.getRightTextView().setVisibility(View.GONE);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mActionBar.setRightTextResource(R.string.sched_table_filter);
		
		if (savedInstanceState != null) {
			mFragment = (GameBroadcastFragment) getSupportFragmentManager().findFragmentByTag(TAG);
		} else {
			mFragment = GameBroadcastFragment.newInstance();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, mFragment, TAG).commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
	};

	@Override
	protected void onEmptyViewClicked() {
	}
}
