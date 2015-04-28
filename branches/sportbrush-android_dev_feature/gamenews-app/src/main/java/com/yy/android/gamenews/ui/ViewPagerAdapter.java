package com.yy.android.gamenews.ui;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class ViewPagerAdapter<E> extends FragmentStatePagerAdapter {
	private List<E> mDatasource;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void updateDataSource(List<E> newTitles) {
		mDatasource = newTitles;
	}

	public final List<E> getDatasource() {
		return mDatasource;
	}

	@Override
	public final Fragment getItem(int position) {

		return getFragment(position);
	}

	public final E getData(int position) {
		return mDatasource == null ? null : mDatasource.get(position);
	}

	@Override
	public int getCount() {
		return mDatasource == null ? 0 : mDatasource.size();
	}

	public abstract CharSequence getPageTitle(int position);

	public abstract Fragment getFragment(int position);

}
