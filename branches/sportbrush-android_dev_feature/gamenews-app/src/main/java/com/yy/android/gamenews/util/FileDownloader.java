package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.duowan.gamenews.StoreAppInfo;
import com.duowan.gamenews.StoreAppStatus;
import com.yy.android.gamenews.util.DownloadUtil.DownloadCallback;
import com.yy.android.gamenews.util.thread.LimitedThreadBackgroundTask;

public class FileDownloader {

	private List<TaskRunner> taskRunners = new ArrayList<FileDownloader.TaskRunner>();

	private static final String TAG = "FileDownloader";

	public FileDownloader() {
	}

	public void download(Object key, String title, String url,
			OnFileDownloadListener listener) {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		download(key, title, url, filename, listener);
	}

	public void download(Object key, String title, String url,
			String localFileName, OnFileDownloadListener listener) {
		download(key, title, url, localFileName, true, listener);
	}

	public void download(Object key, String title, String url,
			String localFileName, boolean autoOpen,
			OnFileDownloadListener listener) {
		DownloadTask task = new DownloadTask();
		task.key = key;
		task.title = title;
		task.url = url;
		task.localFileName = localFileName;
		task.autoOpen = autoOpen;
		download(task, listener);
	}

	public void download(DownloadTask task, OnFileDownloadListener listener) {
		TaskRunner taskRunner = new TaskRunner(task, listener);
		taskRunners.add(taskRunner);
		taskRunner.execute();
	}

	public boolean isBusy() {
		return !taskRunners.isEmpty();
	}

	@SuppressWarnings("unused")
	private void downloadSync(final DownloadTask task,
			final OnFileDownloadListener listener) {

		DownloadCallback callback = new DownloadCallback() {
			public void onDownload(int downloadBytes, int totalBytes) {

				int progress = 0;
				listener.onProgressUpdate(task, progress);

				if (downloadBytes >= totalBytes) {
					listener.onFinished(task, true);
				}
			}

			public void onFail() {
				listener.onFinished(task, false);
			}
		};
		listener.onPrepare(task, false);
		listener.onStart(task);
		listener.onProgressUpdate(task, 0);
		DownloadUtil.download(task.url, task.localFileName, callback);
	}

	/**
	 * 通过key获取下载任务
	 * 
	 * @param key
	 * @return
	 */
	public TaskRunner getTaskRunner(int key) {
		for (TaskRunner taskRunner : taskRunners) {
			if ((Integer) taskRunner.getDownloadTask().key == key) {
				return taskRunner;
			}
		}
		return null;
	}

	/**
	 * 获取下载队列
	 * 
	 * @return List<StoreAppInfo>
	 */
	public List<StoreAppInfo> getStoreAppQueue() {
		List<StoreAppInfo> storeAppInfos = null;
		for (TaskRunner taskRunner : taskRunners) {
			if (storeAppInfos == null) {
				storeAppInfos = new ArrayList<StoreAppInfo>();
			}
			StoreAppInfo storeAppInfo = new StoreAppInfo();
			storeAppInfo.setId((Integer) taskRunner.getDownloadTask().key);
			storeAppInfo
					.setStatus(taskRunner.isPendind() ? StoreAppStatus._STORE_APP_PENDING
							: StoreAppStatus._STORE_APP_DOWNLOADING);
			storeAppInfos.add(storeAppInfo);
		}
		return storeAppInfos;
	}

	public int getThreadPoolSize() {
		return TaskRunner.getThreadPoolSize();
	}

	public class DownloadTask {
		public Object key;
		public String url;
		public String title;
		public String localFileName;
		public boolean autoOpen;
	}

	private class TaskRunner extends
			LimitedThreadBackgroundTask<Void, Integer, Boolean> {

		private DownloadTask mDownloadTask;
		private OnFileDownloadListener mListener;
		private boolean pending = true;

		public TaskRunner(DownloadTask task, OnFileDownloadListener listener) {
			mListener = listener;
			mDownloadTask = task;
		}

		@Override
		protected void onPreExecute(boolean pending) {
			this.pending = pending;
			if (mListener != null) {
				mListener.onPrepare(mDownloadTask, pending);
			}
			super.onPreExecute(pending);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			/**
			 * 加同步锁，防止多线程执行导致下载状态不对（onStart 是后台线程， getStoreAppQueue是UI线程）
			 */
			synchronized (FileDownloader.this) {
				pending = false;
			}
			if (mListener != null) {
				mListener.onStart(mDownloadTask);
			}
			return DownloadUtil.download(mDownloadTask.url,
					mDownloadTask.localFileName, mCallBack);
		}

		@Override
		protected void onProgressUpdate(Integer progress) {
			if (mListener != null) {
				mListener.onProgressUpdate(mDownloadTask, progress);
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {

			Log.d(TAG, "onPostExecute, this = " + this
					+ ", mDownloadTask.getKey = " + mDownloadTask.key);
			taskRunners.remove(this);
			if (mListener != null) {
				mListener.onFinished(mDownloadTask, success);
			}
		}

		private DownloadTask getDownloadTask() {
			return mDownloadTask;
		}

		private boolean isPendind() {
			return pending;
		}

		private DownloadCallback mCallBack = new DownloadCallback() {
			public void onDownload(int downloadBytes, int totalBytes) {
				float progress = ((float) downloadBytes) / ((float) totalBytes)
						* 100;
				publishProgress((int) progress);
			};

			public void onFail() {
				// do nothing;
			};
		};
	}

	public interface OnFileDownloadListener {
		/**
		 * 任务已加入线程池，等待执行，所在线程：UI线程
		 * 
		 * @param task
		 * @param pending
		 *            ，是否需要等待其它任务执行
		 */
		public void onPrepare(DownloadTask task, boolean pending);

		/**
		 * 任务开始执行，所在线程：后台线程
		 * 
		 * @param task
		 * @param pending
		 */
		public void onStart(DownloadTask task);

		/**
		 * 任务执行完毕,所在线程：UI线程
		 * 
		 * @param task
		 * @param success
		 *            下载是否成功
		 */
		public void onFinished(DownloadTask task, boolean success);

		/**
		 * 更新进度，所在线程：UI线程
		 * 
		 * @param task
		 * @param progress
		 */
		public void onProgressUpdate(DownloadTask task, Integer progress);
	}
}
