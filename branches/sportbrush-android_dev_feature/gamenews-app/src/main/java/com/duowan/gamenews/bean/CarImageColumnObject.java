package com.duowan.gamenews.bean;

import java.io.Serializable;

public class CarImageColumnObject implements Serializable {

	private static final long serialVersionUID = 3684271388468110503L;
	
	private int type;
	private Object object;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
