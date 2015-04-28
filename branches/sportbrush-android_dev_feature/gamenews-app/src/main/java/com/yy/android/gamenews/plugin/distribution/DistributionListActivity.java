package com.yy.android.gamenews.plugin.distribution;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.gamenews.StoreAppInfo;
import com.yy.android.gamenews.plugin.distribution.DistributionListAdapter.DownloadListener;
import com.yy.android.gamenews.receiver.DistributeReceiver;
import com.yy.android.gamenews.service.FileDownloadService;
import com.yy.android.gamenews.service.FileDownloadService.MyServiceConnection;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class DistributionListActivity extends BaseActivity {

	public static final String FROM_MYHOME = "home";
	public static final String FROM_H5WEB = "h5";

	private static final String TAG = DistributionListActivity.class
			.getSimpleName();
	private ActionBar mActionBar;
	private static DistributionListFragment distributionListFragment;

	public static void startDistributionListActivity(Context context,
			String param) {
		Intent intent = new Intent(context, DistributionListActivity.class);
		context.startActivity(intent);

		StatsUtil.statsReportAllData(context, "into_distribution_list", "from",
				param);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.distribution_list_activity);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mActionBar.setTitle(getResources().getString(R.string.get_tdou));
		if (bundle != null) {
			distributionListFragment = (DistributionListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG);
		} else {
			distributionListFragment = new DistributionListFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.fragment_container, distributionListFragment, TAG)
					.commit();
		}

		Intent intent = new Intent(DistributeReceiver.ACTION_SYNC_STATUS,
				Uri.parse("package://"));
		sendBroadcast(intent);
	}

	public void startDownload(int key, String title, String url, String fileName) {
		FileDownloadService.startDownload(mServiceConnection.getRemote(),
				key, title, url, fileName);
	}

	private class MyConnection extends MyServiceConnection {

		public MyConnection(Handler handler) {
			super(handler);
		}
	}

	private MyConnection mServiceConnection;
	private Handler mClientHandler = new ClientHandler(this);

	private static class ClientHandler extends Handler {
		WeakReference<DistributionListActivity> client;

		public ClientHandler(DistributionListActivity client) {
			this.client = new WeakReference<DistributionListActivity>(client);
		}

		public void handleMessage(Message msg) {
			client.get().handleServiceMsg(msg);
		};
	};

	@SuppressWarnings("unchecked")
	private void handleServiceMsg(Message msg) {
		int key = msg.getData().getInt(FileDownloadService.PARAM_KEY);
		switch (msg.what) {
		case FileDownloadService.MSG_REGIST_REPLY: {
			Log.d(TAG, "MSG_REGIST_REPLY");
			List<StoreAppInfo> storeAppInfos = (List<StoreAppInfo>) msg
					.getData().getSerializable(FileDownloadService.PARAM_QUEUE);
			distributionListFragment.setDownloadQueue(storeAppInfos);
			break;
		}
		case FileDownloadService.MSG_DOWNLOAD_ON_START: {
			Log.d(TAG, "MSG_DOWNLOAD_ON_START, key = " + key);
			distributionListFragment.startDownload(key);
			break;
		}
		case FileDownloadService.MSG_DOWNLOAD_ON_PREPARE: {
			boolean isPending = msg.getData().getBoolean(
					FileDownloadService.PARAM_IS_PENDING);
			Log.d(TAG, "MSG_DOWNLOAD_ON_PREPARE,isPending = " + isPending
					+ ", key = " + key);
			distributionListFragment.prepareDownload(key, isPending);
			break;
		}
		case FileDownloadService.MSG_DOWNLOAD_ON_FINISH: {
			boolean isSuccess = msg.getData().getBoolean(
					FileDownloadService.PARAM_IS_SUCCESS);
			Log.d(TAG, "MSG_DOWNLOAD_ON_FINISH,isSuccess = " + isSuccess
					+ ", key = " + key);
			distributionListFragment.finishDownload(key, isSuccess);
			break;
		}
		case FileDownloadService.MSG_DOWNLOAD_UPDATE_PROGRESS: {

			int progress = msg.getData().getInt(
					FileDownloadService.PARAM_PROGRESS);
			Log.d(TAG, "MSG_DOWNLOAD_UPDATE_PROGRESS progress = " + progress
					+ ", key = " + key);
			break;
		}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mServiceConnection = new MyConnection(mClientHandler);
		FileDownloadService.bindService(this, mServiceConnection);
		FileDownloadService.startService(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "unBindService");
		FileDownloadService.unBindService(this, mServiceConnection);
		FileDownloadService.unregistReply(mServiceConnection.getLocal(),
				mServiceConnection.getRemote());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
