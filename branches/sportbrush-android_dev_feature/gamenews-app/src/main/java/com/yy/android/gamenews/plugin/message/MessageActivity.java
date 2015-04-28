package com.yy.android.gamenews.plugin.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class MessageActivity extends BaseActivity {

	private static final String KEY_FRAGMENT = "fragment";
	private MessageFragment mFragment;
	private ActionBar mActionBar;
	private Preference mPref;

	public static void startMessageActivity(Context context) {
		Intent intent = new Intent(context, MessageActivity.class);
		context.startActivity(intent);
		String eventId = "status_see_message";
		String key = "person_message";
		String value = "person_message";
		StatsUtil.statsReport(context, eventId, key, value);
		StatsUtil.statsReportByMta(context, eventId, key, value);
		StatsUtil.statsReportByHiido(eventId, value);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle(getResources().getString(R.string.person_message));
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mFragment = (MessageFragment) getSupportFragmentManager()
				.findFragmentByTag(KEY_FRAGMENT);
		mPref = Preference.getInstance();
		mPref.setNotifacation(null);
	}

	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragment == null) {
			mFragment = new MessageFragment();
			transaction.add(R.id.container, mFragment, KEY_FRAGMENT);
		}
		transaction.show(mFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}

}
