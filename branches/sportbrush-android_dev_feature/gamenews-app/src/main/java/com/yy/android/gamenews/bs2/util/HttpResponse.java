package com.yy.android.gamenews.bs2.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

	// / 应答的标准http头保存时全部是小写
	public final static String LOCATION = "location";

	int rspCode;
	String reason;
	Map<String, String> headers;
	String statusLine;
	String charset;
	byte[] rspData;

	public int getRspCode() {
		return rspCode;
	}

	public void setRspCode(int rspCode) {
		this.rspCode = rspCode;
	}

	public String getReason() {
		return reason;
	}

	public String getHeader(String key) {
		if (this.headers.containsKey(key)) {
			return this.headers.get(key);
		}
		return "";
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	public byte[] getRspData() {
		return rspData;
	}

	public void setRspData(byte[] rspData) {
		this.rspData = rspData;
	}

	public String getRspDataString() {
		String s = null;
		try {
			s = new String(this.rspData, this.charset);
		} catch (UnsupportedEncodingException e) {
		}
		return s;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void readResponse(InputStream in) throws Exception {
		// 读取状态行
		statusLine = readStatusLine(in);
		// LogCat.d("statusLine:" + statusLine);
		// System.out.println("statusLine:" + statusLine);
		int pos = statusLine.indexOf(' ');
		if (pos < 0) {
			throw new Exception("get statusLine error:" + statusLine);
		}
		int pos2 = statusLine.indexOf(' ', pos + 1);
		if (pos < 0) {
			throw new Exception("get statusLine error:" + statusLine);
		}
		rspCode = Integer.valueOf(statusLine.substring(pos + 1, pos2));
		reason = statusLine.substring(pos2 + 1);
		// 消息报头
		headers = readHeaders(in);

		int contentLength = Integer.valueOf(headers.get("content-length"));

		// 可选的响应正文
		rspData = readResponseBody(in, contentLength);

		charset = headers.get("content-type");
		if (charset.matches(".+;charset=.+")) {
			charset = charset.split(";")[1].split("=")[1];
		} else {
			charset = "ISO-8859-1"; // 默认编码
		}
	}

	private static byte[] readResponseBody(InputStream in, int contentLength)
			throws IOException {
		ByteArrayOutputStream buff = new ByteArrayOutputStream(contentLength);

		byte[] m = new byte[4096];
		int count = 0;
		int b = 0;
		while (count < contentLength) {
			b = in.read(m);
			buff.write(m, 0, b);
			count += b;
		}

		return buff.toByteArray();
	}

	private static Map<String, String> readHeaders(InputStream in)
			throws IOException {
		Map<String, String> headers = new HashMap<String, String>();

		String line;

		while (!("".equals(line = readLine(in)))) {
			// LogCat.d("response line : " + line.toString());
			String[] nv = line.split(": "); // 头部字段的名值都是以(冒号+空格)分隔的
			headers.put(nv[0].toLowerCase(), nv[1]);
		}

		return headers;
	}

	private static String readStatusLine(InputStream in) throws IOException {
		return readLine(in);
	}

	/**
	 * 读取以CRLF分隔的一行，返回结果不包含CRLF
	 */
	private static String readLine(InputStream in) throws IOException {
		int b;

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		while ((b = in.read()) != '\r') {
			buff.write(b);
		}

		in.read(); // 读取 LF (\n)
		String line = buff.toString();
		return line;
	}

}
