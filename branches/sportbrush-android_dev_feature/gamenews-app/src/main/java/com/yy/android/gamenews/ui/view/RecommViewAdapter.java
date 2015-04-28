package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.ArticleInfo;
import com.yy.android.sportbrush.R;

public class RecommViewAdapter extends
		ViewHolderAdapter<ArticleInfo, RecommViewAdapter.ViewHolder> {

	public RecommViewAdapter(Context context) {
		super(context);
	}

	public static class ViewHolder {
		ImageView image;
		TextView text;
	}

	@Override
	protected int getViewResourceId(int type) {
		return R.layout.article_detail_image_recomm_list_item;
	}

	@Override
	protected ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();

		holder.image = (ImageView) convertView
				.findViewById(R.id.recomm_item_img);
		holder.text = (TextView) convertView
				.findViewById(R.id.recomm_item_text);
		return holder;
	}

	@Override
	protected void updateHolder(ArticleInfo item, ViewHolder holder) {
		if (holder == null || item == null) {
			return;
		}

		ArrayList<String> imageList = item.getImageList();
		String imageUrl = "";
		if (imageList != null && imageList.size() > 0) {
			imageUrl = imageList.get(0);
		}
		displayImage(imageUrl, holder.image);
		holder.text.setText(item.getTitle());
	}
}
