package com.yy.android.gamenews.util;

import java.io.File;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;

public class WebViewCacheUtil {

	private static final String TAG = WebViewCacheUtil.class.getSimpleName();
	public static final int DELAY_MILLIS = 5000;// 延迟时长
	public static final String WEBVIEW_CACHE_STATE = "webview_cache_state";
	public static final String APP_CACAHE_DIRNAME = "webviewCache";
	public static final String webView_url_one = "http://mtq.yy.com/#detail.index?tid=14487&type=issue";
	public static final String webView_url_two = "http://mtq.yy.com/#game.index?gid=383";

	public static String getWebViewCachePath(Context context) {
		return context.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
	}

//	/**
//	 * 测试游戏刷子预加载地址
//	 * 
//	 * @param webView
//	 * @param url
//	 */
//	public static void startTestPreWebView(WebView webView, Context context,
//			String url, final Handler mHandler) {
//		startWebViewCache(webView, context);
//		WebSettings webSettings = webView.getSettings();
//		String userAgent = Constants.USER_AGENT_PREFIX
//				+ GameNewsApplication.getInstance().getPackageInfo().versionName;
//		String userAgentString = webSettings.getUserAgentString();
//		webSettings.setUserAgentString(userAgentString + userAgent);
//		webView.setVisibility(View.VISIBLE);
//		webView.setWebViewClient(new WebViewClient() {
//			@Override
//			public void onPageFinished(final WebView view, String url) {
//				super.onPageFinished(view, url);
//				mHandler.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						view.removeAllViews();
//						view.destroy();
//						Preference.getInstance().setWebViewCacheState(true);
//					}
//				}, DELAY_MILLIS);
//
//			}
//		});
//		webView.loadUrl(url);
//	}

	/**
	 * http://mtq.yy.com/#game.index?gid=383
	 * http://mtq.yy.com/#detail.index?tid=14487&type=issue
	 * 
	 * 游戏刷子预加载地址
	 * 
	 * @param webView
	 * @param url
	 */
	public static void startPreWebView(Context context,WebView webView, String url,
			final Handler mHandler) {
		startWebViewCache(webView, context);
		WebSettings webSettings = webView.getSettings();
		String userAgent = Constants.USER_AGENT_PREFIX
				+ GameNewsApplication.getInstance().getPackageInfo().versionName;
		String userAgentString = webSettings.getUserAgentString();
		webSettings.setUserAgentString(userAgentString + userAgent);
		webView.setVisibility(View.GONE);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, String url) {
				super.onPageFinished(view, url);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (view != null) {
							view.destroy();
							Preference.getInstance().setWebViewCacheState(true);
						}
					}
				}, DELAY_MILLIS);
			}
		});
		webView.loadUrl(url);
	}

	/**
	 * 开启WebView缓存
	 */
	public static void startWebViewCache(WebView webView, Context context) {
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setDomStorageEnabled(true); // 开启 database storage API 功能
		webSettings.setDatabaseEnabled(true);
		// 设置数据库缓存路径
		webSettings.setDatabasePath(WebViewCacheUtil
				.getWebViewCachePath(context));
		// 设置 Application Caches 缓存目录
		webSettings.setAppCachePath(WebViewCacheUtil
				.getWebViewCachePath(context));
		// 开启 Application Caches 功能
		webSettings.setAppCacheEnabled(true);
	}

	/**
	 * 清除WebView缓存
	 */
	public static void clearWebViewCache(Context context) {
		// WebView 缓存文件
		File appCacheDir = new File(getWebViewCachePath(context));
		Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());
		if (appCacheDir.exists()) {
			deleteFile(appCacheDir);
		}
	}

	/**
	 * 递归删除 文件/文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		Log.d(TAG, "delete file path=" + file.getAbsolutePath());
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
			Log.d(TAG, "delete file no exists " + file.getAbsolutePath());
		}
	}

}
