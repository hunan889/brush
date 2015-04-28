package com.duowan.android.base.event;

/**
 * 
 * UniPacket在解析时错误的事件传递
 * 
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午3:32:21
 */
public class UniPacketErrorEvent extends BaseEvent {

	public UniPacketErrorEvent(String msg) {
		this.msg = msg;
	}
}
