package com.duowan.gamenews.bean;

import java.util.Map;

public class Manifest {
	private String version;

	private Map<String, ManifestItem> data;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, ManifestItem> getData() {
		return data;
	}

	public void setData(Map<String, ManifestItem> data) {
		this.data = data;
	}
}
