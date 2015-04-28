package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

import com.duowan.autonews.CarImageList;
import com.duowan.autonews.CarPicInfo;
import com.duowan.autonews.GetCarImageColumnReq;
import com.duowan.autonews.GetCarImageColumnRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class GetCarImageColumnModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = true;

	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getCarImageColumn(
			final ResponseListener<GetCarImageColumnRsp> responseListener,
			final int id, String attachInfo) {
		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					GetCarImageColumnRsp rsp = new GetCarImageColumnRsp();
					rsp.setTitle("汽车刷子");
					CarPicInfo carPicInfo = new CarPicInfo();
					carPicInfo
							.setSmallUrl("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
					carPicInfo
							.setDesc("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
					carPicInfo
							.setBigUrl("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
					rsp.setBannerImage(carPicInfo);

					Map<Integer, CarImageList> hashmap = new HashMap<Integer, CarImageList>();
					for (int i = 0; i < 5; i++) {
						CarImageList CarImageList = new CarImageList();
						CarImageList.setTitle("车身外观" + i);
						ArrayList<CarPicInfo> picList = new ArrayList<CarPicInfo>();
						for (int j = 0; j < 6; j++) {
							CarPicInfo info = new CarPicInfo();
							info.setSmallUrl("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
							info.setDesc("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
							info.setBigUrl("http://car0.autoimg.cn/upload/2013/6/8/u_201306081912099314136.jpg");
							picList.add(info);
						}
						CarImageList.setPicList(picList);
						hashmap.put(i, CarImageList);
					}
					rsp.setImageList(hashmap);

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

		GetCarImageColumnReq req = new GetCarImageColumnReq();
		req.setAttachInfo(attachInfo);
		req.setId(id);
		UniPacket uniPacket = createUniPacket("GetCarImageColumn", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetCarImageColumnRsp rsp = new GetCarImageColumnRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}

}
