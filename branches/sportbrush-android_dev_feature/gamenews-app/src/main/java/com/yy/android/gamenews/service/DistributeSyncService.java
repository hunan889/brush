package com.yy.android.gamenews.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetStoreAppListRsp;
import com.duowan.gamenews.StoreAppInfo;
import com.duowan.gamenews.StoreAppStatus;
import com.yy.android.gamenews.event.DistributionAppEvent;
import com.yy.android.gamenews.model.StoreAppModel;
import com.yy.android.gamenews.receiver.DistributeReceiver;
import com.yy.android.gamenews.util.Preference;

import de.greenrobot.event.EventBus;

public class DistributeSyncService extends IntentService {

	public DistributeSyncService() {
		super("DistributeSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("DistributeSyncService", intent.getAction());
		if (mPref == null) {
			mPref = Preference.getInstance();
		}
		String action = intent.getAction();
		StoreAppInfo info = null;
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)
				|| Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			info = getInstallAppInfo(intent);
			if (info != null) {
				info = (StoreAppInfo) info.clone();
				info.setStatus(StoreAppStatus._STORE_APP_HAS_EXIST);

				ArrayList<StoreAppInfo> installAppList = mPref
						.getInstalledAppList();
				if (installAppList == null) {
					installAppList = new ArrayList<StoreAppInfo>();
				}
				installAppList.add(info);
				mPref.saveInstalledAppList(installAppList);

				// 修改本地缓存状态
				updateStoreAppStatus(info.getId(),
						StoreAppStatus._STORE_APP_HAS_REWARD);

				Map<Integer, Integer> installAppMap = new HashMap<Integer, Integer>();
				installAppMap.put(info.getId(),
						StoreAppStatus._STORE_APP_HAS_REWARD);
				refreshUI(installAppMap, false);
			}

		} else if (DistributeReceiver.ACTION_DOWNLOADED.equals(action)) {
			int appId = intent.getIntExtra(DistributeReceiver.PARAM_ID, 0);
			if (appId != 0) {
				int status = StoreAppStatus._STORE_APP_HAS_DOWNLOAD;
				info = new StoreAppInfo();
				info.setId(appId);
				info.setStatus(status);

				ArrayList<StoreAppInfo> downloadAppList = mPref
						.getDownloadedAppList();
				if (downloadAppList == null) {
					downloadAppList = new ArrayList<StoreAppInfo>();
				}
				downloadAppList.add(info);
				mPref.saveDownloadededAppList(downloadAppList);

				// 修改本地缓存状态
				updateStoreAppStatus(appId, status);
			}
		} else if (DistributeReceiver.ACTION_SYNC_STATUS.equals(action)) {
			// only syncStatus
		}
		syncStatus();
	}

	/**
	 * 下载完成或者完成安装之后，为避免网络问题发送失败的情况，要修改本地缓存数据的状态
	 * 
	 * @param appId
	 * @param status
	 */
	private void updateStoreAppStatus(int appId, int status) {
		Log.d("DistributeSyncService",
				"update status to:" + String.valueOf(status));
		GetStoreAppListRsp mRsp = mPref.getStoreAppListRsp();
		if (mRsp != null) {
			ArrayList<StoreAppInfo> downloadAppList = mRsp.getAppList();
			if (downloadAppList != null) {
				for (StoreAppInfo storeAppInfo : downloadAppList) {
					if (storeAppInfo.getId() == appId) {
						storeAppInfo.setStatus(status);
						mPref.saveStoreAppListRsp(mRsp);
						break;
					}
				}
			}
		}
	}

	private Preference mPref;

	private StoreAppInfo getInstallAppInfo(Intent intent) {
		String installedPkgName = intent.getDataString();
		if (installedPkgName != null && installedPkgName.length() > 0) {
			installedPkgName = installedPkgName.substring(8);
		}

		GetStoreAppListRsp rsp = mPref.getStoreAppListRsp();

		if (rsp == null) {
			return null;
		}
		ArrayList<StoreAppInfo> list = rsp.getAppList();
		if (list == null) {
			return null;
		}
		for (StoreAppInfo info : list) {
			// 只有当前状态是已下载时才更新
			if (info.getStatus() == StoreAppStatus._STORE_APP_HAS_DOWNLOAD) {
				String pkgName = info.getPackageName();
				if (pkgName != null && pkgName.equals(installedPkgName)) {
					info.setStatus(StoreAppStatus._STORE_APP_HAS_EXIST);
					return info;
				}
			}
		}

		return null;
	}

	/**
	 * 先发送downloaded app list再发送installed app list
	 */
	@SuppressLint("UseSparseArrays")
	private void syncStatus() {
		final ArrayList<StoreAppInfo> downloadAppList = mPref
				.getDownloadedAppList();
		if (downloadAppList == null || downloadAppList.size() <= 0) {
			sendInstallAppStatus();
			return;
		}
		Map<Integer, Integer> downloadAppMap = new HashMap<Integer, Integer>();

		for (StoreAppInfo info : downloadAppList) {
			downloadAppMap.put(info.getId(), info.getStatus());
		}

		StoreAppModel.updateAppStatus(downloadAppMap,
				new ResponseListener<Void>(null) {

					@Override
					public void onResponse(Void response) {
						Log.d("DistributeSyncService",
								"send downloadAppList success");
						downloadAppList.clear();
						mPref.saveDownloadededAppList(downloadAppList);
						sendInstallAppStatus();
					}
				});
	}

	@SuppressLint("UseSparseArrays")
	private void sendInstallAppStatus() {
		final ArrayList<StoreAppInfo> installedAppList = mPref
				.getInstalledAppList();
		if (installedAppList == null || installedAppList.size() <= 0) {
			return;
		}
		final Map<Integer, Integer> installAppMap = new HashMap<Integer, Integer>();

		for (StoreAppInfo info : installedAppList) {
			installAppMap.put(info.getId(), info.getStatus());
		}

		StoreAppModel.updateAppStatus(installAppMap,
				new ResponseListener<Void>(null) {

					@Override
					public void onResponse(Void response) {
						Log.d("DistributeSyncService",
								"send installedAppList success");
						refreshUI(installAppMap, true);
						installedAppList.clear();
						mPref.saveInstalledAppList(installedAppList);
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						refreshUI(installAppMap, false);
					}
				});
	}

	private void refreshUI(Map<Integer, Integer> installAppMap,
			boolean needRefresh) {
		DistributionAppEvent distributionAppEvent = new DistributionAppEvent();
		distributionAppEvent.setInstallAppMap(installAppMap);
		distributionAppEvent.setNeedRefresh(needRefresh);
		EventBus.getDefault().post(distributionAppEvent);
	}
}
