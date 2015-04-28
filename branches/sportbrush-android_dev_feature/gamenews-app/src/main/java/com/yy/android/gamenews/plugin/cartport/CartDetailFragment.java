package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.duowan.autonews.CarDetailItemDetail;
import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.ChannelArticleInfoFragment;
import com.yy.android.gamenews.ui.ChannelDepotActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class CartDetailFragment extends BaseFragment implements
		FragmentMessageListener {
	private static final String TAG = CartDetailFragment.class.getSimpleName();

	Runnable mTitleSelector;
	ViewGroup mTitleContainer;
	RadioGroup mTitles;
	ViewPager mViewPager;
	ActionBar mActionBar;
	private LayoutInflater mInflater;

	private static final String TITLE_SUMMARY = "综述";
	private static final String TITLE_PARAMS = "参配";
	private static final String TITLE_IMAGE = "图文";

	private Preference mPref;

	private View mAddTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		mPref = Preference.getInstance();

		EventBus.getDefault().register(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void showTab(int pos) {

		if (mTitles != null) {
			mTitles.check(pos);
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private List<String> getTitles() {
		List<String> titles = new ArrayList<String>();

		titles.add(TITLE_SUMMARY);
		titles.add(TITLE_PARAMS);
		titles.add(TITLE_IMAGE);
		return titles;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.cartdetail_pager, container,
				false);
		mActionBar = (ActionBar) view.findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onActivityBackPressed();
			}
		});
		View mActionBarBg = mActionBar.findViewById(R.id.actionbar_container);
		mActionBarBg.setBackgroundColor(0xff222528);
		mActionBar.setTitle(getArguments().getString(
				CartDetailActivity.TAG_FGMT_BRANDNAME));
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());

		mSectionsPagerAdapter.updateDataSource(getTitles());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.v(TAG, "onPageSelected " + position);
						showTab(position);

						FragmentCallbackEvent event = new FragmentCallbackEvent();
						event.mEventType = FragmentCallbackEvent.FRGMT_TAB_CHANGED;
						event.mFragment = CartDetailFragment.this;
						EventBus.getDefault().post(event);

						String eventKey = null;
						String title = null;
						if (position == 0) {
							eventKey = "TITLE_SUMMARY";
							title = TITLE_SUMMARY;
						} else if (position == 1) {
							eventKey = "TITLE_PARAMS";
							title = TITLE_PARAMS;
						} else if (position == 2) {
							eventKey = "TITLE_PARAMS";
							title = TITLE_IMAGE;
						}
						StatsUtil.statsReport(getActivity(), eventKey,
								"carport", title);
						StatsUtil.statsReportByHiido(eventKey, title);
						StatsUtil.statsReportByMta(getActivity(), eventKey,
								title);
					}
				});

		mTitleContainer = (ViewGroup) view.findViewById(R.id.head);

		mTitles = (RadioGroup) view.findViewById(R.id.titles);
		mTitles.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.v(TAG, "onCheckedChanged " + checkedId);
				if (checkedId != -1) {

					for (int i = 0; i < group.getChildCount(); i++) {
						View view = group.getChildAt(i);
						RadioButton btn = (RadioButton) view
								.findViewById(R.id.radio_btn);
						if (view.getId() != checkedId) {
							btn.setChecked(false);
						} else {
							btn.setChecked(true);
						}
					}

					mViewPager.setCurrentItem(checkedId);
					animateToTitle(checkedId);
				}
			}
		});
		refreshTitleIndicators();

		mAddTitle = view.findViewById(R.id.add_title);
		mAddTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ChannelDepotActivity.class);
				startActivity(intent);
			}
		});

		if (savedInstanceState == null) {
			mTitles.check(0);
		}

		return view;
	}

	private void refreshTitleIndicators() {
		mTitles.removeAllViews();
		SectionsPagerAdapter mSectionsPagerAdapter = (SectionsPagerAdapter) mViewPager
				.getAdapter();

		mTitles.setWeightSum(mSectionsPagerAdapter.getCount());
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			View layout = mInflater.inflate(R.layout.cartdetail_pager_title,
					mTitles, false);
			LayoutParams params = new LayoutParams(0,
					LayoutParams.MATCH_PARENT, 1.0f);
			layout.setId(i);
			RadioButton btn = (RadioButton) layout.findViewById(R.id.radio_btn);
			btn.setText(mSectionsPagerAdapter.getPageTitle(i));
			mTitles.addView(layout, params);

			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mTitles.check(v.getId());
				}
			});
		}
		mTitles.check(-1);
	}

	private void animateToTitle(final int id) {
		Log.v(TAG, "animateToTitle " + id);
		final View titleView = mTitles.findViewById(id);
		if (mTitleSelector != null) {
			mTitleContainer.removeCallbacks(mTitleSelector);
		}
		mTitleSelector = new Runnable() {
			@Override
			public void run() {
				int x = titleView.getLeft()
						- (mTitleContainer.getWidth() - titleView.getWidth())
						/ 2;
				Log.v(TAG, "animateToTitle " + id + " " + titleView.getLeft()
						+ " " + titleView.getWidth() + " " + x);
				mTitleSelector = null;
			}
		};
		mTitleContainer.post(mTitleSelector);
	}

	private Map<String, Fragment> fragmentMap = new HashMap<String, Fragment>();

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
			String channel = mTitles.get(position);
			Fragment fragment = null;
			if (TITLE_SUMMARY.equals(channel)) {
				fragment = new CartDetailSummaryFragment();
				fragment.setArguments(getArguments());
			} else if (TITLE_IMAGE.equals(channel)) {

				int cartId = getArguments().getInt(
						CartDetailActivity.TAG_FGMT_CARTID);
				Channel c = new Channel();
				c.setId(cartId);
				// 正式代码
				fragment = ChannelArticleInfoFragment
						.newInstance(c, true, true);
				// ............................测试代码............................
				// fragment = new CartDetailImageFragment();
				// Bundle bundle = new Bundle();
				// bundle.putLong(SingleFragmentActivity.TAG_FGMT_CARTID, 0);
				// bundle.putString(SingleFragmentActivity.TAG_FGMT_CAR_COLUMN,
				// "汽车阿胡子汽车阿胡子汽车阿胡子    黑色");
				// fragment.setArguments(bundle);
				// ............................测试代码............................

			} else if (TITLE_PARAMS.equals(channel)) {
				fragment = new CartDetailParamFragment();
				fragment.setArguments(getArguments());
			}
			fragmentMap.put(channel, fragment);
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

	private SubscribeEvent mEvent;

	public void onEvent(SubscribeEvent event) {
		mEvent = event;
	}

	public int getCurrentItem() {
		int currentItem = mViewPager.getCurrentItem();
		return currentItem;
	}

	@Override
	public void onMessage(int msg, Object value) {
		switch (msg) {
		case MSG_UPDATE_CART_PARAMS: {
			CartDetailParamFragment fragment = (CartDetailParamFragment) fragmentMap
					.get(TITLE_PARAMS);

			fragment.updateParams((CarDetailItemDetail) value);
			break;
		}
		case MSG_SHOW_CART_PARAMS: {

			SectionsPagerAdapter adapter = (SectionsPagerAdapter) mViewPager
					.getAdapter();
			int pos = adapter.getDatasource().indexOf(TITLE_PARAMS);
			if (pos >= 0) {
				mViewPager.setCurrentItem(pos);
			}
			break;
		}
		}
	}
}
