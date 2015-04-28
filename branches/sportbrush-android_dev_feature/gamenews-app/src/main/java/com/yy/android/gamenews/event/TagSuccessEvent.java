package com.yy.android.gamenews.event;

public class TagSuccessEvent {

	private boolean success;//是否发送成功
	private boolean picSize;//图片大小

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isPicSize() {
		return picSize;
	}

	public void setPicSize(boolean picSize) {
		this.picSize = picSize;
	}

	

}
