package com.duowan.android.base.util;

import android.util.Log;

import com.duowan.android.base.BuildConfig;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-25 下午1:22:47
 */
public class LogUtils {

	private static final String TAG = LogUtils.class.getSimpleName();

	public static void log(Object... args) {
		if (BuildConfig.DEBUG) {
			Throwable ex = new Throwable();
			StackTraceElement[] stackElements = ex.getStackTrace();

			if (stackElements != null) {
				if (stackElements.length >= 2) {
					StringBuilder stack = new StringBuilder();
					stack.append(stackElements[1].getClassName()).append(".");
					stack.append(stackElements[1].getMethodName()).append("|");
					stack.append(stackElements[1].getLineNumber());
					Log.d(TAG, stack.toString());
				}
			}
			StringBuilder log = new StringBuilder(">> ");
			if (args != null) {
				int index = 0;
				for (Object object : args) {
					if (index != 0)
						log.append("|");
					index++;
					log.append(object);
				}
			}
			Log.d(TAG, log.toString());
			Log.d(TAG, "############################################");
		}
	}
}
