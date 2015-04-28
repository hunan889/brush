package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duowan.gamenews.RaceFlag;
import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.bean.GameDate;
import com.yy.android.gamenews.ui.view.HorizontalListView;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.sportbrush.R;

public class CalendarView extends FrameLayout {

	// private Date mStartDate;
	// private Date mEndDate;
	private int mLength = DEFAULT_CALENDAR_LENGTH;
	private static final int DEFAULT_CALENDAR_LENGTH = 14;

	private TextView mMonth;
	private HorizontalListView mListView;
	private DateListAdapter mAdapter;

	private int mCurrentItem = 0;

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CalendarView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View parent = inflater.inflate(R.layout.calendar, null);

		mMonth = (TextView) parent.findViewById(R.id.tv_month);
		mListView = (HorizontalListView) parent
				.findViewById(R.id.date_listview);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				GameDate oldDate = mAdapter.getItem(mCurrentItem);
				if (oldDate != null) {
					oldDate.setSelected(false);
				}
				mCurrentItem = position;
				GameDate date = mAdapter.getItem(position);
				date.setSelected(true);
				moveToCenter(mCurrentItem);
				mAdapter.notifyDataSetChanged();
				// int pos = mListView.getFirstVisiblePosition() + position;
				// mListView.setSelection(pos / 2);
				if (mOnDateClickListener != null) {
					mOnDateClickListener.onClick(position, date.getDate());
					StatsUtil.statsReportAllData(getContext(), "game_match_date_onclick", "ame_match_date", "game_match_date_onclick");
				}
			}
		});

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(parent, params);

		mAdapter = new DateListAdapter(context);
		mListView.setAdapter(mAdapter);

		generateDateList(null);
	}

	private Calendar mCalendar = Calendar.getInstance();
	private Calendar mToday = Calendar.getInstance();

	public Date getToday() {
		return mToday.getTime();
	}

	private List<GameDate> mDateList;

	public void setRaceMap(Map<String, ArrayList<RaceInfo>> map, boolean isFilt) {
		int month = mToday.get(Calendar.MONTH) + 1;
		mMonth.setText(month + "月");

		GameDate firstGameDate = null;
		GameDate lastSeletedDate = null;
		GameDate choosenDate = null;

		boolean needChooseFirst = false;

		for (int i = 0; i < mDateList.size(); i++) {
			GameDate date = mDateList.get(i);
			String dateStr = TimeUtil.parseTimeToYMD(date.getDate());
			ArrayList<RaceInfo> infoList = map == null ? null : map
					.get(dateStr);
			// 如果是筛选，则跳转到最近有赛事的那天，否则跳到第一天
			// if (!isFilt) {
			// if (date.isSelected()) {
			// choosenDate = date;
			// }
			// } else {
			// 如果选择的当天没有比赛，则跳到最近有赛事的那一天
			if (date.isSelected()) {
				lastSeletedDate = date;
				if (infoList == null) {
					needChooseFirst = true;
				}
			}
			if (firstGameDate == null) {
				if (infoList != null) {
					firstGameDate = date;
				}
			}
			// }
			date.setSelected(false);
			date.setFlag(date.getFlag() & ~GameDate.FLAG_IMPORTANT);
			if (infoList != null && infoList.size() > 0) {
				// 设置是否有赛事
				if (isFilt) {
					date.setFlag(date.getFlag() | GameDate.FLAG_SHOW_HAS_GAME);
				} else {
					date.setFlag(date.getFlag() & ~GameDate.FLAG_SHOW_HAS_GAME);
				}

				// 设置是否有重要赛事
				for (RaceInfo info : infoList) {
					if ((info.getRaceFlag() & RaceFlag._RACE_JUE_FLAG) != 0) {
						date.setFlag(date.getFlag() | GameDate.FLAG_IMPORTANT);
						break;
					}
				}
			} else {
				date.setFlag(date.getFlag() & ~GameDate.FLAG_SHOW_HAS_GAME);
			}
		}

		if (choosenDate == null) {
			if (needChooseFirst) {
				if (firstGameDate != null) {
					choosenDate = firstGameDate;
				} else {
					choosenDate = lastSeletedDate;
				}
			} else {
				choosenDate = lastSeletedDate;
			}
		}
		choosenDate.setSelected(true);
		int index = mDateList.indexOf(choosenDate);
		moveToCenter(index);
		mCurrentItem = index;
		if (mOnDateClickListener != null) {
			mOnDateClickListener.onClick(mDateList.indexOf(choosenDate),
					choosenDate.getDate());
		}
	}

	private void moveToCenter(int index) {
		// int firstItem = mListView.getFirstVisible();
		int visibleCount = mListView.getVisibleCount();
		int itemTotal = mListView.getChildCount();
		// int lastItem = firstItem + visibleCount;
		// if (mCurrentItem != index || index < firstItem
		// || index > lastItem) {
		int selection = index - visibleCount / 2;
		if (selection < 0) {
			selection = 0;
		}

		if (selection >= mAdapter.getCount() - visibleCount) {
			selection = mAdapter.getCount() - visibleCount;
		}
		mListView.setSelection(selection);
		// }
	}

	public void selectDate(Date date) {
		if (mDateList != null) {
			String date1 = "";
			String date2 = "";
			for (int i = 0; i < mDateList.size(); i++) {
				GameDate gameDate = mDateList.get(i);
				date1 = TimeUtil.parseTimeToYMD(gameDate.getDate());
				date2 = TimeUtil.parseTimeToYMD(date);
				if (date1.equals(date2)) {
					gameDate.setSelected(true);
					mCurrentItem = i;
				} else {
					gameDate.setSelected(false);
				}
			}
			if (mOnDateClickListener != null) {
				mOnDateClickListener.onClick(mCurrentItem,
						mDateList.get(mCurrentItem).getDate());
			}
			moveToCenter(mCurrentItem);
		}
	}

	public List<GameDate> getDateList() {
		return mDateList;
	}

	public void generateDateList(Date selectedDate) {
		mToday = Calendar.getInstance();
		mCalendar.setTime(mToday.getTime());
		if (mDateList == null) {
			mDateList = new ArrayList<GameDate>();
		} else {
			mDateList.clear();
		}
		boolean flag = false;
		for (int i = 0; i < mLength; i++) {
			int dayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);
			int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
			int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

			GameDate date = new GameDate();

//			if (i == 0) {
//				date.setSelected(true);
//				mCurrentItem = 0;
//			} else {
//				date.setSelected(false);
//			}

			date.setDayOfMonth(String.valueOf(dayOfMonth));
			if (mToday.get(Calendar.DATE) == mCalendar.get(Calendar.DATE)) {
				date.setDayOfMonth("今天");
			} else {
				date.setDayOfMonth(String.valueOf(dayOfMonth));
			}
			date.setDayOfWeek(TimeUtil.getWeekString(dayOfWeek));
			date.setDate(mCalendar.getTime());
			if(selectedDate != null && TimeUtil.parseTimeToYMD(selectedDate).equals(TimeUtil.parseTimeToYMD(date.getDate()))){
				date.setSelected(true);
				mCurrentItem = i;
				flag = true;
			} else {
				date.setSelected(false);
			}
			mDateList.add(date);
			mCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);
		}
		if(!flag){
			mDateList.get(0).setSelected(true);
			mCurrentItem = 0;
		}
		mAdapter.setDataSource(mDateList);
	}

	public void refresh() {
		mAdapter.notifyDataSetChanged();
	}

	private OnDateClickListener mOnDateClickListener;

	public void setOnDateSelectedListener(OnDateClickListener listener) {
		mOnDateClickListener = listener;
	}

	public interface OnDateClickListener {
		public void onClick(int position, Date date);
	}
}
