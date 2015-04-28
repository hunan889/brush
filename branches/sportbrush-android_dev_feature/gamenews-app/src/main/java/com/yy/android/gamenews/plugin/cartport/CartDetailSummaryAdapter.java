package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.autonews.CarDetailItemDetail;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class CartDetailSummaryAdapter extends ImageAdapter<CarDetailItemDetail> {

	public CartDetailSummaryAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.cartdetail_summary_list_item, null);
			holder = new ViewHolder();

			holder.nameView = (TextView) convertView
					.findViewById(R.id.cartdetail_sum_item_name);
			holder.priceView = (TextView) convertView
					.findViewById(R.id.cartdetail_sum_item_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CarDetailItemDetail detail = getItem(position);
		holder.nameView.setText(detail.getName());
		holder.priceView.setText(detail.getPrice());

		return convertView;
	}

	private static final class ViewHolder {
		TextView nameView;
		TextView priceView;
	}

}
