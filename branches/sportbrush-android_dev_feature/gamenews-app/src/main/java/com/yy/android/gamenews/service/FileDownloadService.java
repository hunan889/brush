package com.yy.android.gamenews.service;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.duowan.gamenews.StoreAppInfo;
import com.yy.android.gamenews.receiver.DistributeReceiver;
import com.yy.android.gamenews.util.DownloadUtil;
import com.yy.android.gamenews.util.FileDownloader;
import com.yy.android.gamenews.util.FileDownloader.DownloadTask;
import com.yy.android.gamenews.util.FileDownloader.OnFileDownloadListener;

public class FileDownloadService extends Service {

	private FileDownloader mDownloader;

	public static final String PARAM_FILE_NAME = "file_name";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_TITLE = "title";
	public static final String PARAM_URL = "url";
	public static final String PARAM_PROGRESS = "progress";
	public static final String PARAM_IS_PENDING = "is_pending";
	public static final String PARAM_IS_SUCCESS = "is_success";
	public static final String PARAM_IS_AUTO_OPEN = "is_auto_open";
	public static final String PARAM_QUEUE = "queue";
	public static final int MSG_DOWNLOAD = 10001;
	public static final int MSG_DOWNLOAD_ON_PREPARE = 10002;
	public static final int MSG_DOWNLOAD_ON_START = 10003;
	public static final int MSG_DOWNLOAD_UPDATE_PROGRESS = 10004;
	public static final int MSG_DOWNLOAD_ON_FINISH = 10005;
	public static final int MSG_REGIST_REPLY = 9999;
	public static final int MSG_UNREGIST_REPLY = 8888;

	private Messenger mMsnger;
	private List<Messenger> mReplyList = new ArrayList<Messenger>();
	private Handler mServiceHandler = new ServiceHandler(this);

	static class ServiceHandler extends Handler {
		private WeakReference<FileDownloadService> service;

		public ServiceHandler(FileDownloadService service) {
			this.service = new WeakReference<FileDownloadService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			service.get().handleClientMsg(msg);
		}
	}

	private void handleClientMsg(Message msg) {
		switch (msg.what) {
		case MSG_DOWNLOAD: {
			Bundle bundle = msg.getData();
			String fileName = bundle.getString(PARAM_FILE_NAME);
			int key = bundle.getInt(PARAM_KEY, 0);
			String url = bundle.getString(PARAM_URL);
			String title = bundle.getString(PARAM_TITLE);
			mDownloader.download(key, title, url, fileName, mListener);
			break;
		}
		case MSG_REGIST_REPLY: {
			synchronized (this) {
				mReplyList.add(msg.replyTo);
			}
			// 只发给当前要绑定的client
			sendDonloadQueue(msg.replyTo);
			break;
		}
		case MSG_UNREGIST_REPLY: {
			synchronized (this) {
				mReplyList.remove(msg.replyTo);
			}
			break;
		}
		}
	}

	public static void registReply(Messenger reply, Messenger remote) {
		if (remote != null && reply != null) {
			Message msg = new Message();
			msg.what = MSG_REGIST_REPLY;
			msg.replyTo = reply;

			try {
				remote.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void unregistReply(Messenger reply, Messenger remote) {
		if (remote != null && reply != null) {
			Message msg = new Message();
			msg.what = MSG_UNREGIST_REPLY;
			msg.replyTo = reply;

			try {
				remote.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		mMsnger = new Messenger(mServiceHandler);
		return mMsnger.getBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DownloadUtil.cancelAllNotifies(this);
		stopForeground(true);
		flags |= START_NOT_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		mDownloader = new FileDownloader();
		super.onCreate();
	}

	private synchronized void reply(Message msg) {
		for (Messenger msnger : mReplyList) {
			try {
				msnger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void reply(int msgId, Bundle data) {
		Message msg = new Message();
		msg.what = msgId;
		msg.setData(data);
		reply(msg);
	}

	/**
	 * 当Service 连接后调用，发送当前的下载队列
	 */
	private void sendDonloadQueue(Messenger messenger) {
		/**
		 * 加同步锁，防止多线程执行导致下载状态不对（onStart 是后台线程， getStoreAppQueue是UI线程）
		 */
		synchronized (mDownloader) {
			List<StoreAppInfo> storeAppInfos = mDownloader.getStoreAppQueue();

			Message msg = new Message();
			msg.what = MSG_REGIST_REPLY;
			Bundle bundle = new Bundle();
			bundle.putSerializable(PARAM_QUEUE, (Serializable) storeAppInfos);
			msg.setData(bundle);
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 绑定service
	 * 
	 * @param context
	 * @param conn
	 */
	public static void bindService(Context context, ServiceConnection conn) {
		context.bindService(new Intent(context, FileDownloadService.class),
				conn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 解除绑定service
	 * 
	 * @param context
	 * @param conn
	 */
	public static void unBindService(Context context, ServiceConnection conn) {
		if (conn != null) {
			context.unbindService(conn);
		}
	}

	/**
	 * 启动service
	 * 
	 * @param context
	 */
	public static void startService(Context context) {
		Intent intent = new Intent(context, FileDownloadService.class);
		context.startService(intent);
	}

	/**
	 * 通知service添加下载任务
	 * 
	 * @param remoteMessenger
	 * @param key
	 * @param url
	 * @param fileName
	 */
	public static void startDownload(Messenger remoteMessenger, int key,
			String title, String url, String fileName) {
		if (remoteMessenger != null) {
			Message msg = new Message();
			msg.what = MSG_DOWNLOAD;
			Bundle bundle = new Bundle();
			bundle.putInt(PARAM_KEY, key);
			bundle.putString(PARAM_TITLE, title);
			bundle.putString(PARAM_URL, url);
			bundle.putString(PARAM_FILE_NAME, fileName);
			msg.setData(bundle);
			try {
				remoteMessenger.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static class MyServiceConnection implements ServiceConnection {

		private Handler mHandler;
		private Messenger mRemote;
		private Messenger mLocal;

		public MyServiceConnection(Handler handler) {
			this.mHandler = handler;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemote = null;
			mLocal = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemote = new Messenger(service);
			mLocal = new Messenger(mHandler);
			FileDownloadService.registReply(mLocal, mRemote);
		}

		public Messenger getRemote() {
			return mRemote;
		}

		public Messenger getLocal() {
			return mLocal;
		}
	}

	private OnFileDownloadListener mListener = new OnFileDownloadListener() {

		private long time;

		@Override
		public void onStart(DownloadTask task) {
			time = System.currentTimeMillis();

			Bundle data = new Bundle();
			data.putInt(PARAM_KEY, (Integer) task.key);
			reply(MSG_DOWNLOAD_ON_START, data);
		}

		@Override
		public void onProgressUpdate(DownloadTask task, Integer progress) {

			Bundle data = new Bundle();
			data.putInt(PARAM_PROGRESS, progress);
			data.putInt(PARAM_KEY, (Integer) task.key);
			reply(MSG_DOWNLOAD_UPDATE_PROGRESS, data);

			DownloadUtil.showProgressNotify(FileDownloadService.this,
					(Integer) task.key, time, progress, 100, task.title);
		}

		@Override
		public void onPrepare(DownloadTask task, boolean pending) {

			Bundle data = new Bundle();
			data.putInt(PARAM_KEY, (Integer) task.key);
			data.putBoolean(PARAM_IS_PENDING, pending);
			reply(MSG_DOWNLOAD_ON_PREPARE, data);
			Notification notification = DownloadUtil.showProgressNotify(
					FileDownloadService.this, (Integer) task.key, time, -1,
					100, task.title);
			startForeground((Integer) task.key, notification);
		}

		@Override
		public void onFinished(DownloadTask task, boolean success) {
			Bundle data = new Bundle();
			data.putInt(PARAM_KEY, (Integer) task.key);
			data.putBoolean(PARAM_IS_SUCCESS, success);
			reply(MSG_DOWNLOAD_ON_FINISH, data);
			if (success) {
				DownloadUtil.showDoneNotify(FileDownloadService.this,
						(Integer) task.key, task.title, task.localFileName, true);

				Intent intent = new Intent(
						DistributeReceiver.ACTION_DOWNLOADED,
						Uri.parse("package://"));
				intent.putExtra(DistributeReceiver.PARAM_ID, (Integer) task.key);
				FileDownloadService.this.sendBroadcast(intent);

				if (task.autoOpen) {
					DownloadUtil.checkDownloaded(FileDownloadService.this,
							task.localFileName);
				}
			} else {
				DownloadUtil.showDoneNotify(FileDownloadService.this,
						(Integer) task.key, task.title, task.localFileName, false);
			}

		}
	};
}
