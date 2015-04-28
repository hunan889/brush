package com.yy.android.gamenews.event;

public class OnCustonListener {

	private int id;
	private boolean intercept;
	private int[] nums;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isIntercept() {
		return intercept;
	}

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	public int[] getNums() {
		return nums;
	}

	public void setNums(int[] nums) {
		this.nums = nums;
	}

}
