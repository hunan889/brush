package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;

public class RefreshPlaListWrapper extends
		RefreshableViewWrapper<MultiColumnListView> {

	public RefreshPlaListWrapper(Context context, MultiColumnListView listView) {
		super(context, listView);
		// TODO Auto-generated constructor stub
	}

	public RefreshPlaListWrapper(Context context, MultiColumnListView listView,
			View headerView) {
		super(context, listView, headerView);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initView(MultiColumnListView listView) {

		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(PLA_AbsListView view,
					int scrollState) {

				if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE
						&& (hasLoadingBar())
						&& (view.getLastVisiblePosition() >= view.getCount() - 1)) {
					if (mListener != null) {
						mListener.onLoading();
					}
				}

				if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE) {
					goIdle();
				}

				if (mListener != null) {
					mListener.onScrollStateChanged((MultiColumnListView) view,
							scrollState);
				}
			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
				if (firstVisibleItem == 0) {
					setRefreshable(true);
				} else {
					setRefreshable(false);
				}
				if (mListener != null) {
					mListener.onScroll((MultiColumnListView) view,
							firstVisibleItem, visibleItemCount, totalItemCount,
							getDirection());
				}
			}
		});
	}

	@Override
	protected void addHeader(MultiColumnListView view, View header) {

		if (view != null && header != null) {
			view.addHeaderView(header, null, false);
		}
	}

	@Override
	protected void addFooter(MultiColumnListView view, View footer) {
		if (view != null && footer != null) {
			view.addFooterView(footer, null, false);
		}
	}

	@Override
	protected void setSelection(MultiColumnListView view, int pos) {
		if (view != null) {
			view.setSelection(pos);
		}
	}
}
