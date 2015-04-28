package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.gamenews.GetTeamListRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.Team;
import com.duowan.gamenews.bean.TeamListItemObject;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.thread.BackgroundTask;

public class TeamMoreFragment extends BaseListFragment<TeamListItemObject> {

	private TeamListAdapter mTeamAdapter;
	private GetTeamListRsp mRsp;
//	protected IPageCache mPageCache;
	private Preference mPref;
	private String leagueId;
	
	public static TeamMoreFragment newInstance(String leagueId) {
		TeamMoreFragment fragment = new TeamMoreFragment();
		Bundle args = new Bundle();
		args.putString(TeamMoreActivity.LEAGUE_ID, leagueId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		leagueId = getArguments().getString(TeamMoreActivity.LEAGUE_ID);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	private class TeamLoadingTask extends BackgroundTask<Void, Void, Boolean> {

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
				showView(VIEW_TYPE_EMPTY);
				return;
			}

			requestFinish(RefreshType._REFRESH_TYPE_REFRESH, getResource(mRsp), false, true, false);

			super.onPostExecute(needReload);
		}
	}
	
	protected GetTeamListRsp getResponseFromDisk() {
//		GetTeamListRsp rsp = mPageCache
//				.getJceObject(Constants.CACHE_KEY_TEAM_LIST, new GetTeamListRsp());
		GetTeamListRsp rsp = mPref.getTeamListRsp();
		return rsp;
	}
	
	private ArrayList<TeamListItemObject> getResource(GetTeamListRsp param) {
		ArrayList<Team> teamList = param.getTeamList();
		ArrayList<TeamListItemObject> sourceList = new ArrayList<TeamListItemObject>();

		if (teamList != null) {
			ArrayList<Team> leagueTeamList = new ArrayList<Team>();
			for (Team team : teamList) {
				if(team.getLeagueId().equals(leagueId)){
					leagueTeamList.add(team);
				}
			}
			if(leagueTeamList.size() > 0){
				for (int i = 0; i < leagueTeamList.size(); i = i + 3) {
					TeamListItemObject teamItemObject = new TeamListItemObject();
					teamItemObject.setType(TeamListAdapter.VIEW_TYPE_TEAM);
					if (i < leagueTeamList.size()) {
						teamItemObject.setObjectOne(leagueTeamList.get(i));
					}
					if (i + 1 < leagueTeamList.size()) {
						teamItemObject.setObjectTwo(leagueTeamList.get(i + 1));
					}
					if (i + 2 < leagueTeamList.size()) {
						teamItemObject.setObjectThree(leagueTeamList.get(i + 2));
					}
					sourceList.add(teamItemObject);
				}
			}
		}
		return sourceList;
	}

	@Override
	protected void requestFinish(int refresh, ArrayList<TeamListItemObject> sourceList, 
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null) {
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
	protected void requestData(int refreType) {
		if (mTeamAdapter != null
				&& mTeamAdapter.getDataSource() != null) {
			ArrayList<TeamListItemObject> dataSource = mTeamAdapter.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}
		new TeamLoadingTask().execute();
	}
	
	@Override
	protected boolean isRefreshable() {
		return false;
	}
	
	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected ImageAdapter<TeamListItemObject> initAdapter() {
		mTeamAdapter = new TeamListAdapter(getActivity());
		mTeamAdapter.setMorePage(true);
		return mTeamAdapter;
	}
}
