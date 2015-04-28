package com.yy.android.gamenews.model;

import android.content.Context;

import com.duowan.gamenews.AppInitReq;
import com.duowan.gamenews.AppInitRsp;
import com.duowan.gamenews.DeviceType;
import com.duowan.gamenews.UserInitReq;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class InitModel extends CommonModel {

	/**
	 * 
	 * @param context
	 * @param responseListener
	 * @param req
	 * @param visible
	 *            行为是否用户可见（showDialog & showErrorMsg)
	 */
	public static void sendUserInitReq(Context context,
			final ResponseListener<UserInitRsp> responseListener,
			UserInitReq req, boolean visible) {
		if (req == null) {
			req = new UserInitReq();
		}
		req.setDeviceType(DeviceType._DEVICE_TYPE_ANDROID);
		req.setUuid(Util.getDeviceUUID(context));

		String channelName = context.getString(R.string.channelname);
		req.setAppPlat(channelName);

		UniPacket uniPacket = createUniPacket("UserInit", req);

		new CommonRequest<UserInitRsp>(responseListener.get(), uniPacket)
				.setup(responseListener, new UserInitRsp())
				.setShowErrorMsg(visible).setShowProgressDialog(visible)
				.execute();
	}

	public static void sendAppInitReq(Context context, AppInitReq req,
			final ResponseListener<AppInitRsp> listener) {
		UniPacket uniPacket = createUniPacket("AppInit", req);
		new CommonRequest<AppInitRsp>(listener.get(), uniPacket)
				.setup(listener, new AppInitRsp()).setShowErrorMsg(false)
				.setShowProgressDialog(false).execute();
	}
}
