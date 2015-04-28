package com.yy.android.gamenews.plugin.gamerace;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetUnionListRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.UnionInfo;
import com.duowan.gamenews.UnionType;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.event.SupportUnionEvent;
import com.yy.android.gamenews.model.UnionModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;

import de.greenrobot.event.EventBus;

public class UnionListFragment extends BaseListFragment<UnionInfo> {

	private static final String KEY_TOP_UNION = "top_union";
	private static final String KEY_OTHER_UNION = "other_union";
	
	private UnionListAdapter unionListAdapter;
	private Preference mPref;
	private GetUnionListRsp mRsp;
	
	private String preferenceKey = KEY_TOP_UNION;
	private int unionType;
	
	public static UnionListFragment newInstance(int tab){
		UnionListFragment unionListFragment = new UnionListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(UnionTabFragment.TAB_KEY, tab);
		unionListFragment.setArguments(bundle);
		return unionListFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		mPref = Preference.getInstance();
		if(getArguments() != null && getArguments().containsKey(UnionTabFragment.TAB_KEY)){
			int currentTab = getArguments().getInt(UnionTabFragment.TAB_KEY);
			if(currentTab == UnionTabFragment.TOP_TAB){
				preferenceKey = KEY_TOP_UNION;
				unionType = UnionType._UNION_TYPE_TOP;
			}else{
				preferenceKey = KEY_OTHER_UNION;
				unionType = UnionType._UNION_TYPE_OTHER;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
		
		new LoadCacheUnionTask().execute();
		
		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(SupportUnionEvent supportUnionEvent){
		if(supportUnionEvent != null){
			long unionId = supportUnionEvent.getUnionId();
			if(unionType == UnionType._UNION_TYPE_TOP){
				requestData(RefreshType._REFRESH_TYPE_REFRESH);
			}else{
				supportUnion(unionId);
			}
		}
	}
	
	private void supportUnion(long unionId){
		ArrayList<UnionInfo> unions = getDataSource();
		if(unions != null){
			for (UnionInfo unionInfo : unions) {
				if(unionInfo.getId() == unionId){
					unionInfo.setHeat(unionInfo.getHeat() + 1);
					break;
				}
			}
			unionListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		UnionInfo unionInfo = (UnionInfo) adapter.getItem(position);
		UnionInfoActivity.startActivity(getActivity(), unionInfo);
		
		String key = null;
		if(unionType == UnionType._UNION_TYPE_TOP){
			key = MainTabEvent.CLICK_TOP_UNION;
		}else{
			key = MainTabEvent.CLICK_OTHER_UNION;
		}
		MainTabStatsUtil.statistics(mContext,
				MainTabEvent.TAB_GAMERACE_INFO, key, unionInfo.getName());
		super.onItemClick(parent, adapter, view, position, id);
	}

	@Override
	protected void requestData(final int refreType) {
		if (unionListAdapter != null && unionListAdapter.getDataSource() != null) {
			ArrayList<UnionInfo> dataSource = unionListAdapter.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}
		String attachInfo = null;
		if (mRsp != null && refreType == RefreshType._REFRESH_TYPE_LOAD_MORE) {
			attachInfo = mRsp.getAttachInfo();
		}
		UnionModel.getUnionList(
				new ResponseListener<GetUnionListRsp>(getActivity()) {

					@Override
					public void onResponse(GetUnionListRsp param) {
						mRsp = param;
						if (param != null && param.getUnionList() != null 
								&& !param.getUnionList().isEmpty()) {
							requestFinish(refreType, param.getUnionList(), param.hasMore, true, false);
							if(refreType == RefreshType._REFRESH_TYPE_REFRESH){
								cacheUnionList(param);
							}
						}else{
							requestFinish(refreType, null,false, false, false);
//							ArrayList<UnionInfo> dataSource = unionListAdapter.getDataSource();
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

				}, unionType, attachInfo, refreType);
	}
	
	@Override
	protected void requestFinish(int refresh, ArrayList<UnionInfo> sourceList, 
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null && sourceList.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			ArrayList<UnionInfo> dataSource = unionListAdapter.getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	@Override
	protected ImageAdapter<UnionInfo> initAdapter() {
		unionListAdapter = new UnionListAdapter(getActivity());
		unionListAdapter.setUnionType(unionType);
		return unionListAdapter;
	}
	
	protected void cacheUnionList(GetUnionListRsp param) {
		cacheUnionLisyTask.execute(param);
	}

	protected GetUnionListRsp loadCacheUnionList() {
		GetUnionListRsp rsp = mPref.getUnionListRsp(preferenceKey);
		return rsp;
	}

	protected CacheUnionLisyTask cacheUnionLisyTask = new CacheUnionLisyTask();

	class CacheUnionLisyTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			Object value = params[0];
			mPref.saveUnionListRsp(preferenceKey, (GetUnionListRsp) value);
			return null;
		}
	}

	private class LoadCacheUnionTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = loadCacheUnionList();
			if (mRsp != null && mRsp.getUnionList() != null) {
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

			requestFinish(RefreshType._REFRESH_TYPE_REFRESH, mRsp.getUnionList(),
					false, true, false);

			super.onPostExecute(needReload);
		}
	}
	
	@Override
	protected boolean isRefreshableLoad() {
		if(unionType == UnionType._UNION_TYPE_TOP){
			return false;
		}else{
			return super.isRefreshableLoad();
		}
	}
	
	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}
	
	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}
	
}
