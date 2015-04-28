package com.yy.android.gamenews.util.maintab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.ChannelDepotActivity;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

/**
 * 特性：体育刷子右上角添加频道
 * 
 * @author liuchaoqun
 * @lastmodify yeyuelai 2014-10-16
 */
public class MainTab2Sportbrush extends MainTab2 {

	public MainTab2Sportbrush(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, savedInstance);
	}

	@Override
	protected void custActionbarRight() {

		mActionBar.getRightImageView().setImageResource(
				R.drawable.btn_add_channel_selector);
		mActionBar.getRightTextView().setText("");
		mActionBar.setRightTextVisibility(View.GONE);
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
