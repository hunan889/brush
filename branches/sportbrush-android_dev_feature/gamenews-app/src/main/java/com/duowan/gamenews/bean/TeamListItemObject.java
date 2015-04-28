package com.duowan.gamenews.bean;

import java.io.Serializable;

public class TeamListItemObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2781194809445428208L;
	private int type;
	private Object objectOne;
	private Object objectTwo;
	private Object objectThree;
	private boolean flagOne;
	private boolean flagTwo;
	private boolean flagThree;

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

	public boolean isFlagOne() {
		return flagOne;
	}

	public void setFlagOne(boolean flagOne) {
		this.flagOne = flagOne;
	}

	public boolean isFlagTwo() {
		return flagTwo;
	}

	public void setFlagTwo(boolean flagTwo) {
		this.flagTwo = flagTwo;
	}

	public boolean isFlagThree() {
		return flagThree;
	}

	public void setFlagThree(boolean flagThree) {
		this.flagThree = flagThree;
	}
}
