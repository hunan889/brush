package com.yy.android.gamenews.plugin.schetable;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duowan.gamenews.bean.GameDate;
import com.yy.android.sportbrush.R;

public class DateListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<GameDate> mDataSource;
	private Calendar mCalendar;
	private Context mContext;

	public DateListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mCalendar = Calendar.getInstance();
		mContext = context;
	}

	public void setDataSource(List<GameDate> ds) {
		mDataSource = ds;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataSource == null ? 0 : mDataSource.size();
	}

	@Override
	public GameDate getItem(int position) {
		return mDataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static final String TAG = "DateListAdapter";
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView + for " + position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sched_date_item, null);
			holder = new ViewHolder();
			holder.mDayOfMonth = (TextView) convertView
					.findViewById(R.id.tv_dayOfMonth);
			holder.mDayOfWeek = (TextView) convertView
					.findViewById(R.id.tv_dayOfWeek);
			holder.gameIndicator = convertView
					.findViewById(R.id.game_indicator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GameDate date = getItem(position);
		holder.mDayOfMonth.setText(date.getDayOfMonth());
		holder.mDayOfWeek.setText(date.getDayOfWeek());

		mCalendar.setTime(date.getDate());

		int flag = date.getFlag();

		if ((flag & GameDate.FLAG_SHOW_HAS_GAME) != 0) {
			holder.gameIndicator.setVisibility(View.VISIBLE);
		} else {
			holder.gameIndicator.setVisibility(View.INVISIBLE);
		}

		if ((flag & GameDate.FLAG_IMPORTANT) != 0) {
			holder.mDayOfMonth
					.setBackgroundResource(R.drawable.ic_important_game);
		} else {

			holder.mDayOfMonth.setBackgroundResource(0);
		}

		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			holder.mDayOfMonth.setTextColor(mContext.getResources().getColor(
					R.color.sched_page_color));
			holder.mDayOfWeek.setTextColor(mContext.getResources().getColor(
					R.color.sched_page_color));
		} else {

			holder.mDayOfMonth.setTextColor(mContext.getResources().getColor(
					R.color.calendar_week_normal));
			holder.mDayOfWeek.setTextColor(mContext.getResources().getColor(
					R.color.calendar_week_normal));
		}

		if (date.isSelected()) {
			holder.mDayOfMonth.setTextColor(mContext.getResources().getColor(
					R.color.calendar_week_selected));
			holder.mDayOfMonth
					.setBackgroundResource(R.drawable.ic_game_day_selected);
			holder.gameIndicator.setVisibility(View.INVISIBLE);
		} else {

			holder.mDayOfWeek.setBackgroundResource(0);
		}
		Log.d(TAG, "getView - for " + position);
		return convertView;
	}

	private static final class ViewHolder {
		TextView mDayOfMonth;
		View gameIndicator;
		TextView mDayOfWeek;
	}
}
