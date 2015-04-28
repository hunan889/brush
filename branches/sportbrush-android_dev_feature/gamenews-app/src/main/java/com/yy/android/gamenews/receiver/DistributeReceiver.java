package com.yy.android.gamenews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yy.android.gamenews.service.DistributeSyncService;

public class DistributeReceiver extends BroadcastReceiver {

	public static final String ACTION_DOWNLOADED = "com.yy.android.gamenews.action.app_downloaded";
	public static final String ACTION_SYNC_STATUS = "com.yy.android.gamenews.action.app_sync_status";
	public static final String PARAM_ID = "app_id";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("DistributeReceiver", intent.getAction());
		intent.setClass(context, DistributeSyncService.class);
		context.startService(intent);
	}

}
