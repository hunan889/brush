package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.autonews.CarListInfo;
import com.duowan.autonews.GetHotCarListReq;
import com.duowan.autonews.GetHotCarListRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class HotCartModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;

	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getHotcartList(
			final ResponseListener<GetHotCarListRsp> responseListener,
			int count, String attachInfo) {
		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					GetHotCarListRsp rsp = new GetHotCarListRsp();

					rsp.setHasMore(100);
					rsp.setAttachInfo("GetHotCarList");
					ArrayList<CarListInfo> hotCarList = new ArrayList<CarListInfo>();
					for (int i = 0; i < 100; i++) {
						CarListInfo carListInfo = new CarListInfo();
						carListInfo.setId(i);
						carListInfo.setName("大众" + i);
						carListInfo
								.setIcon("http://car0.autoimg.cn/upload/2014/8/21/u_20140821111502574444411.jpg");
						hotCarList.add(carListInfo);
					}
					rsp.setHotCarList(hotCarList);
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

		GetHotCarListReq req = new GetHotCarListReq();
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("GetHotCarList", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetHotCarListRsp rsp = new GetHotCarListRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}

}
