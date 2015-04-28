package com.yy.android.gamenews.plugin.cartport;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
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

import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BrandSecondPagerlFragment extends BaseFragment {

	private static final String TAG = CartportFragment.class.getSimpleName();

	private ViewPager mViewPager;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.brand_second_frament, container,
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
		Bundle arguments = getArguments();
		if (arguments.getString(BrandDetailActivity.BRAND_NAME) != null) {
			mActionBar.setTitle(arguments
					.getString(BrandDetailActivity.BRAND_NAME));
		}
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		return view;
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			BrandDetailFragment fragment = null;
			fragment = new BrandDetailFragment();
			fragment.setArguments(getArguments());
			return fragment;
		}

		@Override
		public int getCount() {

			return 1;
		}
	}

}
