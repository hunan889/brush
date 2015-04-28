package com.yy.android.gamenews.util;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.duowan.Comm.UpgradeRsp;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.model.LaunchModel;
import com.duowan.android.base.model.UpgradeModel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.service.UpdateService;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.sportbrush.R;

/**
 * APK更新管理类
 * 
 * @author Royal
 * 
 */
public class UpdateManager {

	// 上下文对象
	private FragmentActivity mContext;
	// 更新版本信息对象
	// private VersionInfo info = null;
	// 下载进度条
	// private ProgressBar progressBar;
	// // 是否终止下载
	// private boolean isInterceptDownload = false;
	// // 进度条显示数值
	// private int progress = 0;

	private static UpdateManager INSTANCE;

	/**
	 * 参数为Context(上下文activity)的构造函数
	 * 
	 * @param context
	 */
	public UpdateManager(FragmentActivity context) {
		this.mContext = context;
	}

	private boolean checkUpdate;

	public void doLaunch() {
		checkUpdate = false;
		LaunchModel.doLaunch(mContext, new ResponseListener<UpgradeRsp>(
				mContext) {

			@Override
			public void onResponse(UpgradeRsp arg0) {
				onEvent(arg0);
			}

			@Override
			public void onError(Exception e) {
				onEvent(null);
				super.onError(e);
			}
		}, Constants.ECOMM_APP_TYPE);
	}

	public void checkUpdate() {
		checkUpdate = true;
		UpgradeModel.doUpgrade(new ResponseListener<UpgradeRsp>(mContext) {

			@Override
			public void onResponse(UpgradeRsp arg0) {
				onEvent(arg0);
			}

			@Override
			public void onError(Exception e) {
				onEvent(null);
				super.onError(e);
			}
		}, Constants.ECOMM_APP_TYPE);
	}

	public void onEvent(Object event) {
		if (event == null) {
			ToastUtil.showToast(R.string.my_check_update_fail);
			mOnUpdateInfoListener.onCheckFinish(false, false);
			return;
		}

		if (event instanceof UpgradeRsp) {
			UpgradeRsp rsp = (UpgradeRsp) event;
			final String title = rsp.getSTitle();
			final String message = rsp.getSText();
			final String url = rsp.getSURL();
			// 0 optional int iStatus; // 0：不变化,1：提示更新,2：强制更新
			// 1 optional string sTitle; // 标题
			// 2 optional string sText; // 信息提示
			// 3 optional string sURL; // 下载地址
			// 4 optional string sReleaseTime; // 版本发布时间 2012-11-26
			switch (rsp.iStatus) {
			case 0: { // 无变化

				mOnUpdateInfoListener.onCheckFinish(false, false);
				if (checkUpdate) {
					Toast.makeText(mContext, R.string.global_no_update,
							Toast.LENGTH_LONG).show();
				}
				break;
			}
			case 1: { // 提示更新
				mOnUpdateInfoListener.onCheckFinish(true, false);
				UiUtils.showDialog(mContext, title, message,
						R.string.global_ok, R.string.global_cancel,
						new AppDialog.OnClickListener() {

							@Override
							public void onDialogClick(int nButtonId) {
								mOnUpdateInfoListener.onClick(nButtonId, false);
								if (nButtonId == AppDialog.BUTTON_POSITIVE) {
									Intent intent = new Intent(mContext,
											UpdateService.class);
									intent.putExtra("downloadUrl", url);
									mContext.startService(intent);
									Toast.makeText(mContext,
											R.string.upgrade_start,
											Toast.LENGTH_SHORT).show();

									String eventId = "stat_user_click_update";
									String key = "download_path";
									String value = String.valueOf(url);
									StatsUtil.statsReport(mContext, eventId,
											key, value);
									StatsUtil.statsReportByMta(mContext,
											eventId, key, value);
									StatsUtil.statsReportByHiido(eventId, key
											+ value);
								}
							}

							@Override
							public void onDismiss() {
								mOnUpdateInfoListener.onClick(
										AppDialog.BUTTON_NEGATIVE, false);

							}
						});
				break;
			}
			case 2: { // 强制更新
				mOnUpdateInfoListener.onCheckFinish(true, true);
				UiUtils.showDialog(mContext, title, message,
						R.string.global_ok, R.string.global_cancel,
						new AppDialog.OnClickListener() {

							@Override
							public void onDialogClick(int nButtonId) {
								mOnUpdateInfoListener.onClick(nButtonId, true);
								if (nButtonId == AppDialog.BUTTON_POSITIVE) {
									Intent intent = new Intent(mContext,
											UpdateService.class);

									String eventId = "stat_user_click_update";
									String key = "download_path";
									String value = String.valueOf(url);
									StatsUtil.statsReport(mContext, eventId,
											key, value);
									StatsUtil.statsReportByMta(mContext,
											eventId, key, value);
									StatsUtil.statsReportByHiido(eventId, key
											+ value);

									intent.putExtra("downloadUrl", url);
									mContext.startService(intent);
									Toast.makeText(mContext,
											R.string.upgrade_start,
											Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onDismiss() {
								mOnUpdateInfoListener.onClick(
										AppDialog.BUTTON_NEGATIVE, false);

							}
						});
				break;
			}
			}
		}
	}

	private OnUpdateInfoListener mOnUpdateInfoListener;

	public void setOnUpdateInfoListener(OnUpdateInfoListener listener) {
		mOnUpdateInfoListener = listener;
	}

	public interface OnUpdateInfoListener {
		public void onCheckFinish(boolean needUpdate, boolean isForceUpdate);

		public void onClick(int button, boolean isForceUpdate);
	}

	// /**
	// * 从服务端获取版本信息
	// *
	// * @return
	// */
	// private VersionInfo getVersionInfoFromServer() {
	// VersionInfo info = null;
	// URL url = null;
	// try {
	// // 10.0.2.2相当于localhost
	// url = new URL("http://10.0.2.2:8080/updateApkServer/version.xml");
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// if (url != null) {
	// try {
	// // 使用HttpURLConnection打开连接
	// HttpURLConnection urlConn = (HttpURLConnection) url
	// .openConnection();
	// // 读取服务端version.xml的内容(流)
	// info = XMLParserUtil.getUpdateInfo(urlConn.getInputStream());
	// urlConn.disconnect();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return info;
	// }

	/**
	 * 提示更新对话框
	 * 
	 * @param info
	 *            版本信息对象
	 */
	// private void showUpdateDialog() {
	// Builder builder = new Builder(mContext);
	// builder.setTitle("版本更新");
	// builder.setMessage(info.getDisplayMessage());
	// builder.setPositiveButton("下载", new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// // 弹出下载框
	// showDownloadDialog();
	// }
	// });
	// builder.setNegativeButton("以后再说", new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// builder.create().show();
	// }
	//
	// /**
	// * 弹出下载框
	// */
	// private void showDownloadDialog() {
	// Builder builder = new Builder(mContext);
	// builder.setTitle("版本更新中...");
	// final LayoutInflater inflater = LayoutInflater.from(mContext);
	// View v = inflater.inflate(R.layout.update_progress, null);
	// progressBar = (ProgressBar) v.findViewById(R.id.pb_update_progress);
	// builder.setView(v);
	// builder.setNegativeButton("取消", new OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// // 终止下载
	// isInterceptDownload = true;
	// }
	// });
	// builder.create().show();
	// // 下载apk
	// downloadApk();
	// }
	//
	// /**
	// * 下载apk
	// */
	// private void downloadApk() {
	// // 开启另一线程下载
	// Thread downLoadThread = new Thread(downApkRunnable);
	// downLoadThread.start();
	// }
	//
	// /**
	// * 从服务器下载新版apk的线程
	// */
	// private Runnable downApkRunnable = new Runnable() {
	// @Override
	// public void run() {
	// if (!android.os.Environment.getExternalStorageState().equals(
	// android.os.Environment.MEDIA_MOUNTED)) {
	// // 如果没有SD卡
	// Builder builder = new Builder(mContext);
	// builder.setTitle("提示");
	// builder.setMessage("当前设备无SD卡，数据无法下载");
	// builder.setPositiveButton("确定", new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// builder.show();
	// return;
	// } else {
	// try {
	// // 服务器上新版apk地址
	// URL url = new URL(
	// "http://10.0.2.2:8080/updateApkServer/updateApkDemo2.apk");
	// HttpURLConnection conn = (HttpURLConnection) url
	// .openConnection();
	// conn.connect();
	// int length = conn.getContentLength();
	// InputStream is = conn.getInputStream();
	// File file = new File(Environment
	// .getExternalStorageDirectory().getAbsolutePath()
	// + "/updateApkFile/");
	// if (!file.exists()) {
	// // 如果文件夹不存在,则创建
	// file.mkdir();
	// }
	// // 下载服务器中新版本软件（写文件）
	// String apkFile = Environment.getExternalStorageDirectory()
	// .getAbsolutePath()
	// + "/updateApkFile/"
	// + info.getApkName();
	// File ApkFile = new File(apkFile);
	// FileOutputStream fos = new FileOutputStream(ApkFile);
	// int count = 0;
	// byte buf[] = new byte[1024];
	// do {
	// int numRead = is.read(buf);
	// count += numRead;
	// // 更新进度条
	// progress = (int) (((float) count / length) * 100);
	// handler.sendEmptyMessage(1);
	// if (numRead <= 0) {
	// // 下载完成通知安装
	// handler.sendEmptyMessage(0);
	// break;
	// }
	// fos.write(buf, 0, numRead);
	// // 当点击取消时，则停止下载
	// } while (!isInterceptDownload);
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// };
	//
	// /**
	// * 声明一个handler来跟进进度条
	// */
	// private Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 1:
	// // 更新进度情况
	// progressBar.setProgress(progress);
	// break;
	// case 0:
	// progressBar.setVisibility(View.INVISIBLE);
	// // 安装apk文件
	// installApk();
	// break;
	// default:
	// break;
	// }
	// };
	// };
	//
	// /**
	// * 安装apk
	// */
	// private void installApk() {
	// // 获取当前sdcard存储路径
	// File apkfile = new File(Environment.getExternalStorageDirectory()
	// .getAbsolutePath() + "/updateApkFile/" + info.getApkName());
	// if (!apkfile.exists()) {
	// return;
	// }
	// Intent i = new Intent(Intent.ACTION_VIEW);
	// // 安装，如果签名不一致，可能出现程序未安装提示
	// i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())),
	// "application/vnd.android.package-archive");
	// mContext.startActivity(i);
	// }
}