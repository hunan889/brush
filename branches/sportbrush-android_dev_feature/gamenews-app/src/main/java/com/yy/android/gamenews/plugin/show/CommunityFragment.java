package com.yy.android.gamenews.plugin.show;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.show.AdvInfo;
import com.duowan.show.AppInitOption;
import com.duowan.show.AppInitRsp;
import com.duowan.show.ServiceType;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.SendTopicEvent;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.AppWebActivity;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.view.BaseBannerView.OnBannerItemClickListener;
import com.yy.android.gamenews.ui.view.InfiniteBannerView;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class CommunityFragment extends BaseFragment implements OnTouchListener,
		OnClickListener, OnBannerItemClickListener {

	private static final int TITLE_SQUARE = 0;
	private static final int TITLE_HOT = 1;

	private Button mSquareButton;
	private Button mHotButton;
	private View advView;
	private InfiniteBannerView advBannerView;
	private ViewPager mViewPager;
	private List<String> titles;
	private AppInitRsp rsp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	public static CommunityFragment newInstance() {
		CommunityFragment fragment = new CommunityFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.show_community_fragment,
				container, false);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		titles = getTitles();
		mSectionsPagerAdapter.updateDataSource(titles);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						showTab(position);
					}
				});
		mSquareButton = (Button) view.findViewById(R.id.btn_square);
		mSquareButton.setText(titles.get(0));
		mHotButton = (Button) view.findViewById(R.id.btn_hot);
		mHotButton.setText(titles.get(1));
		mSquareButton.setSelected(true);
		mSquareButton.setOnClickListener(this);
		mHotButton.setOnClickListener(this);

		advView = view.findViewById(R.id.ll_notice);
		advView.setVisibility(View.GONE);
		advBannerView = (InfiniteBannerView) view
				.findViewById(R.id.adv_list_banner);
		view.findViewById(R.id.notice_view).setOnTouchListener(this);
		return view;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		advBannerView.dispatchTouchEvent(event);
		return false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
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
		loadAdv();
	}

	private void loadAdv() {
		ShowModel.getAdvInfo(new ResponseListener<AppInitRsp>(getActivity()) {

			@Override
			public void onResponse(AppInitRsp param) {
				if (param != null && param.getAdvInfo() != null
						&& param.getAdvInfo().size() > 0) {
					rsp = param;
					String allowEmptyTopicContent = rsp.getOption().get(
							AppInitOption._ALLOW_EMPTY_TOPIC_CONTENT);
					if (rsp.getOption() != null
							&& allowEmptyTopicContent != null) {
						Preference.getInstance().setAllowEmptyTopicContent(
								Integer.valueOf(allowEmptyTopicContent));
					}

					advView.setVisibility(View.VISIBLE);
					initAdvs(rsp.getAdvInfo());
				} else {
					advView.setVisibility(View.GONE);
				}
			}
		}, ServiceType._SERVICE_ADV_TYPE);
	}

	private void initAdvs(ArrayList<AdvInfo> advInfos) {
		NoticeListBannerAdapter noticeListBannerAdapter = new NoticeListBannerAdapter(
				getActivity());
		noticeListBannerAdapter.setDataSource(advInfos);
		advBannerView.setListAdapter(noticeListBannerAdapter);
		advBannerView.startScroll();
		advBannerView.setOnItemClickListener(this);
	}

	@Override
	public void onBannerItemClick(View view, ListAdapter adapter, int position) {
		NoticeListBannerAdapter bannerAdapter = (NoticeListBannerAdapter) adapter;
		if (bannerAdapter != null) {
			AdvInfo advInfo = bannerAdapter.getItem(position);
			if (advInfo != null) {
				AppWebActivity.startWebActivityFromNotice(getActivity(),
						advInfo.getUrl());
				StatsUtil.statsReport(getActivity(), "click_ad", "ad_title",
						advInfo.getTitle());
				StatsUtil.statsReportByHiido("click_ad", advInfo.getTitle());
				StatsUtil.statsReportByMta(getActivity(), "click_ad",
						advInfo.getTitle());
			}
		}
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
		EventBus.getDefault().unregister(this);
	}

	private List<String> getTitles() {
		List<String> titles = new ArrayList<String>();
		String[] array = getResources().getStringArray(R.array.show_tabs);

		Collections.addAll(titles, array);
		return titles;
	}

	public void showTab(int position) {
		String eventKey = "";
		String param = "";

		String key = null;

		if (position == TITLE_SQUARE) {
			mSquareButton.setSelected(true);
			mHotButton.setSelected(false);
			eventKey = "into_square";
			param = titles.get(0);
			key = MainTabEvent.INTO_TOPIC_SQUARE;
		} else if (position == TITLE_HOT) {
			mSquareButton.setSelected(false);
			mHotButton.setSelected(true);
			eventKey = "into_hot_tag";
			param = titles.get(1);
			key = MainTabEvent.INTO_TAG_BOARD;
		}
		StatsUtil.statsReport(getActivity(), eventKey, "param", param);
		StatsUtil.statsReportByHiido(eventKey, param);
		StatsUtil.statsReportByMta(getActivity(), eventKey, param);

		MainTabStatsUtil.statistics(getActivity(), MainTabEvent.TAB_COMMUNITY,
				key, param);
	}

	public void onEvent(SendTopicEvent sendTopicEvent) {
		if (sendTopicEvent != null && sendTopicEvent.getTopic() != null) {
			mSquareButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_SQUARE);
		}
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
			if (position == TITLE_SQUARE) {
				fragment = new SquareTopicListFragment();
			} else if (position == TITLE_HOT) {
				fragment = new HotTagListFragment();
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
		case R.id.btn_square:
			mSquareButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_SQUARE);
			break;
		case R.id.btn_hot:
			mHotButton.setSelected(true);
			mViewPager.setCurrentItem(TITLE_HOT);
			break;
		}
	}

	public int getCurrentItem() {
		if (mViewPager == null) {
			return 0;
		}
		int currentItem = mViewPager.getCurrentItem();
		return currentItem;
	}

}
