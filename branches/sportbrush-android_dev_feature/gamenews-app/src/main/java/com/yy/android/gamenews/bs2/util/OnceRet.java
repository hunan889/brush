package com.yy.android.gamenews.bs2.util;

public class OnceRet extends CallRet {
	String hash;
	String fileName;
	String bucket;

	public String getFileName() {
		return fileName;
	}

	protected void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBucket() {
		return bucket;
	}

	protected void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
