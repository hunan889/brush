package com.duowan.gamenews.bean;

public class ManifestItem {
	private String url;
	private String version;
	private String md5;

	public ManifestItem() {
	}

	public ManifestItem(String url, String version, String md5) {
		this.url = url;
		this.version = version;
		this.md5 = md5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
