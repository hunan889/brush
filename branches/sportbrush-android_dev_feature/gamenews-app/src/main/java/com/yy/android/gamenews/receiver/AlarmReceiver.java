package com.yy.android.gamenews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yy.android.gamenews.util.AlarmUtil;

public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.d(TAG, "[onReceive] app boot completed!");
			AlarmUtil.ensureAlarms(context);
		}
	}
}
