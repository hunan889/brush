package com.yy.android.gamenews.ui.common;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.util.DomainIpTransformatter;

public abstract class ImageAdapter<E> extends BaseAdapter {

	private SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
	// private DisplayImageOptions options;
	protected ArrayList<E> mDataSource;
	protected LayoutInflater mInflater;
	private DomainIpTransformatter mIpFormatter;
	private Resources mResource;

	private Context mContext;

	public ImageAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mResource = context.getResources();
	}

	public Resources getResources() {
		return mResource;
	}

	public Context getContext() {
		return mContext;
	}

	public void setDataSource(ArrayList<E> dataSource) {
		mDataSource = dataSource;

		if (dataSource == null) {
			notifyDataSetInvalidated();
		} else {
			notifyDataSetChanged();
		}
	}

	public ArrayList<E> getDataSource() {
		return mDataSource;
	}

	@Override
	public int getCount() {
		return mDataSource == null ? 0 : mDataSource.size();
	}

	@Override
	public E getItem(int position) {
		if (mDataSource == null || position < 0
				|| position >= mDataSource.size()) {
			return null;
		}
		return mDataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected void displayImage(String url, ImageView view) {
		displayImage(url, view, null);
	}

	protected void displayImage(String url, ImageView view,
			DisplayImageOptions options) {

		displayImage(url, view, options, null);
	}

	public void displayImage(String url, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener) {

		if (needTransferDomainToIp()) {
			if (mIpFormatter == null) {
				mIpFormatter = DomainIpTransformatter.getInstance();
			}
			url = mIpFormatter.domainToIp(url);
		}
		mImageLoader.displayImage(url, imageView, options, listener, false);
	}

	protected void loadImage(String url, ImageLoadingListener listener) {
		mImageLoader.loadImage(url, listener);
	}

	/**
	 * To pause heavy data loading task for adapter to improve the performance
	 * if list view is doing UI operation like scrolling
	 */
	public void pause() {
		mImageLoader.pause();
	}

	/**
	 * Resume data loading when you finish UI operation
	 */
	public void resume() {
		mImageLoader.resume();
	}

	protected boolean needTransferDomainToIp() {
		return false;
	}
}
