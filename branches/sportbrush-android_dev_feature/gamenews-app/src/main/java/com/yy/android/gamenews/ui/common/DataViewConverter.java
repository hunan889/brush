package com.yy.android.gamenews.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * 对数据视图进行封装，当需要使用的视图拥有不同父类时， 可以继承该类并实现调用它们的一些方法（相当于适配器的作用） 可通过
 * {@link DataViewConverterFactory}来获取
 * 
 * @see DataViewConverterFactory
 * @author liuchaoqun
 * 
 * @param <VIEW>
 */
public abstract class DataViewConverter<VIEW extends View> {
	protected Context mContext;
	public RefreshableViewWrapper<VIEW> mWrapper;

	public DataViewConverter(Context context) {
		mContext = context;
	}

	public abstract VIEW getDataView();

	public abstract RefreshableViewWrapper<VIEW> getViewWrapper(boolean addHeader,boolean addFooter);

	public abstract RefreshableViewWrapper<VIEW> getViewWrapper(View header);
	
	public abstract void setAdapter(ImageAdapter<?> adapter);

	public abstract Adapter getAdapter();

	public abstract View createView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	public abstract void setSelection(int selection);

	public abstract int getFirstVisiblePosition();
	
	public abstract void stopScroll();

	public abstract void setOnItemClickListener(OnItemClickListener listener);

	public abstract void addHeader(View header);

	public abstract void addFooter(View footer);

	public interface OnItemClickListener {
		public void onItemClick(View parent, Adapter adapter, View view,
				int position, long id);
	}
}
