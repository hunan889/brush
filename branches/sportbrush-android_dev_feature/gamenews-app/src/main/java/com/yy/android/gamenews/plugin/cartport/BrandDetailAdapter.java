package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.autonews.CarListInfo;
import com.duowan.gamenews.bean.CarCateListItembject;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class BrandDetailAdapter extends ImageAdapter<CarCateListItembject> {

	private Context mContext;

	/** 头部数字 */
	public final static int VIEW_TYPE_HEAD = 0;

	/** 车系 */
	public final static int VIEW_TYPE_BRAND = 1;

	public final static int BRANDID_TYPE = 100;

	public BrandDetailAdapter(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		if (itemViewType == VIEW_TYPE_BRAND) {
			return getItemBrandView(position, convertView, parent);
		} else if (itemViewType == VIEW_TYPE_HEAD) {
			return getItemHeadView(position, convertView, parent);
		}
		return null;
	}

	private View getItemBrandView(int position, View convertView,
			ViewGroup parent) {
		CarCateListItembject item = getItem(position);
		ViewBrandDetailHolder viewBrandDetailHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewBrandDetailHolder = new ViewBrandDetailHolder();
			convertView = mInflater.inflate(R.layout.brand_detail_fg_layout,
					null);
			viewBrandDetailHolder.brand_detail_item = (LinearLayout) convertView
					.findViewById(R.id.brand_detail_item);
			viewBrandDetailHolder.brand_detail_img = (ImageView) convertView
					.findViewById(R.id.brand_detail_img);
			viewBrandDetailHolder.brand_detail_name = (TextView) convertView
					.findViewById(R.id.brand_detail_name);
			viewBrandDetailHolder.brand_detail_price = (TextView) convertView
					.findViewById(R.id.brand_detail_price);
			convertView.setTag(viewBrandDetailHolder);
		} else {
			viewBrandDetailHolder = (ViewBrandDetailHolder) convertView
					.getTag();
		}

		CarListInfo carCateItem = (CarListInfo) item.getObject();

		if (carCateItem != null) {
			displayImage(carCateItem.getIcon(),
					viewBrandDetailHolder.brand_detail_img);
			viewBrandDetailHolder.brand_detail_name.setText(carCateItem
					.getName());
			viewBrandDetailHolder.brand_detail_price.setText(carCateItem
					.getPrice());
			showOrHide(viewBrandDetailHolder.brand_detail_item, true,
					carCateItem.getId(), carCateItem.getName());
		} else {
			showOrHide(viewBrandDetailHolder.brand_detail_item, false, -1, null);
		}

		return convertView;
	}

	private View getItemHeadView(int position, View convertView,
			ViewGroup parent) {
		CarCateListItembject item = getItem(position);
		ViewHeadHolder viewTigitalHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewTigitalHolder = new ViewHeadHolder();
			convertView = mInflater.inflate(
					R.layout.brand_detail_header_layout, null);
			viewTigitalHolder.brand_detail_item = (LinearLayout) convertView
					.findViewById(R.id.brand_detail_header_item);
			viewTigitalHolder.brand_detail_txt = (TextView) convertView
					.findViewById(R.id.brand_detail_header_txt);
			convertView.setTag(viewTigitalHolder);
		} else {
			viewTigitalHolder = (ViewHeadHolder) convertView.getTag();
		}
		String carHeadItem = (String) item.getObject();
		viewTigitalHolder.brand_detail_txt.setText(carHeadItem);
		return convertView;
	}

	private void showOrHide(View view, boolean isShow, final int brandid,
			final String brandName) {
		if (isShow) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					StatsUtil.statsReport(mContext, "into_cart_datail", "desc",
							"into_cart_datail");
					StatsUtil
							.statsReportByHiido("into_cart_datail", "into_cart_datail");
					StatsUtil.statsReportByMta(mContext, "into_cart_datail",
							"into_cart_datail");
					CartDetailActivity.startCartDetailActivity(mContext,
							brandid, brandName);
				}
			});
		} else {
			view.setVisibility(View.GONE);
			view.setOnClickListener(null);
		}
	}

	@Override
	public int getItemViewType(int position) {
		CarCateListItembject item = getItem(position);
		if (item.getType() == VIEW_TYPE_HEAD) {
			return VIEW_TYPE_HEAD;
		} else if (item.getType() == VIEW_TYPE_BRAND) {
			return VIEW_TYPE_BRAND;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	class ViewBrandDetailHolder {
		LinearLayout brand_detail_item;

		ImageView brand_detail_img;

		TextView brand_detail_name;

		TextView brand_detail_price;

	}

	class ViewHeadHolder {
		LinearLayout brand_detail_item;

		TextView brand_detail_txt;

	}

}
