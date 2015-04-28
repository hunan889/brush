package com.duowan.android.base.event;

import com.android.volley.VolleyError;

/**
 * 
 * @see com.duowan.android.base.net.VolleyClient#newRequestQueue 请求失败时事件
 * 
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午3:36:53
 */
public class VolleyErrorEvent extends BaseEvent {

	public VolleyError error;

	public VolleyErrorEvent(VolleyError error) {
		this.error = error;
	}
}
