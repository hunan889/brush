package com.yy.android.gamenews.jcewrapper;

import com.duowan.taf.jce.JceStruct;
/**
 * 为远程JCE对象提供本地封装，以支持JCE对象所不支持的继承
 * @author liuchaoqun
 *
 * @param <E>JCE RSP对象
 */
public abstract class GetDataRsp<E extends JceStruct> {

	private E object;

	public E getObject() {
		return object;
	}

	public void setObject(E object) {
		this.object = object;
	}

	public abstract E clone();
}
