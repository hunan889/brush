package com.duowan.gamenews.bean;

import java.util.Date;

public class GameDate {

	public static final int FLAG_SHOW_HAS_GAME = 1;
	public static final int FLAG_IMPORTANT = 2;
	private int flag;
	private String dayOfMonth;
	private String dayOfWeek;
	private Date date;
	private boolean isSelected;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
