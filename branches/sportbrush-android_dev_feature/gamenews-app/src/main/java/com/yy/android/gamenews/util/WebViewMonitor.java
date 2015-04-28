package com.yy.android.gamenews.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Build;

import com.yy.android.gamenews.ui.ArticleDetailActivity;

public class WebViewMonitor {

	static final int RESTART_DURATION = 5000;
	private ArticleDetailActivity mActivity;

	/**
	 * @param articleDetailActivity
	 */
	public WebViewMonitor(ArticleDetailActivity articleDetailActivity) {
		mActivity = articleDetailActivity;
	}

	private Timer mTimer = new Timer();
	private TimerTask mTask = new TimerTask() {

		@Override
		public void run() {
			Util.restartApp(mActivity);
		}
	};

	public void startMonitor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mTimer.schedule(mTask, RESTART_DURATION);
		}
	}

	/**
	 * 只要webview有回调就取消重启任务
	 */
	public void onCancelMonitor() {
		mTimer.cancel();
	}
}