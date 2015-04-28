package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.gamenews.ArticleInfo;
import com.yy.android.gamenews.ui.GalleryAdapter;

public class ArticleGalleryAdapter extends GalleryAdapter {

	public ArticleGalleryAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getItemViewType(int position) {
		GalleryItem item = getItem(position);
		if (item instanceof ArticleGalleryItem) {
			return getRecommItemViewType();
		}
		return super.getItemViewType(position);
	}

	private int getRecommItemViewType() {
		return getViewTypeCount() - 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		GalleryItem item = getItem(position);
		if (item instanceof ArticleGalleryItem) {
			RecommView view = new RecommView(getContext());

			view.setDataSource(((ArticleGalleryItem) item).recommList);

			return view;

		}
		return super.getView(position, convertView, parent);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return super.getViewTypeCount() + 1;
	}

	/**
	 * 推荐页
	 * 
	 * @author liuchaoqun
	 * 
	 */
	public static class ArticleGalleryItem extends
			com.yy.android.gamenews.ui.GalleryAdapter.GalleryItem {
		public ArrayList<ArticleInfo> recommList;
	}
}
