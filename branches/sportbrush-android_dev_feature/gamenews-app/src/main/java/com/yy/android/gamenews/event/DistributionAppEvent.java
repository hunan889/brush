package com.yy.android.gamenews.event;

import java.util.Map;

public class DistributionAppEvent {

	private Map<Integer, Integer> installAppMap;
	private boolean needRefresh;

	public DistributionAppEvent() {
		super();
	}
	
	public boolean isNeedRefresh() {
		return needRefresh;
	}

	public void setNeedRefresh(boolean needRefresh) {
		this.needRefresh = needRefresh;
	}

	public Map<Integer, Integer> getInstallAppMap() {
		return installAppMap;
	}

	public void setInstallAppMap(Map<Integer, Integer> installAppMap) {
		this.installAppMap = installAppMap;
	}
	
	
}
