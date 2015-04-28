package com.duowan.android.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014年4月11日 下午6:10:41
 */
public class SerializableUtils {

	public static String SERIALIZABLE_DIR = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

	public static String output(Context context, String name, Object obj) {
		File folder = new File(SERIALIZABLE_DIR + (SERIALIZABLE_DIR.endsWith(File.separator) ? "" : File.separator) + context.getPackageName() + File.separator + "cache");
		if (!name.endsWith(".xml")) {
			name += ".xml";
		}
		if (!folder.exists())
			folder.mkdirs();
		return output(new File(folder, name), obj);
	}

	public static String output(File xmlFile, Object obj) {
		ObjectOutputStream oos = null;
		try {
			if (!xmlFile.exists())
				xmlFile.createNewFile();
			if (xmlFile.canWrite()) {
				oos = new ObjectOutputStream(new FileOutputStream(xmlFile));
				oos.writeObject(obj);
				oos.flush();
				return xmlFile.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

	public static <T> T load(Context context, String name, Class<T> toValueType) {
		File folder = new File(SERIALIZABLE_DIR + (SERIALIZABLE_DIR.endsWith(File.separator) ? "" : File.separator) + context.getPackageName() + File.separator + "cache");
		if (!name.endsWith(".xml")) {
			name += ".xml";
		}
		return load(new File(folder, name), toValueType);
	}

	@SuppressWarnings("unchecked")
	public static synchronized <T> T load(final File xmlFile, Class<T> toValueType) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(xmlFile));
			Object obj = ois.readObject();
			if (obj != null)
				return (T) obj;
		} catch (Exception e) {
			// xmlFile.delete();
			Log.w("SerializableUtils", "load(): " + e.getMessage() + " name: " + xmlFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
				}
		}
		return null;
	}
}
