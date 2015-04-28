package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.duowan.autonews.CarDetailItemDetail;
import com.duowan.autonews.ItemDetail;
import com.duowan.autonews.SubItemDetail;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.bean.CartDetailParamItem;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class CartDetailParamFragment extends
		BaseListFragment<CartDetailParamItem> {

	private static final String KEY_RSP = "detail_rsp";
	private TextView mTitle;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		View header = getLayoutInflater(savedInstanceState).inflate(
				R.layout.cartdetail_params_list_title, null);

		mTitle = (TextView) header.findViewById(R.id.params_name);

		mDataViewConverter.addHeader(header);

		if (savedInstanceState != null) {
			mDetail = (CarDetailItemDetail) savedInstanceState
					.getSerializable(KEY_RSP);
		}
		if (mDetail != null) {
			updateParams(mDetail);
		} else {
			requestData(0);
		}
		setEmptyViewClickable(false);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void requestData(int refreType) {
		showView(VIEW_TYPE_EMPTY);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_RSP, mDetail);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected ImageAdapter<CartDetailParamItem> initAdapter() {
		return new CartDetailParamAdapter(getActivity());
	}

	@Override
	protected boolean isRefreshable() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	private CarDetailItemDetail mDetail;

	public void updateParams(CarDetailItemDetail detail) {
		mDetail = detail;
		String name = "";
		ArrayList<ItemDetail> detailList = null;
		if (detail != null) {
			name = detail.getName();
			detailList = detail.getDetail();
		}
		if (mTitle != null) {
			mTitle.setText(name);
		}

		requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
				getCartDetailParamItemList(detailList), false, true, false);
	}

	private ArrayList<CartDetailParamItem> getCartDetailParamItemList(
			ArrayList<ItemDetail> detailList) {
		ArrayList<CartDetailParamItem> list = new ArrayList<CartDetailParamItem>();
		CartDetailParamItem item = null;
		if (detailList == null) {
			return list;
		}
		for (ItemDetail detail : detailList) {
			item = new CartDetailParamItem();
			item.setName(detail.getName());
			item.setType(CartDetailParamItem.TYPE_NAME);
			list.add(item);
			for (SubItemDetail subDetail : detail.getSubList()) {

				item = new CartDetailParamItem();
				item.setType(CartDetailParamItem.TYPE_ITEM);
				item.setDetail(subDetail);
				list.add(item);
			}
		}
		return list;
	}

}
