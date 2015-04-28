package com.duowan.gamenews.bean;

import java.io.Serializable;

public class BrandListItemObject implements Serializable {

	private static final long serialVersionUID = -429398704226275604L;

	private int type;
	private Object objectOne;
	private Object objectTwo;
	private Object objectThree;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getObjectOne() {
		return objectOne;
	}

	public void setObjectOne(Object objectOne) {
		this.objectOne = objectOne;
	}

	public Object getObjectTwo() {
		return objectTwo;
	}

	public void setObjectTwo(Object objectTwo) {
		this.objectTwo = objectTwo;
	}

	public Object getObjectThree() {
		return objectThree;
	}

	public void setObjectThree(Object objectThree) {
		this.objectThree = objectThree;
	}

}
