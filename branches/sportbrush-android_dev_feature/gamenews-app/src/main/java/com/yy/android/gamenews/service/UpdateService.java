package com.yy.android.gamenews.service;

import android.app.IntentService;
import android.content.Intent;

import com.yy.android.gamenews.util.DownloadUtil;
import com.yy.android.gamenews.util.DownloadUtil.DownloadCallback;
import com.yy.android.sportbrush.R;

public class UpdateService extends IntentService {

	private String mAppName;
	private static final int NOTIFICATION_ID = 0;

	public UpdateService() {
		super("UpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final long startTime = System.currentTimeMillis();
		mAppName = getString(R.string.app_name);
		String downloadUrl = intent.getStringExtra("downloadUrl");
		final String storeFileName = "gamenews" + ".apk";
		if (DownloadUtil.isDowning(storeFileName)) {
			return;
		}
		DownloadCallback callback = new DownloadCallback() {
			public void onDownload(int downloadBytes, int totalBytes) {
				if (downloadBytes < totalBytes) {
					DownloadUtil.showProgressNotify(UpdateService.this,
							NOTIFICATION_ID, startTime, downloadBytes,
							totalBytes, mAppName);
				} else {
					DownloadUtil.showDoneNotify(UpdateService.this,
							NOTIFICATION_ID, storeFileName, storeFileName, true);
					DownloadUtil.checkDownloaded(UpdateService.this,
							storeFileName);
				}
			}

			public void onFail() {
				DownloadUtil.showDoneNotify(UpdateService.this,
						NOTIFICATION_ID, storeFileName, storeFileName, false);
			}
		};
		DownloadUtil.showProgressNotify(this, NOTIFICATION_ID, startTime, -1, 0,
				mAppName);
		DownloadUtil.download(downloadUrl, storeFileName, callback);
	}
}
