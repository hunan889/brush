package com.yy.android.gamenews.bs2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * 支持单次域名解释ip保留和长连接
 * 
 * @author tesla
 * 
 */

public class OnceUploadClient extends BaseClient {

	protected OnceUploadClient(AppInfo appInfo) {
		super(appInfo);
		this.fullHost = this.appInfo.getBucket() + "." + Config.UL_HOST;
	}

	/**
	 * 单次上传
	 * 
	 * @param binaryData
	 *            数据
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet onceUpload(byte[] binaryData, int dataLen) throws Exception {
		return onceUpload(binaryData, dataLen, "");
	}

	/**
	 * 单次上传
	 * 
	 * @param binaryData
	 *            数据
	 * @param mime
	 *            数据类型，要符合http数据类型格式，如application/octet-stream
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet onceUpload(byte[] binaryData, int dataLen, String mime)
			throws Exception {
		return onceUpload(binaryData, dataLen, mime, "");
	}

	/**
	 * 
	 * @param binaryData
	 *            数据
	 * @param mime
	 *            数据类型，要符合http数据类型格式，如application/octet-stream
	 * @param fileName
	 *            文件名
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet onceUpload(byte[] binaryData, int dataLen, String mime,
			String fileName) throws Exception {
		return onceUpload(binaryData, dataLen, mime, fileName, -1);
	}

	/**
	 * 
	 * @param binaryData
	 *            数据
	 * @param mime
	 *            数据类型，要符合http数据类型格式，如application/octet-stream
	 * @param fileName
	 *            文件名
	 * @param expireDay
	 *            有效天数，如100则表示有效时间是一百天
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet onceUpload(byte[] binaryData, int dataLen, String mime,
			String fileName, int expireDay) throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.PUT);
		httpHeader.addHeader(HttpRequest.HOST, this.fullHost);
		// conn.setConnectTimeout(30 * 1000);
		// conn.setReadTimeout(30 * 1000);
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.PUT, fileName, expires, appInfo.getBucket(),
				appInfo.getAccessKey(), appInfo.getAccessSecret()));
		httpHeader.addHeader(HttpRequest.CONTENT_LENGTH,
				String.valueOf(dataLen));
		if (mime != "") {
			httpHeader.addHeader(HttpRequest.CONTENT_TYPE, mime);
		}
		if (expireDay > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, expireDay);
			httpHeader.addHeader(HttpRequest.EXPIRE_TIME,
					dataformat.format(calendar.getTime()));
		}
		String headerString = httpHeader.toString();
		// LogCat.d("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		socket.getOutputStream().write(binaryData, 0, dataLen);
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		OnceRet ret = new OnceRet();
		ret.setHttpResponse(rsp);
		ret.setHash(rsp.getHeader("etag"));
		// LogCat.d("response header : " + rsp.getHeader("etag"));
		return ret;
	}

	/**
	 * 
	 * @param file
	 *            数据
	 * @param mime
	 *            数据类型，要符合http数据类型格式，如application/octet-stream
	 * @param fileName
	 *            文件名
	 * @param expireDay
	 *            有效天数，如100则表示有效时间是一百天
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet UploadFile(File file, long dataLen, String mime,
			String fileName, int expireDay) throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.PUT);
		httpHeader.addHeader(HttpRequest.HOST, this.fullHost);
		// conn.setConnectTimeout(30 * 1000);
		// conn.setReadTimeout(30 * 1000);
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.PUT, fileName, expires, appInfo.getBucket(),
				appInfo.getAccessKey(), appInfo.getAccessSecret()));
		httpHeader.addHeader(HttpRequest.CONTENT_LENGTH,
				String.valueOf(dataLen));
		if (mime != "") {
			httpHeader.addHeader(HttpRequest.CONTENT_TYPE, mime);
		}
		if (expireDay > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, expireDay);
			httpHeader.addHeader(HttpRequest.EXPIRE_TIME,
					dataformat.format(calendar.getTime()));
		}
		String headerString = httpHeader.toString();
		// LogCat.d("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		fileToOut(file, socket.getOutputStream());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		OnceRet ret = new OnceRet();
		ret.setHttpResponse(rsp);
		ret.setHash(rsp.getHeader("etag"));
		// LogCat.d("response header : " + rsp.getHeader("etag"));
		return ret;
	}

	/**
	 * 
	 * @param is
	 *            数据
	 * @param mime
	 *            数据类型，要符合http数据类型格式，如application/octet-stream
	 * @param fileName
	 *            文件名
	 * @param expireDay
	 *            有效天数，如100则表示有效时间是一百天
	 * @return 单次上传应答对象，如果没有指定了文件名，则应答对象包含文件名信息
	 * @throws Exception
	 */
	public OnceRet UploadStream(InputStream is, long dataLen, String mime,
			String fileName, int expireDay) throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.PUT);
		httpHeader.addHeader(HttpRequest.HOST, this.fullHost);
		// conn.setConnectTimeout(30 * 1000);
		// conn.setReadTimeout(30 * 1000);
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.PUT, fileName, expires, appInfo.getBucket(),
				appInfo.getAccessKey(), appInfo.getAccessSecret()));
		httpHeader.addHeader(HttpRequest.CONTENT_LENGTH,
				String.valueOf(dataLen));
		if (mime != "") {
			httpHeader.addHeader(HttpRequest.CONTENT_TYPE, mime);
		}
		if (expireDay > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, expireDay);
			httpHeader.addHeader(HttpRequest.EXPIRE_TIME,
					dataformat.format(calendar.getTime()));
		}
		String headerString = httpHeader.toString();
		// LogCat.d("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		// socket.getOutputStream().write(binaryData, 0, dataLen);
		inToOut(is, socket.getOutputStream());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		OnceRet ret = new OnceRet();
		ret.setHttpResponse(rsp);
		ret.setHash(rsp.getHeader("etag"));
		// LogCat.d("response header : " + rsp.getHeader("etag"));
		return ret;
	}

	public void fileToOut(File file, OutputStream out) {
		if (!file.exists()) {
			return;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			inToOut(is, out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void inToOut(InputStream is, OutputStream out) {
		try {
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) != -1) {
				out.write(bytes, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
