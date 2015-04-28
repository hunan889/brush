package com.yy.android.gamenews.bs2.util;

import java.util.Calendar;

public class ObjectOptClient extends BaseClient {

	protected ObjectOptClient(AppInfo appInfo) {
		super(appInfo);
		this.fullHost = this.appInfo.getBucket() + "." + Config.BS2_HOST;
	}
	
	
	public CallRet delete(String fileName) throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.DELETE);
		httpHeader.addHeader(HttpRequest.HOST,  this.fullHost);
		httpHeader.addHeader(HttpRequest.DATE, dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(HttpRequest.Method.DELETE, fileName, expires, appInfo.getBucket(), appInfo.getAccessKey(), appInfo.getAccessSecret()));   
		
		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());	
		return new CallRet(rsp);
	}

}
