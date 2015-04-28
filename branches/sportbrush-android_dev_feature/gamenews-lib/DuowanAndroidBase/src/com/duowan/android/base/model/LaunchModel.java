package com.duowan.android.base.model;

import android.content.Context;

import com.duowan.Comm.LaunchReq;
import com.duowan.Comm.LaunchRsp;
import com.duowan.Comm.UpgradeRsp;
import com.duowan.android.base.event.GuidEvent;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.net.VolleyClient;
import com.duowan.jce.wup.UniPacket;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-20 下午2:44:18
 */
public class LaunchModel extends BaseModel {

	public static void doLaunch(Context context,
			final ResponseListener<UpgradeRsp> responseListener, int commAppType) {
		UniPacket req = createUniPacket("launch", "doLaunch");

		LaunchReq launchReq = new LaunchReq(createCommUserbase(context,
				commAppType));
		req.put("tReq", launchReq);

		VolleyClient.newRequestQueue(BaseModel.HOST, req,
				new VolleyClient.Listener() {

					@Override
					public void onResponse(UniPacket response) {
						LaunchRsp tRsp = new LaunchRsp();
						tRsp = response.getByClass("tRsp", tRsp);
						if (tRsp == null) {
							responseListener.onResponse(null);							
							return;
						}
						byte[] vGuid = tRsp.vGuid;
						if (vGuid != null)
							postEvent(new GuidEvent(vGuid));

						UpgradeRsp tUpgradeRsp = tRsp.tUpgradeRsp;
						if (tUpgradeRsp != null)
							postEvent(tUpgradeRsp);
						
						responseListener.onResponse(tUpgradeRsp);
					}

					@Override
					public void onError(Exception e) {
						responseListener.onError(e);
					}

				});
	}
}
