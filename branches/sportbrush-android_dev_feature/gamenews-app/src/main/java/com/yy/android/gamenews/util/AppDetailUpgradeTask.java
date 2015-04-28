package com.yy.android.gamenews.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.service.UpgradeService;

public class AppDetailUpgradeTask extends AsyncTask<Void, Void, Boolean> {

	private FragmentActivity mActivity;
	private SharedPreferences msharedPre;

	public AppDetailUpgradeTask(FragmentActivity activity) {
		mActivity = activity;
		msharedPre = mActivity.getSharedPreferences(
				Constants.KEY_UPDATE_GLOBAL, Context.MODE_PRIVATE);
	}

	protected void onPreExecute() {
		if (msharedPre.getBoolean(Constants.KEY_COPY_DETAIL_WEB, false)) {
			Intent intent = new Intent(mActivity, UpgradeService.class);
			mActivity.startService(intent);
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (msharedPre.getBoolean(Constants.KEY_COPY_DETAIL_WEB, false)) {
			return true;
		}
		try {
			AssetManager assetManager = mActivity.getAssets();
			File fileDir;
			String[] files;
			String currIndexDir;
			fileDir = mActivity.getDir("0", Context.MODE_PRIVATE);
			if (fileDir.isFile()) {
				fileDir.delete();
			}
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			// copy css
			currIndexDir = Constants.NEWS_CSS;
			File fileDirCss = new File(fileDir, currIndexDir);
			if (!fileDirCss.exists()) {
				fileDirCss.mkdirs();
			}
			files = assetManager.list(currIndexDir);
			for (String file : files) {
				if (isCancelled()) {
					return false;
				}
				InputStream in = assetManager.open(currIndexDir + "/" + file);
				try {
					FileUtil.save(in, new File(fileDirCss, file));
				} finally {
					in.close();
				}
			}

			// copy js
			currIndexDir = Constants.NEWS_JS;
			File fileDirJs = new File(fileDir, currIndexDir);
			if (!fileDirJs.exists()) {
				fileDirJs.mkdirs();
			}
			files = assetManager.list(currIndexDir);
			for (String file : files) {
				if (isCancelled()) {
					return false;
				}
				InputStream in = assetManager.open(currIndexDir + "/" + file);
				try {
					FileUtil.save(in, new File(fileDirJs, file));
				} finally {
					in.close();
				}
			}

			// copy version
			currIndexDir = Constants.MANIFEST_FILE;
			File fileDirVersion = new File(fileDir, currIndexDir);
			if (!fileDirVersion.exists()) {
				fileDirVersion.createNewFile();
			}
			InputStream versionin = assetManager.open(currIndexDir);
			try {
				FileUtil.save(versionin, fileDirVersion);
			} finally {
				versionin.close();
			}

			// copy image
			currIndexDir = Constants.NEWS_IMAGE_LOADING;
			File fileDirImageLoading = new File(fileDir, currIndexDir);
			if (!fileDirImageLoading.exists()) {
				fileDirImageLoading.createNewFile();
			}
			InputStream imageLoadingIn = assetManager.open(currIndexDir);
			try {
				FileUtil.save(imageLoadingIn, fileDirImageLoading);
			} finally {
				imageLoadingIn.close();
			}
			currIndexDir = Constants.NEWS_IMAGE_FAIL;
			File fileDirImageFail = new File(fileDir, currIndexDir);
			if (!fileDirImageFail.exists()) {
				fileDirImageFail.createNewFile();
			}
			InputStream imageFailIn = assetManager.open(currIndexDir);
			try {
				FileUtil.save(imageFailIn, fileDirImageFail);
			} finally {
				imageFailIn.close();
			}

			// copy NEWS_HTML
			currIndexDir = Constants.NEWS_HTML;
			File fileDirCurrindex = new File(fileDir, currIndexDir);
			if (!fileDirCurrindex.exists()) {
				fileDirVersion.createNewFile();
			}
			InputStream sportsdetailin = assetManager.open(currIndexDir);
			try {
				FileUtil.save(sportsdetailin, fileDirCurrindex);
			} finally {
				sportsdetailin.close();
			}

			// copy SPORTS_HTML
			currIndexDir = Constants.SPORTS_HTML;
			fileDirCurrindex = new File(fileDir, currIndexDir);
			if (!fileDirCurrindex.exists()) {
				fileDirVersion.createNewFile();
			}
			 sportsdetailin = assetManager.open(currIndexDir);
			try {
				FileUtil.save(sportsdetailin, fileDirCurrindex);
			} finally {
				sportsdetailin.close();
			}

			return msharedPre.edit()
					.putBoolean(Constants.KEY_COPY_DETAIL_WEB, true).commit();
		} catch (IOException e) {
			return false;
		}
	};

	@Override
	protected void onPostExecute(Boolean result) {
		if (mOnAppDetailUpgradeTaskListener != null) {
			onTaskFinish();
		}
	}

	private void onTaskFinish() {
		mOnAppDetailUpgradeTaskListener.onTaskFinished();
	}

	private OnAppDetailUpgradeTaskListener mOnAppDetailUpgradeTaskListener;

	public void setAppDetailUpgradeTaskListener(
			OnAppDetailUpgradeTaskListener listener) {
		mOnAppDetailUpgradeTaskListener = listener;
	}

	public interface OnAppDetailUpgradeTaskListener {
		public void onTaskFinished();
	}

}
