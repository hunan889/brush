package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.duowan.gamenews.RaceInfo;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.AlarmUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

public class AlarmListActivity extends BaseActivity {

	private ListView mListView;
	private GameListAdapter<Object> mAdapter;
	private Preference mPref = Preference.getInstance();
	private ActionBar mActionBar;

	public static void startAlarmListActivity(Context context) {
		Intent intent = new Intent(context, AlarmListActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_alarm_list);
		super.onCreate(savedInstanceState);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mListView = (ListView) findViewById(R.id.list);
		setDataView(mListView);
		mAdapter = new GameListAdapter<Object>(this);
		mAdapter.setIsAlarm(true);
		mListView.setAdapter(mAdapter);
		setEmptyText(getString(R.string.alarm_empty));
		showView(VIEW_TYPE_LOADING);
		new AlarmLoadingTask().execute();
	}

	private class AlarmLoadingTask extends
			BackgroundTask<Void, Void, ArrayList<Object>> {
		@Override
		protected ArrayList<Object> doInBackground(Void... params) {

			AlarmUtil.ensureAlarms(AlarmListActivity.this);
			List<RaceInfo> alarmList = mPref.getAlarmRaceList();

			if (alarmList == null || alarmList.size() == 0) {
				return null;
			}
			RaceInfo[] infoArray = new RaceInfo[alarmList.size()];
			alarmList.toArray(infoArray);
			Arrays.sort(infoArray, new Comparator<RaceInfo>() {
				@Override
				public int compare(RaceInfo lhs, RaceInfo rhs) {
					int leftLiveTime = lhs.liveTime;
					int rightLiveTime = rhs.liveTime;

					return leftLiveTime - rightLiveTime;
				}
			});

			ArrayList<Object> dataList = new ArrayList<Object>();
			Calendar currCal = Calendar.getInstance();
			Calendar prevCal = Calendar.getInstance();
			for (int i = 0; i < infoArray.length; i++) {
				RaceInfo info = infoArray[i];
				long infoTime = (long) info.getLiveTime() * 1000;
				currCal.setTimeInMillis(infoTime);
				if (i == 0) {
					dataList.add(getLabelString(currCal));
					dataList.add(info);
					prevCal.setTimeInMillis(currCal.getTimeInMillis());
				} else {
					int currDate = currCal.get(Calendar.DAY_OF_YEAR);
					int prevDate = prevCal.get(Calendar.DAY_OF_YEAR);
					if (currDate != prevDate) {
						dataList.add(getLabelString(currCal));
					}
					dataList.add(info);
					prevCal.setTimeInMillis(currCal.getTimeInMillis());
				}
			}

			return dataList;
		}

		private String getLabelString(Calendar calendar) {
			Calendar today = Calendar.getInstance();
			String todayString = TimeUtil.parseTimeToYMD(today.getTime());
			String date = TimeUtil.parseTimeToYMD(calendar.getTime());

			String week = TimeUtil.getWeekString(calendar
					.get(Calendar.DAY_OF_WEEK));

			String retString = "";
			if (todayString.equals(date)) {
				retString = date + " 今天";
			} else {
				retString = date + " 周" + week;
			}

			return retString;
		}

		@Override
		protected void onPostExecute(ArrayList<Object> result) {
			if (result == null || result.size() == 0) {
				showView(VIEW_TYPE_EMPTY);
				return;
			}

			showView(VIEW_TYPE_DATA);
			mAdapter.setDataSource(result);
			super.onPostExecute(result);
		}
	}
}
