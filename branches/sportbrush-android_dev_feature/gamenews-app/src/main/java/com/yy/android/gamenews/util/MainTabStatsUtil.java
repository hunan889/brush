package com.yy.android.gamenews.util;

import android.content.Context;

import com.duowan.Comm.ECommAppType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.util.maintab.MainTab1;
import com.yy.android.gamenews.util.maintab.MainTab2;
import com.yy.android.gamenews.util.maintab.MainTab3;
import com.yy.android.gamenews.util.maintab.MainTab4;
import com.yy.android.gamenews.util.maintab.MainTab5;

import de.greenrobot.event.EventBus;

public class MainTabStatsUtil {

	public static void postStatisEvent(String eventId, String paramKey,
			String paramValue) {
		MainTabEvent event = new MainTabEvent();
		event.setEventId(eventId);
		event.setKey(paramKey);
		event.setValue(paramValue);
		EventBus.getDefault().post(event);
	}

	public static void addchangeTabStatistics(Context context,
			int currentIndex, int targetIndex) {
		if (currentIndex == targetIndex) {
			return;
		}
		String eventId = null;
		if (currentIndex == MainTab1.INDEX) {
			eventId = MainTabEvent.TAB_HEAD_INFO;
		} else if (currentIndex == MainTab2.INDEX) {
			eventId = MainTabEvent.TAB_ORDER_INFO;
		} else if (currentIndex == MainTab3.INDEX) {
			if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
				eventId = MainTabEvent.TAB_SPORTRACE_INFO;
			} else if (Constants
					.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
				eventId = MainTabEvent.TAB_GAMERACE_INFO;
			}
		} else if (currentIndex == MainTab4.INDEX) {
			eventId = MainTabEvent.TAB_COMMUNITY;
		} else if (currentIndex == MainTab5.INDEX) {
			eventId = MainTabEvent.TAB_GIFT;
		}

		String key = null;
		String value = null;
		if (targetIndex == MainTab1.INDEX) {
			key = MainTabEvent.INTO_HAND_INFO;
			value = MainTabEvent.INTO_HAND_INFO_NAME;
		} else if (targetIndex == MainTab2.INDEX) {
			key = MainTabEvent.INTO_ORDER_CHANNEL;
			value = MainTabEvent.INTO_ORDER_CHANNEL_NAME;
		} else if (targetIndex == MainTab3.INDEX) {
			if (Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
				key = MainTabEvent.INTO_SCHETABLE;
				value = MainTabEvent.INTO_SCHETABLE_NAME;
			} else if (Constants
					.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
				key = MainTabEvent.INTO_GAMERACE;
				value = MainTabEvent.INTO_GAMERACE_NAME;
			}
		} else if (targetIndex == MainTab4.INDEX) {
			key = MainTabEvent.INTO_COMMUNITY;
			value = MainTabEvent.INTO_COMMUNITY_NAME;
		} else if (targetIndex == MainTab5.INDEX) {
			key = MainTabEvent.INTO_GIFT;
			value = MainTabEvent.INTO_GIFT_NAME;

			//礼包需要另外上报该事件
			StatsUtil.statsReportAllData(context, "gamenews_gift_event",
					"event_key", "gamenews_gift_event");
		}

		if (eventId != null && key != null && value != null) {
			MainTabEvent event = new MainTabEvent();
			event.setEventId(eventId);
			event.setKey(key);
			event.setValue(value);

			statistics(context, event);
		}
	}

	public static void statistics(Context context, String eventId,
			String paramKey, String paramValue) {
		if (eventId == null) {
			return;
		}
		StatsUtil.statsReportAllData(context, eventId, paramKey, paramValue);
	}

	public static void statistics(Context context, MainTabEvent event) {
		if (event == null) {
			return;
		}
		StatsUtil.statsReportAllData(context, event.getEventId(),
				event.getKey(), event.getValue());
	}
}
