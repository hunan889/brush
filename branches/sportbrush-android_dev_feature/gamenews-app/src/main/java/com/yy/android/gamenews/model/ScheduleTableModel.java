package com.yy.android.gamenews.model;

import com.duowan.gamenews.GetTeamListReq;
import com.duowan.gamenews.GetTeamListRsp;
import com.duowan.gamenews.SportRaceListReq;
import com.duowan.gamenews.SportRaceListRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

/**
 * 
 * @author Administrator
 * 
 */
public class ScheduleTableModel extends CommonModel {

	public static void getLiveRsp(
			final ResponseListener<SportRaceListRsp> listener, String attachInfo) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		SportRaceListReq req = new SportRaceListReq();
		req.setAttachInfo(attachInfo);

		UniPacket uniPacket = createUniPacket("SportRaceList", req);

		// String cacheKey = String.format("%s-%s", uniPacket.getServantName(),
		// uniPacket.getFuncName());
		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				SportRaceListRsp rsp = new SportRaceListRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	public static void getTeamRsp(
			final ResponseListener<GetTeamListRsp> listener, String attachInfo) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetTeamListReq req = new GetTeamListReq();
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetTeamList", req);

		// String cacheKey = String.format("%s-%s", uniPacket.getServantName(),
		// uniPacket.getFuncName());
		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetTeamListRsp rsp = new GetTeamListRsp();
				rsp = response.getByClass("result", rsp);
				// if(rsp == null){
				// rsp = getTema();
				// }
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}
}
