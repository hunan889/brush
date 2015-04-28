package com.yy.android.gamenews.util;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.util.db.DbFactory;

public class DataCleanManager {

	public static File getInternalCache(Context context) {
		return context.getCacheDir();
	}

	public static File getExternalCache(Context context) {
		return context.getExternalCacheDir();
	}

	private static File getDatabases(Context context) {
		return context.getDatabasePath(Constants.SD_DATABASE_NAME)
				.getParentFile();
	}

	private static File getSharedPreferences(Context context) {
		return new File(getPrivateDataPath(context) + "/shared_prefs");
	}

	private static File getFilesDir(Context context) {
		return context.getFilesDir();
	}

	public static long getInternalCacheSize(Context context) {
		return getFileSize(getInternalCache(context));// .length();
	}

	public static long getDatabasesSize(Context context) {
		return getFileSize(getDatabases(context));
	}

	public static long getSharedPreferencesSize(Context context) {
		return getFileSize(getSharedPreferences(context));
	}

	public static long getFilesDirSize(Context context) {
		return getFileSize(getFilesDir(context));
	}

	public static long getExternalCacheSize(Context context) {
		return getFileSize(getExternalCache(context));
	}

	public static long getCustomizeFileSize(Context context, String path) {
		return getFileSize(new File(path));
	}

	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFiles(getInternalCache(context));
	}

	private static String getPrivateDataPath(Context context) {
		return context.getFilesDir().getPath() + "/";
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		DbFactory.closeDataBase();
		deleteFiles(getDatabases(context));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFiles(getSharedPreferences(context));
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFiles(getFilesDir(context));
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFiles(getExternalCache(context));
		}
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		deleteFiles(new File(filePath));
	}

	/** * 清除本应用所有的数据 * * @param context * @param filepath */
	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		// cleanSharedPreference(context);
		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();
		cleanFiles(context);
		if (filepath != null) {
			for (String filePath : filepath) {
				cleanCustomCache(filePath);
			}
		}

	}

	private static void deleteFiles(File file) {

		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			for (File item : file.listFiles()) {
				deleteFiles(item);
			}
		}
		file.delete();

		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// }
	}

	public static long getAppCacheSize(Context context) {
		long dbSize = getDatabasesSize(context);
		long internalSize = getInternalCacheSize(context);
		long externalSize = getExternalCacheSize(context);
		long filesDirSize = getFilesDirSize(context);
		// long customizeSize = getCustomizeFileSize(context,
		// FileUtil.getBaseDir());

		return dbSize + internalSize + externalSize + filesDirSize;
	}

	public static void cleanAppCache(Context context) {
		cleanApplicationData(context);
	}

	public static long getFileSize(File file)// 取得文件夹大小
	{

		long size = 0;
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File fileList[] = file.listFiles();
				if (fileList != null) {

					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].isDirectory()) {
							size = size + getFileSize(fileList[i]);
						} else {
							size = size + fileList[i].length();
						}
					}
				}
			} else {
				size = file.length();
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小

		if (fileS == 0) {
			return "0";
		}
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	public long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;

	}
}