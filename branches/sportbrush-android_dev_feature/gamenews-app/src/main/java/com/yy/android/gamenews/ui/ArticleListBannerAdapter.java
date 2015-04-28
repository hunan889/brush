package com.yy.android.gamenews.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.ArticleFlag;
import com.duowan.gamenews.ArticleInfo;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.sportbrush.R;

public class ArticleListBannerAdapter extends ImageAdapter<ArticleInfo> {

	private static final String TAG = "Banner";

	public ArticleListBannerAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ArticleInfo info = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.article_list_banner_item,
					null);
			holder = new ViewHolder();
			holder.mCount = (TextView) convertView
					.findViewById(R.id.article_list_banner_count);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.article_list_banner_image);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.article_list_banner_title);
			holder.mPos = (TextView) convertView
					.findViewById(R.id.article_list_banner_pos);
			holder.mSep = (TextView) convertView
					.findViewById(R.id.article_list_banner_sep);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String bigImage = info.extraInfo
				.get((long) ArticleFlag._ARTICLE_FLAG_BIGIMAGE);
		displayImage(bigImage, holder.mImage,
				SwitchImageLoader.DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER);

		int count = getCount();
		if (count > 1) {
			holder.mCount.setText("" + getCount());
			holder.mPos.setText("" + (position + 1));
			holder.mCount.setVisibility(View.VISIBLE);
			holder.mPos.setVisibility(View.VISIBLE);
			holder.mSep.setVisibility(View.VISIBLE);
		} else {
			holder.mCount.setVisibility(View.GONE);
			holder.mPos.setVisibility(View.GONE);
			holder.mSep.setVisibility(View.GONE);

		}
		holder.mTitle.setText(info.title);

		return convertView;
	}

	private static class ViewHolder {
		ImageView mImage;
		TextView mTitle;
		TextView mPos;
		TextView mCount;
		TextView mSep;
	}

}
