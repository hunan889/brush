package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.gamenews.UserInitRsp;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.ChannelArticleInfoFragment;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.MyHomeActivity;
import com.yy.android.gamenews.ui.NewsFragment;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.tab.FrameFragmentItem;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public abstract class MainTab extends FrameFragmentItem {

	protected MainActivity mContext;
	protected ActionBar mActionBar;
	protected Preference mPref;
	private SwitchImageLoader mImageLoader;
	private String currentTab;
	private String tag;
	private String displayName;

	public MainTab(MainActivity context, ActionBar actionbar, String fromTab,
			Bundle savedInstance) {

		mContext = context;
		mActionBar = actionbar;
		currentTab = fromTab;
		mPref = Preference.getInstance();
		mImageLoader = SwitchImageLoader.getInstance();

		Fragment fragment = null;
		displayName = getDisplayName();
		if (savedInstance != null) { // onSaveInstanceState里保存的当前选择的tab
			fragment = mContext.getSupportFragmentManager().findFragmentByTag(
					displayName);
		}

		if (fragment == null) {
			fragment = initFragment();
		}

		setName(displayName);
		setFragment(fragment);

		MainTabBtn button = new MainTabBtn(mContext);
		button.setTitle(displayName);
		button.setDrawableResource(getButtonDrawableResource());
		setButton(button);
	}

	@Override
	protected void onHidden() {
		super.onHidden();
	}

	@Override
	protected void onShown() {

		String eventId = "stats_change_tab";
		String key = "key_change_tab";
		String value = displayName;
		StatsUtil.statsReportAllData(mContext, eventId, key, value);
		customizeActionbar();
		super.onShown();
	}

	protected void customizeActionbar() {
		custActionbarLeft();
		custActionbarTitle();
		custActionbarRight();
	}
	
	protected void custActionbarTitle() {

		mActionBar.setTitle(displayName);
		if (canReload()) {
			mActionBar.showLoadingbar(true);
			mActionBar.getLoadingLayout().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							refresh();
						}
					});
		} else {
			mActionBar.showLoadingbar(false);
			mActionBar.getLoadingLayout().setOnClickListener(null);
		}

	}

	protected void custActionbarLeft() {
		if (mPref.isUserLogin()) {
			UserInitRsp user = mPref.getInitRsp();
			if (user != null && user.getUser().getIcon() != null) {
				mActionBar.showLeftImgBorder(true);
				mImageLoader.displayImage(user.getUser().getIcon(),
						mActionBar.getLeftImageView(), true);
			} else {
				mActionBar.showLeftImgBorder(false);
				mActionBar.setLeftImageResource(R.drawable.ic_person_default);
			}
		} else {
			mActionBar.showLeftImgBorder(false);
			mActionBar.setLeftImageResource(R.drawable.ic_person_default);
		}

		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MyHomeActivity.startMyHomeActivityForResult(mContext);
				MainTabStatsUtil.statistics(mContext, currentTab,
						MainTabEvent.INTO_MY_HOME_CENTER,
						MainTabEvent.INTO_MY_HOME_CENTER_NAME);
			}
		});
	
		mContext.showPersonMessage();
	}

	public void checkExpire() {
		Fragment mFragment = getFragment();
		if (mFragment == null) {
			return;
		}
		if (mFragment instanceof ChannelArticleInfoFragment) {

			((ChannelArticleInfoFragment) mFragment).checkExpire();
		} else if (mFragment instanceof NewsFragment) {

			((NewsFragment) mFragment).checkExpireCurrent();
		}
	}

	protected boolean canReload() {
		return false;
	}

	public void refresh() {

	}

	protected abstract void custActionbarRight();

	protected abstract Fragment initFragment();

	protected abstract int getButtonDrawableResource();

	protected abstract String getDisplayName();

	public abstract int getId();

}
