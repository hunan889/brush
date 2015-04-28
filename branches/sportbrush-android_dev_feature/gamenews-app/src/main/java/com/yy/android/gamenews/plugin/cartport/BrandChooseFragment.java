package com.yy.android.gamenews.plugin.cartport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.autonews.CarBrandInfo;
import com.duowan.autonews.GetCarBrandListRsp;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.bean.BrandListItemObject;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BrandChooseFragment extends BaseListFragment<BrandListItemObject> {

	private static final int COUNT = 20;
	protected IPageCache mPageCache;
	protected Preference mPref;
	private FragmentActivity mActivity;
	private GetCarBrandListRsp mRsp;
	private SideBar mSideBar;
	private TextView mDialogText;
	private HashMap<String, Integer> mHashMap;
	private BrandChooseAdapter mBrandChooseAdapter;
	private WindowManager mWindowManager;

	public BrandChooseFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);
		addSideBar(parentView);
		new BgTask().execute();
		return parentView;
	}

	public void addSideBar(ViewGroup parentView) {
		View view = mInflater.inflate(R.layout.activity_sidebar, null);
		mSideBar = (SideBar) view.findViewById(R.id.mSideBar);
		mDialogText = (TextView) mInflater
				.inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		parentView.addView(view, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		mSideBar.init(mSideBar.getHeight(mActivity)
				- Util.dip2px(mActivity, 105));
		mWindowManager = (WindowManager) mActivity
				.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		mSideBar.setTextView(mDialogText);
		mSideBar.setVisibility(View.GONE);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected boolean isRefreshable() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	// @Override
	// protected void customizeView(ViewGroup viewGroup) {
	// super.customizeView(viewGroup);
	// View emptyView = mInflater.inflate(R.layout.empty_view, null);
	// if (mDataViewConverter != null) {
	// mDataViewConverter.addHeader(emptyView);
	// }
	// }

	@Override
	protected void requestData(final int refreType) {
		if (mBrandChooseAdapter != null
				&& mBrandChooseAdapter.getDataSource() != null) {
			ArrayList<BrandListItemObject> dataSource = mBrandChooseAdapter
					.getDataSource();
			if (dataSource == null) {
				showView(VIEW_TYPE_LOADING);
			}
		} else {
			showView(VIEW_TYPE_LOADING);
		}

		BrandChooseModel.getBrandChooseList(
				new ResponseListener<GetCarBrandListRsp>(mActivity) {

					@Override
					public void onResponse(GetCarBrandListRsp param) {
						if (param != null && param.getAllBrandInfo() != null) {
							saveListToDisk(param);
							requestFinish(refreType,
									getResource(param), false, true, false);
						}
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType,
								null, false, true, false);
					}

				}, COUNT, "");

	}

	private ArrayList<BrandListItemObject> getResource(GetCarBrandListRsp param) {
		Map<String, ArrayList<CarBrandInfo>> allBrandInfo = param
				.getAllBrandInfo();
		ArrayList<BrandListItemObject> sourceList = new ArrayList<BrandListItemObject>();

		int nums = -1;
		mHashMap = new HashMap<String, Integer>();
		if (mHashMap != null) {
			mHashMap.clear();
		}
		for (char j = 65; j < 92; j++) {
			String key = String.valueOf(j);
			ArrayList<CarBrandInfo> arrayList = allBrandInfo.get(key);
			if (arrayList != null) {
				BrandListItemObject gititalObject = new BrandListItemObject();
				gititalObject.setType(BrandChooseAdapter.VIEW_TYPE_GITITAL);
				gititalObject.setObjectOne(key);
				sourceList.add(gititalObject);
				nums++;
				mHashMap.put(key, nums);
				for (int i = 0; i < arrayList.size(); i = i + 3) {
					BrandListItemObject brandObject = new BrandListItemObject();
					brandObject
							.setType(BrandChooseAdapter.VIEW_TYPE_BRANDCHOOSE);
					if (i < arrayList.size()) {
						brandObject.setObjectOne(arrayList.get(i));
					}
					if (i + 1 < arrayList.size()) {
						brandObject.setObjectTwo(arrayList.get(i + 1));
					}
					if (i + 2 < arrayList.size()) {
						brandObject.setObjectThree(arrayList.get(i + 2));
					}
					sourceList.add(brandObject);
					nums++;
				}

			}
		}
		return sourceList;
	}

	@Override
	protected void requestFinish(int refresh,
			ArrayList<BrandListItemObject> sourceList, boolean hasMore,
			boolean replace, boolean error) {
		super.requestFinish(refresh, sourceList, hasMore, replace, error);
		if (sourceList != null) {
			showView(VIEW_TYPE_DATA);
			mSideBar.setListView((ListView) getDataView());
			mBrandChooseAdapter.setData(mHashMap);
			mSideBar.setVisibility(View.VISIBLE);
		} else {
			ArrayList<BrandListItemObject> dataSource = mBrandChooseAdapter
					.getDataSource();
			if (dataSource != null && dataSource.size() > 0) {
				showView(VIEW_TYPE_DATA);
			} else {
				showView(VIEW_TYPE_EMPTY);
			}
		}
	}

	@Override
	protected ImageAdapter<BrandListItemObject> initAdapter() {
		mBrandChooseAdapter = new BrandChooseAdapter(getActivity());
		return mBrandChooseAdapter;

	}

	// @Override
	// protected void onEmptyViewClicked() {
	// // SingleFragmentActivity.startCartDetailActivity(mContext);
	// requestData(RefrshType._REFRESH_TYPE_REFRESH);
	// }

	protected void saveListToDisk(GetCarBrandListRsp param) {
		String key = Constants.CACHE_KEY_BRAND_CHOOSE_LIST;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION,
				true);
	}

	protected GetCarBrandListRsp getResponseFromDisk() {
		GetCarBrandListRsp rsp = mPageCache
				.getJceObject(Constants.CACHE_KEY_BRAND_CHOOSE_LIST,
						new GetCarBrandListRsp());
		return rsp;
	}

	protected SaveCacheTask mSaveCacheTask = new SaveCacheTask();

	class SaveCacheTask extends BackgroundTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {

			String key = (String) params[0];
			Object value = params[1];
			int duration = (Integer) params[2];
			boolean isJceObject = (Boolean) params[3];

			if (isJceObject) {
				mPageCache.setJceObject(key, value, duration);
			} else {
				mPageCache.setObject(key, value, duration);
			}
			return null;
		}
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = getResponseFromDisk();
			if (mRsp != null && mRsp.getAllBrandInfo() != null) {
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

}
