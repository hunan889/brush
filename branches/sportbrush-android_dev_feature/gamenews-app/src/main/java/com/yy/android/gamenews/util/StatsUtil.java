package com.yy.android.gamenews.util;

import java.util.HashMap;
import java.util.Properties;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.yy.android.gamenews.Constants;
import com.yy.hiidostatis.api.HiidoSDK;

public class StatsUtil {

	public static void statsReport(Context context, String eventId) {
		MobclickAgent.onEvent(context, eventId);
	}

	public static void statsReport(Context context, String eventId, String key,
			String value) {
		HashMap<String, String> statsData = new HashMap<String, String>();
		statsData.put(key, value);
		MobclickAgent.onEvent(context, eventId, statsData);
	}

	public static void statsReport(Context context, String eventId, String key,
			String value, String key1, String value1) {
		HashMap<String, String> statsData = new HashMap<String, String>();
		statsData.put(key, value);
		statsData.put(key1, value1);
		MobclickAgent.onEvent(context, eventId, statsData);
	}

	public static void statsReportByMta(Context context, String eventId,
			String lable) {
		com.tencent.stat.StatService.trackCustomEvent(context, eventId, lable);
	}

	public static void statsReportByMta(Context context, String eventId,
			String key, String value) {
		Properties prop = new Properties();
		prop.setProperty(key, value);
		com.tencent.stat.StatService.trackCustomKVEvent(context, eventId, prop);
	}

	public static void statsReportByMta(Context context, String eventId,
			String key, String value, String key1, String value1) {
		Properties prop = new Properties();
		prop.setProperty(key, value);
		prop.setProperty(key1, value1);
		com.tencent.stat.StatService.trackCustomKVEvent(context, eventId, prop);
	}

	public static void statsReportByHiido(String eventId, String lable) {
		HiidoSDK.instance().reportTimesEvent(Constants.UID, eventId, lable);
	}

	/**
	 * 所有的统计
	 * 
	 * @param context
	 * @param eventId
	 * @param key
	 * @param value
	 */
	public static void statsReportAllData(Context context, String eventId,
			String key, String value) {
		StatsUtil.statsReport(context, eventId, key, value);
		StatsUtil.statsReportByHiido(eventId, value);
		StatsUtil.statsReportByMta(context, eventId, key, value);
	}

}
