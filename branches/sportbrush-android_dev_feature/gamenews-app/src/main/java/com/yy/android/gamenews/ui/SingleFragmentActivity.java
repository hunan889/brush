package com.yy.android.gamenews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.yy.android.gamenews.plugin.cartport.FragmentMessageListener;
import com.yy.android.sportbrush.R;

public abstract class SingleFragmentActivity extends BaseActivity {
	private static final String TAG = SingleFragmentActivity.class
			.getSimpleName();
	public static final String TAG_NAME_FRAGMENT = "fragment";
	private Fragment mFragment;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_single_fragment);

		mFragment = getSupportFragmentManager().findFragmentByTag(
				TAG_NAME_FRAGMENT);
	}

	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragment == null) {
			mFragment = initFragment();// getFragmentByType(mType);
			Intent intent = getIntent();
			if (intent != null) {
				Bundle params = intent.getExtras();
				if (mFragment != null) {
					mFragment.setArguments(params);
				}
			}
			transaction.add(R.id.container, mFragment, TAG_NAME_FRAGMENT);
		}
		transaction.show(mFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}

	protected abstract Fragment initFragment();

	public FragmentMessageListener getListener() {
		if (mFragment instanceof FragmentMessageListener) {
			return (FragmentMessageListener) mFragment;
		}
		return null;
	}
}
