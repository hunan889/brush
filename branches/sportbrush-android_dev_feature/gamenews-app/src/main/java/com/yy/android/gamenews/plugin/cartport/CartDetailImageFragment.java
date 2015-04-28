package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.autonews.CarImageList;
import com.duowan.autonews.CarPicInfo;
import com.duowan.autonews.GetCarImageColumnRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.bean.CarImageColumnDoubleItem;
import com.duowan.gamenews.bean.CarImageColumnOneItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class CartDetailImageFragment extends BaseListFragment<Object> {

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private ActionBar mActionBar;
	private ImageView mImgView;
	private long mId;
	public String mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mId = bundle.getLong(CartDetailActivity.TAG_FGMT_CARTID, 0);
		mTitle = bundle.getString(CartDetailImageActivity.TAG_FGMT_CAR_COLUMN);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected void customizeView(ViewGroup viewGroup) {
		super.customizeView(viewGroup);
		View view = mInflater.inflate(R.layout.brand_detail_header, null);
		mImgView = (ImageView) view.findViewById(R.id.brand_detail_header_img);
		mImgView.setScaleType(ScaleType.CENTER_CROP);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = Util.dip2px(getActivity(), 48);
		mImgView.setLayoutParams(params);
		if (mDataViewConverter != null) {
			mDataViewConverter.addHeader(view);
		}
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected boolean isRefreshable() {
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup createView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);
		View view = mInflater.inflate(R.layout.cartdetail_image_fragment, null);
		mActionBar = (ActionBar) view.findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onActivityBackPressed();
			}
		});
		View mActionBarBg = mActionBar.findViewById(R.id.actionbar_container);
		mActionBarBg.setBackgroundColor(0xff222528);
		if (!TextUtils.isEmpty(mTitle)) {
			mActionBar.setTitle(mTitle.trim());
		}
		createView.addView(view, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		return createView;

	}

	@Override
	protected void requestData(final int refreType) {
		showView(VIEW_TYPE_LOADING);
		GetCarImageColumnModel.getCarImageColumn(
				new ResponseListener<GetCarImageColumnRsp>(getActivity()) {

					@Override
					public void onResponse(GetCarImageColumnRsp arg0) {
						analysisData(refreType, arg0);
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType, null, false, true, false);
					}
				}, (int) mId, "");

	}

	private void analysisData(int refreType, GetCarImageColumnRsp arg0) {
		if (arg0 != null && arg0.getBannerImage() != null) {
			mImageLoader.displayImage(arg0.getBannerImage().getBigUrl(),
					mImgView);
		}
		if (arg0 != null && arg0.getImageList() != null) {
			ArrayList<Object> list = new ArrayList<Object>();
			Map<Integer, CarImageList> hashMap = arg0.getImageList();
			Set<Integer> keySet = hashMap.keySet();
			if (arg0.getImageList() != null) {
				adapter.setData(hashMap);
			}
			for (Integer key : keySet) {
				CarImageList carImageList = hashMap.get(key);
				CarImageColumnOneItem columnOneItem = new CarImageColumnOneItem();
				columnOneItem.setType(CartDetailImageAdapter.POS_TITLE);
				columnOneItem.setTitle(carImageList.getTitle());
				columnOneItem.setNums(carImageList.getPicList().size());
				list.add(columnOneItem);
				ArrayList<CarPicInfo> picList = carImageList.getPicList();
				for (int i = 0; i < carImageList.getPicList().size() - 1
						&& i < 4; i = i + 2) {
					CarImageColumnDoubleItem columnDoubleItem = new CarImageColumnDoubleItem();
					columnDoubleItem.setType(CartDetailImageAdapter.POS_PARAMS);
					columnDoubleItem.setKeyItem(key);
					columnDoubleItem.setPicInfoOne(picList.get(i));
					columnDoubleItem.setPicInfoTwo(picList.get(i + 1));
					columnDoubleItem.setPicInfoOneLocation(i);
					columnDoubleItem.setPicInfoTwoLocation(i + 1);
					list.add(columnDoubleItem);
				}
				if (carImageList.getPicList().size() < 4
						&& carImageList.getPicList().size() % 2 != 0) {
					CarImageColumnDoubleItem columnDoubleItem = new CarImageColumnDoubleItem();
					columnDoubleItem.setType(CartDetailImageAdapter.POS_PARAMS);
					columnDoubleItem.setPicInfoOne(picList.get(carImageList
							.getPicList().size() - 1));
					columnDoubleItem.setPicInfoTwo(null);
					columnDoubleItem.setKeyItem(key);
					columnDoubleItem.setPicInfoOne(picList.get(carImageList
							.getPicList().size() - 1));
					list.add(columnDoubleItem);
				}
			}
			requestFinish(refreType, list, false, true, false);
		}

	}

	@Override
	protected void requestFinish(int refresh, ArrayList<Object> data,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
		if (data != null & data.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
	}

	private CartDetailImageAdapter adapter;

	@Override
	protected ImageAdapter<Object> initAdapter() {
		if (adapter == null) {
			adapter = new CartDetailImageAdapter(getActivity(), mTitle);
		}
		return adapter;
	}

}
