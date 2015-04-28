package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.event.AlarmSchedChangedEvent;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.AlarmUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.thread.BackgroundTask;

import de.greenrobot.event.EventBus;

public class AlarmListFragment extends BaseListFragment<Object> {

	private FragmentActivity mActivity;
	private GameListAdapter<Object> mAdapter;
	private Preference mPref;
	
	public AlarmListFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mActivity = getActivity();
		mPref = Preference.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(mAdapter != null){
			requestData(RefreshType._REFRESH_TYPE_REFRESH);
		}
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);
		return parentView;
	}

	public void onEvent(AlarmSchedChangedEvent event) {
		if(event != null && event.getRaceInfo() != null){
			requestData(RefreshType._REFRESH_TYPE_REFRESH);
		}
	}

	private class AlarmLoadingTask extends
			BackgroundTask<Void, Void, ArrayList<Object>> {
		@Override
		protected ArrayList<Object> doInBackground(Void... params) {

			AlarmUtil.ensureAlarms(mActivity);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}
	
	@Override
	protected boolean isRefreshable() {
		return true;
	}

	@Override
	protected boolean isRefreshableHead() {
		return false;
	}

	@Override
	protected boolean isRefreshableLoad() {
		return false;
	}
	
	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected void requestData(int refreType) {
		if (mAdapter != null
				&& mAdapter.getDataSource() != null) {
			ArrayList<Object> dataSource = mAdapter.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}
		new AlarmLoadingTask().execute();
	}

	@Override
	protected ImageAdapter<Object> initAdapter() {
		mAdapter = new GameListAdapter<Object>(mActivity);
		mAdapter.setIsAlarm(true);
		return mAdapter;
	}
}
