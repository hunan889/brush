package com.yy.android.gamenews.bs2.util;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

public class DownloadClient extends BaseClient {

	public DownloadClient(AppInfo appInfo) {
		super(appInfo);
		this.fullHost = this.appInfo.getBucket() + "." + Config.DL_HOST;
	}

	public DownloadRet download(String fileName) throws Exception {
		return download(fileName, -1, -1);
	}

	public DownloadRet download(String fileName, long rangeBeginPos,
			long rangeEndPos) throws Exception {
		connect();
		String expires = String.valueOf(Calendar.getInstance()
				.getTimeInMillis() / 1000 + 360000);
		httpHeader.clear();
		httpHeader.setUri(fileName);
		httpHeader.setMethod(HttpRequest.Method.GET);
		httpHeader.addHeader(HttpRequest.HOST, this.fullHost);
		httpHeader.addHeader(HttpRequest.DATE,
				dataformat.format(Calendar.getInstance().getTime()));
		httpHeader.addHeader(HttpRequest.AUTHORIZATION, Util.getAuthorization(
				HttpRequest.Method.GET, fileName, expires, appInfo.getBucket(),
				appInfo.getAccessKey(), appInfo.getAccessSecret()));
		if (rangeBeginPos != rangeEndPos) {
			String range = String.valueOf(rangeBeginPos) + "-"
					+ String.valueOf(rangeEndPos);
			httpHeader.addHeader(HttpRequest.RANGE, range);
		}
		String headerString = httpHeader.toString();
		System.out.println("req header:\r\n" + headerString);
		socket.getOutputStream().write(headerString.getBytes());
		HttpResponse rsp = new HttpResponse();
		rsp.readResponse(socket.getInputStream());
		if (rsp.getRspCode() != 302) {
			// / 非跳转，直接返回应答
			return new DownloadRet(rsp);
		}
		System.out.println("302 redirect:");
		System.out.println(rsp.getHeaders().toString());
		// / 302则跳转
		String location = rsp.getHeader(HttpResponse.LOCATION);
		if (location.toLowerCase().startsWith("http://")) {
			location = location.substring(7);
		}
		String[] part = location.split("/");
		String redirectHost = part[0];
		String cmdline = part[1];
		InetAddress inetHost = InetAddress.getByName(redirectHost);
		String redirectIp = inetHost.getHostAddress();
		Socket redirectSocket = null;
		for (int i = 0; i < 3; i++) {
			try {
				redirectSocket = new Socket(redirectIp, Config.UD_PORT);
				redirectSocket.setKeepAlive(false);
				break;
			} catch (Exception e) {
				if (i == 2) {
					throw e;
				}
			}
		}
		httpHeader.setCmdline(cmdline);
		httpHeader.addHeader(HttpRequest.HOST, redirectHost);
		headerString = httpHeader.toString();
		System.out.println("302 redirect header:\r\n" + headerString);
		redirectSocket.getOutputStream().write(headerString.getBytes());
		rsp = new HttpResponse();
		rsp.readResponse(redirectSocket.getInputStream());
		return new DownloadRet(rsp);

	}

}
