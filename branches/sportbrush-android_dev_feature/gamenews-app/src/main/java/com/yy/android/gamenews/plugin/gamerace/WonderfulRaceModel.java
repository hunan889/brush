package com.yy.android.gamenews.plugin.gamerace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

import com.duowan.gamenews.GetWonderfulRaceReq;
import com.duowan.gamenews.GetWonderfulRaceRsp;
import com.duowan.gamenews.RaceTopicInfo;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class WonderfulRaceModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;

	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getWonderfulRaceList(
			final ResponseListener<GetWonderfulRaceRsp> responseListener,
			int count, Map<Integer, String> attachInfo, int refreshType) {

		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			responseListener.onError(null);
			return;
		}

		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					GetWonderfulRaceRsp rsp = new GetWonderfulRaceRsp();
					ArrayList<RaceTopicInfo> raceList = new ArrayList<RaceTopicInfo>();
					for (int i = 0; i < 10; i++) {
						RaceTopicInfo raceTopicInfo = new RaceTopicInfo();
						raceTopicInfo.setId(i);
						raceTopicInfo
								.setImg("http://wenwen.soso.com/p/20110925/20110925192501-1757377332.jpg");
						raceTopicInfo.setName("lol" + i);
						raceTopicInfo.setRaceTopicType(1);
						raceTopicInfo.setRaceTopicFlag(1);
						raceList.add(raceTopicInfo);
					}
					for (int i = 0; i < 10; i++) {
						RaceTopicInfo raceTopicInfo = new RaceTopicInfo();
						raceTopicInfo.setId(i);
						raceTopicInfo
								.setImg("http://img.hexun.com/2011-06-02/130200903.jpg");
						raceTopicInfo.setName("英雄联盟" + i);
						raceTopicInfo.setRaceTopicType(2);
						raceTopicInfo.setRaceTopicFlag(2);
						raceList.add(raceTopicInfo);
					}
					rsp.setHasMore(true);
					rsp.setRaceList(raceList);
					rsp.setAttachInfo(new HashMap());
					responseListener.onResponse(rsp);
				}
			}, 1000);
			return;
		}

		GetWonderfulRaceReq req = new GetWonderfulRaceReq();
		req.setCount(count); // 默认10个
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refreshType);

		UniPacket uniPacket = createUniPacket("GetWonderfulRace", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetWonderfulRaceRsp rsp = new GetWonderfulRaceRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}

}
