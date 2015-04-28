package com.yy.android.gamenews.bs2.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BaseClient {
	AppInfo appInfo;

	String hostIp;
	Socket socket;
	String fullHost;

	long lstConnTime = 0L;
	SimpleDateFormat dataformat = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.CHINA);

	HttpRequest httpHeader = new HttpRequest();

	public BaseClient(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	protected String getHostIp() throws UnknownHostException {
		this.lstConnTime = System.currentTimeMillis();
		InetAddress inetHost = InetAddress.getByName(this.fullHost);
		String hostName = inetHost.getHostName();
		// LogCat.d("The host name was: " + hostName);
		// LogCat.d("The hosts IP address is: " + inetHost.getHostAddress());
		return inetHost.getHostAddress();
	}

	protected String getConnHost() throws UnknownHostException {
		if (hostIp != "") {
			long now = System.currentTimeMillis();
			if (now - this.lstConnTime > 50000) {
				try {
					String retIp = getHostIp();
					return retIp;
				} catch (Exception e) {
					// LogCat.d(e.toString());
				}
				return hostIp;
			} else {
				return hostIp;
			}
		}
		return getHostIp();
	}

	/**
	 * 无论连接是否已经建立，都重新建立一次连接
	 * 
	 * @throws Exception
	 */
	public void reConnect() throws Exception {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {

			}
		}
		String ip = "";
		for (int i = 0; i < 3; i++) {
			try {
				ip = getConnHost();
				socket = new Socket(ip, Config.UD_PORT);
				socket.setKeepAlive(true);
				break;
			} catch (Exception e) {
				if (i == 2) {
					throw e;
				}
			}
		}
		// 连接成功，记录当前的hostIp
		this.hostIp = ip;
	}

	/**
	 * 如果socket已经连接，则不再重连，否则重新连接
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception {
		if (socket == null || !socket.isConnected()) {
			reConnect();
		}
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
			socket = null;
		}
	}

}
