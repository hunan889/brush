package com.yy.android.gamenews.bs2.util;

/**
 * 应用信息 包括鉴权信息，bucket指定
 * 
 * @author tesla
 * 
 */
public class AppInfo {
	// 鉴权信息
	String accessKey;
	String accessSecret;

	String bucket;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

}
