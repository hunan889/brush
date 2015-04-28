package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.AppWebActivity;
import com.yy.android.gamenews.ui.AppWebFragment;
import com.yy.android.gamenews.ui.AppWebFragment.OnNavigationChangeListener;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class MainTab5Gamenews extends MainTab5 {

	public MainTab5Gamenews(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, "", savedInstance);
	}

	private boolean mCanBack;

	private static String URL = Constants.GIFT_URL;

	@Override
	protected Fragment initFragment() {
		AppWebFragment fragment = AppWebFragment.getInstanceWithYYToken(
				mContext, URL, true, false);

		fragment.setOnNavigationChangeListener(new OnNavigationChangeListener() {

			@Override
			public void onNavChanged(boolean canForward, boolean canBackward) {
//				if (canBackward == mCanBack) {
//					return;
//				}
				mCanBack = canBackward;
				if (canBackward) {
					custBackward();
				} else {
					customizeActionbar();
				}

			}
		});
		return fragment;
	}

	@Override
	protected void custActionbarLeft() {
		if (mCanBack) {
			custBackward();
		} else {
			super.custActionbarLeft();
		}
	}

	private void custBackward() {
		if (!isVisible()) {
			return;
		}
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppWebFragment fragment = (AppWebFragment) getFragment();
				fragment.goBack();
			}
		});

		mActionBar.showLeftImgBorder(false);
		mActionBar.setLeftImageResource(R.drawable.actionbar_back_selector);
		mActionBar.setLeftMsgCountVisibility(View.GONE);
		mActionBar.setLeftMsgHintVisibility(View.GONE);

		mActionBar.setRightImageResource(R.drawable.ic_main_home_selector);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppWebFragment fragment = (AppWebFragment) getFragment();
				fragment.loadUrlClearTop(URL);
			}
		});
	}

	@Override
	protected void custActionbarRight() {
		super.custActionbarRight();
	}

	@Override
	protected void onItemClick() {
		// String giftUrl = mPref.getGiftAddressContent();
		// if (giftUrl != null) {
		// AppWebActivity.startWebActivityWithYYToken(mContext, giftUrl, true);
		// } else {
		// AppWebActivity.startWebActivityWithYYToken(mContext,
		// Constants.GIFT_URL, true);
		// }
		//
		// StatsUtil.statsReportAllData(mContext, "gamenews_gift_event",
		// "event_key", "gamenews_gift_event");// 游戏刷子礼包统计
		//
		// int lastTabIndex = mPref.getLastTabIndex();
		// MainTabStatsUtil.addchangeTabStatistics(mContext, lastTabIndex,
		// INDEX);
		super.onItemClick();
	}

	@Override
	public void refresh() {
		AppWebFragment fragment = (AppWebFragment) getFragment();
		fragment.reload();
		super.refresh();
	}

	public void refreshAfterLoginChanged() {
		AppWebFragment fragment = (AppWebFragment) getFragment();
		if (fragment != null) {
			fragment.loadUrlClearTopWithToken(URL);
		}
	}

	@Override
	protected boolean canReload() {
		return true;
	}
}
