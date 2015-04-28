package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * 
 * @author liuchaoqun
 * 
 */
public class RefreshListWrapper extends RefreshableViewWrapper<ListView> {

	public RefreshListWrapper(Context context, ListView listView,
			boolean addHeader, boolean addFooter) {
		super(context, listView, addHeader, addFooter);
		// TODO Auto-generated constructor stub
	}

	public RefreshListWrapper(Context context, ListView listView,
			View headerView) {
		super(context, listView, headerView);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initView(ListView listView) {

		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE
						&& (hasLoadingBar())
						&& (view.getLastVisiblePosition() >= view.getCount() - 1)
						&& getDirection() == DIRECTION_UP) {
					if (mListener != null) {
						mListener.onLoading();
					}
				}

				if (scrollState == OnListViewEventListener.SCROLL_STATE_IDLE) {
					goIdle();
				}

				if (mListener != null) {
					mListener
							.onScrollStateChanged((ListView) view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.d(TAG, "firstvisibleitem = " + firstVisibleItem);
				if(visibleItemCount == 0) {
					return;
				}
				if (firstVisibleItem == 0) {
					setRefreshable(true);
				} else {
					setRefreshable(false);
				}
				if (mListener != null) {
					mListener.onScroll((ListView) view, firstVisibleItem,
							visibleItemCount, totalItemCount, getDirection());
				}
			}
		});
	}

	@Override
	protected void addHeader(ListView view, View header) {

		if (view != null && header != null) {
			view.addHeaderView(header, null, false);
		}
	}

	@Override
	protected void addFooter(ListView view, View footer) {
		if (view != null && footer != null) {
			view.addFooterView(footer, null, false);
		}
	}

	@Override
	protected void setSelection(ListView view, int pos) {
		if (view != null) {
			view.setSelection(pos);
		}
	}

}
