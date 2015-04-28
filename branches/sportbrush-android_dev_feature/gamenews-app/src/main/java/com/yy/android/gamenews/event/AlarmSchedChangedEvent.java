package com.yy.android.gamenews.event;

import com.duowan.gamenews.RaceInfo;

public class AlarmSchedChangedEvent {

	public RaceInfo raceInfo;
	public boolean mIsAlarm;
	public boolean isAdd;

	public RaceInfo getRaceInfo() {
		return raceInfo;
	}

	public void setRaceInfo(RaceInfo raceInfo) {
		this.raceInfo = raceInfo;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean ismIsAlarm() {
		return mIsAlarm;
	}

	public void setmIsAlarm(boolean mIsAlarm) {
		this.mIsAlarm = mIsAlarm;
	}
	
}
