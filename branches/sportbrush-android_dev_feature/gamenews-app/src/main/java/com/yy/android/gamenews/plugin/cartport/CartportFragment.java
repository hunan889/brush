package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;

import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

@SuppressLint("CutPasteId")
public class CartportFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = CartportFragment.class.getSimpleName();
	Button mBrandChoose;
	Button mHotCart;
	ViewPager mViewPager;

	private static final String TITLE_CHOOSE_CART = "精准选车";
	private static final String TITLE_HOT_CART = "热门车型";
	private static final String TITLE_CHOOSE_BRAND = "品牌选择";

	private static final int TITLE_CHOOSE_CART_DATA = 0;
	private static final int TITLE_HOT_CART_DATA = 1;
	private static final int TITLE_CHOOSE_BRAND_DATA = 2;

	private Preference mPref;
	private ActionBar mActionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		mPref = Preference.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private List<String> getTitles() {
		List<String> titles = new ArrayList<String>();

		titles.add(TITLE_CHOOSE_BRAND);
		titles.add(TITLE_HOT_CART);
		// titles.add(TITLE_CHOOSE_CART);
		return titles;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.cartport_pager, container, false);

		mActionBar = (ActionBar) view.findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onActivityBackPressed();
			}
		});
		// mActionBar.setActionBarBgColor(0xff222528);
		View mActionBarBg = mActionBar.findViewById(R.id.actionbar_container);
		mActionBarBg.setBackgroundColor(0xff222528);
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
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
						event.mFragment = CartportFragment.this;
						EventBus.getDefault().post(event);
						
						String eventKey = position == 0 ? "TITLE_CHOOSE_BRAND" : "TITLE_HOT_CART";
						String title = position == 0 ? TITLE_CHOOSE_BRAND : TITLE_HOT_CART;
						StatsUtil.statsReport(getActivity(), eventKey, "carport", title);
						StatsUtil.statsReportByHiido(eventKey, title);
						StatsUtil.statsReportByMta(getActivity(), eventKey, title);

					}
				});
		mBrandChoose = (Button) view.findViewById(R.id.brand_choose_but);
		mHotCart = (Button) view.findViewById(R.id.brand_hot_but);
		mBrandChoose.setOnClickListener(this);
		mHotCart.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showTab(TITLE_CHOOSE_CART_DATA);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.brand_choose_but:
			mViewPager.setCurrentItem(TITLE_CHOOSE_CART_DATA);
			refreshTitleIndicators(TITLE_CHOOSE_CART_DATA);
			break;
		case R.id.brand_hot_but:
			mViewPager.setCurrentItem(TITLE_HOT_CART_DATA);
			refreshTitleIndicators(TITLE_HOT_CART_DATA);
			break;
		default:
			break;
		}

	}

	public void showTab(int pos) {
		switch (pos) {
		case TITLE_CHOOSE_CART_DATA:
			refreshTitleIndicators(TITLE_CHOOSE_CART_DATA);
			break;
		case TITLE_HOT_CART_DATA:
			refreshTitleIndicators(TITLE_HOT_CART_DATA);
			break;
		default:
			break;
		}
	}

	private void refreshTitleIndicators(int nums) {
		if (nums == TITLE_CHOOSE_CART_DATA) {
			mBrandChoose.setPressed(true);
			mHotCart.setPressed(false);
			mBrandChoose.setTextColor(0xff3b3b3b);
			mHotCart.setTextColor(0xff8fa3b2);
			mBrandChoose.setBackgroundResource(R.drawable.car_tab_left_pressed);
			mHotCart.setBackgroundResource(R.drawable.car_tab_right);
		} else if (nums == TITLE_HOT_CART_DATA) {
			mBrandChoose.setPressed(false);
			mHotCart.setPressed(true);
			mBrandChoose.setTextColor(0xff8fa3b2);
			mHotCart.setTextColor(0xff3b3b3b);
			mBrandChoose.setBackgroundResource(R.drawable.car_tab_left);
			mHotCart.setBackgroundResource(R.drawable.car_tab_right_pressed);
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
			String channel = mTitles.get(position);
			Fragment fragment = null;
			if (TITLE_CHOOSE_CART.equals(channel)) {

			} else if (TITLE_CHOOSE_BRAND.equals(channel)) {

				fragment = new BrandChooseFragment();
			} else if (TITLE_HOT_CART.equals(channel)) {
				fragment = new HotCartFragment();
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

}
