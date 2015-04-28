package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.view.View;

import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

public abstract class MainTab5 extends MainTab {

	public MainTab5(MainActivity context, ActionBar actionbar, String fromTab,
			Bundle savedInstance) {
		super(context, actionbar, fromTab, savedInstance);
	}

	@Override
	public String getDisplayName() {
		return mContext.getString(R.string.main_info_extra3);
	}

	@Override
	protected void custActionbarRight() {
		mActionBar.setRightTextVisibility(View.GONE);
		mActionBar.setOnRightClickListener(null);
		mActionBar.getRightImageView().setImageResource(0);
		mActionBar.setRightVisibility(View.INVISIBLE);
	}

	@Override
	protected int getButtonDrawableResource() {
		return R.drawable.main_extra3_btn_selector;
	}

	public static final int INDEX = 4;

	@Override
	public int getId() {
		return INDEX;
	}
}