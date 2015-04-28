package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory.PageType;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public class MainTab3Gamenews extends MainTab3 {

	public MainTab3Gamenews(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, MainTabEvent.TAB_GAMERACE_INFO, savedInstance);
	}

	@Override
	protected void custActionbarRight() {
		mActionBar.setRightTextVisibility(View.GONE);
		mActionBar.setOnRightClickListener(null);
		mActionBar.getRightImageView().setImageResource(0);
		mActionBar.setRightVisibility(View.INVISIBLE);
	}

	@Override
	protected Fragment initFragment() {
		return ViewPagerFragmentFactory.createViewPagerFragment(
				PageType.ASSOCIATION_ENTRY, null);
	}

}
