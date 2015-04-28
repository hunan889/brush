package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.autonews.SubItemDetail;
import com.duowan.gamenews.bean.CartDetailParamItem;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class CartDetailParamAdapter extends ImageAdapter<CartDetailParamItem> {

	public CartDetailParamAdapter(Context context) {
		super(context);
	}

	private static final int POS_TITLE = 0;
	private static final int POS_PARAMS = 1;

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {

		CartDetailParamItem detail = getItem(position);
		switch (detail.getType()) {
		case CartDetailParamItem.TYPE_ITEM: {
			return POS_PARAMS;
		}
		case CartDetailParamItem.TYPE_NAME: {
			return POS_TITLE;
		}
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			switch (getItemViewType(position)) {
			case POS_PARAMS: {
				convertView = mInflater.inflate(
						R.layout.cartdetail_params_list_item, null);
				break;
			}

			case POS_TITLE: {
				convertView = mInflater.inflate(
						R.layout.cartdetail_params_list_sep, null);
				break;
			}

			}
			holder = new ViewHolder();

			holder.nameView = (TextView) convertView
					.findViewById(R.id.params_name);
			holder.valueView = (TextView) convertView
					.findViewById(R.id.params_value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CartDetailParamItem detail = getItem(position);

		switch (getItemViewType(position)) {
		case POS_PARAMS: {
			SubItemDetail subDetail = detail.getDetail();
			holder.nameView.setText(subDetail.getName());
			holder.valueView.setText(subDetail.getValue());
			break;
		}
		case POS_TITLE: {
			holder.nameView.setText(detail.getName());
			break;
		}
		}

		return convertView;
	}

	private static final class ViewHolder {
		TextView nameView;
		TextView valueView;
	}
}
