package com.yy.android.gamenews.model;

import java.util.Map;

import com.duowan.gamenews.GetStoreAppListReq;
import com.duowan.gamenews.GetStoreAppListRsp;
import com.duowan.gamenews.updateStoreAppStatusReq;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class StoreAppModel extends CommonModel {

	public static void updateAppStatus(Map<Integer, Integer> appStatusMap,
			ResponseListener<Void> listener) {
		updateStoreAppStatusReq req = new updateStoreAppStatusReq();
		req.setUpdateData(appStatusMap);

		UniPacket uniPacket = createUniPacket("updateStoreAppStatus", req);

		new CommonRequest<Void>(listener.get(), uniPacket)
				.setup(listener, null).setShowErrorMsg(true)
				.setShowProgressDialog(false).execute();
	}

	public static void getStoreAppList(
			final ResponseListener<GetStoreAppListRsp> listener,
			String attachInfo, int storeServiceType) {

		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetStoreAppListReq req = new GetStoreAppListReq();
		req.setStoreServiceType(storeServiceType);
		req.setAttachInfo(attachInfo);

		UniPacket uniPacket = createUniPacket("GetStoreAppList", req);

		new CommonRequest<GetStoreAppListRsp>(listener.get(), uniPacket)
				.setup(listener, new GetStoreAppListRsp())
				.setShowErrorMsg(true).setShowProgressDialog(false).execute();
	}
}
