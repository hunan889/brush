package com.duowan.android.base.model;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.duowan.Comm.AppUA;
import com.duowan.Comm.CommUserbase;
import com.duowan.Comm.EPlatform;
import com.duowan.android.base.R;
import com.duowan.android.base.net.VolleyClient;
import com.duowan.android.base.util.LocalLog;
import com.duowan.android.base.util.LogUtils;
import com.duowan.jce.wup.UniPacket;

import de.greenrobot.event.EventBus;

public abstract class BaseModel {

	// online
	public static String HOST = "http://proxy.shua.duowan.com/";

	public static void setYYuid(Context context, int yyuid) {
		SharedPreferences shared = context.getSharedPreferences("CommUserbase",
				Context.MODE_PRIVATE);
		shared.edit().putInt("yyuid", yyuid).commit();
	}

	public static void setGuid(Context context, byte[] vGuid) {
		if (vGuid != null) {
			SharedPreferences shared = context.getSharedPreferences(
					"CommUserbase", Context.MODE_PRIVATE);
			shared.edit().putString("vGuid", new String(vGuid)).commit();
		}
	}

	public static CommUserbase createCommUserbase(Context context,
			int commAppType) {
		CommUserbase commUserbase = new CommUserbase();

		commUserbase.eType = commAppType;

		byte[] vGuid = null;
		int yyuid = 0;

		SharedPreferences shared = context.getSharedPreferences("CommUserbase",
				Context.MODE_PRIVATE);
		if (shared.contains("vGuid")) {
			String guid = shared.getString("vGuid", null);
			if (guid != null && !guid.equals(""))
				vGuid = guid.getBytes();
		}
		if (shared.contains("yyuid")) {
			yyuid = shared.getInt("yyuid", 0);
		}

		commUserbase.vGuid = vGuid;
		commUserbase.yyuid = yyuid;

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		PackageManager packageManager = context.getPackageManager();

		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			commUserbase.sIMEI = telephonyManager.getDeviceId();
		} catch (Exception e1) {
		}
		String channel = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			channel = appInfo.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AppUA appUA = new AppUA();
		appUA.ePlat = EPlatform.PLAT_ADR.value();
		appUA.iWidth = dm.widthPixels;
		appUA.iHeight = dm.heightPixels;
		appUA.sChannel = channel;
		appUA.sOSVersion = android.os.Build.MODEL + " "
				+ android.os.Build.VERSION.SDK_INT + " "
				+ android.os.Build.VERSION.RELEASE;
		appUA.sDevice = android.os.Build.DEVICE;
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			appUA.sVersion = packInfo.versionName;
		} catch (NameNotFoundException e) {
		}

		commUserbase.tUA = appUA;
		return commUserbase;
	}

	public static void postEvent(Object event) {
		EventBus.getDefault().post(event);
	}

	/**
	 * @param ServantName
	 *            调用的后台服务
	 * @param funcName
	 *            调用的函数
	 * @return
	 */
	protected static UniPacket createUniPacket(String ServantName,
			String funcName) {
		UniPacket uniPacket = new UniPacket();
		uniPacket.useVersion3();
		uniPacket.setServantName(ServantName);
		uniPacket.setFuncName(funcName);
		return uniPacket;
	}

	public static abstract class Request {
		/** Socket timeout in milliseconds for WUP requests */
		private static final int WUP_TIMEOUT_MS = 30000;

		/** Default number of retries for WUP requests */
		private static final int WUP_MAX_RETRIES = 1;

		/** Default backoff multiplier for WUP requests */
		private static final float WUP_BACKOFF_MULT = 1f;

		private WeakReference<FragmentActivity> activityReference;
		private UniPacket uniPacket;
		private String host = HOST;
		private String cacheKey;
		private String tag;
		private RetryPolicy retryPolicy = new DefaultRetryPolicy(
				WUP_TIMEOUT_MS, WUP_MAX_RETRIES, WUP_BACKOFF_MULT);
		private boolean showProgressDialog = true;
		private long cacheHitButRefreshed = 10 * 60 * 1000; // in 10 minutes
															// cache
															// will be hit, but
															// also refreshed on
															// background
		private long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this
															// cache
															// entry expires
															// completely

		public Request(FragmentActivity activity, UniPacket uniPacket) {
			this.activityReference = new WeakReference<FragmentActivity>(
					activity);
			this.uniPacket = uniPacket;
			this.tag = uniPacket.getFuncName();
		}

		public Request(FragmentActivity activity, UniPacket uniPacket,
				String cacheKey) {
			this.activityReference = new WeakReference<FragmentActivity>(
					activity);
			this.uniPacket = uniPacket;
			this.cacheKey = cacheKey;
			this.tag = uniPacket.getFuncName();
		}

		public Request(UniPacket uniPacket) {
			this.activityReference = null;
			this.uniPacket = uniPacket;
			this.tag = uniPacket.getFuncName();
			showProgressDialog = false;
		}

		public Request setHost(String host) {
			this.host = host;
			return this;
		}

		public Request setCacheKey(String cacheKey) {
			this.cacheKey = cacheKey;
			return this;
		}

		public Request setRetryPolicy(RetryPolicy retryPolicy) {
			this.retryPolicy = retryPolicy;
			return this;
		}

		public Request setShowProgressDialog(boolean showProgressDialog) {
			this.showProgressDialog = showProgressDialog;
			return this;
		}

		public Request setCacheHitButRefreshed(long cacheHitButRefreshed) {
			this.cacheHitButRefreshed = cacheHitButRefreshed;
			return this;
		}

		public Request setCacheExpired(long cacheExpired) {
			this.cacheExpired = cacheExpired;
			return this;
		}

		public void execute() {
			if (showProgressDialog) {
				showProgressDialog();
			}
			VolleyClient.newRequestQueue(host, uniPacket, cacheKey, tag,
					cacheHitButRefreshed, cacheExpired, retryPolicy,
					new VolleyClient.Listener() {
						@Override
						public void onResponse(UniPacket response) {

							int code = 0;
							int subCode = 0;
							String msg = "";
							Object result = "";
							if (response != null) {
								try {
									code = response.getByClass("code", 0);
									subCode = response.get("subcode", 0);
									msg = response.get("msg", "");
									// result = response.get("result",
									// new Object());
									LocalLog.d(
											"",
											String.format(
													"[onResponse], code = %d, subcode = %d, msg = %s, result = %s",
													code, subCode, msg,
													response));
								} catch (Exception e) {

								}
							}

							try {
								Request.this.onResponse(response);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (showProgressDialog) {
								hiddenProgressDialog();
							}
						}

						@Override
						public void onError(Exception e) {
							if (showProgressDialog) {
								hiddenProgressDialog();
							}
							LocalLog.d("", "[onError]" + e);
							Request.this.onError(e);
						}
					});
		}

		@SuppressLint("NewApi")
		private void showProgressDialog() {
			if (checkActivityState()) {
				FragmentActivity activity = activityReference.get();

				FragmentManager fm = activity.getSupportFragmentManager();

				DialogFragment newFragment = ProgressDialogFragment
						.newInstance(tag);
				newFragment.show(fm, "progress_dialog");
			}
		}

		private void hiddenProgressDialog() {
			if (checkActivityState()) {
				FragmentActivity activity = activityReference.get();
				FragmentManager fm = activity.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment prev = fm.findFragmentByTag("progress_dialog");
				if (prev != null) {
					((ProgressDialogFragment) prev).finish();
					ft.remove(prev);
				}
				ft.commitAllowingStateLoss();
			}
		}

		@SuppressLint("NewApi")
		private boolean checkActivityState() {
			if (activityReference != null) {
				FragmentActivity activity = activityReference.get();
				if (activity.isFinishing())
					return false;

				if (Build.VERSION.SDK_INT >= 17)
					if (activity.isDestroyed())
						return false;
				return true;
			}
			return false;
		}

		public abstract void onResponse(UniPacket response);

		public void onError(Exception e) {
		}

		public static class ProgressDialogFragment extends DialogFragment {

			private String tag;
			private Dialog dialog;

			public static ProgressDialogFragment newInstance(String tag) {
				ProgressDialogFragment fragment = new ProgressDialogFragment();
				Bundle args = new Bundle();
				args.putString("tag", tag);
				fragment.setArguments(args);
				return fragment;
			}

			public void finish() {
				this.tag = null;
			}

			@Override
			public void onStop() {
				super.onStop();
				if (!TextUtils.isEmpty(tag)) {
					VolleyClient.getRequestQueue().cancelAll(tag);
					LogUtils.log("cancel request: " + tag);
				}
			}

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				Bundle args = getArguments();
				tag = args.getString("tag");
				dialog = new Dialog(getActivity(), R.style.BaseDialog);
				dialog.setContentView(R.layout.base_dialog_progress);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(false);
				return dialog;
			}

		}
	}

	public static abstract class ResponseListener<T> {

		private WeakReference<FragmentActivity> activityReference;

		public ResponseListener(FragmentActivity activity) {
			this.activityReference = new WeakReference<FragmentActivity>(
					activity);
		}

		public FragmentActivity get() {
			return activityReference.get();
		}

		public abstract void onResponse(T response);

		public void onError(Exception e) {
		}

	}

}
