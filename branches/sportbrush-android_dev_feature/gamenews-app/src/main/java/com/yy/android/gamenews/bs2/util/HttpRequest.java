package com.yy.android.gamenews.bs2.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpRequest {
	public static class Method {
		public final static String PUT = "PUT";
		public final static String GET = "GET";
		public final static String POST = "POST";
		public final static String DELETE = "DELETE";
	}

	public final static String HOST = "Host";
	public final static String DATE = "Date";
	public final static String AUTHORIZATION = "Authorization";
	public final static String CONTENT_LENGTH = "Content-Length";
	public final static String CONTENT_TYPE = "Content-Type";
	public final static String CONNECTION = "Connection";
	public final static String RANGE = "Range";

	public final static String EXPIRE_TIME = "x-bs2-expiry-date";

	String method;
	String uri;
	String cmdline;

	public String getCmdline() {
		return cmdline;
	}

	public void setCmdline(String cmdline) {
		this.cmdline = cmdline;
	}

	Map<String, String> queryStringMap = new HashMap<String, String>();
	Map<String, String> headers = new HashMap<String, String>();

	public void clear() {
		method = "";
		uri = "";
		queryStringMap.clear();
		headers.clear();
		cmdline = null;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void addQueryString(String k, String v) {
		queryStringMap.put(k, v);
	}

	public void addHeader(String k, String v) {
		headers.put(k, v);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String> getQueryStringMap() {
		return queryStringMap;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setIsKeepAlive(boolean isset) {
		if (headers.containsKey(HttpRequest.CONNECTION)) {
			headers.remove(HttpRequest.CONNECTION);
		}
		if (isset) {
			this.addHeader(HttpRequest.CONNECTION, "keep-alive");
		} else {
			this.addHeader(HttpRequest.CONNECTION, "close");
		}
	}

	public String toString() {
		if (!this.headers.containsKey(HttpRequest.CONNECTION)) {
			this.setIsKeepAlive(true);
		}

		StringBuffer sb = new StringBuffer();
		Set<String> key;

		// /查询字符串
		boolean first = true;
		key = queryStringMap.keySet();
		for (Iterator<String> it = key.iterator(); it.hasNext();) {
			String k = it.next();
			String v = queryStringMap.get(k);
			if (!first) {
				sb.append('&');
			}
			if (v == "") {
				sb.append(k);
			} else {
				sb.append(k).append('=').append(v);
			}
			first = false;
		}

		String queryString = sb.toString();// URLEncoder.encode(sb.toString());

		// / headers
		if (!headers.containsKey(HttpRequest.CONTENT_LENGTH)) {
			this.addHeader(HttpRequest.CONTENT_LENGTH, "0");
		}
		sb.setLength(0);
		key = headers.keySet();
		for (Iterator<String> it = key.iterator(); it.hasNext();) {
			String k = it.next();
			String v = headers.get(k);
			sb.append(k).append(": ").append(v).append("\r\n");
		}
		String headerString = sb.toString();

		sb.setLength(0);
		if (this.cmdline != null && !this.cmdline.isEmpty()) {
			sb.append(method).append(" /").append(this.cmdline);
		} else {
			sb.append(method).append(" /").append(uri);
			if (!queryString.isEmpty()) {
				sb.append("?").append(queryString);
			}
		}
		sb.append(" HTTP/1.1\r\n");
		sb.append(headerString);
		sb.append("\r\n");
		return sb.toString();
	}

}
