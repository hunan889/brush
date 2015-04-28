package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.gamenews.RaceInfo;
import com.yy.android.gamenews.plugin.schetable.AlarmActivity;

public class AlarmUtil {

	 public static final long TIME_REMIND_BUFFER = 15 * 60 * 1000;// 15分钟

//	public static final long TIME_REMIND_BUFFER = 5 * 60 * 1000;
	private static final String TAG = "AlarmUtil";

	public static final void addToAlarm(Context context, RaceInfo info) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Calendar liveTime = Calendar.getInstance();

		Calendar currentTime = Calendar.getInstance();
		// liveTime.setTimeInMillis(currentTime.getTimeInMillis() + 5 * 1000);

		liveTime.setTimeInMillis((long) info.getLiveTime() * 1000);
		// 如果离比赛开始时间少于提醒时间，则将闹钟设置为比赛开始时
		if (liveTime.getTimeInMillis() - currentTime.getTimeInMillis() < TIME_REMIND_BUFFER) {
			liveTime.setTimeInMillis(liveTime.getTimeInMillis());
		} else {
			liveTime.setTimeInMillis(liveTime.getTimeInMillis()
					- TIME_REMIND_BUFFER);
		}

		PendingIntent pi = getAlarmPendingIntent(context, info);
		am.set(AlarmManager.RTC_WAKEUP, liveTime.getTimeInMillis(), pi);
	}

	private static Intent getAlarmIntent(Context context, RaceInfo info) {
		Intent intent = new Intent(context, AlarmActivity.class);
		intent.setAction(AlarmActivity.ACTION_ALARM);
		intent.putExtra(AlarmActivity.KEY_ID, info.getId());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NO_HISTORY);

		return intent;
	}

	private static PendingIntent getAlarmPendingIntent(Context context,
			RaceInfo info) {
		String id = info.getId();
		int key = 0;
		if (!TextUtils.isEmpty(id)) {
			key = id.hashCode();
		}
		PendingIntent pi = PendingIntent.getActivity(context, key,
				getAlarmIntent(context, info), 0);

		return pi;
	}

	public static void removeAlarm(Context context, RaceInfo info) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = getAlarmPendingIntent(context, info);
		am.cancel(pi);
	}

	/**
	 * 检查设置的闹钟，删除过期的闹钟，并将现有闹钟加到系统闹钟
	 * 
	 * @param context
	 */
	public static void ensureAlarms(Context context) {
		List<RaceInfo> alarmList = Preference.getInstance().getAlarmRaceList();
		List<RaceInfo> schedAlarmList = Preference.getInstance().getSchedAlarmRaceList();
		if (alarmList == null || alarmList.size() == 0) {
			return;
		}
		Log.d(TAG, "[addToAlarm]alarmList size = " + alarmList.size());
		List<RaceInfo> removeList = new ArrayList<RaceInfo>();
		long now = System.currentTimeMillis();
		for (RaceInfo info : alarmList) {
			if (info == null) {
				continue;
			}
			boolean isExpire = false;
			long liveTime = (long) info.getLiveTime() * 1000;
			if (now > liveTime) {
				isExpire = true;
			}

			Date nowDate = new Date(now);
			Date liveDate = new Date(liveTime);
			Log.d(TAG,
					"[addToAlarm]liveTime = "
							+ TimeUtil.parseTimeToYMDHMS(liveDate) + ", now = "
							+ TimeUtil.parseTimeToYMDHMS(nowDate));

			if (isExpire) {
				removeList.add(info);
				// TODO:对过期的闹钟做操作，是否需要再次提醒 ？
			} else {
				addToAlarm(context, info);
			}
		}

		for (RaceInfo info : removeList) {
			removeAlarm(context, info);
			alarmList.remove(info);
		}
		
		for (Iterator<RaceInfo> it = schedAlarmList.iterator(); it.hasNext();) {
			RaceInfo info = it.next();
			if (info == null) {
				continue;
			}
			boolean isExpire = false;
			long liveTime = (long) info.getLiveTime() * 1000;
			if (now > liveTime) {
				isExpire = true;
			}
			if (isExpire) {
				schedAlarmList.remove(info);
			}
		}
		
		Preference.getInstance().saveAlarmRaceList(alarmList);
		Preference.getInstance().saveSchedAlarmRaceList(schedAlarmList);
	}

}
