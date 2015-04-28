package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;

import com.duowan.Comm.ECommAppType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.view.ActionBar;

public class MainTabFactory {

	public static final int TAB_COUNT_GAMENEWS = 4;
	public static final int TAB_COUNT_SPORT = 3;
	public static final int TAB_COUNT_CAR = 3;

	public static int getTabCount() {
		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
			return TAB_COUNT_SPORT;
		} else if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_CARBRUSH)) {
			return TAB_COUNT_CAR;
		} else if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
			return TAB_COUNT_GAMENEWS;
		}

		return 0;
	}

	public static MainTab getTab(int index, MainActivity context,
			ActionBar actionBar, Bundle savedInstance) {
		MainTab tab = null;

		if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
			return getTabForSportbrush(index, context, actionBar, savedInstance);
		} else if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_CARBRUSH)) {
			return getTabForCarbrush(index, context, actionBar, savedInstance);
		} else if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
			return getTabForGamenews(index, context, actionBar, savedInstance);
		}

		return tab;
	}

	private static MainTab getTabForSportbrush(int index, MainActivity context,
			ActionBar actionBar, Bundle savedInstance) {
		MainTab tab = null;
		switch (index) {
		case 0: {
			tab = new MainTab1(context, actionBar, savedInstance);
			break;
		}
		case 1: {
			tab = new MainTab2Sportbrush(context, actionBar, savedInstance);
			break;
		}
		case 2: {
			tab = new MainTab3SportBrush(context, actionBar, savedInstance);
			break;
		}
		}
		return tab;
	}

	private static MainTab getTabForGamenews(int index, MainActivity context,
			ActionBar actionBar, Bundle savedInstance) {
		MainTab tab = null;
		switch (index) {
		case 0: {
			tab = new MainTab1Gamenews(context, actionBar, savedInstance);
			break;
		}
		case 1: {
			tab = new MainTab2Gamenews(context, actionBar, savedInstance);
			break;
		}
		case 2: {
			tab = new MainTab3Gamenews(context, actionBar, savedInstance);
			break;
		}
		/*
		 * modify by yellow 2015-03-05
		 * 暂时去掉社区
		 * 
		 * case 3: { tab = new MainTab4Gamenews(context, actionBar,
		 * savedInstance); break; }
		 */
		case 3: {
			tab = new MainTab5Gamenews(context, actionBar, savedInstance);
			break;
		}
		}
		return tab;
	}

	private static MainTab getTabForCarbrush(int index, MainActivity context,
			ActionBar actionBar, Bundle savedInstance) {
		MainTab tab = null;
		switch (index) {
		case 0: {
			tab = new MainTab1(context, actionBar, savedInstance);
			break;
		}
		case 1: {
			tab = new MainTab2Carbrush(context, actionBar, savedInstance);
			break;
		}
		case 2: {
			break;
		}
		}
		return tab;
	}

}
