package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

import com.duowan.autonews.CarBrandInfo;
import com.duowan.autonews.GetCarBrandListReq;
import com.duowan.autonews.GetCarBrandListRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class BrandChooseModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;

	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getBrandChooseList(
			final ResponseListener<GetCarBrandListRsp> responseListener,
			int count, String attachInfo) {
		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					GetCarBrandListRsp rsp = new GetCarBrandListRsp();

					Map<String, ArrayList<CarBrandInfo>> allBrandInfo = new HashMap<String, ArrayList<CarBrandInfo>>();
					ArrayList<CarBrandInfo> list = new ArrayList<CarBrandInfo>();
					for (int i = 0; i < 10; i++) {
						CarBrandInfo carBrandInfo = new CarBrandInfo();
						carBrandInfo.setId(i);
						carBrandInfo.setName("大众" + i);
						carBrandInfo
								.setIcon("http://img.kumi.cn/photo/5e/36/7f/5e367fe5b64776c1.jpg");
						list.add(carBrandInfo);
					}
					allBrandInfo.put("A", list);

					list = new ArrayList<CarBrandInfo>();
					for (int i = 0; i < 10; i++) {
						CarBrandInfo carBrandInfo = new CarBrandInfo();
						carBrandInfo.setId(i);
						carBrandInfo.setName("丰田" + i);
						carBrandInfo
								.setIcon("http://img.kumi.cn/photo/5e/36/7f/5e367fe5b64776c1.jpg");
						list.add(carBrandInfo);
					}
					allBrandInfo.put("B", list);

					list = new ArrayList<CarBrandInfo>();
					for (int i = 0; i < 14; i++) {
						CarBrandInfo carBrandInfo = new CarBrandInfo();
						carBrandInfo.setId(i);
						carBrandInfo.setName("别克" + i);
						carBrandInfo
								.setIcon("http://img.kumi.cn/photo/5e/36/7f/5e367fe5b64776c1.jpg");
						list.add(carBrandInfo);
					}

					allBrandInfo.put("D", list);

					rsp.setAllBrandInfo(allBrandInfo);
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

		GetCarBrandListReq req = new GetCarBrandListReq();
		req.setCount(count); // 默认10个
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetCarBrandList", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetCarBrandListRsp rsp = new GetCarBrandListRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}

}
