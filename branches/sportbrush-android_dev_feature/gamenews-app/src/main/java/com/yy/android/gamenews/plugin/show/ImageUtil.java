package com.yy.android.gamenews.plugin.show;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class ImageUtil {

	public static final String PIC_TYPE_JPG = ".jpg";
	public static final String PIC_TYPE_PNG = ".png";
	public static final int PIC_WIDTH = 640;
	public static final int PIC_HEIGHT = 1080;
	public static final int PIC_QUALITY = 80;
	public static final int SMALL_PIC_SIZE = 164;
	public static final int MIN_WIDTH = 260;
	public static final int MIN_HEIGHT = 260;

	public static Bitmap ImageCrop(Bitmap bitmap, int width, int height,
			boolean isRecycled) {
		if (bitmap == null) {
			return null;
		}
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		if (bitmapWidth <= width && bitmapHeight <= height) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / bitmapWidth);
		float scaleHeight = ((float) height / bitmapHeight);
		if (scaleWidth > scaleHeight) {
			matrix.postScale(scaleWidth, scaleWidth);
		} else {
			matrix.postScale(scaleHeight, scaleHeight);
		}
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
				bitmapHeight, matrix, true);
		if (isRecycled && bitmap != null && !bitmap.equals(newbmp)
				&& !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		int w = newbmp.getWidth();
		int h = newbmp.getHeight();
		int wh = w > h ? h : w;
		int retX = w > h ? (w - h) / 2 : 0;
		int retY = w > h ? 0 : (h - w) / 2;
		Bitmap bmp = Bitmap.createBitmap(newbmp, retX, retY, wh, wh, null,
				false);
		if (isRecycled && newbmp != null && !newbmp.equals(bmp)
				&& !newbmp.isRecycled()) {
			newbmp.recycle();
			newbmp = null;
		}
		return bmp;
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height,
			boolean isRecycled) {
		if (bitmap == null) {
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w >= h) {
			int temp = height;
			height = width;
			width = temp;
		}
		if (w <= width && h <= height) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		if (w >= h) {
			float scaleWidth = ((float) width / w);
			matrix.postScale(scaleWidth, scaleWidth);
		} else {
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleHeight, scaleHeight);
		}
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		if (isRecycled && bitmap != null && !bitmap.equals(newbmp)
				&& !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		return newbmp;
	}

	public static Bitmap getSmallBitmap(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		BitmapFactory.Options options = getBitmapOptions(filePath);
		options.inSampleSize = calculateInSampleSize(options, PIC_WIDTH,
				PIC_HEIGHT);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static BitmapFactory.Options getBitmapOptions(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		return options;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (width < MIN_WIDTH || height < MIN_HEIGHT) {
			return inSampleSize;
		}
		if (height > reqHeight && width > reqWidth) {
			if (width >= height) {
				int temp = reqHeight;
				reqHeight = reqWidth;
				reqWidth = temp;
			}
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static String getRealFilePath(Context context, final Uri uri) {
		if (uri == null) {
			return null;
		}
		final String scheme = uri.getScheme();
		String path = null;
		if (scheme == null) {
			path = uri.getPath();
		} else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			path = uri.getPath(); 
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { ImageColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						path = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return path;
	}

	public static boolean savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName, String picType) {
		if (photoBitmap == null) {
			return false;
		}
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File photoFile = new File(path, photoName + picType);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					CompressFormat format = null;
					if (PIC_TYPE_JPG.equals(picType)) {
						format = Bitmap.CompressFormat.JPEG;
					} else if (PIC_TYPE_PNG.equals(picType)) {
						format = Bitmap.CompressFormat.PNG;
					} else {
						format = Bitmap.CompressFormat.JPEG;
					}
					if (photoBitmap.compress(format, PIC_QUALITY,
							fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
				return true;
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean deleteDirectory(String filePath) {
		if (null == filePath) {
			return false;
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return false;
		}
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}
		return file.delete();
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File file = new File(path);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getRootPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static File getDCIM() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).getPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static String getPhoneIMEIInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
