package com.yy.android.gamenews.util.maintab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.ChannelDepotActivity;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.channeldetail.ChannelListFragment;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class MainTab2Gamenews extends MainTab2 {

	public MainTab2Gamenews(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, savedInstance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment initFragment() {
		return new ChannelListFragment();
	}

	@Override
	public void refresh() {
		((ChannelListFragment) getFragment()).callRefresh();
	}

	@Override
	protected void custActionbarRight() {

		mActionBar.getRightImageView().setImageResource(
				R.drawable.btn_add_channel_selector);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.setOnRightClickListener(mOnRightClickListener);
	}

	private OnClickListener mOnRightClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(mContext, ChannelDepotActivity.class);
			mContext.startActivity(intent);

			StatsUtil.statsReport(mContext, "add_channel", "param", "进入频道仓库");
			StatsUtil.statsReportByHiido("add_channel", "进入频道仓库");
			StatsUtil.statsReportByMta(mContext, "add_channel", "进入频道仓库");

			MainTabStatsUtil.statistics(mContext, MainTabEvent.TAB_ORDER_INFO,
					MainTabEvent.INTO_CHANNEL_STORAGE,
					MainTabEvent.INTO_CHANNEL_STORAGE_NAME);
		}
	};
}
