package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetTeamListRsp;
import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.SportRaceListRsp;
import com.duowan.gamenews.Team;
import com.duowan.gamenews.TeamFlag;
import com.duowan.gamenews.bean.TeamListItemObject;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.AlarmTeamChangedEvent;
import com.yy.android.gamenews.model.ScheduleTableModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.AlarmUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TeamListFragment  extends BaseListFragment<TeamListItemObject> {

//	protected IPageCache mPageCache;
	protected Preference mPref;
	private FragmentActivity mActivity;
	private GetTeamListRsp mRsp;
	private TeamListAdapter mTeamAdapter;

	public TeamListFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mActivity = getActivity();
//		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
		new BgTask().execute();
		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
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
	protected void requestData(final int refreType) {
		if (mTeamAdapter != null && mTeamAdapter.getDataSource() != null) {
			ArrayList<TeamListItemObject> dataSource = mTeamAdapter.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}
		String attachInfo = null;
		if (mRsp != null) {
			attachInfo = mRsp.getAttachInfo();
		}
		ScheduleTableModel.getTeamRsp(
				new ResponseListener<GetTeamListRsp>(mActivity) {

					@Override
					public void onResponse(GetTeamListRsp param) {
						mRsp = param;
						if (param != null && param.getTeamList() != null 
								&& !param.getTeamList().isEmpty()) {
							saveListToDisk(param);
							requestFinish(refreType, getResource(param), false, true, false);
						}else{
							requestFinish(refreType, null,false, false, false);
//							ArrayList<TeamListItemObject> dataSource = mTeamAdapter.getDataSource();
//							if (dataSource != null && dataSource.size() > 0) {
//								showView(VIEW_TYPE_DATA);
//							} else {
//								showView(VIEW_TYPE_EMPTY);
//							}
						}
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType,
								null, false, true, false);
					}

				}, attachInfo);
	}

	private ArrayList<TeamListItemObject> getResource(GetTeamListRsp param) {
		ArrayList<Team> originalTeamList = param.getTeamList();
		ArrayList<Team> teamList = new ArrayList<Team>();
		for (Team team : originalTeamList) {
			if(!TextUtils.isEmpty(team.getSportTypeId()) 
					&& !TextUtils.isEmpty(team.getSportTypeName())
					&& !TextUtils.isEmpty(team.getLeagueId()) 
					&& !TextUtils.isEmpty(team.getLeagueName())){
				teamList.add(team);
			}
		}
		ArrayList<TeamListItemObject> sourceList = new ArrayList<TeamListItemObject>();

		if (teamList != null) {
			
			Collections.sort(teamList, new Comparator<Team>() {

				@Override
				public int compare(Team lhs, Team rhs) {
					return  rhs.getSportTypeName().compareTo(lhs.getSportTypeName());
				}
			});
			
			ArrayList<Team> sportTypeList = new ArrayList<Team>();
			ArrayList<String> leagueList = new ArrayList<String>();
			ArrayList<Team> hotList = new ArrayList<Team>();
			for (Team team : teamList) {
				//热门
				if((team.getFlag() & TeamFlag._TEAM_FLAG_HOT) != 0){
					if(hotList.size() < 6){
						hotList.add(team);
					}
				}
				
				if(!leagueList.contains(team.getLeagueId())){
					leagueList.add(team.getLeagueId());
					sportTypeList.add(team);
				}
			}
			
			if(hotList.size() > 0){
				leagueList.add(0, "-1");
				sportTypeList.add(0, null);
			}
			
			for (int j = 0; j < leagueList.size(); j++) {
				String league = leagueList.get(j);
				ArrayList<Team> leagueTeamList = new ArrayList<Team>();
				if(j == 0){
					leagueTeamList.addAll(hotList);
				}else{
					for (Team team : teamList) {
						if(team.getLeagueId().equals(league)){
							leagueTeamList.add(team);
						}
					}
				}
				if(leagueTeamList.size() > 0){
					TeamListItemObject leagueItemObject = new TeamListItemObject();
					leagueItemObject.setType(TeamListAdapter.VIEW_TYPE_LEAGUE);
					if(leagueList.indexOf(league) == 0){
						leagueItemObject.setObjectOne(getResources().getString(R.string.recent_hot));
						leagueItemObject.setObjectTwo(null);
						leagueItemObject.setObjectThree(null);
					}else{
						if(j > 1 && sportTypeList.get(j).getSportTypeName().equals(sportTypeList.get(j - 1).getSportTypeName())){
							leagueItemObject.setObjectOne(null);
						}else{
							leagueItemObject.setObjectOne(leagueTeamList.get(0).getSportTypeName());
						}
						leagueItemObject.setObjectTwo(leagueTeamList.get(0).getLeagueId());
						leagueItemObject.setObjectThree(leagueTeamList.get(0).getLeagueName());
					}
					sourceList.add(leagueItemObject);
					
					int size = leagueTeamList.size() > 6 ? 6 : leagueTeamList.size();
					for (int i = 0; i < size; i = i + 3) {
						TeamListItemObject teamItemObject = new TeamListItemObject();
						teamItemObject.setType(TeamListAdapter.VIEW_TYPE_TEAM);
						if (i < size) {
							if(j == 0){
								teamItemObject.setFlagOne(true);
							}
							teamItemObject.setObjectOne(leagueTeamList.get(i));
						}
						if (i + 1 < size) {
							if(j == 0){
								teamItemObject.setFlagTwo(true);
							}
							teamItemObject.setObjectTwo(leagueTeamList.get(i + 1));
						}
						if (i + 2 < size) {
							if(j == 0){
								teamItemObject.setFlagThree(true);
							}
							teamItemObject.setObjectThree(leagueTeamList.get(i + 2));
						}
						sourceList.add(teamItemObject);
					}
				}
			}
		}
		return sourceList;
	}

	@Override
	protected void requestFinish(int refresh, ArrayList<TeamListItemObject> sourceList, 
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null && sourceList.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<TeamListItemObject> dataSource = mTeamAdapter.getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	@Override
	protected ImageAdapter<TeamListItemObject> initAdapter() {
		mTeamAdapter = new TeamListAdapter(getActivity());
		mTeamAdapter.setMorePage(false);
		return mTeamAdapter;

	}

	protected void saveListToDisk(GetTeamListRsp param) {
		String key = Constants.CACHE_KEY_TEAM_LIST;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION, true);
	}

	protected GetTeamListRsp getResponseFromDisk() {
//		GetTeamListRsp rsp = mPageCache
//				.getJceObject(Constants.CACHE_KEY_TEAM_LIST, new GetTeamListRsp());
		GetTeamListRsp rsp = mPref.getTeamListRsp();
		return rsp;
	}

	protected SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {

//			String key = (String) params[0];
			Object value = params[1];
//			int duration = (Integer) params[2];
//			boolean isJceObject = (Boolean) params[3];

//			if (isJceObject) {
//				mPageCache.setJceObject(key, value, duration);
//			} else {
//				mPageCache.setObject(key, value, duration);
//			}
			mPref.saveTeamListRsp((GetTeamListRsp) value);
			return null;
		}
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = getResponseFromDisk();
			if (mRsp != null && mRsp.getTeamList() != null) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {

			if (!needReload) {
				if (Util.isNetworkConnected()) {
					showView(VIEW_TYPE_LOADING);
				} else {
					showView(VIEW_TYPE_EMPTY);
				}
				return;
			}

			requestFinish(RefreshType._REFRESH_TYPE_REFRESH, getResource(mRsp),
					false, true, false);

			super.onPostExecute(needReload);
		}
	}
	
	private AlarmTeamChangedEvent event;
	public void onEvent(AlarmTeamChangedEvent event) {
		if(event.isMorePage()){
			mTeamAdapter.notifyDataSetChanged();
		}
		this.event = event;
		new FollowTeamChangedTask().execute();
	}
	
	private boolean isRaceExpire(RaceInfo info) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTime(new Date());
		if ((long) info.getLiveTime() * 1000 < mCalendar.getTimeInMillis()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 添加 or 取消提醒球队的赛事
	 * @param event
	 */
	private void followTeamChanged(){
		String teamId = event.getTeam().getId().trim();
		boolean follow = event.isFollow();
//		SportRaceListRsp rsp = mPageCache.getJceObject(
//				Constants.CACHE_KEY_SCHETABLE, new SportRaceListRsp());
		SportRaceListRsp rsp = mPref.getSportRaceRsp();
		Map<String, ArrayList<RaceInfo>> mCurrentMap = rsp == null ? null : rsp.getAllRaceList();
		if(mCurrentMap != null){
			List<RaceInfo> alarmRaceList = mPref.getAlarmRaceList();
			List<RaceInfo> teamRaceList = new ArrayList<RaceInfo>();
			List<Team> followTeamList = mPref.getFollowTeamList();
			if(mCurrentMap != null){
				Set<String> keySet = mCurrentMap.keySet();
				for (String dateKey : keySet) {
					ArrayList<RaceInfo> infoList = mCurrentMap.get(dateKey);
					if (infoList != null) {
						for (RaceInfo info : infoList) {
							List<Team> temas = info.getTeamList();
							for (Team team : temas) {
								if(team.getId() != null 
										&& team.getId().trim().equals(teamId) 
										&& !isRaceExpire(info)){
									teamRaceList.add(info);
									break;
								}
							}
						}
					}
				}
			}
			
			if(follow){
				for (RaceInfo teamRace : teamRaceList) {
					boolean needToAddAlarm = true;
					for (RaceInfo alarmRace : alarmRaceList) {
						if (alarmRace.getId().equals(teamRace.getId())) {
							alarmRaceList.remove(alarmRace);
							needToAddAlarm = false;
							break;
						}
					}
					alarmRaceList.add(teamRace);
					if (needToAddAlarm) {
						AlarmUtil.addToAlarm(getActivity(), teamRace);
					}
				}
			}else{
				List<RaceInfo> schedAlarmRaceList = mPref.getSchedAlarmRaceList();
				for (RaceInfo teamRace : teamRaceList) {
					boolean found = false;
					for (RaceInfo raceInfo : schedAlarmRaceList) {
						if(teamRace.getId() != null && raceInfo.getId() != null 
								&& teamRace.getId().equals(raceInfo.getId())){
							found = true;
							break;
						}
					}
					if(!found){
						for (RaceInfo alarmRace : alarmRaceList) {
							if (alarmRace.getId() != null && teamRace.getId() != null 
									&& alarmRace.getId().equals(teamRace.getId())) {
								boolean flag = false;
								List<Team> alarmTeams = alarmRace.getTeamList();
								for (int i = 0; i < followTeamList.size() && !flag; i++) {
									Team team = followTeamList.get(i);
									for (Team alarmTeam : alarmTeams) {
										if (team.getId() != null
												&& alarmTeam.getId() != null
												&& !alarmTeam.getId().trim()
														.equals(teamId)
												&& alarmTeam
														.getId()
														.trim()
														.equals(team.getId()
																.trim())) {
											flag = true;
											break;
										}
									}
								}
								if(!flag){
									alarmRaceList.remove(alarmRace);
									AlarmUtil.removeAlarm(getActivity(), alarmRace);
								}
								break;
							}
						}
					}
				}
			}
			mPref.saveAlarmRaceList(alarmRaceList);
		}
	}
	
	private class FollowTeamChangedTask extends BackgroundTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			followTeamChanged();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

}