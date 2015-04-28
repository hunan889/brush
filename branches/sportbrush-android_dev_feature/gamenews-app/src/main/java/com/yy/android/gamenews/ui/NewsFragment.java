package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.duowan.Comm.ECommAppType;
import com.duowan.gamenews.ArticleCategory;
import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.CheckExpireEvent;
import com.yy.android.gamenews.event.ClickHotChannelEvent;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

public class NewsFragment extends ViewPagerFragment {
	private int mType = TYPE_MY_FAVOR;
	public static final int TYPE_HEADLINES = 1001; // 头条/广场
	public static final int TYPE_MY_FAVOR = 1002; // 我的

	@Override
	protected PagerAdapter getAdapter() {
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mSectionsPagerAdapter.updateDataSource(getTitles());
		return mSectionsPagerAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		if (bundle != null) {
			mType = bundle.getInt(KEY_NEWS_TYPE, TYPE_MY_FAVOR);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		showView(VIEW_TYPE_DATA);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		if (mEvent != null && mType == TYPE_MY_FAVOR) {
			boolean hasChanged = mEvent.isSubscribeChanged;
			boolean isMulti = mEvent.isSubscribeMultiple;
			if (hasChanged) {

				refreshChannelPager();

				if (isMulti) {
					showTab(0);
					refreshCurrent();
				} else {
					showTab(mTitles.getTitleCount() - 1);
				}

			}
		}
		mEvent = null;

		if (mNeedCheckExpireRefresh) {
			doCheckExpireCurrent();
		}
		super.onResume();
	}

	private List<Channel> getTitles() {
		List<Channel> channelList = null;
		switch (mType) {
		case TYPE_HEADLINES: {
			channelList = getHeadLineChannelList();
			break;
		}
		case TYPE_MY_FAVOR: {
			channelList = getMyFavorChannelList();
			break;
		}
		}
		return channelList;
	}

	private List<Channel> getHeadLineChannelList() {

		List<Channel> channelList = new ArrayList<Channel>();
		List<Channel> savedList = mPref.getTopChannelList();

		boolean needAdd = true;
		if (savedList != null) {
			for (Channel channel : savedList) {
				if (Constants.RECOMMD_ID == channel.getId()) {
					needAdd = false;
				}
				List<ArticleCategory> categoryList = channel.getCategoryList();

				if (categoryList != null) {
					for (ArticleCategory category : categoryList) {
						Channel subChannel = (Channel) channel.clone();
						subChannel.categoryList = new ArrayList<ArticleCategory>();
						subChannel.categoryList.add(category);
						subChannel.setName(category.getName());

						channelList.add(subChannel);
					}
				} else {
					channelList.add(channel);
				}
			}
		}

		if (needAdd) {
			Channel recomd = new Channel();
			recomd.setId(Constants.RECOMMD_ID);
			if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
				recomd.setName(Constants.TITLE_LEADERBOARD);
			} else {
				recomd.setName(Constants.TITLE_RECMD);
			}
			channelList.add(0, recomd);
		}
		return channelList;
	}

	private List<Channel> getMyFavorChannelList() {
		List<Channel> channelList = new ArrayList<Channel>();

		List<Channel> savedList = mPref.getMyFavorChannelList();
		if (savedList != null) {
			channelList.addAll(savedList);
		}

		// 设置(修改)信鸽推送的tag
		if (savedList != null && savedList.size() > 0) {
			if (Util.isNetworkConnected()) {
				PushUtil.addChannelTag(getActivity(), savedList);
				mPref.setXinGeData(PushUtil.ADD_XINGE_PUSH_DATA, "");
			} else {
				mPref.setXinGeListData(PushUtil.ADD_XINGE_PUSH_DATA, savedList);
			}
		}
		Channel myFavor = new Channel();
		myFavor.setId(Constants.MY_FAVOR_CHANNEL_ID);
		myFavor.setName(Constants.TITLE_MY_FAVOR);
		channelList.add(0, myFavor);
		return channelList;
	}

	public class SectionsPagerAdapter extends ViewPagerAdapter<Channel> {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Channel channel = getData(position);
			return channel == null ? "" : channel.getName();
		}

		@Override
		public Fragment getFragment(int position) {
			int cacheSize = Constants.CACH_SIZE_HOME_ARTI_LIST;
			Channel channel = getData(position);
			if (mType == TYPE_HEADLINES) {
				cacheSize = Constants.CACH_SIZE_HOME_HEAD_LIST;
				if (channel.getId() == Constants.RECOMMD_ID) {
					cacheSize = Constants.CACH_SIZE_HOME_HOT_LIST;
				}
			}

			List<ArticleCategory> categoryList = channel.getCategoryList();
			ArticleCategory category = null;
			if (categoryList != null && categoryList.size() > 0) {
				category = categoryList.get(0);
			}
			ChannelArticleInfoFragment fragment = ChannelArticleInfoFragment
					.newInstance(channel, cacheSize, mType, category);

			return fragment;
		}
	}

	public void onEvent(ClickHotChannelEvent event) {
		Channel hotChannel = event.getChannel();
		if (hotChannel != null) {
			SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
					.getAdapter();
			List<Channel> channels = adapter.getDatasource();
			for (int i = 0; i < channels.size(); i++) {
				if (channels.get(i).getId() == hotChannel.getId()) {
					// mViewPager.setCurrentItem(i, true);
					mTitles.check(i);
					break;
				}
			}
		}
	}

	public Channel getCurrentChannel() {
		SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		List<Channel> dataList = adapter.getDatasource();
		int currentPosition = mViewPager.getCurrentItem();
		return dataList.get(currentPosition);
	}

	public void refreshCurrent() {
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		Channel channel = mSectionsPagerAdapter.getData(mViewPager
				.getCurrentItem());
		RefreshEvent event = new RefreshEvent();
		event.mChannel = channel;
		EventBus.getDefault().post(event);
	}

	private void doCheckExpireCurrent() {
		mNeedCheckExpireRefresh = false;
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		Channel channel = mSectionsPagerAdapter.getData(mViewPager
				.getCurrentItem());
		CheckExpireEvent event = new CheckExpireEvent();
		event.mChannel = channel;
		EventBus.getDefault().post(event);
	}

	private boolean mNeedCheckExpireRefresh;

	public void checkExpireCurrent() {
		mNeedCheckExpireRefresh = true;
	}

	private SubscribeEvent mEvent;

	public void onEvent(SubscribeEvent event) {
		mEvent = event;
	}

	protected void customizeAddTitle(View addTitle) {
		switch (mType) {
		// 头条不需要添加频道
		case TYPE_HEADLINES: {
			if (addTitle != null) {
				addTitle.setVisibility(View.GONE);
			}
			break;
		}
		case TYPE_MY_FAVOR: {
			if (addTitle != null) {
				if (!Constants
						.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
					addTitle.setVisibility(View.VISIBLE);
				} else {
					addTitle.setVisibility(View.GONE);
				}
			}
			break;
		}
		}
	}

	@Override
	protected void onViewPageSelected(int index) {
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				FragmentCallbackEvent event = new FragmentCallbackEvent();
				event.mEventType = FragmentCallbackEvent.FRGMT_TAB_CHANGED;
				event.mFragment = NewsFragment.this;
				EventBus.getDefault().post(event);

				doCheckExpireCurrent();

				String title = getCurrentChannel().getName();
				// String eventKey = mType == TYPE_HEADLINES ?
				// "view_info_channel"
				// : "view_favor_channel";
				// StatsUtil.statsReport(getActivity(), eventKey,
				// "channel_name",
				// title);
				// StatsUtil.statsReportByHiido(eventKey, title);
				// StatsUtil.statsReportByMta(getActivity(), eventKey, title);

				String eventId = (mType == TYPE_HEADLINES ? MainTabEvent.TAB_HEAD_INFO
						: MainTabEvent.TAB_ORDER_INFO);
				String key = (mType == TYPE_HEADLINES ? MainTabEvent.ONCLICK_GATE_HEAD_TOP
						: MainTabEvent.ONCLICK_ARTICLE_TOP);
				MainTabStatsUtil.postStatisEvent(eventId, key, title);
				ArticleDetailActivity.CURRENT_ARTICLE_TAB = title;
			}
		});
	}

	@Override
	protected void onAddTitleClick(View v) {
		Intent intent = new Intent(getActivity(), ChannelDepotActivity.class);
		startActivity(intent);

		StatsUtil.statsReport(getActivity(), "add_channel", "param", "进入频道仓库");
		StatsUtil.statsReportByHiido("add_channel", "进入频道仓库");
		StatsUtil.statsReportByMta(getActivity(), "add_channel", "进入频道仓库");
	}
}