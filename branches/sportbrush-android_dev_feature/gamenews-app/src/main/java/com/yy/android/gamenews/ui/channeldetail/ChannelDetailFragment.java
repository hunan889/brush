package com.yy.android.gamenews.ui.channeldetail;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;

import com.duowan.gamenews.ArticleCategory;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.LoginActionFlag;
import com.duowan.gamenews.SubType;
import com.duowan.gamenews.UserInitRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.ClickHotChannelEvent;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.ui.AppWebFragment;
import com.yy.android.gamenews.ui.ChannelArticleInfoFragment;
import com.yy.android.gamenews.ui.ViewPagerAdapter;
import com.yy.android.gamenews.ui.ViewPagerFragment;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

/**
 * 游戏刷子频道点进去之后的ViewPager
 * 
 * @author liuchaoqun
 * 
 */
public class ChannelDetailFragment extends ViewPagerFragment {

	public static final String KEY_CHANNEL = "channel";

	private Channel mChannel;

	@Override
	protected PagerAdapter getAdapter() {
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());
		ArrayList<ArticleCategory> categoryList = mChannel.getCategoryList();
		if (categoryList == null) {
			categoryList = new ArrayList<ArticleCategory>();
		}
		if (categoryList.size() <= 0) {
			categoryList.add(new ArticleCategory());
		}
		mSectionsPagerAdapter.updateDataSource(categoryList);

		return mSectionsPagerAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		if (bundle != null) {
			mChannel = (Channel) bundle.getSerializable(KEY_CHANNEL);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected boolean needCheckDivide() {
		return true;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		showView(VIEW_TYPE_DATA);
		super.onViewCreated(view, savedInstanceState);
	}

	//
	// private void requestData() {
	// showView(VIEW_TYPE_LOADING);
	//
	// ArticleModel.getArticleCategoryReq(
	// new ResponseListener<GetChannelArticleCategoryRsp>(
	// getActivity()) {
	//
	// @Override
	// public void onResponse(GetChannelArticleCategoryRsp response) {
	// ArrayList<ArticleCategory> list = null;
	// if (response != null) {
	// list = response.getCategoryList();
	// }
	// if (list != null && list.size() > 0) {
	// showView(VIEW_TYPE_DATA);
	// } else {
	// showView(VIEW_TYPE_EMPTY);
	// }
	// SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
	// .getAdapter();
	// adapter.updateDataSource(list);
	// adapter.notifyDataSetChanged();
	// refreshTitleIndicators();
	// mTitles.check(0);
	// }
	//
	// @Override
	// public void onError(Exception e) {
	//
	// showView(VIEW_TYPE_EMPTY);
	// super.onError(e);
	// }
	// }, mChannel.getId());
	//
	// }

	private List<Channel> getTitles() {
		List<Channel> channelList = null;
		channelList = getMyFavorChannelList();
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

	public class SectionsPagerAdapter extends ViewPagerAdapter<ArticleCategory> {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			ArticleCategory category = getData(position);
			return category == null ? "" : category.getName();
		}

		public Fragment getFragment(int position) {
			Fragment fragment = null;
			ArticleCategory category = getData(position);
			int type = -1;
			if (category != null) {
				type = category.getId();
			}
			switch (type) {
			case SubType._SUBTYPE_GIFT: {

				UserInitRsp rsp = mPref.getInitRsp();
				String token = "";
				if (rsp != null) {
					token = rsp.extraInfo
							.get(LoginActionFlag._LOGIN_ACTION_FLAG_YY_TOKEN);
				}
				String url = category.url;
				if (token != null && (!TextUtils.isEmpty(token))) {
					url = url + token;
				}
				fragment = AppWebFragment.getInstance(getActivity(), url, false, true);
				break;
			}
			default: {
				fragment = ChannelArticleInfoFragment.newInstance(mChannel,
						category);
			}
			}

			return fragment;
		}

	}

	public void onEvent(ClickHotChannelEvent event) {
		// Channel hotChannel = event.getChannel();
		// if (hotChannel != null) {
		// SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
		// .getAdapter();
		// List<ArticleCategory> channels = adapter.getDatasource();
		// for (int i = 0; i < channels.size(); i++) {
		// if (channels.get(i).getId() == hotChannel.getId()) {
		// mViewPager.setCurrentItem(i, true);
		// break;
		// }
		// }
		// }
	}

	public ArticleCategory getCurrentChannel() {
		SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		List<ArticleCategory> dataList = adapter.getDatasource();
		int currentPosition = mViewPager.getCurrentItem();
		return dataList.get(currentPosition);
	}

	public void refreshCurrent() {
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();
		ArticleCategory category = mSectionsPagerAdapter.getData(mViewPager
				.getCurrentItem());
		RefreshEvent event = new RefreshEvent();
		event.mChannel = mChannel;
		event.mCategory = category;
		EventBus.getDefault().post(event);
	}

	@Override
	protected void onViewPageSelected(int index) {
		FragmentCallbackEvent event = new FragmentCallbackEvent();
		event.mEventType = FragmentCallbackEvent.FRGMT_TAB_CHANGED;
		event.mFragment = this;
		EventBus.getDefault().post(event);

	}

}
