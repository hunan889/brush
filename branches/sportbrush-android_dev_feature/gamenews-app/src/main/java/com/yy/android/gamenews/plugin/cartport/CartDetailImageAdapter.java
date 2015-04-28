package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.autonews.CarImageList;
import com.duowan.autonews.CarPicInfo;
import com.duowan.gamenews.bean.CarImageColumnDoubleItem;
import com.duowan.gamenews.bean.CarImageColumnOneItem;
import com.yy.android.gamenews.ui.ImageZoomDetailViewerActivity;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class CartDetailImageAdapter extends ImageAdapter<Object> {

	public static final int POS_TITLE = 0;
	public static final int POS_PARAMS = 1;
	public static final int FIRST_ITEM = 1;
	public static final int SECOND_ITME = 2;
	private String mTitle;
	private Map<Integer, CarImageList> mHashMap;

	public CartDetailImageAdapter(Context context, String title) {
		super(context);
		this.mTitle = title;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	public void setData(Map<Integer, CarImageList> hashMap) {
		this.mHashMap = hashMap;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			switch (getItemViewType(position)) {
			case POS_PARAMS: {
				convertView = mInflater.inflate(
						R.layout.cartdetail_image_list_item, null);
				break;
			}

			case POS_TITLE: {
				convertView = mInflater.inflate(
						R.layout.cartdetail_image_list_sep, null);
				break;
			}

			}
			holder = new ViewHolder();

			holder.nameView = (TextView) convertView
					.findViewById(R.id.params_name);
			holder.picNums = (TextView) convertView
					.findViewById(R.id.params_nums);
			holder.imageOne = (ImageView) convertView
					.findViewById(R.id.image_one);
			holder.imageTwo = (ImageView) convertView
					.findViewById(R.id.image_two);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (getItemViewType(position)) {
		case POS_PARAMS: {
			CarImageColumnDoubleItem columnDoubleItem = (CarImageColumnDoubleItem) getItem(position);
			CarPicInfo picInfoOne = columnDoubleItem.getPicInfoOne();
			CarPicInfo picInfoTwo = columnDoubleItem.getPicInfoTwo();
			if (picInfoOne != null) {
				holder.imageOne.setVisibility(View.VISIBLE);
				displayImage(picInfoOne.getDesc(), holder.imageOne,
						SwitchImageLoader.DEFAULT_ARTICLE_ITEM_DISPLAYER);
				holder.imageOne.setOnClickListener(new OnItemclick(
						columnDoubleItem.getKeyItem(), columnDoubleItem
								.getPicInfoOneLocation()));
			} else {
				holder.imageOne.setVisibility(View.GONE);
			}
			if (picInfoTwo != null) {
				holder.imageTwo.setVisibility(View.VISIBLE);
				displayImage(picInfoTwo.getDesc(), holder.imageTwo,
						SwitchImageLoader.DEFAULT_ARTICLE_ITEM_DISPLAYER);
				holder.imageTwo.setOnClickListener(new OnItemclick(
						columnDoubleItem.getKeyItem(), columnDoubleItem
								.getPicInfoTwoLocation()));
			} else {
				holder.imageTwo.setVisibility(View.GONE);
			}
			break;
		}
		case POS_TITLE: {
			CarImageColumnOneItem columnOneItem = (CarImageColumnOneItem) getItem(position);
			holder.nameView.setText(columnOneItem.getTitle());
			holder.picNums.setText(String.valueOf(columnOneItem.getNums())
					+ "张");
			break;
		}
		}

		return convertView;
	}

	class OnItemclick implements OnClickListener {
		private int items;
		private int position;

		public OnItemclick(int items, int position) {
			this.items = items;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			ArrayList<CarPicInfo> picList = mHashMap.get(items).getPicList();
			if (picList == null) {
				return;
			}
			ArrayList<String> arrayList = new ArrayList<String>();
			for (CarPicInfo carPicInfo : picList) {
				arrayList.add(carPicInfo.getBigUrl());
			}
			ImageZoomDetailViewerActivity.startZoomDetailActivity(getContext(),
					arrayList, position, mTitle);
			StatsUtil.statsReport(getContext(), "into_cart_image_onclick",
					"desc", "into_cart_image_onclick");
			StatsUtil.statsReportByHiido("into_cart_image_onclick",
					"into_cart_image_onclick");
			StatsUtil.statsReportByMta(getContext(), "into_cart_image_onclick",
					"into_cart_image_onclick");
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) != null
				&& getItem(position) instanceof CarImageColumnOneItem) {
			return POS_TITLE;
		} else if (getItem(position) != null
				&& getItem(position) instanceof CarImageColumnDoubleItem) {
			return POS_PARAMS;
		} else {
			return -1;
		}
	}

	private static final class ViewHolder {
		TextView nameView;
		TextView picNums;
		ImageView imageOne;
		ImageView imageTwo;
	}
}
