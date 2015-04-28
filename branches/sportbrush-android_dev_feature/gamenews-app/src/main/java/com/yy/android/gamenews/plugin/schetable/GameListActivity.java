package com.yy.android.gamenews.plugin.schetable;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.SchedTabChangedEvent;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class GameListActivity extends BaseActivity{
	private static final String TAG = GameListActivity.class.getSimpleName();
	
	private ActionBar mActionBar;
	private SchedFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_schedule_table);

		setDataView(findViewById(R.id.fragment_container));

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mActionBar.setRightTextResource(R.string.sched_table_filter);

		mActionBar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFragment.showFilterView(mActionBar);
			}
		});
		
		setEmptyText(getString(R.string.sched_table_empty));
		
		if (savedInstanceState != null) {
			mFragment = (SchedFragment) getSupportFragmentManager().findFragmentByTag(TAG);
		} else {
			mFragment = SchedFragment.newInstance();
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
	
	public void onEvent(SchedTabChangedEvent event) {
		int visibility = event.getVisibility();
		((View)mActionBar.getRightImageView().getParent()).setVisibility(visibility);
	}
}
