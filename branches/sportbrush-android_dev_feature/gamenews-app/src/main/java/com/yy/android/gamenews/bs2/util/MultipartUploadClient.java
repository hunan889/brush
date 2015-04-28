package com.yy.android.gamenews.bs2.util;

import java.util.Calendar;

public class MultipartUploadClient extends BaseClient {
	long partNumber;
	String fileName;
	String mime;
	MulInitRet initRet = new MulInitRet();

	public long getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(long partNumber) {
		this.partNumber = partNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	protected MultipartUploadClient(AppInfo appInfo) {
		super(appInfo);
		this.fullHost = this.appInfo.getBucket() + "." + Config.UL_HOST;
	}

	public MulInitRet getInitRet() {
		return initRet;
	}

	public void setInitRet(MulInitRet initRet) {
		this.initRet = initRet;
	}

	public MulInitRet multipartUploadInit(String fileName, String mime)
			throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.POST);
		httpHeader.addQueryString("uploads", "");
		httpHeader.addHeader(HttpRequest.HOST, this.fullHost);
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.POST, fileName, expires,
				appInfo.getBucket(), appInfo.getAccessKey(),
				appInfo.getAccessSecret()));
		if (mime != "") {
			httpHeader.addHeader(HttpRequest.CONTENT_TYPE, mime);
		}

		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		System.out.println("req ip :\r\n" + this.hostIp);
		socket.getOutputStream().write(headerString.getBytes());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		initRet.setHttpResponse(rsp);
		this.partNumber = 0;
		this.fileName = fileName;
		this.mime = mime;
		if (initRet.getCode() != 200) {
			return initRet;
		}
		if (this.fullHost.compareTo(initRet.getZoneHost()) != 0) {
			this.fullHost = initRet.getZoneHost();
			this.hostIp = "";
			reConnect();
		}
		return initRet;
	}

	public CallRet multipartUploadPart(byte[] data, int dataLen)
			throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(this.fileName);
		httpHeader.setMethod(HttpRequest.Method.PUT);
		httpHeader
				.addQueryString("partnumber", String.valueOf(this.partNumber));
		httpHeader.addQueryString("uploadid", this.initRet.getUploadid());
		httpHeader.addHeader(HttpRequest.HOST, this.initRet.getZoneHost());
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.CONTENT_LENGTH,
				String.valueOf(dataLen));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.PUT, this.fileName, expires,
				appInfo.getBucket(), appInfo.getAccessKey(),
				appInfo.getAccessSecret()));
		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		System.out.println("req ip :\r\n" + this.hostIp);
		socket.getOutputStream().write(headerString.getBytes());
		socket.getOutputStream().write(data, 0, dataLen);
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		if (rsp.getRspCode() == 200) {
			// ����ϴ��ɹ���������ֿ�ţ�Ϊ��һ�ηֿ��ϴ���׼��
			this.partNumber++;
		}
		return new CallRet(rsp);
	}

	public CallRet multipartUploadComplete(String mime) throws Exception {
		this.setMime(mime);
		return multipartUploadComplete();

	}

	public CallRet multipartUploadComplete() throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		String data = "{ \"partcount\":" + this.partNumber + "}";
		httpHeader.clear();
		httpHeader.setUri(this.fileName);
		httpHeader.setMethod(HttpRequest.Method.POST);
		httpHeader.addQueryString("uploadid", this.initRet.getUploadid());
		httpHeader.addHeader(HttpRequest.HOST, this.initRet.getZoneHost());
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.POST, fileName, expires,
				appInfo.getBucket(), appInfo.getAccessKey(),
				appInfo.getAccessSecret()));
		if (this.mime != "") {
			httpHeader.addHeader(HttpRequest.CONTENT_TYPE, this.mime);
		}
		httpHeader.addHeader(HttpRequest.CONTENT_LENGTH,
				String.valueOf(data.length()));
		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		socket.getOutputStream().write(data.getBytes());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		return new CallRet(rsp);
	}

	public QueryPartRet queryPartInfo() throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(this.fileName);
		httpHeader.setMethod(HttpRequest.Method.GET);
		httpHeader.addQueryString("getlastpart", "");
		httpHeader.addQueryString("uploadid", this.initRet.getUploadid());
		httpHeader.addHeader(HttpRequest.HOST, this.initRet.getZoneHost());
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.GET, fileName, expires, appInfo.getBucket(),
				appInfo.getAccessKey(), appInfo.getAccessSecret()));
		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		return new QueryPartRet(rsp);

	}

}
