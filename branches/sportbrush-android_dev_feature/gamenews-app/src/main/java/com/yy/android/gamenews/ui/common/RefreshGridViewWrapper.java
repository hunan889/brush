package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

/**
 * 
 * @author liuchaoqun
 * 
 */
public class RefreshGridViewWrapper extends RefreshableViewWrapper<GridView> {

	public RefreshGridViewWrapper(Context context, GridView gridView) {
		super(context, gridView);
		// TODO Auto-generated constructor stub
	}

	public RefreshGridViewWrapper(Context context, GridView gridView,
			View headerView) {
		super(context, gridView, headerView);
		// TODO Auto-generated constructor stub
	}

	public RefreshGridViewWrapper(Context context, GridView gridView,
			boolean addHeader, boolean addFooter) {
		super(context, gridView, addHeader, addFooter);
	}

	@Override
	protected void initView(GridView listView) {

		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

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
					mListener
							.onScrollStateChanged((GridView) view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
				if (firstVisibleItem == 0) {
					setRefreshable(true);
				} else {
					setRefreshable(false);
				}
				if (mListener != null) {
					mListener.onScroll((GridView) view, firstVisibleItem,
							visibleItemCount, totalItemCount, getDirection());
				}
			}
		});
	}

	@Override
	protected void addHeader(GridView view, View header) {

		// if (view != null && header != null) {
		// view.addHeaderView(header, null, false);
		// }
	}

	@Override
	protected void addFooter(GridView view, View footer) {
		// if (view != null && footer != null) {
		// view.addFooterView(footer, null, false);
		// }
	}

	@Override
	protected void setSelection(GridView view, int pos) {
		if (view != null) {
			view.setSelection(pos);
		}
	}

}
