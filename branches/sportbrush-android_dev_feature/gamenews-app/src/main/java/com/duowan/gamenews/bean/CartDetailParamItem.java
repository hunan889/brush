package com.duowan.gamenews.bean;

import java.io.Serializable;

import com.duowan.autonews.SubItemDetail;

public class CartDetailParamItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6995875888021256359L;
	public static final int TYPE_NAME = 1001;
	public static final int TYPE_ITEM = 1002;

	private int type;
	private String name;
	private SubItemDetail detail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SubItemDetail getDetail() {
		return detail;
	}

	public void setDetail(SubItemDetail detail) {
		this.detail = detail;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
