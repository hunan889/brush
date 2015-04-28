package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.gamenews.Team;
import com.duowan.gamenews.TeamFlag;
import com.duowan.gamenews.bean.TeamListItemObject;
import com.yy.android.gamenews.event.AlarmTeamChangedEvent;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class TeamListAdapter extends ImageAdapter<TeamListItemObject>
		implements OnClickListener {
	private Context mContext;
	private Preference mPref;
	private List<Team> mFollowTeamList;
	private boolean isMorePage = false;

	/** 头部运动联赛信息 */
	public final static int VIEW_TYPE_LEAGUE = 0;

	/** 球队信息 */
	public final static int VIEW_TYPE_TEAM = 1;


	public TeamListAdapter(Context context) {
		super(context);
		this.mContext = context;
		mPref = Preference.getInstance();
	}
	
	@Override
	public void notifyDataSetChanged() {
		mFollowTeamList = mPref.getFollowTeamList();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TeamListItemObject item = getItem(position);
		if (item.getType() == VIEW_TYPE_TEAM) {
			return getTeamItemView(position, convertView, parent);
		} else if (item.getType() == VIEW_TYPE_LEAGUE) {
			return getLeagueItemView(position, convertView, parent);
		}
		return null;
	}

	private View getTeamItemView(int position, View convertView,
			ViewGroup parent) {
		TeamListItemObject item = getItem(position);
		ViewTeamHolder viewTeamHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewTeamHolder = new ViewTeamHolder();
			convertView = mInflater.inflate(R.layout.team_item, null);
			viewTeamHolder.firstTeamView = (RelativeLayout) convertView
					.findViewById(R.id.rl_first_team);
			viewTeamHolder.secondTeamView = (RelativeLayout) convertView
					.findViewById(R.id.rl_second_team);
			viewTeamHolder.thirdTeamView = (RelativeLayout) convertView
					.findViewById(R.id.rl_third_team);
			viewTeamHolder.firstTeamImageView = (ImageView) convertView
					.findViewById(R.id.iv_first_team);
			viewTeamHolder.secondTeamImageView = (ImageView) convertView
					.findViewById(R.id.iv_second_team);
			viewTeamHolder.thirdTeamImageView = (ImageView) convertView
					.findViewById(R.id.iv_third_team);
			viewTeamHolder.firstTeamTextView = (TextView) convertView
					.findViewById(R.id.tv_firs_team);
			viewTeamHolder.secondTeamTextView = (TextView) convertView
					.findViewById(R.id.tv_second_team);
			viewTeamHolder.thirdTeamTextView = (TextView) convertView
					.findViewById(R.id.tv_third_team);
			viewTeamHolder.firstSelectedImageView = (ImageView) convertView
					.findViewById(R.id.iv_first_selected);
			viewTeamHolder.secondSelectedImageView = (ImageView) convertView
					.findViewById(R.id.iv_second_selected);
			viewTeamHolder.thirdSelectedImageView = (ImageView) convertView
					.findViewById(R.id.iv_third_selected);
			convertView.setTag(viewTeamHolder);
		} else {
			viewTeamHolder = (ViewTeamHolder) convertView.getTag();
		}
		Team firstTeam = (Team) item.getObjectOne();
		Team secondTeam = (Team) item.getObjectTwo();
		Team thirdTeam = (Team) item.getObjectThree();
		
		viewTeamHolder.firstSelectedImageView.setVisibility(View.GONE);
		viewTeamHolder.secondSelectedImageView.setVisibility(View.GONE);
		viewTeamHolder.thirdSelectedImageView.setVisibility(View.GONE);
		
		if (firstTeam != null) {
			displayImage(firstTeam.getIcon(), viewTeamHolder.firstTeamImageView);
			if((firstTeam.getFlag() & TeamFlag._TEAM_FLAG_HOT) != 0 && item.isFlagOne()){
				viewTeamHolder.firstTeamTextView.setText(firstTeam.getLeagueName() + "-" + firstTeam.getName());
			}else{
				viewTeamHolder.firstTeamTextView.setText(firstTeam.getName());
			}
			showOrHide(viewTeamHolder.firstTeamView, true, firstTeam);
		} else {
			showOrHide(viewTeamHolder.firstTeamView, false, firstTeam);
		}
		if (secondTeam != null) {
			displayImage(secondTeam.getIcon(), viewTeamHolder.secondTeamImageView);
			if((secondTeam.getFlag() & TeamFlag._TEAM_FLAG_HOT) != 0 && item.isFlagTwo()){
				viewTeamHolder.secondTeamTextView.setText(secondTeam.getLeagueName() + "-" + secondTeam.getName());
			}else{
				viewTeamHolder.secondTeamTextView.setText(secondTeam.getName());
			}
			showOrHide(viewTeamHolder.secondTeamView, true, secondTeam);
		} else {
			showOrHide(viewTeamHolder.secondTeamView, false, secondTeam);
		}
		if (thirdTeam != null) {
			displayImage(thirdTeam.getIcon(), viewTeamHolder.thirdTeamImageView);
			if((thirdTeam.getFlag() & TeamFlag._TEAM_FLAG_HOT) != 0 && item.isFlagThree()){
				viewTeamHolder.thirdTeamTextView.setText(thirdTeam.getLeagueName() + "-" + thirdTeam.getName());
			}else{
				viewTeamHolder.thirdTeamTextView.setText(thirdTeam.getName());
			}
			showOrHide(viewTeamHolder.thirdTeamView, true, thirdTeam);
		} else {
			showOrHide(viewTeamHolder.thirdTeamView, false, thirdTeam);
		}
		if(mFollowTeamList != null){
			for (Team team : mFollowTeamList) {
				if(firstTeam != null && firstTeam.getId().equals(team.getId())){
					viewTeamHolder.firstSelectedImageView.setVisibility(View.VISIBLE);
				}
				if(secondTeam != null && secondTeam.getId().equals(team.getId())){
					viewTeamHolder.secondSelectedImageView.setVisibility(View.VISIBLE);
				}
				if(thirdTeam != null && thirdTeam.getId().equals(team.getId())){
					viewTeamHolder.thirdSelectedImageView.setVisibility(View.VISIBLE);
				}
			}
		}
		return convertView;
	}

	private View getLeagueItemView(int position, View convertView,
			ViewGroup parent) {
		TeamListItemObject item = getItem(position);
		ViewLeagueHolder viewLeagueHolder = null;
		if (convertView == null || convertView.getTag() == null) {
			viewLeagueHolder = new ViewLeagueHolder();
			convertView = mInflater.inflate(R.layout.team_league_layout, null);
			viewLeagueHolder.sportTypeView =  convertView.findViewById(R.id.ll_sport_type);
			viewLeagueHolder.leagueView = convertView.findViewById(R.id.rl_league);
			viewLeagueHolder.sportTypeTextView = (TextView) convertView.findViewById(R.id.tv_sport_type);
			viewLeagueHolder.leagueTextView = (TextView) convertView.findViewById(R.id.tv_league_name);
			viewLeagueHolder.allTeamView = convertView.findViewById(R.id.ll_all_team);
			convertView.setTag(viewLeagueHolder);
		} else {
			viewLeagueHolder = (ViewLeagueHolder) convertView.getTag();
		}
		if(item.getObjectOne() == null){
			viewLeagueHolder.sportTypeView.setVisibility(View.GONE);
		}else{
			viewLeagueHolder.sportTypeView.setVisibility(View.VISIBLE);
			String sportType = (String) item.getObjectOne();
			viewLeagueHolder.sportTypeTextView.setText(sportType);
		}
		if(item.getObjectTwo() == null || item.getObjectThree() == null){
			viewLeagueHolder.leagueView.setVisibility(View.GONE);
		}else{
			viewLeagueHolder.leagueView.setVisibility(View.VISIBLE);
			final String leagueId = (String) item.getObjectTwo();
			final String league = (String) item.getObjectThree();
			viewLeagueHolder.leagueTextView.setText(league);
			viewLeagueHolder.allTeamView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					StatsUtil.statsReport(mContext, "all_team", "league", league);
					StatsUtil.statsReportByHiido("all_team", league);
					StatsUtil.statsReportByMta(mContext, "all_team", "league", league);
					TeamMoreActivity.startActivity(mContext, leagueId, league);
				}
			});
		}
		
		return convertView;
	}

	@Override
	public void onClick(View view) {
		Team team = (Team) view.getTag();
		if (team != null) {
			boolean follow = true;
			if(mFollowTeamList == null){
				mFollowTeamList = new ArrayList<Team>();
			}
			for (Team followTeam : mFollowTeamList) {
				if(team.getId().equals(followTeam.getId())){
					mFollowTeamList.remove(followTeam);
					follow = false;
					break;
				}
			}
			View imageView = view.findViewById(R.id.iv_first_selected);
			if(imageView == null){
				imageView = view.findViewById(R.id.iv_second_selected);
				if(imageView == null){
					imageView = view.findViewById(R.id.iv_third_selected);
				}
			}
			if(imageView != null){
				imageView.setVisibility(follow ? View.VISIBLE : View.GONE);
			}
			if(follow){
				mFollowTeamList.add(team);
				ToastUtil.showToast(R.string.add_alarm_succeed);
			}else{
				ToastUtil.showToast(R.string.remove_alarm_succeed);
			}
			mPref.saveFollowTeamList(mFollowTeamList);
			
			AlarmTeamChangedEvent event = new AlarmTeamChangedEvent();
			event.setTeam(team);
			event.setFollow(follow);
			event.setMorePage(isMorePage);
			EventBus.getDefault().post(event);
			
			StatsUtil.statsReport(mContext, follow ? "follow_team" : "unfollow_team", "team", team.getName());
			StatsUtil.statsReportByHiido(follow ? "follow_team" : "unfollow_team", team.getName());
			StatsUtil.statsReportByMta(mContext, follow ? "follow_team" : "unfollow_team", "team", team.getName());
		} else {
			ToastUtil.showToast(R.string.team_choose_no_models);
		}
	}

	private void showOrHide(View view, boolean isShow, Team team) {
		if (isShow) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(this);
			if (team != null) {
				view.setTag(team);
			}
		} else {
			view.setVisibility(View.INVISIBLE);
			view.setOnClickListener(null);
			view.setClickable(false);
		}
	}

	@Override
	public int getItemViewType(int position) {
		TeamListItemObject item = getItem(position);
		if (item.getType() == VIEW_TYPE_LEAGUE) {
			return VIEW_TYPE_LEAGUE;
		} else if (item.getType() == VIEW_TYPE_TEAM) {
			return VIEW_TYPE_TEAM;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	static class ViewTeamHolder {
		View firstTeamView;
		View secondTeamView;
		View thirdTeamView;
		ImageView firstTeamImageView;
		ImageView secondTeamImageView;
		ImageView thirdTeamImageView;
		TextView firstTeamTextView;
		TextView secondTeamTextView;
		TextView thirdTeamTextView;
		ImageView firstSelectedImageView;
		ImageView secondSelectedImageView;
		ImageView thirdSelectedImageView;
	}

	static class ViewLeagueHolder {
		View sportTypeView;
		View leagueView;
		TextView sportTypeTextView;
		TextView leagueTextView;
		View allTeamView;
	}

	public void setMorePage(boolean isMorePage) {
		this.isMorePage = isMorePage;
	}
}
