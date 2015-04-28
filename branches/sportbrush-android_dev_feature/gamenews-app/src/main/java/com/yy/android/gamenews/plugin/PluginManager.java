package com.yy.android.gamenews.plugin;

import android.content.Context;
import android.content.Intent;

import com.yy.android.gamenews.plugin.cartport.CartportActivity;
import com.yy.android.gamenews.plugin.schetable.GameListActivity;
import com.yy.android.gamenews.util.StatsUtil;

public class PluginManager {
	public static final int PLUGIN_CARPORT = 1001;
	public static final int PLUGIN_SCHEDULDE_TABLE = 1002;

	public static void startPlugin(Context context, int type) {
		switch (type) {
		case PLUGIN_CARPORT: {
			StatsUtil.statsReport(context, "into_carport", "desc", "into_carport");
			StatsUtil.statsReportByHiido("into_carport", "into_carport");
			StatsUtil.statsReportByMta(context, "into_carport", "into_carport");
			CartportActivity.startCarportActivity(context);
			break;
		}
		case PLUGIN_SCHEDULDE_TABLE: {
			StatsUtil.statsReport(context, "into_race", "desc", "into_race");
			StatsUtil.statsReportByHiido("into_race", "into_race");
			StatsUtil.statsReportByMta(context, "into_race", "into_race");
			Intent intent = new Intent(context, GameListActivity.class);
			context.startActivity(intent);
			break;
		}
		}
	}
}
