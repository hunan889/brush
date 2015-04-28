package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.autonews.CarBrandInfo;
import com.duowan.autonews.CarCategory;
import com.duowan.autonews.CarListInfo;
import com.duowan.autonews.GetCarListRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.bean.CarCateListItembject;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.sportbrush.R;

@SuppressLint("ValidFragment")
public class BrandDetailFragment extends BaseListFragment<CarCateListItembject> {

	private SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
	private static final int COUNT = 20;
	protected IPageCache mPageCache;
	protected Preference mPref;
	private FragmentActivity mActivity;
	private LayoutInflater mInflater;
	private CarBrandInfo mRsp;
	private int mBrandId;
	private String mBrandName;
	private String mImgUrl;

	public BrandDetailFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle arguments = getArguments();
		mBrandId = arguments.getInt(BrandDetailActivity.TAG_BRANDID, 0);
		mBrandName = arguments.getString(BrandDetailActivity.BRAND_NAME);
		mImgUrl = arguments.getString(BrandDetailActivity.IMG_URL);
		mActivity = getActivity();
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected boolean isRefreshable() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected void customizeView(ViewGroup viewGroup) {
		super.customizeView(viewGroup);
		View view = mInflater.inflate(R.layout.brand_detail_header, null);
		ImageView img = (ImageView) view
				.findViewById(R.id.brand_detail_header_img);
		mImageLoader.displayImage(mImgUrl, img);
		if (mDataViewConverter != null) {
			mDataViewConverter.addHeader(view);
		}
	}

	@Override
	protected void requestData(int refreType) {
		if (mRsp != null) {
			// attachInfo = mRsp.getAttachInfo();
		}
		showView(VIEW_TYPE_LOADING);
		BrandDetailModel.getBrandDetailList(
				new ResponseListener<GetCarListRsp>(mActivity) {

					@Override
					public void onResponse(GetCarListRsp arg0) {
						if (arg0 == null || arg0.getCarList() == null) {
							requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
									null, false, false, false);
							return;
						}
						ArrayList<CarCateListItembject> carCatelist = new ArrayList<CarCateListItembject>();
						ArrayList<CarCategory> carList = arg0.getCarList();
						for (CarCategory carCategory : carList) {
							CarCateListItembject headItem = new CarCateListItembject();
							headItem.setType(BrandDetailAdapter.VIEW_TYPE_HEAD);
							headItem.setObject(carCategory.getName());
							carCatelist.add(headItem);
							ArrayList<CarListInfo> list = carCategory.getList();
							for (CarListInfo carListInfo : list) {
								CarCateListItembject carListInfoitem = new CarCateListItembject();
								carListInfoitem
										.setType(BrandDetailAdapter.VIEW_TYPE_BRAND);
								carListInfoitem.setObject(carListInfo);
								carCatelist.add(carListInfoitem);
							}
						}
						requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
								carCatelist, false, false, false);
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(RefreshType._REFRESH_TYPE_REFRESH, null,
								false, false, false);
					}
				}, COUNT, "", mBrandId);

	}

	@Override
	protected void requestFinish(int refresh,
			ArrayList<CarCateListItembject> data, boolean hasMore,
			boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
		if (data != null) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
	}

	@Override
	protected ImageAdapter<CarCateListItembject> initAdapter() {
		return new BrandDetailAdapter(getActivity());
	}
}
