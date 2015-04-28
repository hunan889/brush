package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yy.android.gamenews.ui.view.ViewPagerHeader;
import com.yy.android.gamenews.ui.view.ViewPagerHeader.OnCheckedChangeListener;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

//import com.duowan.gamenews.Channel;

/**
 * 对viewpager进行封装，增加头部点击切换控件
 * 
 * @author liuchaoqun
 * 
 */
public abstract class ViewPagerFragment extends BaseFragment {
	private static final String TAG = ViewPagerFragment.class.getSimpleName();

	public static final String KEY_NEWS_TYPE = "type";
	// Runnable mTitleSelector;
	// public HorizontalScrollView mTitleContainer;
	// protected RadioGroup mTitles;

	public ViewPagerHeader mTitles;
	protected CustomDurationViewPager mViewPager;
	private LayoutInflater mInflater;
	protected Preference mPref;
	private View mAddTitle;
	private OnCheckedChangeListener onCheckedChangeListener;
	private OnPageChangeListener onPageChangeListener;

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

	public void refreshChannelPager() {
		mViewPager.setAdapter(getAdapter());
		refreshTitleIndicators();
	}

	protected abstract PagerAdapter getAdapter();

	public void showTab(int pos) {
		mTitles.check(pos);
		// if (mTitles != null) {
		// mTitles.check(pos);
		// }
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.my_favor_news, container, false);

		mViewPager = (CustomDurationViewPager) view.findViewById(R.id.pager);
		mViewPager.setScrollDurationFactor(3);
		PagerAdapter mSectionsPagerAdapter = getAdapter();

		mViewPager.setAdapter(mSectionsPagerAdapter);
		onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.v(TAG, "onPageSelected " + position);
				mTitles.setOnCheckedChangeListener(null);
				showTab(position);
				onViewPageSelected(position);
				mTitles.setOnCheckedChangeListener(onCheckedChangeListener);
			}
		};
		mViewPager.setOnPageChangeListener(onPageChangeListener);

		// mTitleContainer = (HorizontalScrollView) view
		// .findViewById(R.id.title_container);

		mTitles = (ViewPagerHeader) view.findViewById(R.id.head);
		onCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(ViewParent group, int checkedId) {
				Log.v(TAG, "onCheckedChanged " + checkedId);
				if (checkedId != -1) {
					mViewPager.setOnPageChangeListener(null);
					mViewPager.setCurrentItem(checkedId);
					// animateToTitle(checkedId);
					onViewPageSelected(checkedId);
					mViewPager.setOnPageChangeListener(onPageChangeListener);
				}
			}
		};
		mTitles.setOnCheckedChangeListener(onCheckedChangeListener);

		mTitles.setNeedCheckEqually(needCheckDivide());
		// if (needCheckDivide()) {
		// mHeader.getViewTreeObserver().addOnGlobalLayoutListener(
		// mOnGlobalLayoutListener);
		// }
		refreshTitleIndicators();

		mAddTitle = view.findViewById(R.id.add_title);
		mAddTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddTitleClick(v);
			}
		});

		if (savedInstanceState == null) {
			mTitles.check(0);
		}

		customizeAddTitle(mAddTitle);
		setContainer(container);
		return view;
	}

	/**
	 * 检查当条目过于少的时候（宽度小于其父布局），自动平分整个title
	 * 
	 * @return
	 */
	protected boolean needCheckDivide() {
		return false;
	}

	// private OnGlobalLayoutListener mOnGlobalLayoutListener = new
	// OnGlobalLayoutListener() {
	//
	// @Override
	// public void onGlobalLayout() {
	// int orientation = getResources().getConfiguration().orientation;
	// if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
	// return;
	// }
	// int width = mTitles.getWidth();
	// View parent = (View) mTitles.getParent();
	// int parentWidth = parent.getWidth();
	// if (width < parentWidth) {
	// int childCount = mTitles.getChildCount();
	//
	// // 总父视图宽度减去所有字的宽度（即child width的总和），再平分得出每个视图的margin
	// int totalChildWidth = 0;
	// for (int i = 0; i < childCount; i++) {
	// View child = mTitles.getChildAt(i);
	// int childWidth = child.getWidth();
	// totalChildWidth += childWidth;
	// }
	//
	// int remainWidth = parentWidth - totalChildWidth;
	// int margin = remainWidth / childCount / 2;
	//
	// for (int i = 0; i < childCount; i++) {
	// View child = mTitles.getChildAt(i);
	//
	// LayoutParams childParams = (LayoutParams) child
	// .getLayoutParams();
	// childParams.leftMargin = margin;
	// childParams.rightMargin = margin;
	// }
	// mTitles.requestLayout();
	// }
	// }
	// };

	@Override
	protected View getDataView() {
		return (View) mViewPager.getParent();
	}

	protected void customizeAddTitle(View addTitleView) {
		addTitleView.setVisibility(View.GONE);
	}

	protected void onAddTitleClick(View v) {
	}

	protected void onViewPageSelected(int index) {
	}

	protected void refreshTitleIndicators() {

		List<String> titles = new ArrayList<String>();

		PagerAdapter adapter = mViewPager.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			titles.add(adapter.getPageTitle(i).toString());
		}

		mTitles.update(titles);

		// mTitles.removeAllViews();
		// PagerAdapter mSectionsPagerAdapter = mViewPager.getAdapter();
		//
		// if (mSectionsPagerAdapter.getCount() <= 1) {
		// mTitleContainer.setVisibility(View.GONE);
		// } else {
		// mTitleContainer.setVisibility(View.VISIBLE);
		// }
		// for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
		// RadioButton btn = (RadioButton) mInflater.inflate(
		// R.layout.my_favor_news_title, mTitles, false);
		// btn.setId(i);
		// btn.setText(mSectionsPagerAdapter.getPageTitle(i));
		// mTitles.addView(btn);
		// }
		//
		// mTitles.check(-1);
	}

	// private void animateToTitle(final int id) {
	// Log.v(TAG, "animateToTitle " + id);
	// final View titleView = mTitles.findViewById(id);
	// if (mTitleSelector != null) {
	// mTitleContainer.removeCallbacks(mTitleSelector);
	// }
	// if (titleView == null) {
	// return;
	// }
	// mTitleSelector = new Runnable() {
	// @Override
	// public void run() {
	// int x = titleView.getLeft()
	// - (mTitleContainer.getWidth() - titleView.getWidth())
	// / 2;
	// Log.v(TAG, "animateToTitle " + id + " " + titleView.getLeft()
	// + " " + titleView.getWidth() + " " + x);
	// mTitleContainer.smoothScrollTo(x, 0);
	// mTitleSelector = null;
	// }
	// };
	// mTitleContainer.post(mTitleSelector);
	// }

	public int getCurrentItem() {
		int currentItem = mViewPager.getCurrentItem();
		return currentItem;
	}

	public void refreshCurrent() {
		// For child implementation
	}
}