package com.yy.android.gamenews.ui.view;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class CustomPagerAdapter extends PagerAdapter {

	private View mCurrentView;
	@Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View)object;
    }
                                             
    public View getPrimaryItem() {
        return mCurrentView;
    }
}
