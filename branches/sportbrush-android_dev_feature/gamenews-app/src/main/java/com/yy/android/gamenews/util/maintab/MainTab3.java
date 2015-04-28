package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;

import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public abstract class MainTab3 extends MainTab {

	public MainTab3(MainActivity context, ActionBar actionbar, String fromTab,
			Bundle savedInstance) {
		super(context, actionbar, fromTab, savedInstance);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDisplayName() {
		return mContext.getString(R.string.main_info_extra1);
	}

	@Override
	protected int getButtonDrawableResource() {
		return R.drawable.main_extra1_btn_selector;
	}

	public static final int INDEX = 2;

	@Override
	public int getId() {
		return INDEX;
	}
}
