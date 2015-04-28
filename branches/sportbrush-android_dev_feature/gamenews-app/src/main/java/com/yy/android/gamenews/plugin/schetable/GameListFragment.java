package com.yy.android.gamenews.plugin.schetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.SportFlag;
import com.duowan.gamenews.SportRaceListRsp;
import com.duowan.gamenews.Team;
import com.duowan.gamenews.sportInfo;
import com.yy.android.gamenews.event.AlarmSchedChangedEvent;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.model.ScheduleTableModel;
import com.yy.android.gamenews.plugin.schetable.CalendarView.OnDateClickListener;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.common.RefreshListWrapper;
import com.yy.android.gamenews.util.AlarmUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.TimeUtil;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class GameListFragment extends BaseFragment{
	
	protected static final int VIEW_TYPE_EMPTY = 1;
	protected static final int VIEW_TYPE_DATA = 2;
	protected static final int VIEW_TYPE_LOADING = 3;
	public CalendarView mCalendarView;
	private SportRaceListRsp mRsp;
	private ListView mGameListView;
	private GameListAdapter<RaceInfo> mGameListAdapter;
	
	private Map<sportInfo, Map<String, ArrayList<RaceInfo>>> mFilterMap;
	private Map<String, ArrayList<RaceInfo>> mCurrentMap;

//	private IPageCache mPageCache;
	protected Preference mPref;
	
	private ViewGroup mContainer;
	protected LayoutInflater mInflater;
	private View mDataView;
	private View mEmptyLayout;
	private View mEmptyView;
	private TextView mEmptyTextView;
	private View mProgressBar;
	private View mProgressBarInner;
	private Animation mLoadingAnimation = null;
	private Date date = null;
	private GameListLoadedCallback callback;
	
	private int mLastFirstVisibleItem;
	private boolean mIsScrollingUp;
	
	private int filterIndex = 0;
	
	public void setCallback(GameListLoadedCallback callback) {
		this.callback = callback;
	}

	public interface GameListLoadedCallback{
		void onLoaded(SportRaceListRsp mRsp);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mPref = Preference.getInstance();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mInflater = inflater;
		
		View view = inflater.inflate(R.layout.fragment_game_list, container, false);
		mContainer = (ViewGroup) view.findViewById(R.id.root);
		mDataView = view.findViewById(R.id.data_view);
		mCalendarView = (CalendarView) view.findViewById(R.id.calendar_view);
		mCalendarView.setOnDateSelectedListener(mOnDateClickListener);

		mGameListView = (ListView) view.findViewById(R.id.game_list);
		mGameListAdapter = new GameListAdapter<RaceInfo>(getActivity());

		mGameListView.setAdapter(mGameListAdapter);

		mGameListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		
		mGameListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem > mLastFirstVisibleItem) {
					mIsScrollingUp = false;
				} else if (firstVisibleItem < mLastFirstVisibleItem) {
					mIsScrollingUp = true;
				}

				mLastFirstVisibleItem = firstVisibleItem;

				int event = 0;
				Object params = null;
				if (firstVisibleItem == 0) {
					event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD;
				} else {
					if (mIsScrollingUp) {
						event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN;
					} else {
						event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP;
					}
				}
				notifyListener(event, params);
			}
		});
		
		mGameListView.setOnTouchListener(mOnTouchListener);

		View mEmptyView = view.findViewById(R.id.game_list_empty);
		((TextView) view.findViewById(R.id.reload_empty_text))
				.setText(getString(R.string.sched_table_empty));
		mGameListView.setEmptyView(mEmptyView);
		setContainer();
//		mPageCache = new IPageCache();
		return view;
	}
	
	private int mLastEvent; // 防止多余的刷新
	private void notifyListener(int eventType, Object params) {

		if (mLastEvent != eventType) {
			mLastEvent = eventType;
			FragmentCallbackEvent event = new FragmentCallbackEvent();
			event.mEventType = eventType;
			event.mParams = params;
			event.mTarget = getActivity();
			event.mFragment = this;
			EventBus.getDefault().post(event);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((MainActivity)getActivity()).setTitleContainerWidget(mCalendarView);
	}

	@Override
	public void onResume() {
		super.onResume();
		new ReadCacheTask().execute();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser && mGameListAdapter != null && mRsp != null ){
			mGameListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 设置容器，并添加empty view
	 * 
	 * @param container
	 */
	protected void setContainer() {
		if (mContainer == null) {
			return;
		}

		if (mEmptyLayout == null) {
			mEmptyLayout = mInflater.inflate(R.layout.global_reload, null,false);

			showView(mEmptyLayout, false);
			mEmptyTextView = (TextView) mEmptyLayout.findViewById(R.id.reload_empty_text);
			mEmptyTextView.setText(getString(R.string.sched_table_empty));

			mEmptyView = mEmptyLayout.findViewById(R.id.reload_layout);
			mEmptyView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onEmptyViewClicked();
				}
			});
			mEmptyView.setClickable(true);
			mProgressBar = mEmptyLayout.findViewById(R.id.reload_progressbar);
			mProgressBarInner = mEmptyLayout.findViewById(R.id.reload_progressbar_inner);
		} else {
			if (mContainer != null) {
				mContainer.removeView(mEmptyLayout);
			}
		}

		mContainer.addView(mEmptyLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	protected void onEmptyViewClicked() {
		requestData();
	}
	
	private void showView(View view, boolean show) {
		int visibility = show ? View.VISIBLE : View.GONE;
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	protected void showView(int viewType) {
		switch (viewType) {
		case VIEW_TYPE_DATA: {
			showView(mDataView, true);
			showView(mEmptyView, false);
			hidenLoading();
			showView(mEmptyLayout, false);

			break;
		}
		case VIEW_TYPE_EMPTY: {
			showView(mDataView, false);
			showView(mEmptyView, true);
			hidenLoading();
			showView(mEmptyLayout, true);
			break;
		}
		case VIEW_TYPE_LOADING: {
			showView(mDataView, false);
			showView(mEmptyView, false);
			showLoading();
			showView(mEmptyLayout, true);
			break;
		}
		}
	}

	/**
	 * 显示“正在加载”视图
	 */
	private void showLoading() {
		if (mLoadingAnimation == null) {
			mLoadingAnimation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.article_detail_loading);
			mLoadingAnimation.setInterpolator(new LinearInterpolator());
			mLoadingAnimation.setFillAfter(true);// 动画停止时保持在该动画结束时的状态
		}
		showView(mProgressBar, true);
		if (mProgressBarInner != null) {
			mProgressBarInner.startAnimation(mLoadingAnimation);
		}
	}

	protected void hidenLoading() {
		showView(mProgressBar, false);
		if (mProgressBarInner != null) {
			mProgressBarInner.clearAnimation();
		}
	}
	
	public void onEvent(AlarmSchedChangedEvent event) {
		if(event != null && event.getRaceInfo() != null){
			mGameListAdapter.setDataSource(mGameListAdapter.getDataSource());
		}
	}
	
	public void filterData(int position){
		this.filterIndex = position;
		if (mRsp == null) {
			return;
		}
		mCurrentMap = null;
		boolean isFilt = false;
		if (position == 0) {
			mCurrentMap = mRsp.getAllRaceList();
			isFilt = false;
			
		} else {
			isFilt = true;
			if(mRsp.getSportList() != null && mRsp.getSportList().size() >= position - 1){
				mCurrentMap = mFilterMap.get(mRsp
						.getSportList().get(position - 1));
			}
		}

		mCalendarView.setRaceMap(mCurrentMap, isFilt);
		mCalendarView.refresh();
	}

	/**
	 * 以sportInfo为关键字创建过滤器map
	 */
	private Map<sportInfo, Map<String, ArrayList<RaceInfo>>> generateFilterMap(
			SportRaceListRsp rsp) {
		if (rsp == null) {
			return null;
		}
		Map<String, ArrayList<RaceInfo>> originMap = rsp.getAllRaceList();
		if (originMap == null) {
			return null;
		}
		Map<sportInfo, Map<String, ArrayList<RaceInfo>>> filterMap = new HashMap<sportInfo, Map<String, ArrayList<RaceInfo>>>();

		Map<String, ArrayList<RaceInfo>> map = null;

		List<sportInfo> sportInfoList = rsp.getSportList();
		Set<String> dateKeySet = originMap.keySet();
		for (String dateKey : dateKeySet) {
			ArrayList<RaceInfo> infoList = originMap.get(dateKey);
			if (infoList != null) {
				for (RaceInfo info : infoList) {
					for (sportInfo sportInfo : sportInfoList) {
						if ((sportInfo.getSportFlag() & SportFlag._SPORT_INDEX_FLAG) != 0) {
							if (info.getSportType().equals(sportInfo.getId())) {
								map = filterMap.get(sportInfo);
								if (map == null) {
									map = new HashMap<String, ArrayList<RaceInfo>>();
									filterMap.put(sportInfo, map);
								}
								ArrayList<RaceInfo> raceInfoList = map
										.get(dateKey);
								if (raceInfoList == null) {
									raceInfoList = new ArrayList<RaceInfo>();
									map.put(dateKey, raceInfoList);
								}
//								if (!raceInfoList.contains(info)) {
//									raceInfoList.add(info);
//								}
								boolean isInList = false;
								for (RaceInfo raceInfo : raceInfoList) {
									if (raceInfo.getId().equals(info.getId())) {
										isInList = true;
										break;
									}
								}
								if(!isInList){
									raceInfoList.add(info);
								}
							}
						}
					}
				}
			}
		}
		return filterMap;
	}

	public void requestData() {
		String attachInfo = null;
		if (mRsp != null) {
			attachInfo = mRsp.getAttachInfo();
		}else{
			showView(VIEW_TYPE_LOADING);
		}
		ScheduleTableModel.getLiveRsp(new ResponseListener<SportRaceListRsp>(getActivity()) {

			@Override
			public void onResponse(SportRaceListRsp rsp) {
				if (rsp != null) {
					if(rsp.getAllRaceList() != null && !rsp.getAllRaceList().isEmpty()){
						mRsp = rsp; 
						updateResult(mRsp);
						new CompareDataTask().execute(rsp);
						new WriteCacheTask().execute((SportRaceListRsp) mRsp.clone());
						if(callback != null){
							callback.onLoaded(mRsp);
						}
					}else{
						if(mCurrentMap != null && !mCurrentMap.isEmpty()){
							showView(VIEW_TYPE_DATA);
						}else{
							showView(VIEW_TYPE_EMPTY);
						}
					}
				} else {
					new ReadCacheTask().execute();
				}
			}

			@Override
			public void onError(Exception e) {
				new ReadCacheTask().execute();
				super.onError(e);
			}
		}, attachInfo);
	}
	
	private void updateResult(SportRaceListRsp rsp) {
		if (rsp == null) {
			return;
		}

		new DataProcessTask().execute(rsp);
	}

	private class WriteCacheTask extends
			BackgroundTask<SportRaceListRsp, Void, Void> {
		@Override
		protected Void doInBackground(SportRaceListRsp... params) {
//			mPageCache.setJceObject(Constants.CACHE_KEY_SCHETABLE, params[0],
//					Constants.CACHE_DURATION_FOREVER);
			mPref.saveSportRaceRsp(params[0]);
			return null;
		}
	}

	private class ReadCacheTask extends
			BackgroundTask<Void, Void, SportRaceListRsp> {
		@Override
		protected SportRaceListRsp doInBackground(Void... params) {
//			SportRaceListRsp rsp = mPageCache.getJceObject(
//					Constants.CACHE_KEY_SCHETABLE, new SportRaceListRsp());
			SportRaceListRsp rsp = mPref.getSportRaceRsp();
			return rsp;
		}

		@Override
		protected void onPostExecute(SportRaceListRsp result) {
			mRsp = result;
			if (mRsp != null) {
				updateResult(mRsp);
				if(callback != null){
					callback.onLoaded(mRsp);
				}
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
			super.onPostExecute(result);
		}
	}
	
	private class CompareDataTask extends BackgroundTask<SportRaceListRsp, Void, Void> {
		@Override
		protected Void doInBackground(SportRaceListRsp... params) {
//			SportRaceListRsp rsp = mPageCache.getJceObject(Constants.CACHE_KEY_SCHETABLE, new SportRaceListRsp());
			SportRaceListRsp rsp = mPref.getSportRaceRsp();
			List<Team> teamList = Preference.getInstance().getFollowTeamList();
			if(params[0] != null && rsp != null && teamList != null && teamList.size() > 0){
				Map<String, ArrayList<RaceInfo>> currentRaces = params[0].getAllRaceList();
				Map<String, ArrayList<RaceInfo>> cacheRaces = rsp.getAllRaceList();
				
				ArrayList<RaceInfo> currentList = new ArrayList<RaceInfo>();
				ArrayList<RaceInfo> cacheList = new ArrayList<RaceInfo>();
				
				ArrayList<RaceInfo> addRaces = new ArrayList<RaceInfo>();
				ArrayList<RaceInfo> removeRaces = new ArrayList<RaceInfo>();
				
				Set<String> dateKeySet = cacheRaces.keySet();
				for (String dateKey : dateKeySet) {
					cacheList.addAll(cacheRaces.get(dateKey));
				}
				dateKeySet = currentRaces.keySet();
				for (String dateKey : dateKeySet) {
					currentList.addAll(currentRaces.get(dateKey));
				}
				
				for (RaceInfo cacheRace : cacheList) {
					boolean remove = true;
					for (RaceInfo currentRace : currentList) {
						if(cacheRace.getId().equals(currentRace.getId())){
							remove = false;
							break;
						}
					}
					if(remove){
						removeRaces.add(cacheRace);
					}
				}
				
				
				for (RaceInfo currentRace : currentList) {
					boolean add = true;
					for (RaceInfo cacheRace : cacheList) {
						if(cacheRace.getId().equals(currentRace.getId())){
							add = false;
							break;
						}
					}
					if(add){
						addRaces.add(currentRace);
					}
				}
				
				List<RaceInfo> alarmRaceList = Preference.getInstance().getAlarmRaceList();
				for (RaceInfo removeRaceInfo : removeRaces) {
					for (RaceInfo raceInfo : alarmRaceList) {
						if(removeRaceInfo.getId().equals(raceInfo.getId())){
							alarmRaceList.remove(raceInfo);
							AlarmUtil.removeAlarm(getActivity(), raceInfo);
							break;
						}
					}
				}
				
				for (RaceInfo info : addRaces) {
					List<Team> temas = info.getTeamList();
					boolean flag = false;
					for (int i = 0; i < temas.size() && !flag; i++) {
						Team team = temas.get(i);
						if(team.getId() != null){
							for (Team followTeam : teamList) {
								if(team.getId().equals(followTeam.getId())){
									flag = true;
									alarmRaceList.add(info);
									AlarmUtil.addToAlarm(getActivity(), info);
									break;
								}
							}
						}
					}
				}
				Preference.getInstance().saveAlarmRaceList(alarmRaceList);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	private class DataProcessTask
			extends
			BackgroundTask<SportRaceListRsp, Void, Map<sportInfo, Map<String, ArrayList<RaceInfo>>>> {
		@Override
		protected Map<sportInfo, Map<String, ArrayList<RaceInfo>>> doInBackground(
				SportRaceListRsp... params) {
			return generateFilterMap(params[0]);
		}

		@Override
		protected void onPostExecute(
				Map<sportInfo, Map<String, ArrayList<RaceInfo>>> result) {
			mFilterMap = result;
			showView(VIEW_TYPE_DATA);
			mCurrentMap = mRsp.getAllRaceList();
			if(date == null){
				date = new Date();
			}
			mCalendarView.generateDateList(date);
			mCalendarView.setRaceMap(mCurrentMap, false);
			mCalendarView.selectDate(date);
			mCalendarView.refresh();
			if(filterIndex > 0){
				filterData(filterIndex);
			}
			super.onPostExecute(result);
		}
	}
	
	private OnDateClickListener mOnDateClickListener = new OnDateClickListener() {
		public void onClick(int position, java.util.Date date) {
			GameListFragment.this.date = date;
			ArrayList<RaceInfo> todayRaceList = null;
			if (mCurrentMap != null) {
				todayRaceList = mCurrentMap.get(TimeUtil.parseTimeToYMD(date));
			}
			mGameListAdapter.setDataSource(todayRaceList);
			if(TimeUtil.parseTimeToYMD(date).equals(TimeUtil.parseTimeToYMD(new Date())) && todayRaceList != null){
				int index = 0;
				for (RaceInfo raceInfo : todayRaceList) {
					if(isRaceExpire(raceInfo)){
						index ++;
					}
				}
				mGameListView.setSelection(index);
			}
		};
	};
	
	private boolean isRaceExpire(RaceInfo info) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTime(new Date());
		if ((long) info.getLiveTime() * 1000 < mCalendar.getTimeInMillis()) {
			return true;
		}
		return false;
	}
	
	private int mStartY; // 用户手指按下时的位置
	private int mLastY; // 上次event时的位置，用于记录用户是向上还是向下滑
	private int mDirection = DIRECTION_NONE;
	public static final int DIRECTION_UP = -1;
	public static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_DOWN = 1;
	private final static int OFFSET_SCROLL = 0;
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent ev) {

			int currentY = (int) ev.getY();
			int action = ev.getAction();

			switch (action) {
			case MotionEvent.ACTION_DOWN: {
				mLastY = mStartY;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mLastY - currentY > OFFSET_SCROLL) {
					// Log.d(TAG, "mDirection = DIRECTION_UP");
					mDirection = DIRECTION_UP;
				} else if (mLastY - currentY < -OFFSET_SCROLL) {
					// Log.d(TAG, "mDirection = DIRECTION_DOWN");
					mDirection = DIRECTION_DOWN;
				}
				mLastY = currentY;
				
				onScroll(mGameListView.getFirstVisiblePosition(), 0, 0);
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				break;
			}
			if (mGameListView != null) {
				return mGameListView.onTouchEvent(ev);
			}
			return true;
		}
	};
	
	public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount){
		int event = 0;
		Object params = null;

		switch (mDirection) {
		case RefreshListWrapper.DIRECTION_DOWN: {
			if (firstVisibleItem == 0) {
				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD;
			} else {
				event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_DOWN;
			}
			break;
		}
		case RefreshListWrapper.DIRECTION_UP: {

			event = FragmentCallbackEvent.FRGMT_LIST_SCROLL_UP;
			break;
		}
		}
		notifyListener(event, params);
	}

}
