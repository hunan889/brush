package com.duowan.android.base.event;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-20 下午3:10:26
 */
public class GuidEvent extends BaseEvent {

	public byte[] vGuid;

	public GuidEvent(byte[] vGuid) {
		super();
		this.vGuid = vGuid;
	}
}
