package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.plugin.PluginManager;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

/**
 * 
 * @author Administrator
 *
 */
public class MainTab2Carbrush extends MainTab2{

	public MainTab2Carbrush(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, savedInstance);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void custActionbarRight() {
		
		mActionBar.getRightImageView().setImageResource(
				R.drawable.plugin_cartport_selecteor);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.setOnRightClickListener(mOnRightClickListener);
	}
	
	private OnClickListener mOnRightClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			PluginManager.startPlugin(mContext, PluginManager.PLUGIN_CARPORT);	
			
			MainTabStatsUtil.statistics(mContext, MainTabEvent.TAB_ORDER_INFO,
					MainTabEvent.INTO_CHANNEL_STORAGE,
					MainTabEvent.INTO_CHANNEL_STORAGE_NAME);
		}
	};
}
