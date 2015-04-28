package com.duowan.gamenews.bean;

import java.io.Serializable;

public class CarImageColumnOneItem implements Serializable {

	private static final long serialVersionUID = 6211071710202897429L;

	private String title;
	private int nums;
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNums() {
		return nums;
	}

	public void setNums(int nums) {
		this.nums = nums;
	}

}
