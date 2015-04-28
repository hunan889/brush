package com.yy.android.gamenews.bs2.util;

public class DownloadRet extends CallRet {
	byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public DownloadRet() {
	}

	public DownloadRet(HttpResponse httpResponse) {
		this.setHttpResponse(httpResponse);
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		super.setHttpResponse(httpResponse);
		this.data = httpResponse.getRspData();
	}

}
