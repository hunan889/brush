package com.yy.android.gamenews.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;

import com.yy.android.sportbrush.R;

public class TimeUtil {
	static private final SimpleDateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	static private final SimpleDateFormat mDateFormatYMDHMS = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static private final SimpleDateFormat mDateFormatThisYear = new SimpleDateFormat(
			"MM-dd HH:mm");
	static private final SimpleDateFormat mDateFormatYMD = new SimpleDateFormat(
			"yyyy-MM-dd");
	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间
	 */
	public static final int ONE_MINUTE = 60;
	/**
	 * 一小时的毫秒值，用于判断上次的更新时间
	 */
	public static final int ONE_HOUR = 60 * ONE_MINUTE;

	/**
	 * 一天的毫秒值，用于判断上次的更新时间
	 */
	public static final int ONE_DAY = 24 * ONE_HOUR;

	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final int ONE_MONTH = 30 * ONE_DAY;

	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final int ONE_YEAR = 12 * ONE_MONTH;

	public static String parseTime(int createTime) {
		Date createDate = new Date((long) createTime * 1000);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(new Date());
		c2.setTime(createDate);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
			return mDateFormatThisYear.format(createDate);
		}
		return mDateFormat.format(createDate);
	}
	
	public static String parseTimeToYMD(Date date) {
		return mDateFormatYMD.format(date);
	}
	
	public static String parseTimeToYMDHMS(Date date) {
		return mDateFormatYMDHMS.format(date);
	}
	

	public static String parseTime(Context context, int createTime) {

		Resources resources = context.getResources();
		if (createTime == 0) {
			return resources.getString(R.string.just_now);
		}
		String res = "";
		long currentTime = System.currentTimeMillis();
		long timePassed = currentTime / 1000 - createTime;
		long timeIntoFormat;

		if (timePassed < 0) {
			res = resources.getString(R.string.just_now);
		} else if (timePassed < ONE_MINUTE) {
			res = resources.getString(R.string.just_now);
		} else if (timePassed < ONE_HOUR) {
			timeIntoFormat = timePassed / ONE_MINUTE;
			String value = timeIntoFormat + "分钟";
			res = String.format(resources.getString(R.string.post_at), value);
		} else if (timePassed < ONE_DAY) {
			timeIntoFormat = timePassed / ONE_HOUR;
			String value = timeIntoFormat + "小时";
			res = String.format(resources.getString(R.string.post_at), value);
		} else{
			res = parseTime(createTime);
		}
//		else if (timePassed < ONE_MONTH) {
//			timeIntoFormat = timePassed / ONE_DAY;
//			String value = timeIntoFormat + "天";
//			res = String.format(resources.getString(R.string.post_at), value);
//		} else if (timePassed < ONE_YEAR) {
//			timeIntoFormat = timePassed / ONE_MONTH;
//			String value = timeIntoFormat + "个月";
//			res = String.format(resources.getString(R.string.post_at), value);
//		} else {
//			timeIntoFormat = timePassed / ONE_YEAR;
//			String value = timeIntoFormat + "年";
//			res = String.format(resources.getString(R.string.post_at), value);
//		}
		return res;
	}

	public static String parseTimeForRefresh(Context context, long createTime) {

		Resources resources = context.getResources();
		if (createTime == 0) {
			return resources.getString(R.string.just_now);
		}
		String res = "";
		long currentTime = System.currentTimeMillis();
		long timePassed = (currentTime - createTime) / 1000;
		long timeIntoFormat;

		if (timePassed < ONE_HOUR) {
			timeIntoFormat = timePassed / ONE_MINUTE;
			if (timeIntoFormat <= 0) {
				timeIntoFormat = 1; // 至少是1分钟
			}
			String value = timeIntoFormat + "分钟";
			res = String.format(resources.getString(R.string.post_at), value);
		} else if (timePassed < ONE_DAY) {
			timeIntoFormat = timePassed / ONE_HOUR;
			String value = timeIntoFormat + "小时";
			res = String.format(resources.getString(R.string.post_at), value);
		} else if (timePassed < ONE_DAY * 2) {
			timeIntoFormat = timePassed / ONE_DAY;
			String value = timeIntoFormat + "天";
			res = String.format(resources.getString(R.string.post_at), value);
		} else {
			res = parseTime((int) (createTime / 1000));
		}
		return res;
	}

	public static final String getWeekString(int value) {

		String s = "";
		switch (value) {
		case Calendar.MONDAY: {
			return "一";
		}
		case Calendar.TUESDAY: {
			return "二";
		}
		case Calendar.WEDNESDAY: {
			return "三";
		}
		case Calendar.THURSDAY: {
			return "四";
		}
		case Calendar.FRIDAY: {
			return "五";
		}
		case Calendar.SATURDAY: {
			return "六";
		}
		case Calendar.SUNDAY: {
			return "日";
		}
		}
		return s;
	}

	public static boolean isExpire(String time) {
		Long timeLong = 0L;
		try {
			timeLong = Long.parseLong(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long current = System.currentTimeMillis();
		return timeLong * 1000 <= current;
	}

}
