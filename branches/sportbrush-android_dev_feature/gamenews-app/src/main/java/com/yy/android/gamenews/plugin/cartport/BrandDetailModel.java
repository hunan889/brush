package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.autonews.CarCategory;
import com.duowan.autonews.CarListInfo;
import com.duowan.autonews.GetCarListReq;
import com.duowan.autonews.GetCarListRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class BrandDetailModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;

	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getBrandDetailList(
			final ResponseListener<GetCarListRsp> responseListener, int count,
			String attachInfo, int brandId) {
		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					GetCarListRsp rsp = new GetCarListRsp();
					rsp.setHasMore(100);
					rsp.setAttachInfo("GetCarList");
					ArrayList<CarCategory> carList = new ArrayList<CarCategory>();
					for (int i = 0; i < 3; i++) {
						CarCategory carCategory = new CarCategory();
						ArrayList<CarListInfo> carListInfoList = new ArrayList<CarListInfo>();
						carCategory.setName("宝马" + i);
						for (int j = 0; j < 10; j++) {
							CarListInfo carListInfo = new CarListInfo();
							carListInfo.setId(Integer.valueOf(i + "" + j));
							carListInfo.setName("丰田" + j);
							carListInfo
									.setIcon("http://www.mofei.com.cn/tupian/UploadPic/2010/2010524101128632.jpg");
							carListInfo.setPrice("240万元");
							carListInfoList.add(carListInfo);
						}
						carCategory.setList(carListInfoList);
						carList.add(carCategory);
					}
					rsp.setCarList(carList);
					responseListener.onResponse(rsp);
				}
			}, 2000);
			return;
		}
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			responseListener.onError(null);
			return;
		}

		GetCarListReq req = new GetCarListReq();
		req.setBrandId(brandId);
		req.setCount(count); // 默认10个
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetCarList", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetCarListRsp rsp = new GetCarListRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}
}
