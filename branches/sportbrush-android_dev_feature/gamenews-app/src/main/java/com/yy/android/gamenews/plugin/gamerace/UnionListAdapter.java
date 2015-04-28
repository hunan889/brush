package com.yy.android.gamenews.plugin.gamerace;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.UnionInfo;
import com.duowan.gamenews.UnionType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.sportbrush.R;

public class UnionListAdapter extends ImageAdapter<UnionInfo> {
	public static final int UNION_RACE_TOPIC = 3;
	private Context mContext;
	private int unionType;
	private DisplayImageOptions displayImageOptions;

	private final static int[] orderImages = { R.drawable.one, R.drawable.two,
			R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six,
			R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten };

	public UnionListAdapter(Context context) {
		super(context);
		this.mContext = context;
		displayImageOptions = SwitchImageLoader
				.getDisplayOptions(R.drawable.union_default_logo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		UnionInfo unionInfo = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater
					.inflate(R.layout.union_list_item_view, null);
			viewHolder.orderImageView = (ImageView) convertView
					.findViewById(R.id.iv_order);
			viewHolder.unionLogoImageView = (ImageView) convertView
					.findViewById(R.id.iv_union_logo);
			viewHolder.unionNameTextView = (TextView) convertView
					.findViewById(R.id.tv_union_name);
			viewHolder.unionDescTextView = (TextView) convertView
					.findViewById(R.id.tv_union_desc);
			viewHolder.unioHeatTextView = (TextView) convertView
					.findViewById(R.id.tv_union_heat);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
		mImageLoader.displayImage(unionInfo.getImg(),
				viewHolder.unionLogoImageView, displayImageOptions);
		viewHolder.unionNameTextView.setText(unionInfo.getName());
		viewHolder.unionDescTextView.setText(unionInfo.getDesc());
		viewHolder.unioHeatTextView.setText(String.format(mContext
				.getResources().getString(R.string.union_support), unionInfo
				.getHeat()));
		if (unionType == UnionType._UNION_TYPE_TOP) {
			viewHolder.orderImageView.setVisibility(View.VISIBLE);
			int index = position < orderImages.length ? position
					: orderImages.length - 1;
			viewHolder.orderImageView.setImageResource(orderImages[index]);
		} else if (unionType == UnionListAdapter.UNION_RACE_TOPIC) {
			convertView
					.setBackgroundResource(R.drawable.union_race_item_selector);
			if (position >= 10) {
				viewHolder.orderImageView.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.orderImageView.setVisibility(View.VISIBLE);
				int index = position < orderImages.length ? position
						: orderImages.length - 1;
				viewHolder.orderImageView.setImageResource(orderImages[index]);
			}
		} else {
			viewHolder.orderImageView.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView orderImageView;
		ImageView unionLogoImageView;
		TextView unionNameTextView;
		TextView unionDescTextView;
		TextView unioHeatTextView;
	}

	public void setUnionType(int unionType) {
		this.unionType = unionType;
	}
}
