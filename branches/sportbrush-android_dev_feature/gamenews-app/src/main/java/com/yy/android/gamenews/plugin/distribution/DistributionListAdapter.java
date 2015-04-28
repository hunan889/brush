package com.yy.android.gamenews.plugin.distribution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.StoreAppInfo;
import com.duowan.gamenews.StoreAppStatus;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.DownloadUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class DistributionListAdapter extends ImageAdapter<StoreAppInfo> {

	private DownloadListener downloadListener;
	private Map<String, Boolean> installedMap = new HashMap<String, Boolean>();
	private boolean isYYlogin = true;

	public DistributionListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.store_app_list_item, null);
			viewHolder.appIconImageView = (ImageView) convertView
					.findViewById(R.id.iv_app_icon);
			viewHolder.appNameTextView = (TextView) convertView
					.findViewById(R.id.tv_app_name);
			viewHolder.appDescTextView = (TextView) convertView
					.findViewById(R.id.tv_app_desc);
			viewHolder.tipTextView = (TextView) convertView
					.findViewById(R.id.tv_download_tip);
			viewHolder.downloadButton = (Button) convertView
					.findViewById(R.id.btn_download);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		StoreAppInfo storeAppInfo = getItem(position);

		displayImage(storeAppInfo.getIcon(), viewHolder.appIconImageView);
		viewHolder.appNameTextView.setText(storeAppInfo.getName());
		viewHolder.appDescTextView.setText(storeAppInfo.getDesc());
		viewHolder.tipTextView.setText(storeAppInfo.getRewardBasis());

		updateStatus(storeAppInfo, viewHolder.downloadButton);

		return convertView;
	}

	private void updateStatus(StoreAppInfo storeAppInfo, Button button) {
		String text;

		int status = storeAppInfo.getStatus();

		if (status == StoreAppStatus._STORE_APP_PENDING) {
			text = getResources().getString(R.string.pending);
			updateDownloadButtonStatus(button, text, false);
		} else if (status == StoreAppStatus._STORE_APP_DOWNLOADING) {
			text = getResources().getString(R.string.downloading);
			updateDownloadButtonStatus(button, text, false);
		} else if (status == StoreAppStatus._STORE_APP_HAS_DOWNLOAD) {
			boolean installed = checkAppInstalled(storeAppInfo.getPackageName());
			if (installed) {
				text = getResources().getString(R.string.has_reward);
				updateDownloadButtonStatus(button, text, false);
			} else {
				String url = storeAppInfo.getDownloadUrl();
				if (url != null) {
					String filename = getFileNameByUrl(url);
					String baseDir = DownloadUtil.getBaseDir();
					File f = new File(baseDir + "/" + filename);
					if (f.exists()) {
						text = getResources().getString(R.string.install);
						updateDownloadButtonStatus(button, text, true);
						setClickListener(button, storeAppInfo.getId(), null,
								storeAppInfo.getDownloadUrl(), true);
					} else {
						text = storeAppInfo.getAward();
						updateDownloadButtonStatus(button, text, true);
						setClickListener(button, storeAppInfo.getId(),
								storeAppInfo.getName(),
								storeAppInfo.getDownloadUrl(), false);
					}
				}
			}
		} else if (status == StoreAppStatus._STORE_APP_HAS_REWARD) {
			text = getResources().getString(R.string.has_reward);
			updateDownloadButtonStatus(button, text, false);
		} else {
			boolean installed = checkAppInstalled(storeAppInfo.getPackageName());
			if (installed) {
				text = getResources().getString(R.string.has_exist);
				updateDownloadButtonStatus(button, text, false);
			} else {
				text = storeAppInfo.getAward();
				updateDownloadButtonStatus(button, text, true);
				setClickListener(button, storeAppInfo.getId(),
						storeAppInfo.getName(), storeAppInfo.getDownloadUrl(),
						false);
			}
		}
		if (!isYYlogin) {
			button.setEnabled(false);
			button.setClickable(false);
			button.setOnClickListener(null);
		}
	}

	private void updateDownloadButtonStatus(Button button, String text,
			boolean enable) {
		button.setText(text);
		button.setClickable(enable);
		button.setEnabled(enable);
		if (!enable) {
			button.setOnClickListener(null);
		}
	}

	private void setClickListener(final Button button, final int key,
			final String name, final String url, final boolean install) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filename = getFileNameByUrl(url);
				if (install) {
					String baseDir = DownloadUtil.getBaseDir();
					File file = new File(baseDir + "/" + filename);
					if (file.exists()) {
						installApp(file);
					}
				} else {
					if (downloadListener != null) {
						if (Environment.MEDIA_MOUNTED.equals(Environment
								.getExternalStorageState())) {
							downloadListener.startDownload(key, name, url,
									filename);
							button.setClickable(false);
							button.setOnClickListener(null);
						} else {
							ToastUtil.showToast(R.string.sdcard_unmounted);
						}
					}
				}
			}
		});
	}

	private String getFileNameByUrl(String url) {
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(UUID.nameUUIDFromBytes(url.getBytes()));
		return fileNameBuilder.toString();
	}

	private boolean checkAppInstalled(String packageName) {
		if (packageName == null) {
			return false;
		}
		boolean installed = false;
		if (installedMap.containsKey(packageName)) {
			installed = installedMap.get(packageName);
		} else {
			PackageManager packageManager = GameNewsApplication.getInstance()
					.getApplicationContext().getPackageManager();
			PackageInfo packageInfo;
			try {
				packageInfo = packageManager.getPackageInfo(packageName,
						PackageManager.GET_ACTIVITIES);
			} catch (PackageManager.NameNotFoundException e) {
				packageInfo = null;
			}
			installed = packageInfo != null;
			installedMap.put(packageName, installed);
		}
		return installed;
	}

	private void installApp(File file) {
		if (!file.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(new File(file.getAbsolutePath())),
				"application/vnd.android.package-archive");
		getContext().startActivity(i);
	}

	public void setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}

	public void cleanUpInstalledMap() {
		installedMap.clear();
	}

	public void setYYLogin(boolean isYYlogin) {
		this.isYYlogin = isYYlogin;
	}

	class ViewHolder {
		ImageView appIconImageView;
		TextView appNameTextView;
		TextView appDescTextView;
		TextView tipTextView;
		Button downloadButton;
	}

	public interface DownloadListener {
		void startDownload(int key, String title, String url, String fileName);
	}
}
