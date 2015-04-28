package com.yy.android.gamenews.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory.PageType;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ViewPagerActivity extends BaseActivity {
	// private static final String TAG =
	// ViewPagerActivity.class.getSimpleName();
	private ViewPagerFragment mFragment;
	public static final String KEY_PAGE_TYPE = "";
	private static final String KEY_FRAGMENT = "fragment";
	public static final String KEY_TITLE = "title";
	private String mTitle;
	private PageType mPageType;
	private ActionBar mActionBar;

	public static void startViewPagerActivity(Context context, PageType type,
			String title, Bundle bundle) {
		Intent intent = new Intent(context, ViewPagerActivity.class);
		intent.putExtra(KEY_PAGE_TYPE, type);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(ViewPagerFragmentFactory.KEY_BUNDLE, bundle);
		context.startActivity(intent);
	}

	public static void startViewPagerActivity(Context context, PageType type,
			String title) {
		startViewPagerActivity(context, type, title, null);
	}

	@Override
	protected void onCreate(Bundle data) {
		super.onCreate(data);
		setContentView(R.layout.activity_view_pager);

		Intent intent = getIntent();
		if (intent != null) {
			mPageType = (PageType) intent.getSerializableExtra(KEY_PAGE_TYPE);
			mTitle = intent.getStringExtra(KEY_TITLE);
		}
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle(mTitle);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		mActionBar.showLoadingbar(true);
		mActionBar.getLoadingLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mFragment != null) {
					mFragment.refreshCurrent();
				}
			}
		});
		mFragment = (ViewPagerFragment) getSupportFragmentManager()
				.findFragmentByTag(KEY_FRAGMENT);
	}

	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragment == null) {

			Bundle args = getIntent().getBundleExtra(
					ViewPagerFragmentFactory.KEY_BUNDLE);
			mFragment = ViewPagerFragmentFactory.createViewPagerFragment(
					mPageType, args);
			transaction.add(R.id.container, mFragment, KEY_FRAGMENT);
		}
		transaction.show(mFragment);
		transaction.commitAllowingStateLoss();
		EventBus.getDefault().register(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	public void onEvent(FragmentCallbackEvent event) {
		if (event == null || (event.mTarget != this && event.mTarget != null)) {
			return;
		}

		int eventType = event.mEventType;
		switch (eventType) {
		case FragmentCallbackEvent.FRGMT_LIST_REFRESHING: {
			mActionBar.startLoading();
			break;
		}

		case FragmentCallbackEvent.FRGMT_LIST_REFRESH_DONE: {
			mActionBar.stopLoading();
			break;
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment fs = getSupportFragmentManager().findFragmentByTag(
				ArticleSocialDialog.TAG_SOCIAL_DIALOG);
		if (fs != null && fs.isAdded() && fs instanceof ArticleSocialDialog) {
			((ArticleSocialDialog) fs).onActivityResult(requestCode,
					resultCode, data);
		}
	}

}