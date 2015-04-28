package com.yy.android.gamenews.util.cache;

import android.content.Context;

import com.yy.android.gamenews.util.Util;

public class StorageFactory {
	public static FileStorage getFileStorage(Context context) {
		return ( context == null || Util.isSDExists()) ? new SDCache() : new InnerCache(context);
	}
}
