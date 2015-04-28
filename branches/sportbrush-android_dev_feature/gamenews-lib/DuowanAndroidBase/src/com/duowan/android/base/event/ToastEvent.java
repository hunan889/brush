package com.duowan.android.base.event;

import com.duowan.android.base.event.BaseEvent;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-19 上午10:25:37
 */
public class ToastEvent extends BaseEvent {

	public String msg;

	public ToastEvent(String msg) {
		super();
		this.msg = msg;
	}

}
