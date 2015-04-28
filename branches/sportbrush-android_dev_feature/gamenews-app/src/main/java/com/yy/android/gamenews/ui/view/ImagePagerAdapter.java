package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImagePagerAdapter extends PagerAdapter {
	private List<View> mPageList = new ArrayList<View>();

	private FragmentManager mFragmentMgr;
	private Context mContext;
	private FragmentActivity mActivity;
	private LayoutInflater mInflater;


	public ImagePagerAdapter(Context context) {
		if (context instanceof FragmentActivity) {
			mActivity = (FragmentActivity) context;
		}
		mContext = context;
		mInflater = LayoutInflater.from(context);
		if (mActivity != null) {
			mFragmentMgr = mActivity.getSupportFragmentManager();
		}
	}

	public void updateDatasource(List<View> resList) {
		if (mPageList.size() > 0) {
			mPageList.clear();
		}

		if(resList != null) {
			mPageList.addAll(resList);
		}
		notifyDataSetChanged();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == arg1;
	}

	@Override
	public int getCount() {

		return mPageList.size();
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view = mPageList.get(position);
		container.removeView(view);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = mPageList.get(position);
		container.addView(view);
		return view;
	}
}
