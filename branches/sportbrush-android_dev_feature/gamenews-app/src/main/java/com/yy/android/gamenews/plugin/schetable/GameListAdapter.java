package com.yy.android.gamenews.plugin.schetable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.duowan.gamenews.RaceFlag;
import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.Team;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.AlarmUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class GameListAdapter<E> extends ImageAdapter<E> {

	private Context mContext;
	private List<RaceInfo> mSavedAlarmList;
	private List<RaceInfo> mSchedSavedAlarmList;
	private Preference mPref;
	private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");
	private Calendar mCalendar = Calendar.getInstance();
	private boolean mIsAlarm;
	private String mTextVS;
	private String mTextFinal;
	private Resources mResource;

	public GameListAdapter(Context context) {
		super(context);
		mContext = context;
		mPref = Preference.getInstance();
		mResource = context.getResources();
		mTextVS = mResource.getString(R.string.text_vs);
		mTextFinal = mResource.getString(R.string.text_final);
	}

	@Override
	public void notifyDataSetChanged() {
		mSavedAlarmList = mPref.getAlarmRaceList();
		mSchedSavedAlarmList = mPref.getSchedAlarmRaceList();
		super.notifyDataSetChanged();
	}

	public void setIsAlarm(boolean isAlarm) {
		mIsAlarm = isAlarm;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		int type = getItemViewType(position);
		if (convertView == null) {
			convertView = getViewForTeamsType(convertView,
					getItemViewType(position));
		}
		holder = (ViewHolder) convertView.getTag();
		Object item = getItem(position);
		if (type == TYPE_DIVIDER) {
			holder.title.setText(String.valueOf(item));
		} else {
			displayInfoForTeams((RaceInfo) item, holder, position);
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public int getItemViewType(int position) {

		Object item = getItem(position);
		if (item instanceof String) {
			return TYPE_DIVIDER;
		}
		RaceInfo info = (RaceInfo) item;
		int teamCount = 0;
		if (info != null) {
			teamCount = info.getTeamList().size();
		}
		if (teamCount > 1) {
			if (mIsAlarm) {
				return TYPE_ALARM_DOUBLE;
			}
			return TYPE_DOUBLE;
		} else {
			if (mIsAlarm) {
				return TYPE_ALARM_SINGLE;
			}
			return TYPE_SINGLE;
		}
	}

	private static final int TYPE_DOUBLE = 0;
	private static final int TYPE_SINGLE = 1;
	private static final int TYPE_ALARM_DOUBLE = 2;
	private static final int TYPE_ALARM_SINGLE = 3;
	private static final int TYPE_DIVIDER = 4;

	private View getViewForTeamsType(View convertView, int viewType) {

		if (convertView == null) {

			switch (viewType) {
			case TYPE_DOUBLE: {
				convertView = mInflater.inflate(R.layout.sched_game_list_item,
						null);
				break;
			}
			case TYPE_SINGLE: {
				convertView = mInflater.inflate(
						R.layout.sched_game_list_item_single, null);
				break;
			}
			case TYPE_ALARM_DOUBLE: {
				convertView = mInflater.inflate(R.layout.sched_alarm_list_item,
						null);
				break;
			}
			case TYPE_ALARM_SINGLE: {
				convertView = mInflater.inflate(
						R.layout.sched_alarm_list_item_single, null);
				break;
			}
			case TYPE_DIVIDER: {
				convertView = mInflater.inflate(R.layout.alarm_list_item_date,
						null);
				break;
			}
			}
			ViewHolder holder = new ViewHolder();
			holder.contentView = convertView.findViewById(R.id.content_view);
			holder.layerView = convertView.findViewById(R.id.fl_layer_view);
			holder.gameName = (TextView) convertView
					.findViewById(R.id.game_list_game_name);
			holder.guestIcon = (ImageView) convertView
					.findViewById(R.id.game_list_guest_img);
			holder.guestName = (TextView) convertView
					.findViewById(R.id.game_list_guest_name);
			holder.hostIcon = (ImageView) convertView
					.findViewById(R.id.game_list_host_img);
			holder.hostName = (TextView) convertView
					.findViewById(R.id.game_list_host_name);
			holder.sourceName = (TextView) convertView
					.findViewById(R.id.game_list_source_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.game_list_time);
			holder.timeLine = (View) convertView
					.findViewById(R.id.game_list_timeline);
			holder.clock = (CheckBox) convertView
					.findViewById(R.id.game_list_clock);
			holder.expireHint = convertView
					.findViewById(R.id.game_list_clock_expired);
			holder.gameStatus = (TextView) convertView
					.findViewById(R.id.game_list_game_status);
			holder.title = (TextView) convertView.findViewById(R.id.alarm_date);
			holder.dividerView = convertView.findViewById(R.id.global_divider_2);
			convertView.setTag(holder);
		}

		return convertView;
	}

	private void displayInfoForTeams(final RaceInfo info,
			final ViewHolder holder, int position) {
		if (holder != null && info != null) {

			boolean expired = isRaceExpire(info);
			
			if(expired){
				holder.layerView.setVisibility(View.VISIBLE);
			}else{
				holder.layerView.setVisibility(View.GONE);
			}
			// 时间线
			if (holder.timeLine != null) {
				if (position == 0) {
					holder.timeLine
							.setBackgroundResource(R.drawable.ic_timeline_head);

					LayoutParams params = (LayoutParams) holder.timeLine
							.getLayoutParams();
					params.setMargins(0, Util.dip2px(getContext(), 10), 0, 0);
				} else {
					holder.timeLine
							.setBackgroundResource(R.drawable.ic_timeline_body);
					LayoutParams params = (LayoutParams) holder.timeLine
							.getLayoutParams();
					params.setMargins(0, 0, 0, 0);
				}
			}

			// 时间
			if (holder.time != null) {
				holder.time.setText(mFormat.format(new Date((long) info
						.getLiveTime() * 1000)));
			}

			// 队伍信息
			List<Team> teamList = info.getTeamList();
			for (Team team : teamList) {

				ImageView icon = null;
				TextView name = null;
				if (team.isGuest) {
					icon = holder.guestIcon;
					name = holder.guestName;
				} else {
					icon = holder.hostIcon;
					name = holder.hostName;
				}

				if (icon != null) {
					displayImage(team.icon, icon);
				}
				if (name != null) {
					name.setText(team.name);
					if (expired) {
						name.setTextColor(mContext.getResources().getColor(R.color.global_text_info_color));
					}else{
						name.setTextColor(mContext.getResources().getColor(R.color.black));
					}
				}
			}

			if (holder.gameName != null) {
				holder.gameName.setText(info.leagueName);
			}

			// 转播源
			List<String> sourceList = info.getLiveSourceList();
			String source = "";
			if (sourceList != null && sourceList.size() > 0) {

				for (int i = 0; i < sourceList.size(); i++) {
					String s = sourceList.get(i);
					if (i != sourceList.size() - 1) {
						source += s + " ";
					} else {
						source += s;
					}
				}
				// if (sourceList.size() > 1) {
				// source = sourceList.get(0) + " " + sourceList.get(1);
				// } else {
				// source = sourceList.get(0);
				// }
			}
			if (holder.sourceName != null) {
				holder.sourceName.setText(source);
			}

			int viewType = getItemViewType(position);
			if (viewType == TYPE_DOUBLE || viewType == TYPE_ALARM_DOUBLE) {
				if (holder.gameStatus != null) {
					if ((info.getRaceFlag() & RaceFlag._RACE_JUE_FLAG) != 0) {
						holder.gameStatus.setText(mTextFinal);
						if (expired) {
							setGameInfoTextColor(holder,getContext().getResources().getColor(R.color.global_text_info_color));
						}else{
							setGameInfoTextColor(holder,getContext().getResources().getColor(R.color.gamelist_final_text_color));
						}
					} else {
						holder.gameStatus.setText(mTextVS);
						setGameInfoTextColor(holder,getContext().getResources().getColor(R.color.global_text_info_color));
					}
				}

			} else if (viewType == TYPE_SINGLE || viewType == TYPE_ALARM_SINGLE) {
				// 决赛
				if ((info.getRaceFlag() & RaceFlag._RACE_JUE_FLAG) != 0) {
					if (holder.hostName != null) {
						String text = holder.hostName.getText() + "(" + mTextFinal + ")";
						holder.hostName.setText(text);
						if (expired) {
							holder.hostName.setTextColor(mResource.getColor(R.color.global_text_info_color));
						}else{
							holder.hostName.setTextColor(mResource.getColor(R.color.gamelist_final_text_color));
						}
					}

					if (expired) {
						setGameInfoTextColor(holder,getContext().getResources().getColor(R.color.global_text_info_color));
					}else{
						setGameInfoTextColor(holder,getContext().getResources().getColor(R.color.gamelist_final_text_color));
					}
				} else {
					if (holder.hostName != null) {
						if (expired) {
							holder.hostName.setTextColor(mResource.getColor(R.color.global_text_info_color));
						}else{
							holder.hostName.setTextColor(mResource.getColor(R.color.global_lv_primary_text));
						}
					}
					setGameInfoTextColor(holder,mResource.getColor(R.color.global_text_info_color));
				}
			}

			final boolean needSetTime = viewType == TYPE_ALARM_DOUBLE
					|| viewType == TYPE_ALARM_SINGLE;
			
			holder.contentView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder.clock.performClick();
				}
			});
			
			// 闹钟
			holder.clock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isRaceExpire(info)) {
						holder.clock.setEnabled(false);
						if (needSetTime) {
							holder.time.setEnabled(false);
						}
						ToastUtil.showToast(R.string.add_alarm_failed);
						return;
					}

					boolean isChecked = ((CompoundButton) v).isChecked();
					if (needSetTime) {
						holder.time.setSelected(isChecked);
					}
					if (isChecked) {
						boolean needToAddAlarm = true;
						if (mSavedAlarmList != null) {
							for (RaceInfo savedInfo : mSavedAlarmList) {
								if (savedInfo.getId().equals(info.getId())) {
									mSavedAlarmList.remove(savedInfo);
									needToAddAlarm = false;
									break;
								}
							}
							for (RaceInfo savedInfo : mSchedSavedAlarmList) {
								if (savedInfo.getId().equals(info.getId())) {
									mSchedSavedAlarmList.remove(savedInfo);
									break;
								}
							}
							mSavedAlarmList.add(info);
							mSchedSavedAlarmList.add(info);
						}
						/**
						 * 已经添加过了不需要再添加
						 */
						if (needToAddAlarm) {
							ToastUtil.showToast(R.string.add_alarm_succeed);
							AlarmUtil.addToAlarm(getContext(), info);
						}
					} else {
						if (mSavedAlarmList != null) {
							for (RaceInfo savedInfo : mSavedAlarmList) {
								if (savedInfo.getId().equals(info.getId())) {
									mSavedAlarmList.remove(savedInfo);
									break;
								}
							}
						}
						if (mSchedSavedAlarmList != null) {
							for (RaceInfo savedInfo : mSchedSavedAlarmList) {
								if (savedInfo.getId().equals(info.getId())) {
									mSchedSavedAlarmList.remove(savedInfo);
									break;
								}
							}
						}
						ToastUtil.showToast(R.string.remove_alarm_succeed);
						AlarmUtil.removeAlarm(getContext(), info);
					}
					
					mPref.saveAlarmRaceList(mSavedAlarmList);
					mPref.saveSchedAlarmRaceList(mSchedSavedAlarmList);
					
//					if(mIsAlarm){
//						AlarmSchedChangedEvent event = new AlarmSchedChangedEvent();
//						event.raceInfo = info;
//						event.isAdd = isChecked;
//						event.mIsAlarm = mIsAlarm;
//						EventBus.getDefault().post(event);
//					}

					String eventKey = mIsAlarm ? (isChecked ? "alarm_follow_race" : "alarm_unfollow_race") : (isChecked ? "all_follow_race" : "all_unfollow_race");
					String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date((long)info.getLiveTime() * 1000));
					
					StringBuilder msg = new StringBuilder();
					msg.append("race_id:").append(info.getId());
					msg.append("race_time:").append(timeString);
					List<Team> teams = info.getTeamList();
					for (int k = 0; k < teams.size(); k++) {
						Team team = teams.get(k);
						msg.append("teams:").append(team.getName());
						if(k < teams.size() - 1){
							msg.append(" VS ");
						}
					}
					
					StatsUtil.statsReport(mContext, eventKey, "msg", msg.toString());
					StatsUtil.statsReportByHiido(eventKey, msg.toString());
					StatsUtil.statsReportByMta(mContext, eventKey, "msg", msg.toString());
				}
			});

			if (expired) {
				holder.clock.setEnabled(false);
				holder.clock.setClickable(false);
				if (needSetTime) {
					holder.time.setEnabled(false);
				}
			} else {
				if (needSetTime) {
					holder.time.setEnabled(true);
				}
				holder.clock.setEnabled(true);
				holder.clock.setClickable(true);
				boolean isSavedAlarm = false;
				if (mSavedAlarmList != null) {
					for (RaceInfo savedInfo : mSavedAlarmList) {
						if (savedInfo.getId().equals(info.getId())) {
							isSavedAlarm = true;
							break;
						}
					}
				}
				if (isSavedAlarm) {
					if (needSetTime) {

						holder.time.setSelected(true);
					}
					holder.clock.setChecked(true);
				} else {
					if (needSetTime) {

						holder.time.setSelected(false);
					}
					holder.clock.setChecked(false);
				}
			}
		}
	}

	private void setGameInfoTextColor(ViewHolder holder, int color) {
		if (holder.gameName != null) {
			holder.gameName.setTextColor(color);
		}
		if (holder.gameStatus != null) {
			holder.gameStatus.setTextColor(color);
		}
		if (holder.sourceName != null) {
			holder.sourceName.setTextColor(color);
		}
	}

	private boolean isRaceExpire(RaceInfo info) {

		mCalendar.setTime(new Date());
		if ((long) info.getLiveTime() * 1000 < mCalendar.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	private static class ViewHolder {
		View contentView;
		View layerView;
		View timeLine;
		TextView time;
		ImageView hostIcon;
		TextView hostName;
		ImageView guestIcon;
		TextView guestName;
		TextView gameName;
		TextView gameStatus;
		TextView sourceName;
		CheckBox clock;
		View expireHint;
		TextView title;
		View dividerView;
	}

}
