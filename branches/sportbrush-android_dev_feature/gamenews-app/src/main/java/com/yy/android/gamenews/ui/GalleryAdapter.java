package com.yy.android.gamenews.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.ui.GalleryAdapter.GalleryItem;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class GalleryAdapter extends ImageAdapter<GalleryItem> {
	public GalleryAdapter(Context context) {
		super(context);
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	private static final int VIEW_TYPE_IMAGE = 0;

	@Override
	public int getItemViewType(int position) {

		return VIEW_TYPE_IMAGE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		switch (getItemViewType(position)) {
		case VIEW_TYPE_IMAGE: {
			convertView = getImageTypeView(convertView, position);
			break;

		}
		}
		return convertView;
	}

	private View getImageTypeView(View convertView, int position) {
		ImageViewHolder holder = null;

		boolean needInit = false;
		if (convertView == null) {
			needInit = true;

		} else {
			holder = (ImageViewHolder) convertView.getTag();
			if (holder == null) {
				needInit = true;
			}
		}
		if (needInit) {
			convertView = mInflater.inflate(
					R.layout.article_detail_zoom_image_detail, null);
			holder = new ImageViewHolder();
			holder.mProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_bar);
			holder.mView = (ImageView) convertView.findViewById(R.id.image);
			holder.mFailView = (TextView) convertView
					.findViewById(R.id.download_failed);
			convertView.setTag(holder);
			holder.mView.setTag(holder);
		}

		displayImage(getItem(position).url, holder.mView, null,
				mImageLoadingListener);

		return convertView;
	}

	private static class ImageViewHolder {
		ImageView mView;
		TextView mFailView;
		ProgressBar mProgress;
	}

	private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			ImageViewHolder holder = (ImageViewHolder) view.getTag();
			holder.mProgress.setVisibility(View.VISIBLE);
			holder.mFailView.setVisibility(View.INVISIBLE);
			holder.mView.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			if (view != null) {
				ImageViewHolder holder = (ImageViewHolder) view.getTag();
				holder.mProgress.setVisibility(View.INVISIBLE);
				holder.mFailView.setVisibility(View.VISIBLE);
				holder.mView.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (view != null) {
				ImageViewHolder holder = (ImageViewHolder) view.getTag();
				holder.mProgress.setVisibility(View.INVISIBLE);
				holder.mFailView.setVisibility(View.INVISIBLE);
				holder.mView.setVisibility(View.VISIBLE);
				holder.mView.setImageBitmap(loadedImage);
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			if (view != null) {
				ImageViewHolder holder = (ImageViewHolder) view.getTag();
				holder.mProgress.setVisibility(View.INVISIBLE);
				holder.mFailView.setVisibility(View.VISIBLE);
				holder.mView.setVisibility(View.INVISIBLE);
			}
		}

	};

	public static class GalleryItem {
		public String url;
	}
}