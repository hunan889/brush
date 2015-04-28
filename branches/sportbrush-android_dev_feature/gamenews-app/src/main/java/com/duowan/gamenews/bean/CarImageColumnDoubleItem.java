package com.duowan.gamenews.bean;

import java.io.Serializable;

import com.duowan.autonews.CarPicInfo;

public class CarImageColumnDoubleItem implements Serializable {

	private static final long serialVersionUID = 6211071710202897429L;

	private CarPicInfo picInfoOne;
	private CarPicInfo picInfoTwo;
	private int picInfoOneLocation;
	private int picInfoTwoLocation;
	private int keyItem;

	public int getPicInfoOneLocation() {
		return picInfoOneLocation;
	}

	public void setPicInfoOneLocation(int picInfoOneLocation) {
		this.picInfoOneLocation = picInfoOneLocation;
	}

	public int getPicInfoTwoLocation() {
		return picInfoTwoLocation;
	}

	public void setPicInfoTwoLocation(int picInfoTwoLocation) {
		this.picInfoTwoLocation = picInfoTwoLocation;
	}

	public int getKeyItem() {
		return keyItem;
	}

	public void setKeyItem(int keyItem) {
		this.keyItem = keyItem;
	}

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public CarPicInfo getPicInfoOne() {
		return picInfoOne;
	}

	public void setPicInfoOne(CarPicInfo picInfoOne) {
		this.picInfoOne = picInfoOne;
	}

	public CarPicInfo getPicInfoTwo() {
		return picInfoTwo;
	}

	public void setPicInfoTwo(CarPicInfo picInfoTwo) {
		this.picInfoTwo = picInfoTwo;
	}

}
