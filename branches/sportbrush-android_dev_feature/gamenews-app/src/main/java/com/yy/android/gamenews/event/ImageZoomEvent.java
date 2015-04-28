package com.yy.android.gamenews.event;

import android.view.MotionEvent;

public class ImageZoomEvent {
	private MotionEvent motionEvent;

	public MotionEvent getMotionEvent() {
		return motionEvent;
	}

	public void setMotionEvent(MotionEvent motionEvent) {
		this.motionEvent = motionEvent;
	}

}
