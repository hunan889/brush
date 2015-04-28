package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.autonews.CarDetail;
import com.duowan.autonews.CarDetailItemDetail;
import com.duowan.autonews.GetCarDetailRsp;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.model.CartModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.SingleFragmentActivity;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class CartDetailSummaryFragment extends
		BaseListFragment<CarDetailItemDetail> {
	private static final int COUNT = 10;
	private GetCarDetailRsp mRsp;
	private ImageView mCarImageView;
	private TextView mCarNameView;
	private TextView mLevelView;
	private TextView mPriceView;
	private SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();

	private FragmentMessageListener mListener;
	private static final String KEY_RSP = "detail_rsp";
	private int mCartId;
	private String mAttachInfop;

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof SingleFragmentActivity) {
			mListener = ((SingleFragmentActivity) activity).getListener();
		}
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Bundle params = getArguments();

		if (params != null) {
			mCartId = params.getInt(CartDetailActivity.TAG_FGMT_CARTID);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void customizeView(ViewGroup viewGroup) {
		View header = mInflater.inflate(R.layout.cartdetail_summary_header,
				null);
		mCarImageView = (ImageView) header
				.findViewById(R.id.cartdetail_summary_img);
		mCarNameView = (TextView) header.findViewById(R.id.cartdetail_car_name);
		mLevelView = (TextView) header.findViewById(R.id.cartdetail_car_level);
		mPriceView = (TextView) header.findViewById(R.id.cartdetail_car_price);

		mDataViewConverter.addHeader(header);
		super.customizeView(viewGroup);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			mRsp = (GetCarDetailRsp) savedInstanceState
					.getSerializable(KEY_RSP);
		}
		if (mRsp != null) {
			requestFinish(0, mRsp, false);
		} else {
			refreshData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_RSP, mRsp);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void requestData(final int refreshType) {
		if (mAttachInfop == null) {
			mAttachInfop = "";
		}
		CartModel.getCarDetailRsp(new ResponseListener<GetCarDetailRsp>(
				getActivity()) {

			@Override
			public void onResponse(GetCarDetailRsp response) {
				if (response != null && response.getAttachInfo() != null) {
					mAttachInfop = response.getAttachInfo();
				}
				requestFinish(refreshType, response, false);
			}

			@Override
			public void onError(Exception e) {
				requestFinish(refreshType, null, true);
				super.onError(e);
			}
		}, mCartId, COUNT, mAttachInfop);

	}

	private void prepareEmptyText(boolean error) {
		String emptyText = "";

		if (TextUtils.isEmpty(emptyText)) {
			if (error) {
				setEmptyViewClickable(true);
				emptyText = strEmptyReload;
			} else {
				setEmptyViewClickable(false);
				emptyText = strEmptyNoData;
			}
		}
		setEmptyText(emptyText);
	}

	private void requestFinish(int refreshType, GetCarDetailRsp rsp,
			boolean error) {
		mRsp = rsp;
		boolean loadMore = false;
		if (rsp != null && rsp.getHasMore() > 0) {
			loadMore = true;
		} else {
			loadMore = false;
		}
		ArrayList<CarDetailItemDetail> listData = null;
		CarDetailItemDetail first = null;
		prepareEmptyText(error);
		if (rsp != null) {
			CarDetail detail = rsp.getDetail();
			if (detail != null) {
				listData = detail.getItemList();
				if (refreshType == RefreshType._REFRESH_TYPE_LOAD_MORE) {
					requestFinish(refreshType, listData, loadMore, false, false);
					return;
				}
				mCarNameView.setText(detail.getName());
				mLevelView.setText("级别：" + detail.getLevel());
				mPriceView.setText("官方价：" + detail.getPrice());
				mImageLoader.displayImage(detail.getIcon(), mCarImageView);
				if (listData != null && listData.size() > 0) {

					first = listData.get(0);
				}
			}
		}
		updateParams(first);
		requestFinish(refreshType, listData, loadMore, true, false);
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		CarDetailItemDetail detail = (CarDetailItemDetail) adapter
				.getItem(position);
		if(detail!=null){
			StatsUtil.statsReport(mContext, "into_cart_params", "desc",
					"into_cart_params");
			StatsUtil
					.statsReportByHiido("into_cart_params", "into_cart_params");
			StatsUtil.statsReportByMta(mContext, "into_cart_params",
					"into_cart_params");
		}
		onItemClick(detail);
	}

	private void onItemClick(CarDetailItemDetail detail) {
		updateParams(detail);
		showParamsPage();
	}

	private void showParamsPage() {
		if (mListener != null) {
			mListener.onMessage(FragmentMessageListener.MSG_SHOW_CART_PARAMS,
					null);
		}
	}

	private void updateParams(CarDetailItemDetail detail) {
		if (mListener != null) {
			mListener.onMessage(FragmentMessageListener.MSG_UPDATE_CART_PARAMS,
					detail);
		}
	}

	@Override
	protected ImageAdapter<CarDetailItemDetail> initAdapter() {
		return new CartDetailSummaryAdapter(getActivity());
	}

	@Override
	protected boolean isRefreshable() {
		return true;
	}

	@Override
	protected boolean isRefreshableHead() {

		return false;
	}

	@Override
	protected boolean isRefreshableLoad() {
		if (isRefreshable()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

}
