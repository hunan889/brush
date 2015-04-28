package com.yy.android.gamenews.plugin.gamerace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.ui.ViewPagerAdapter;
import com.yy.android.gamenews.ui.ViewPagerFragment;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class AssociationEntryFragment extends ViewPagerFragment {
	private List<String> titleList;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		showView(VIEW_TYPE_DATA);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected PagerAdapter getAdapter() {
		String[] channelNames = getResources().getStringArray(
				R.array.association_tabs);

		titleList = new ArrayList<String>();
		Collections.addAll(titleList, channelNames);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mSectionsPagerAdapter.updateDataSource(titleList);

		return mSectionsPagerAdapter;
	}

	public class SectionsPagerAdapter extends ViewPagerAdapter<String> {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getData(position);
		}

		public Fragment getFragment(int position) {
			Fragment fragment = null;

			switch (position) {
			case 0: {
				fragment = new GameSquareFragment();
				break;
			}
			case 1: {
				fragment = new UnionTabFragment();
				break;
			}
			case 2: {
				fragment = new WonderfulRaceFragment();
				break;
			}
			}

			return fragment;
		}

	}

	public void onEvent(SubscribeEvent e) {

	}
	
	@Override
	protected boolean needCheckDivide() {
		return true;
	}
	
	@Override
	protected void onViewPageSelected(int index) {
		String eventId = MainTabEvent.TAB_GAMERACE_INFO;
		String key = null;
		if(index == 0){
			key = MainTabEvent.INTO_GAMERACE_SQUARE;
		}else if(index == 1){
			key = MainTabEvent.INTO_UNION_TAB;
		}else if(index == 2){
			key = MainTabEvent.INTO_RACE_TOPIC;
		}
		MainTabStatsUtil.statistics(getActivity(), eventId, key, titleList.get(index));
	}
}
