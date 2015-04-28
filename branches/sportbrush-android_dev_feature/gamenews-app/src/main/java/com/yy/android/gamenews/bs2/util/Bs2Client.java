package com.yy.android.gamenews.bs2.util;


public class Bs2Client {
	public static OnceUploadClient newOnceUploadClient(AppInfo appInfo) {
		return new OnceUploadClient(appInfo);

	}
	
	public static MultipartUploadClient newMultipartUploadClient(AppInfo appInfo) {
		return new MultipartUploadClient(appInfo);

	}
	
	public static DownloadClient newDownloadClient(AppInfo appInfo) {
		return new DownloadClient(appInfo);
	}
	
	public static ObjectOptClient newObjectOptClient(AppInfo appInfo) {
		return new ObjectOptClient(appInfo);
	}
	

}
