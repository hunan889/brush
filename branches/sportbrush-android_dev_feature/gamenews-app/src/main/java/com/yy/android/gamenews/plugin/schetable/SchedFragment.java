package com.yy.android.gamenews.plugin.schetable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.SportRaceListRsp;
import com.duowan.gamenews.sportInfo;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.SchedTabChangedEvent;
import com.yy.android.gamenews.plugin.schetable.GameListFragment.GameListLoadedCallback;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.DropDownHelper;
import com.yy.android.gamenews.util.DropDownHelper.OnDropDownClickListener;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class SchedFragment extends BaseFragment implements OnClickListener, GameListLoadedCallback{
	
	private static final String TAG = SchedFragment.class.getSimpleName();
	private static final int TITLE_ALL_SCHED = 0;
	private static final int TITLE_ALL_TEAM = 1;
	private static final int TITLE_MY_ALARM = 2;
	
	private Button mAllSchedButton;
	private Button mAllTeamButton;
	private Button mAlarmButton;
	private ViewPager mViewPager;
	
	private int mFilterIndex; 
	private SportRaceListRsp mRsp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static SchedFragment newInstance() {
		SchedFragment fragment = new SchedFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sched, container, false);
		
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mSectionsPagerAdapter.updateDataSource(getTitles());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						showTab(position);
						
						FragmentCallbackEvent event = new FragmentCallbackEvent();
						event.mEventType = FragmentCallbackEvent.FRGMT_TAB_CHANGED;
						event.mFragment = SchedFragment.this;
						EventBus.getDefault().post(event);
						
						int visibility;
						if(position == TITLE_ALL_SCHED && mRsp != null && mRsp.getAllRaceList() != null && !mRsp.getAllRaceList().isEmpty()){
							visibility = View.VISIBLE;
						}else{
							visibility = View.INVISIBLE;
						}
						SchedTabChangedEvent schedTabChangedEvent = new SchedTabChangedEvent();
						schedTabChangedEvent.setVisibility(visibility);
						EventBus.getDefault().post(schedTabChangedEvent);
					}
				});
		mAllSchedButton = (Button) view.findViewById(R.id.all_sched_but);
		mAllTeamButton = (Button) view.findViewById(R.id.all_team_but);
		mAlarmButton = (Button) view.findViewById(R.id.my_alarm_but);
		mAllSchedButton.setSelected(true);
		mAllSchedButton.setOnClickListener(this);
		mAllTeamButton.setOnClickListener(this);
		mAlarmButton.setOnClickListener(this);
		
		return view;
	}
	
	public void showFilterView(ActionBar mActionBar){
		if(mViewPager.getCurrentItem() == TITLE_ALL_SCHED){
			
			if (mRsp == null || mRsp.getAllRaceList() == null || mRsp.getAllRaceList().isEmpty()) {
				return;
			}
			final List<sportInfo> infoList = mRsp.getSportList();
			if (infoList == null) {
				return;
			}
			String[] sportNameArray = new String[infoList.size() + 1];
			sportNameArray[0] = "全部";
			Object[] imageArray = new Object[infoList.size() + 1];
			imageArray[0] = R.drawable.dropdown_list_all_selector;

			for (int i = 1; i < sportNameArray.length; i++) {
				sportInfo info = infoList.get(i - 1);
				sportNameArray[i] = info.name;
				imageArray[i] = info.icon;
			}

			DropDownHelper.showDropDownList(getActivity(),
					mActionBar.getRightImageView(), sportNameArray,
					imageArray, mFilterIndex,
					new OnDropDownClickListener() {

						@Override
						public void onClick(int position, String text) {
							mFilterIndex = position;
//							if (position == 0) {
//								mActionBar.setTitle(getString(R.string.sched_table_title_all));
//							}else{
//								mActionBar.setTitle(text);
//							}
							Fragment fragment = getChildFragmentManager().getFragments().get(0);
							if(mViewPager.getCurrentItem() == TITLE_ALL_SCHED && fragment instanceof GameListFragment){
								((GameListFragment) fragment).filterData(position);
							}
							
							StatsUtil.statsReport(getActivity(), "filter_race",
									"sport_type", position == 0 ? "全部"
											: infoList.get(position - 1)
													.getName());
							StatsUtil.statsReportByHiido(
									"filter_race",
									position == 0 ? "全部" : infoList.get(
											position - 1).getName());
							StatsUtil.statsReportByMta(
									getActivity(),
									"filter_race",
									"sport_type",
									position == 0 ? "全部" : infoList.get(
											position - 1).getName());
						}
					});
		}
	}
	
	/**
	 * 体育刷子切换到赛事tab，重新请求赛事和球队数据
	 * @author yuelai.ye
	 */
	public void reloadSchedData(){
		List<Fragment> fragments = getChildFragmentManager().getFragments();
		if(fragments != null && fragments.size() > 0){
			Fragment fragment = getChildFragmentManager().getFragments().get(0);
			if(fragment instanceof GameListFragment){
				((GameListFragment) fragment).requestData();
			}
			if(fragments.size() > 1){
				fragment = getChildFragmentManager().getFragments().get(1);
				if(fragment instanceof TeamListFragment){
					((TeamListFragment) fragment).requestData(RefreshType._REFRESH_TYPE_REFRESH);
				}
			}
		}
	}
	
	@Override
    public void onDetach() {
    	super.onDetach();
    	try {
    	    Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
    	    childFragmentManager.setAccessible(true);
    	    childFragmentManager.set(this, null);

    	} catch (NoSuchFieldException e) {
    	    throw new RuntimeException(e);
    	} catch (IllegalAccessException e) {
    	    throw new RuntimeException(e);
    	}
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private List<String> getTitles() {
		List<String> titles = new ArrayList<String>();

		titles.add(getResources().getString(R.string.all_sched));
		titles.add(getResources().getString(R.string.all_team));
		titles.add(getResources().getString(R.string.my_alarm));
		return titles;
	}

	public void showTab(int position) {
		String eventKey = "";
		String param = "";
		if (position == TITLE_ALL_SCHED) {
			mAllSchedButton.setSelected(true);
			mAllTeamButton.setSelected(false);
			mAlarmButton.setSelected(false);
			eventKey = "into_all_race";
			param = getResources().getString(R.string.all_sched);
		} else if (position == TITLE_ALL_TEAM) {
			mAllSchedButton.setSelected(false);
			mAllTeamButton.setSelected(true);
			mAlarmButton.setSelected(false);
			eventKey = "into_all_team";
			param = getResources().getString(R.string.all_team);
		}else if(position == TITLE_MY_ALARM){
			mAllSchedButton.setSelected(false);
			mAllTeamButton.setSelected(false);
			mAlarmButton.setSelected(true);
			eventKey = "into_my_alarm";
			param = getResources().getString(R.string.my_alarm);
		}
		StatsUtil.statsReport(getActivity(), eventKey, "param", param);
		StatsUtil.statsReportByHiido(eventKey, param);
		StatsUtil.statsReportByMta(getActivity(), eventKey, param);
	}
	
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
		private List<String> mTitles;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void updateDataSource(List<String> newTitles) {
			mTitles = newTitles;
		}

		public List<String> getDatasource() {
			return mTitles;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (position == TITLE_ALL_SCHED) {
				fragment = new GameListFragment();
				((GameListFragment) fragment).setCallback(SchedFragment.this);
			} else if (position == TITLE_ALL_TEAM) {
				fragment = new TeamListFragment();
			}else if(position == TITLE_MY_ALARM){
				fragment = new AlarmListFragment();
			}
			return fragment;
		}

		public String getData(int position) {
			return mTitles.get(position);
		}

		@Override
		public int getCount() {
			return mTitles.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTitles.get(position);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_sched_but:
			mAllSchedButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_ALL_SCHED);
			break;
		case R.id.all_team_but:
			mAllTeamButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_ALL_TEAM);
			break;
		case R.id.my_alarm_but:
			mAlarmButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_MY_ALARM);
			break;
		}
	}
	
	@Override
	public void onLoaded(SportRaceListRsp mRsp) {
		this.mRsp = mRsp;
		int visibility;
		if (this.mRsp == null || this.mRsp.getAllRaceList() == null
				|| this.mRsp.getAllRaceList().isEmpty()
				|| mViewPager.getCurrentItem() != 0) {
			visibility = View.INVISIBLE;
		}else{
			visibility = View.VISIBLE;
		}
//		SchedTabChangedEvent schedTabChangedEvent = new SchedTabChangedEvent();
//		schedTabChangedEvent.setVisibility(visibility);
//		EventBus.getDefault().post(schedTabChangedEvent);
	}
	
	public int getCurrentItem() {
		if(mViewPager == null){
			return 0;
		}
		int currentItem = mViewPager.getCurrentItem();
		return currentItem;
	}

}
