package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.yy.android.sportbrush.R;

public class DataPlaListViewConverter extends
		DataViewConverter<MultiColumnListView> {
	public DataPlaListViewConverter(Context context) {
		super(context);
	}

	private MultiColumnListView mListView;

	@Override
	public MultiColumnListView getDataView() {

		return mListView;
	}

	@Override
	public RefreshableViewWrapper<MultiColumnListView> getViewWrapper(boolean addHeader,boolean addFooter) {
		if (mListView == null) {
			return null;
		}
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);
		return new RefreshPlaListWrapper(mContext, mListView);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = new FrameLayout(mContext);
		parentView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		View listLayout = inflater
				.inflate(R.layout.global_waterfall_list, null);
		parentView.addView(listLayout, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		mListView = (MultiColumnListView) listLayout
				.findViewById(R.id.waterfall_list);
		mListView.setCacheColorHint(android.R.color.transparent);
		return parentView;
	}

	@Override
	public void setSelection(int selection) {
		if (mListView != null) {
			mListView.setSelection(selection);
		}
	}

	@Override
	public int getFirstVisiblePosition() {
		return mListView.getFirstVisiblePosition();
	}

	@Override
	public void setAdapter(ImageAdapter<?> adapter) {
		if (mListView != null) {
			mListView.setAdapter(adapter);
		}
	}

	@Override
	public void setOnItemClickListener(final OnItemClickListener listener) {
		mListView
				.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(PLA_AdapterView<?> parent,
							View view, int position, long id) {
						if (listener != null) {
							listener.onItemClick(parent, parent.getAdapter(),
									view, position, id);
						}
					}
				});
	}

	@Override
	public void addHeader(View header) {
		if (mListView != null) {
			mListView.addHeaderView(header, null, false);
		}
	}

	@Override
	public void addFooter(View footer) {
		if (mListView != null) {
			mListView.addFooterView(footer, null, false);
		}
	}

	@Override
	public Adapter getAdapter() {
		Adapter adapter = null;
		if (mListView != null) {
			adapter = mListView.getAdapter();
		}
		return adapter;
	}

	@Override
	public RefreshableViewWrapper<MultiColumnListView> getViewWrapper(
			View header) {
		if (mListView == null) {
			return null;
		}
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		return new RefreshPlaListWrapper(mContext, mListView, header);
	}
	
	@Override
	public void stopScroll() {
		if(mListView != null) {
			mListView.stopScroll();
		}
	}
}
