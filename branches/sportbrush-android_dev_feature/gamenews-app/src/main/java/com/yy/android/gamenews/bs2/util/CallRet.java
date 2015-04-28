package com.yy.android.gamenews.bs2.util;

import java.util.Iterator;
import java.util.Set;

public class CallRet {
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	int code;
	String reason;
	HttpResponse httpResponse;

	public CallRet() {
	}

	public CallRet(HttpResponse httpResponse) {
		this.setHttpResponse(httpResponse);
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
		this.code = this.httpResponse.getRspCode();
		this.reason = this.httpResponse.getReason();
	}

	protected void setCode(int code) {
		this.code = code;
	}

	protected void setReason(String reason) {
		this.reason = reason;
	}

	public int getCode() {
		return code;
	}

	public String getReason() {
		return reason;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(1000);
		sb.append(httpResponse.getStatusLine()).append("\r\n");
		Set<String> key = httpResponse.getHeaders().keySet();
		for (Iterator<String> it = key.iterator(); it.hasNext();) {
			String k = it.next();
			String v = httpResponse.getHeaders().get(k);
			sb.append(k).append(": ").append(v).append("\r\n");
		}
		sb.append("\r\n");
		sb.append(httpResponse.getRspDataString());
		return sb.toString();
	}
}
