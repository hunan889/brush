package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.autonews.CarListInfo;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.view.AutoAdjustImageView;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class HotCartAdapter extends ImageAdapter<CarListInfo> {
	private Context mContext;

	public HotCartAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CarListInfo item = getItem(position);
		ViewHotCartHolder viewHotCartHolder = null;
		if (convertView == null) {
			viewHotCartHolder = new ViewHotCartHolder();
			convertView = mInflater.inflate(R.layout.hot_cart_fg_layout, null);
			viewHotCartHolder.hot_cart_item = (RelativeLayout) convertView
					.findViewById(R.id.hot_cart_item);
			viewHotCartHolder.hot_cart_img = (AutoAdjustImageView) convertView
					.findViewById(R.id.hot_cart_img);
			viewHotCartHolder.hot_cart_img_pressed = (AutoAdjustImageView) convertView
					.findViewById(R.id.hot_cart_img_pressed);
			viewHotCartHolder.hot_cart_txt = (TextView) convertView
					.findViewById(R.id.hot_cart_txt);
			convertView.setTag(viewHotCartHolder);
		} else {
			viewHotCartHolder = (ViewHotCartHolder) convertView.getTag();
		}
		viewHotCartHolder.hot_cart_txt.setText(item.getName());
		displayImage(item.getIcon(), viewHotCartHolder.hot_cart_img);

		viewHotCartHolder.hot_cart_item
				.setOnTouchListener(new MyOnTouchListener(
						viewHotCartHolder.hot_cart_img_pressed));
		viewHotCartHolder.hot_cart_item
				.setOnClickListener(new MyOnClickListener(position));

		return convertView;
	}

	class MyOnTouchListener implements OnTouchListener {
		View view;

		public MyOnTouchListener(View view) {
			this.view = view;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				view.setVisibility(View.VISIBLE);
				// view.setAlpha((float) 0.60);
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				view.setVisibility(View.GONE);
				// view.setAlpha(1);
				break;
			default:
				break;
			}
			return false;
		}

	}

	class MyOnClickListener implements OnClickListener {
		int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			CarListInfo carListInfo = getItem(position);
			if (carListInfo != null) {
				StatsUtil.statsReport(mContext, "hot_cartport", "hot_cartport",
						carListInfo.getName());
				StatsUtil.statsReportByHiido("hot_cartport",
						carListInfo.getName());
				StatsUtil.statsReportByMta(mContext, "hot_cartport",
						carListInfo.getName());
				CartDetailActivity.startCartDetailActivity(mContext,
						carListInfo.getId(), carListInfo.getName());
			}
		}

	}

	static class ViewHotCartHolder {
		RelativeLayout hot_cart_item;

		AutoAdjustImageView hot_cart_img;

		AutoAdjustImageView hot_cart_img_pressed;

		TextView hot_cart_txt;

	}

}
