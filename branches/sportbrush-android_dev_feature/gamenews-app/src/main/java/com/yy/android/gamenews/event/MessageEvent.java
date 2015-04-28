package com.yy.android.gamenews.event;

public class MessageEvent {
	
	public static final boolean STATUS_SUCESS = true;
	
	private boolean needUpdate;
	
	private boolean networkChangeStatus;

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean mStatus) {
		this.needUpdate = mStatus;
	}

	public boolean isNetworkChangeStatus() {
		return networkChangeStatus;
	}

	public void setNetworkChangeStatus(boolean mNetworkStatus) {
		this.networkChangeStatus = mNetworkStatus;
	} 
	
}
