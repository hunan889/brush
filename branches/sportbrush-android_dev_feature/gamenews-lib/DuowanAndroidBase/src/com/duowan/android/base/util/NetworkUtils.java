package com.duowan.android.base.util;

import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午4:30:28
 */
public final class NetworkUtils {

	/**
	 * 描述：判断网络是否有效.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Gps是否打开 需要<uses-permission
	 * android:name="android.permission.ACCESS_FINE_LOCATION" />权限
	 * 
	 * @param context
	 *            the context
	 * @return true, if is gps enabled
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * wifi是否打开.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is wifi enabled
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络.
	 * 
	 * @param context
	 *            the context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是手机网络.
	 * 
	 * @param context
	 *            the context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	public static String getNetworkType(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) { // 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) { // 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					if (info.getType() == ConnectivityManager.TYPE_WIFI) {
						return "WIFI";
					} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
						switch (info.getSubtype()) {
						case TelephonyManager.NETWORK_TYPE_CDMA:
							return "2G";
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
							return "3G";
						case TelephonyManager.NETWORK_TYPE_EVDO_A:
							return "3G";
						case TelephonyManager.NETWORK_TYPE_EVDO_B:
							return "3G";
						case TelephonyManager.NETWORK_TYPE_UMTS:
							return "3G";
						case TelephonyManager.NETWORK_TYPE_HSDPA:
							return "3G";
						case TelephonyManager.NETWORK_TYPE_GPRS:
							return "2G";
						case TelephonyManager.NETWORK_TYPE_EDGE:
							return "2G";
						default:
							return "2G/3G/4G";
						}
					} else {
						Cursor mCursor = context.getContentResolver().query(Uri.parse("content//telephony/carriers"), new String[] { "name" }, "current=", null, null);
						if (mCursor != null && mCursor.moveToFirst()) {
							return mCursor.getString(0);
						}
					}
				}
			}
		}
		return "UNKNOW";
	}
}
