package com.yy.android.gamenews.util;

import android.widget.Toast;

import com.yy.android.gamenews.GameNewsApplication;

public class ToastUtil {
	private static Toast mToast = null;

	public static void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(GameNewsApplication.getInstance(), msg,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	public static void showToast(int resId) {
		if (mToast == null) {
			mToast = Toast.makeText(GameNewsApplication.getInstance(), resId,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(resId);
		}
		mToast.show();
	}

}
