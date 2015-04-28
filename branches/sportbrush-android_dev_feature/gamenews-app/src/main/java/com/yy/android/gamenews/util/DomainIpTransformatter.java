package com.yy.android.gamenews.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.yy.android.gamenews.Constants;

public class DomainIpTransformatter {
	private HandlerThread mThread;
	private NonUiHandler mNonUiHandler;
	private UiHandler mUiHandler = new UiHandler();
	private static final int MSG_GET_IP = 1001;
	private static final int MSG_UPDATE_IP = 1002;

	private static final int MSG_GET_IP_MAP = 1003;
	private static final int MSG_UPDATE_IP_MAP = 1004;
	private Map<String, String> mIpMap;
	private Preference mPref;

	private static DomainIpTransformatter mInstance;

	private DomainIpTransformatter() {
		mThread = new HandlerThread("HostToIpThread");
		mThread.start();
		mNonUiHandler = new NonUiHandler(mThread.getLooper());

		mPref = Preference.getInstance();
		mIpMap = mPref.getIpMap();
	}

	public static DomainIpTransformatter getInstance() {
		if (mInstance == null) {
			synchronized (DomainIpTransformatter.class) {
				if (mInstance == null) {
					mInstance = new DomainIpTransformatter();
				}
			}
		}
		return mInstance;
	}

	public void prepareDefault() {
		Set<String> domainList = null;
		if (mIpMap != null) {
			domainList = mIpMap.keySet();
		}

		if (domainList == null) {
			domainList = getDefaultLookupSet();
		}
		prepare(domainList);
	}

	private Set<String> getDefaultLookupSet() {
		Set<String> domainList = new HashSet<String>();
		domainList.add(Constants.BS2_IMG_HOST);

		return domainList;
	}

	public void prepare(Set<String> domainList) {
		if (domainList == null) {
			return;
		}
		fetchIpInfoMap(domainList);
	}

	/**
	 * 将给定的url中的域名转换为对应的ip地址，如果ip地址暂未保存，则通过后台线程获取，获取成功后，则下次再次访问时即可直接返回
	 * 
	 * @param url
	 * @return
	 */
	public String domainToIp(String url) {
		String domain = Util.getDomainName(url);
		if (Util.isIpUrl(domain)) { // 如果已经是ip地址，则直接返回原址;
			return url;
		}

		String ip = mIpMap.get(domain);

		if (TextUtils.isEmpty(ip)) {

			fetchIpInfo(domain);
		} else {
			url = url.replace(domain, ip);
		}

		return url;
	}

	private void fetchIpInfo(String domain) {
		if (!mNonUiHandler.hasMessages(MSG_GET_IP, domain)) {
			Message msg = mNonUiHandler.obtainMessage(MSG_GET_IP, domain);
			mNonUiHandler.sendMessage(msg);
		}
	}

	private void fetchIpInfoMap(Set<String> domainList) {
		if (!mNonUiHandler.hasMessages(MSG_GET_IP_MAP, domainList)) {
			Message msg = mNonUiHandler.obtainMessage(MSG_GET_IP_MAP,
					domainList);
			mNonUiHandler.sendMessage(msg);
		}
	}

	private class NonUiHandler extends Handler {

		public NonUiHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_GET_IP: {
				String domain = (String) msg.obj;
				String ip = Util.ensureIp(domain);

				DomainToIP object = new DomainToIP();
				object.domain = domain;
				object.ip = ip;

				if (!mUiHandler.hasMessages(MSG_UPDATE_IP, object)) {
					Message uiMsg = mUiHandler.obtainMessage(MSG_UPDATE_IP,
							object);
					mUiHandler.sendMessage(uiMsg);

				}
				break;
			}
			case MSG_GET_IP_MAP: {
				@SuppressWarnings("unchecked")
				Set<String> domainList = (Set<String>) msg.obj;
				Map<String, String> map = Util.ensureIpMap(domainList);

				if (!mUiHandler.hasMessages(MSG_UPDATE_IP_MAP, map)) {
					Message uiMsg = mUiHandler.obtainMessage(MSG_UPDATE_IP_MAP,
							map);
					mUiHandler.sendMessage(uiMsg);

				}
				break;
			}
			}

		}
	}

	private class UiHandler extends Handler {

		public UiHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_IP:
				DomainToIP object = (DomainToIP) msg.obj;
				mIpMap.put(object.domain, object.ip);

				mPref.setIpMap(mIpMap);
				break;
			case MSG_UPDATE_IP_MAP: {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) msg.obj;
				mIpMap = map;
				mPref.setIpMap(mIpMap);
				break;
			}
			default:
				break;
			}
		}
	}

	private static class DomainToIP {
		String domain;
		String ip;
	}
}
