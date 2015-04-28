package com.yy.android.gamenews.util;

public class ClassUtils {

	public static boolean isInstanceOf(Object obj, Class<?> clazz) {
		if (obj == null || clazz == null) {
			return false;
		}
		Class<?> objClass = obj.getClass();

		while (objClass != null) {
			if (clazz.equals(objClass)) {
				return true;
			}
			objClass = objClass.getSuperclass();
		}

		return false;
	}
}
