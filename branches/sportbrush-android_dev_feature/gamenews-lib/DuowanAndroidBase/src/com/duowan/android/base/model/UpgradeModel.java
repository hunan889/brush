package com.duowan.android.base.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.duowan.Comm.UpgradeReq;
import com.duowan.Comm.UpgradeRsp;
import com.duowan.jce.wup.UniPacket;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-20 下午3:44:45
 */
public class UpgradeModel extends BaseModel {

	public static void doUpgrade(final ResponseListener<UpgradeRsp> responseListener, int commAppType) {
		UniPacket req = createUniPacket("launch", "doUpgrade");

		FragmentActivity activity = responseListener.get();

		if (activity == null || activity.isFinishing()) {
			return;
		}

		SharedPreferences preferences = activity.getSharedPreferences("upgrade", Context.MODE_PRIVATE);
		preferences.edit().putBoolean("hasShow", false).putLong("showTime", 0).commit();

		UpgradeReq upgradeReq = new UpgradeReq(createCommUserbase(activity, commAppType), 1);
		req.put("tReq", upgradeReq);

		new Request(responseListener.get(), req) {

			@Override
			public void onResponse(UniPacket response) {
				UpgradeRsp rsp = new UpgradeRsp();
				rsp = response.getByClass("tRsp", rsp);

				responseListener.onResponse(rsp);
			}
			
			public void onError(Exception e) {

				responseListener.onError(e);
				
			};
		}.setShowProgressDialog(true).execute();

	}

	public static void showUpgradeDialog(final Activity activity, final UpgradeRsp upgradeRsp) {
		if (activity.isFinishing())
			return;

		if (upgradeRsp == null)
			return;

		if (upgradeRsp.iStatus == 0)
			return;

		SharedPreferences preferences = activity.getSharedPreferences("upgrade", Context.MODE_PRIVATE);
		boolean hasShow = preferences.getBoolean("hasShow", false);
		long now = System.currentTimeMillis();
		long showTime = preferences.getLong("showTime", 0);
		if (!hasShow || showTime < (now - (60 * 60 * 24 * 1000))) {
			preferences.edit().putBoolean("hasShow", true).putLong("showTime", now).commit();
			new AlertDialog.Builder(activity).setTitle(upgradeRsp.sTitle).setMessage(upgradeRsp.sText).setPositiveButton("下载新版本", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (upgradeRsp.sURL != null && upgradeRsp.sURL.startsWith("http")) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(upgradeRsp.sURL));
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activity.startActivity(i);
					}
					dialog.dismiss();
				}
			}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (upgradeRsp.iStatus == 2) {
						activity.finish();
					}
					dialog.dismiss();
				}
			}).show();
		}

	}
}
