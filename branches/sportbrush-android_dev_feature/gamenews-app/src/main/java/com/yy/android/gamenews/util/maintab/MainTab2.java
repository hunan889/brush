package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.NewsFragment;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory.PageType;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public class MainTab2 extends MainTab {

	public MainTab2(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, MainTabEvent.TAB_ORDER_INFO, savedInstance);
	}

	public MainTab2(MainActivity context, ActionBar actionbar, String fromTab,
			Bundle savedInstance) {
		super(context, actionbar, fromTab, savedInstance);
	}

	@Override
	protected void custActionbarRight() {
		mActionBar.setRightTextVisibility(View.GONE);
		mActionBar.setOnRightClickListener(null);
		mActionBar.getRightImageView().setImageResource(0);
		mActionBar.setRightVisibility(View.INVISIBLE);
	}

	/**
	 * 第二个tab默认为频道文章列表
	 */
	@Override
	public Fragment initFragment() {
		return ViewPagerFragmentFactory.createViewPagerFragment(PageType.NEWS,
				null);
	}

	@Override
	public String getDisplayName() {
		return mContext.getString(R.string.mine);
	}

	@Override
	public void refresh() {
		NewsFragment fragment = (NewsFragment) getFragment();
		fragment.refreshChannelPager();
		fragment.showTab(0);
	}

	@Override
	protected int getButtonDrawableResource() {
		return R.drawable.main_my_favor_btn_selector;
	}

	public static final int INDEX = 1;

	@Override
	public int getId() {
		return INDEX;
	}
}
