package com.yy.android.gamenews.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.sportbrush.R;

public class BaseFragment extends Fragment {
	private View mEmptyLayout;
	private View mEmptyView;
	private TextView mEmptyTextView;
	private String mEmptyText;
	protected LayoutInflater mInflater;
	private int mCurrentView = VIEW_TYPE_EMPTY;
	protected static final int VIEW_TYPE_EMPTY = 1;
	protected static final int VIEW_TYPE_DATA = 2;
	protected static final int VIEW_TYPE_LOADING = 3;
	private ViewGroup mContainer;
	private View mProgressBar;
	private View mProgressBarInner;
	protected Context mContext;
	protected String strEmptyReload;
	protected String strEmptyNoData;
	protected String strEmptyAddChannel;
	protected boolean mIsAppStopped;

	private static final String KEY_CURRENT_VIEW = "current_view";

	public BaseFragment() {

		mContext = GameNewsApplication.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = savedInstanceState;
		}
		readDataFromBundle(bundle);
		mInflater = LayoutInflater.from(getActivity());
		super.onCreate(savedInstanceState);
	}

	protected void readDataFromBundle(Bundle bundle) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentView = savedInstanceState.getInt(KEY_CURRENT_VIEW,
					VIEW_TYPE_EMPTY);

			if (hasData()) {
				mCurrentView = VIEW_TYPE_DATA;
			}
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		strEmptyAddChannel = getString(R.string.main_add_fav);
		strEmptyReload = getString(R.string.global_empty_reload);
		strEmptyNoData = getString(R.string.global_empty_no_data);
		showView(mCurrentView);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	public void onStart() {
		mIsAppStopped = false;
		super.onStart();
	}

	@Override
	public void onStop() {
		mIsAppStopped = true;
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 设置容器，并添加empty view
	 * 
	 * @param container
	 */
	protected void setContainer(ViewGroup container) {
		if (container == null) {
			return;
		}

		if (mEmptyLayout == null) {
			mEmptyLayout = mInflater.inflate(R.layout.global_reload, container,
					false);

			showView(mEmptyLayout, false);
			mEmptyTextView = (TextView) mEmptyLayout
					.findViewById(R.id.reload_empty_text);
			if (!TextUtils.isEmpty(mEmptyText)) {
				mEmptyTextView.setText(mEmptyText);
			}

			mEmptyView = mEmptyLayout.findViewById(R.id.reload_layout);
			mEmptyView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onEmptyViewClicked();
				}
			});
			mEmptyView.setClickable(mEmptyViewClickable);
			mProgressBar = mEmptyLayout.findViewById(R.id.reload_progressbar);
			mProgressBarInner = mEmptyLayout
					.findViewById(R.id.reload_progressbar_inner);
		} else {
			if (mContainer != null) {
				mContainer.removeView(mEmptyLayout);
			}
		}

		mContainer = container;
		mContainer.addView(mEmptyLayout);
	}

	public void setEmptyLayoutBg(int resId) {
		mEmptyLayout.setBackgroundColor(resId);
	}

	private void showView(View view, boolean show) {
		int visibility = show ? View.VISIBLE : View.INVISIBLE;
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	protected void showView(int viewType) {
		mCurrentView = viewType;
		switch (viewType) {
		case VIEW_TYPE_DATA: {
			showView(getDataView(), true);
			showView(mEmptyView, false);
			hidenLoading();
			showView(mEmptyLayout, false);

			break;
		}
		case VIEW_TYPE_EMPTY: {
			showView(getDataView(), false);
			showView(mEmptyView, true);
			hidenLoading();
			showView(mEmptyLayout, true);
			break;
		}
		case VIEW_TYPE_LOADING: {
			showView(getDataView(), false);
			showView(mEmptyView, false);
			showLoading();
			showView(mEmptyLayout, true);
			break;
		}
		}
	}

	/**
	 * 获取数据显示的视图
	 * 
	 * @return
	 */
	protected View getDataView() {
		return null;
	}

	protected void onEmptyViewClicked() {
	}

	protected boolean hasData() {
		return true;
	}

	private boolean mEmptyViewClickable = true;

	protected void setEmptyText(String text) {
		mEmptyText = text;
		if (mEmptyTextView != null) {
			mEmptyTextView.setText(text);
		}
	}

	protected void setEmptyViewClickable(boolean clickable) {
		mEmptyViewClickable = clickable;
		if (mEmptyView != null) {
			mEmptyView.setClickable(clickable);
		}
	}

	private Animation mLoadingAnimation = null;

	/**
	 * 显示“正在加载”视图
	 */
	private void showLoading() {
		if (mLoadingAnimation == null) {
			mLoadingAnimation = AnimationUtils.loadAnimation(mContext,
					R.anim.article_detail_loading);
			mLoadingAnimation.setInterpolator(new LinearInterpolator());
			mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		}
		showView(mProgressBar, true);
		if (mProgressBarInner != null) {
			mProgressBarInner.startAnimation(mLoadingAnimation);
		}
	}

	protected void hidenLoading() {
		showView(mProgressBar, false);
		if (mProgressBarInner != null) {
			mProgressBarInner.clearAnimation();
		}
	}

	public void onActivityBackPressed() {
		Activity activity = getActivity();
		if (activity != null) {
			activity.onBackPressed();
		}
	}

	public boolean onBackPressed() {

		FragmentManager manager = getChildFragmentManager();
		List<Fragment> fragments = manager.getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				if (fragment != null) {
					if (fragment instanceof BaseFragment) {
						if (((BaseFragment) fragment).onBackPressed()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
