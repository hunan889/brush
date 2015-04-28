package com.yy.android.gamenews.plugin.cartport;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.duowan.autonews.CarBrandInfo;
import com.duowan.gamenews.bean.BrandListItemObject;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class BrandChooseAdapter extends ImageAdapter<BrandListItemObject>
		implements OnClickListener, SectionIndexer {
	private Context mContext;
	private HashMap<String, Integer> mHashMap;

	/** 头部数字 */
	public final static int VIEW_TYPE_GITITAL = 0;

	/** 品牌选择 */
	public final static int VIEW_TYPE_BRANDCHOOSE = 1;

	public final static int BRANDID_TYPE = 100;

	public String nums[] = new String[] { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };

	public BrandChooseAdapter(Context context) {
		super(context);
		this.mContext = context;

	}

	public void setData(HashMap<String, Integer> mHashMap) {
		this.mHashMap = mHashMap;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BrandListItemObject item = getItem(position);
		if (item.getType() == VIEW_TYPE_BRANDCHOOSE) {
			return getItemBrandView(position, convertView, parent);
		} else if (item.getType() == VIEW_TYPE_GITITAL) {
			return getItemGitigalView(position, convertView, parent);
		}
		return null;
	}

	private View getItemBrandView(int position, View convertView,
			ViewGroup parent) {
		BrandListItemObject item = getItem(position);
		ViewBrandHolder viewBrandHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewBrandHolder = new ViewBrandHolder();
			convertView = mInflater.inflate(R.layout.brand_choose_fg_layout,
					null);
			viewBrandHolder.brand_choose_item = (LinearLayout) convertView
					.findViewById(R.id.brand_choose_item);
			viewBrandHolder.brand_choose_rtl_one = (LinearLayout) convertView
					.findViewById(R.id.brand_choose_rtl_one);
			viewBrandHolder.brand_choose_rtl_two = (LinearLayout) convertView
					.findViewById(R.id.brand_choose_rtl_two);
			viewBrandHolder.brand_choose_rtl_three = (LinearLayout) convertView
					.findViewById(R.id.brand_choose_rtl_three);
			viewBrandHolder.brand_choose_img_one = (ImageView) convertView
					.findViewById(R.id.brand_choose_img_one);
			viewBrandHolder.brand_choose_img_two = (ImageView) convertView
					.findViewById(R.id.brand_choose_img_two);
			viewBrandHolder.brand_choose_img_three = (ImageView) convertView
					.findViewById(R.id.brand_choose_img_three);
			viewBrandHolder.brand_choose_txt_one = (TextView) convertView
					.findViewById(R.id.brand_choose_txt_one);
			viewBrandHolder.brand_choose_txt_two = (TextView) convertView
					.findViewById(R.id.brand_choose_txt_two);
			viewBrandHolder.brand_choose_txt_three = (TextView) convertView
					.findViewById(R.id.brand_choose_txt_three);
			convertView.setTag(viewBrandHolder);
		} else {
			viewBrandHolder = (ViewBrandHolder) convertView.getTag();
		}
		CarBrandInfo carBrandInfoOne = (CarBrandInfo) item.getObjectOne();
		CarBrandInfo carBrandInfoTwo = (CarBrandInfo) item.getObjectTwo();
		CarBrandInfo carBrandInfoThree = (CarBrandInfo) item.getObjectThree();
		if (carBrandInfoOne != null) {
			displayImage(carBrandInfoOne.getIcon(),
					viewBrandHolder.brand_choose_img_one);
			viewBrandHolder.brand_choose_txt_one.setText(carBrandInfoOne
					.getName());
			showOrHide(viewBrandHolder.brand_choose_rtl_one, true,
					carBrandInfoOne);
		} else {
			showOrHide(viewBrandHolder.brand_choose_rtl_one, false,
					carBrandInfoOne);
		}
		if (carBrandInfoTwo != null) {
			displayImage(carBrandInfoTwo.getIcon(),
					viewBrandHolder.brand_choose_img_two);
			viewBrandHolder.brand_choose_txt_two.setText(carBrandInfoTwo
					.getName());
			showOrHide(viewBrandHolder.brand_choose_rtl_two, true,
					carBrandInfoTwo);
		} else {
			showOrHide(viewBrandHolder.brand_choose_rtl_two, false,
					carBrandInfoTwo);
		}
		if (carBrandInfoThree != null) {
			displayImage(carBrandInfoThree.getIcon(),
					viewBrandHolder.brand_choose_img_three);
			viewBrandHolder.brand_choose_txt_three.setText(carBrandInfoThree
					.getName());
			showOrHide(viewBrandHolder.brand_choose_rtl_three, true,
					carBrandInfoThree);
		} else {
			showOrHide(viewBrandHolder.brand_choose_rtl_three, false,
					carBrandInfoThree);
		}
		return convertView;
	}

	private View getItemGitigalView(int position, View convertView,
			ViewGroup parent) {
		BrandListItemObject item = getItem(position);
		ViewTigitalHolder viewTigitalHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewTigitalHolder = new ViewTigitalHolder();
			convertView = mInflater.inflate(R.layout.brand_gitital_fg_layout,
					null);
			viewTigitalHolder.brand_gitital_item = (LinearLayout) convertView
					.findViewById(R.id.brand_gitital_item);
			viewTigitalHolder.brand_gitital_view = (View) convertView
					.findViewById(R.id.brand_gitital_view);
			viewTigitalHolder.brand_gitital_txt = (TextView) convertView
					.findViewById(R.id.brand_gitital_txt);
			convertView.setTag(viewTigitalHolder);
		} else {
			viewTigitalHolder = (ViewTigitalHolder) convertView.getTag();
		}
		String carBrandGitital = (String) item.getObjectOne();
		viewTigitalHolder.brand_gitital_txt.setText(carBrandGitital);
		return convertView;
	}

	@Override
	public void onClick(View view) {
		CarBrandInfo carBrandInfo = (CarBrandInfo) view.getTag();
		if (carBrandInfo != null) {
			StatsUtil.statsReport(mContext, "brand_choose", "brand_choose",
					carBrandInfo.getName());
			StatsUtil
					.statsReportByHiido("brand_choose", carBrandInfo.getName());
			StatsUtil.statsReportByMta(mContext, "brand_choose",
					carBrandInfo.getName());
			BrandDetailActivity.startActivity(mContext, carBrandInfo.getId(),
					carBrandInfo.getName(), carBrandInfo.getImage());
		} else {
			ToastUtil.showToast(R.string.brand_choose_no_models);
		}
	}

	private void showOrHide(View view, boolean isShow, CarBrandInfo carBrandInfo) {
		if (isShow) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(this);
			if (carBrandInfo != null) {
				view.setTag(carBrandInfo);
			}
		} else {
			view.setVisibility(View.INVISIBLE);
			view.setOnClickListener(null);
			view.setClickable(false);
		}
	}

	@Override
	public int getItemViewType(int position) {
		BrandListItemObject item = getItem(position);
		if (item.getType() == VIEW_TYPE_GITITAL) {
			return VIEW_TYPE_GITITAL;
		} else if (item.getType() == VIEW_TYPE_BRANDCHOOSE) {
			return VIEW_TYPE_BRANDCHOOSE;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	static class ViewBrandHolder {
		LinearLayout brand_choose_item;

		LinearLayout brand_choose_rtl_one;

		LinearLayout brand_choose_rtl_two;

		LinearLayout brand_choose_rtl_three;

		ImageView brand_choose_img_one;

		ImageView brand_choose_img_two;

		ImageView brand_choose_img_three;

		TextView brand_choose_txt_one;

		TextView brand_choose_txt_two;

		TextView brand_choose_txt_three;

	}

	static class ViewTigitalHolder {
		LinearLayout brand_gitital_item;

		View brand_gitital_view;

		TextView brand_gitital_txt;

	}

	@Override
	public Object[] getSections() {

		return null;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		String key = (char) (sectionIndex) + "";
		if (mHashMap != null) {
			Integer integer = mHashMap.get(key);
			if (integer != null) {
				return integer;
			}
			return -1;
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {

		return 0;
	}

}
