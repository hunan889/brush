package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.autonews.CarDetail;
import com.duowan.autonews.CarDetailItemDetail;
import com.duowan.autonews.GetCarDetailReq;
import com.duowan.autonews.GetCarDetailRsp;
import com.duowan.autonews.ItemDetail;
import com.duowan.autonews.SubItemDetail;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class CartModel extends CommonModel {
	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;
	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getCarDetailRsp(
			final ResponseListener<GetCarDetailRsp> responseListener,
			int carId, int count, String attachInfo) {

		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					GetCarDetailRsp rsp = new GetCarDetailRsp();

					CarDetail detail = new CarDetail();
					detail.setLevel("等级等级等级等级等级等级等级等级");
					detail.setName("这是一辆很牛的车");
					detail.setPrice("10 块钱");
					ArrayList<CarDetailItemDetail> list = new ArrayList<CarDetailItemDetail>();

					for (int i = 0; i < 10; i++) {
						CarDetailItemDetail itemDetail = new CarDetailItemDetail();
						itemDetail.setName("很牛的车" + (i + 1));
						itemDetail.setPrice("" + (100 + i));
						ArrayList<ItemDetail> itemDetailObjList = new ArrayList<ItemDetail>();
						for (int j = 0; j < 10; j++) {
							ItemDetail detailObj = new ItemDetail();
							detailObj.setName("Name" + j + ", for item " + i);
							ArrayList<SubItemDetail> subList = new ArrayList<SubItemDetail>();
							for (int k = 0; k < 10; k++) {
								SubItemDetail subdetail = new SubItemDetail();
								subdetail.setName("subname" + k + ", for item"
										+ j);
								subdetail.setValue("subvalue" + k
										+ ", for item" + j);
								subList.add(subdetail);
							}
							detailObj.setSubList(subList);
							itemDetailObjList.add(detailObj);
						}
						itemDetail.setDetail(itemDetailObjList);
						list.add(itemDetail);
					}
					detail.setItemList(list);
					rsp.setDetail(detail);

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

		GetCarDetailReq req = new GetCarDetailReq();

		req.setCarId(carId);
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("GetCarDetail", req);

		// String cacheKey = String.format("%s-%s", uniPacket.getServantName(),
		// uniPacket.getFuncName());
		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetCarDetailRsp rsp = new GetCarDetailRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

}
