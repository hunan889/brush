package com.yy.android.gamenews.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.yy.android.gamenews.plugin.cartport.FragmentMessageListener;
import com.yy.android.sportbrush.R;

public class AppWebFragmentActivity extends BaseActivity {

	private AppWebFragment mFragment;
	public static final String TAG_NAME_FRAGMENT = "fragment";

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_single_fragment);

		mFragment = (AppWebFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_NAME_FRAGMENT);

	}

	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragment == null) {
			mFragment = new AppWebFragment();
			transaction.add(R.id.container, mFragment, TAG_NAME_FRAGMENT);
			mFragment.setArguments(getIntent().getExtras());
		}
		transaction.show(mFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}

	public FragmentMessageListener getListener() {
		if (mFragment instanceof FragmentMessageListener) {
			return (FragmentMessageListener) mFragment;
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mFragment.onKeyDown(keyCode, event);
		return mFragment.onKeyDown(keyCode, event) ? true : super.onKeyDown(
				keyCode, event);
	}
}
