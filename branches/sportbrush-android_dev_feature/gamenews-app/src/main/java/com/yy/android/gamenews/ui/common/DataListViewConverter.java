package com.yy.android.gamenews.ui.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.yy.android.sportbrush.R;

public class DataListViewConverter extends DataViewConverter<ListView> {
	public DataListViewConverter(Context context) {
		super(context);
	}

	private ListView mListView;

	@Override
	public ListView getDataView() {

		return mListView;
	}

	@Override
	public RefreshableViewWrapper<ListView> getViewWrapper(boolean addHeader,
			boolean addFooter) {
		if (mListView == null) {
			return null;
		}
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		return new RefreshListWrapper(mContext, mListView, addHeader, addFooter);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = new FrameLayout(mContext);
		parentView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		View listLayout = inflater.inflate(R.layout.global_list, null);
		parentView.addView(listLayout, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		mListView = (ListView) listLayout.findViewById(R.id.list);
		return parentView;
	}

	@Override
	public void setSelection(int selection) {
		if (mListView != null) {
			 mListView.setSelection(selection);
//			mListView.setSelectionAfterHeaderView();
			// mListView.smoothScrollToPosition(selection);
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
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
	public RefreshableViewWrapper<ListView> getViewWrapper(View header) {
		if (mListView == null) {
			return null;
		}
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterDividersEnabled(false);

		return new RefreshListWrapper(mContext, mListView, header);
	}

	@Override
	public void stopScroll() {
		if (mListView != null) {
			try {
				Field field = android.widget.AbsListView.class
						.getDeclaredField("mFlingRunnable");
				field.setAccessible(true);
				Object flingRunnable = field.get(mListView);
				if (flingRunnable != null) {
					Method method = Class.forName(
							"android.widget.AbsListView$FlingRunnable")
							.getDeclaredMethod("endFling");
					method.setAccessible(true);
					method.invoke(flingRunnable);
				}
			} catch (Exception e) {
			}
		}
	}
}
