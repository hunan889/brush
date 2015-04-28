package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.yy.android.sportbrush.R;

public class DataGridViewConverter extends DataViewConverter<GridView> {
	public DataGridViewConverter(Context context) {
		super(context);
	}

	private GridView mGridView;

	@Override
	public GridView getDataView() {

		return mGridView;
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = new FrameLayout(mContext);
		parentView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		View listLayout = inflater.inflate(R.layout.global_gridview, null);
		parentView.addView(listLayout, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		mGridView = (GridView) listLayout.findViewById(R.id.list);
		return parentView;
	}

	@Override
	public void setSelection(int selection) {
		if (mGridView != null) {
			mGridView.setSelection(selection);
		}
	}

	@Override
	public int getFirstVisiblePosition() {
		return mGridView.getFirstVisiblePosition();
	}

	@Override
	public void setAdapter(ImageAdapter<?> adapter) {
		if (mGridView != null) {
			mGridView.setAdapter(adapter);
		}
	}

	@Override
	public void setOnItemClickListener(final OnItemClickListener listener) {
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listener != null) {
					listener.onItemClick(parent, parent.getAdapter(), view,
							position, id);
				}
			}
		});
	}

	@Override
	public void addHeader(View header) {
		// if (mListView != null) {
		// mListView.addHeaderView(header, null, false);
		// }
	}

	@Override
	public void addFooter(View footer) {
		// if (mListView != null) {
		// mListView.addFooterView(footer, null, false);
		// }
	}

	@Override
	public Adapter getAdapter() {
		Adapter adapter = null;
		if (mGridView != null) {
			adapter = mGridView.getAdapter();
		}
		return adapter;
	}

	@Override
	public RefreshableViewWrapper<GridView> getViewWrapper(View header) {
		if (mGridView == null) {
			return null;
		}
		// mListView.setHeaderDividersEnabled(false);
		// mListView.setFooterDividersEnabled(false);

		return new RefreshGridViewWrapper(mContext, mGridView, header);
	}

	@Override
	public RefreshableViewWrapper<GridView> getViewWrapper(boolean addHeader,
			boolean addFooter) {
		if (mGridView == null) {
			return null;
		}
		// mListView.setHeaderDividersEnabled(false);
		// mListView.setFooterDividersEnabled(false);

		return new RefreshGridViewWrapper(mContext, mGridView, addHeader,
				addFooter);
	}

	@Override
	public void stopScroll() {
		// TODO: stop scroll gridview
	}
}
