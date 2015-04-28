package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.ImageUtil;
import com.yy.android.sportbrush.R;

public class TagListAdapter extends ImageAdapter<Tag> {

	private Context context;
	private ImageLoader mLoader = ImageLoader.getInstance();

	public TagListAdapter(Context context) {
		super(context);
		this.context = context;
	}

	private static final String TAG = "TagListAdapter";

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		Tag tag = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.show_tag_list_item_view,
					null);
			viewHolder = new ViewHolder();
			viewHolder.tagImageView = (ImageView) convertView
					.findViewById(R.id.iv_tag_icon);
			viewHolder.tagNameTextView = (TextView) convertView
					.findViewById(R.id.tv_tag_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tagNameTextView.setText(tag.getName());
		PicInfo picInfo = TopicUtils.getImageFromTag(tag,
				ImageType._IMAGE_TYPE_SMALL);
		if (picInfo != null) {
			Log.d(TAG,
					"[getView] position = " + position + ",url = "
							+ picInfo.getUrl());
			loadImage(picInfo.getUrl(), viewHolder);
		}
		return convertView;
	}

	private void loadImage(String url, final ViewHolder viewHolder) {
		ImageLoadingListener listener = new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String paramString, View paramView) {

			}

			@Override
			public void onLoadingFailed(String paramString, View paramView,
					FailReason paramFailReason) {

				Log.d(TAG, "[onLoadingFailed]url = " + paramString
						+ ", failReason = "
						+ paramFailReason.getCause().getMessage());
			}

			@Override
			public void onLoadingComplete(String paramString, View paramView,
					Bitmap paramBitmap) {
				Log.d(TAG, "[onLoadingComplete]url = " + paramString);
				Bitmap bitmap;
				if(paramBitmap != null){
					bitmap = ImageUtil.toRoundCorner(paramBitmap, 10);
				}else{
					paramBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.article_list_item_loading_big);
					bitmap = ImageUtil.toRoundCorner(paramBitmap, 10);
				}
				viewHolder.tagImageView.setImageBitmap(bitmap);
			}

			@Override
			public void onLoadingCancelled(String paramString, View paramView) {
				mLoader.loadImage(paramString, this); // cancel之后重新load一次，有可能是因为重复加载相同图片导致cancel
			}
		};
		mLoader.loadImage(url, listener);
	}

	static class ViewHolder {
		ImageView tagImageView;
		TextView tagNameTextView;
	}
}
