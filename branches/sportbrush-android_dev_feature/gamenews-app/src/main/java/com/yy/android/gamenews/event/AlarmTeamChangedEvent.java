package com.yy.android.gamenews.event;

import java.io.Serializable;

import com.duowan.gamenews.Team;

public class AlarmTeamChangedEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6916548075979201110L;
	public static final int TEAM_LIST_FOLLOW_TAG = 1;
	public static final int TEAM_MORE_FOLLOW_TAG = 2;
	private Team team;
	private boolean follow;
	private int tag;
	private boolean isMorePage;

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public boolean isFollow() {
		return follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public boolean isMorePage() {
		return isMorePage;
	}

	public void setMorePage(boolean isMorePage) {
		this.isMorePage = isMorePage;
	}
	
}
