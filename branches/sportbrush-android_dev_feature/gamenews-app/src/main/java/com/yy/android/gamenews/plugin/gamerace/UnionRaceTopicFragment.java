package com.yy.android.gamenews.plugin.gamerace;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.GetRaceTopicRsp;
import com.duowan.gamenews.RaceTopicInfo;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.UnionInfo;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.IPageCache;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.thread.BackgroundTask;
import com.yy.android.sportbrush.R;

public class UnionRaceTopicFragment extends BaseListFragment<UnionInfo> {
	private SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
	public static final String RACE_TOPIC = "UnionRaceTopic";
	private UnionListAdapter mUnionListAdapter;
	private FragmentActivity mActivity;
	protected IPageCache mPageCache;
	protected Preference mPref;
	private RelativeLayout mRaceTopicRlt;
	private ImageView mBannerImg;
	private TextView mBannerTitle;
	private TextView mTopicDesc;
	private TextView mTopicTitle;
	private String mAttachInfo;
	private GetRaceTopicRsp mRsp;
	private long mTopicId;
	private boolean IsFirstEnter = true;

	public static UnionRaceTopicFragment newInstance() {
		UnionRaceTopicFragment fragment = new UnionRaceTopicFragment();
		return fragment;
	}

	public void setData(RaceTopicInfo raceTopicInfo) {
		if (raceTopicInfo != null) {
			mTopicId = raceTopicInfo.getId();
		}
	}

	public void setData(long topicId) {
		this.mTopicId = topicId;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (FragmentActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageCache = new IPageCache();
		mPref = Preference.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new BgTask().execute();
		if (IsFirstEnter) {
			showView(VIEW_TYPE_LOADING);
		}
		requestData(RefreshType._REFRESH_TYPE_REFRESH);
	}

	@Override
	protected RefreshableViewWrapper<?> getViewWrapper() {
		View view = mInflater.inflate(R.layout.race_topic_header, null);
		mRaceTopicRlt = (RelativeLayout) view
				.findViewById(R.id.race_topic_desc_rlt);
		mBannerImg = (ImageView) view.findViewById(R.id.race_topic_banner_img);
		mBannerTitle = (TextView) view.findViewById(R.id.race_topic_banner_txt);
		mTopicDesc = (TextView) view.findViewById(R.id.race_topic_desc);
		mTopicTitle = (TextView) view.findViewById(R.id.race_topic_title_txt);
		mRaceTopicRlt.setPadding(Util.dip2px(mActivity, 15),
				Util.dip2px(mActivity, 15), Util.dip2px(mActivity, 15),
				Util.dip2px(mActivity, 8));
		ListView listView = (ListView) mDataViewConverter.getDataView();
		listView.setBackgroundResource(R.color.race_topic_listview_bg_color);
		return mDataViewConverter.getViewWrapper(view);
	}

	@Override
	protected void requestData(final int refreType) {
		UnionRaceTopicModel.getRaceTopicList(
				new ResponseListener<GetRaceTopicRsp>(mActivity) {

					@Override
					public void onResponse(GetRaceTopicRsp arg0) {
						if (IsFirstEnter && arg0 != null
								&& arg0.getUnionList() != null
								&& arg0.getUnionList().size() > 0) {
							saveListToDisk(arg0);
						}
						if (arg0 != null && arg0.getUnionList() != null) {
							mAttachInfo = arg0.getAttachInfo();
							refreshHeader(arg0);
						}
						if (arg0 != null && arg0.getUnionList() != null) {
							if (refreType == RefreshType._REFRESH_TYPE_LOAD_MORE) {
								requestFinish(refreType, arg0.getUnionList(),
										arg0.getHasMore(), false, false);
							} else {
								requestFinish(refreType, arg0.getUnionList(),
										arg0.getHasMore(), true, false);
							}
						} else {
							requestFinish(refreType, null, false, true, false);
						}
					}

					@Override
					public void onError(Exception e) {
						super.onError(e);
						requestFinish(refreType, null, false, false, false);
					}
				}, mTopicId, mAttachInfo, refreType);
	}

	@Override
	protected void requestFinish(int refresh, ArrayList<UnionInfo> data,
			boolean hasMore, boolean replace, boolean error) {
		super.requestFinish(refresh, data, hasMore, replace, error);
		if (IsFirstEnter && data != null && data.size() > 0) {
			showView(VIEW_TYPE_DATA);
			IsFirstEnter = false;
		} else if (IsFirstEnter) {
			showView(VIEW_TYPE_EMPTY);
		}
	}

	private void refreshHeader(GetRaceTopicRsp arg0) {
		if (arg0 != null
				&& !TextUtils.isEmpty(arg0.getTitle())
				&& (((UnionRaceTopicActivity) mActivity).mTpye == UnionRaceTopicActivity._RACE_TOPIC_ID)) {
			((UnionRaceTopicActivity) mActivity).mActionBar.setTitle(arg0
					.getTitle());
		}
		if (arg0 != null && !TextUtils.isEmpty(arg0.getImg())) {
			mImageLoader.displayImage(arg0.getImg(), mBannerImg,
					SwitchImageLoader.DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER);
		}
		if (arg0 != null && arg0.getHeat() >= 0) {
			if (getActivity() != null) {
				String heat = String.format(getActivity().getResources()
						.getString(R.string.race_join_count), arg0.getHeat());
				int endPos = heat.lastIndexOf("人");
				SpannableString str = new SpannableString(heat);
				str.setSpan(new ForegroundColorSpan(getActivity()
						.getResources()
						.getColor(R.color.race_topic_title_color)), 2, endPos,
						SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
				mBannerTitle.setText(str);
			}
		}
		if (arg0 != null && !TextUtils.isEmpty(arg0.getSummary())) {
			SpannableStringBuilder desc = new SpannableStringBuilder();
			desc.append("　　　    ");
			desc.append(arg0.getSummary());
			mTopicDesc.setText(desc);
		}
		if (arg0 != null && !TextUtils.isEmpty(arg0.getSubTitle())) {
			mTopicTitle.setText(arg0.getSubTitle());
		}
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {
		super.onItemClick(parent, adapter, view, position, id);
		UnionInfo unionInfo = (UnionInfo) adapter.getItem(position);
		UnionInfoActivity.startActivity(getActivity(), unionInfo);
	}

	@Override
	protected boolean isRefreshable() {
		return true;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	protected boolean isRefreshableHead() {
		return true;
	}

	@Override
	protected ImageAdapter<UnionInfo> initAdapter() {
		mUnionListAdapter = new UnionListAdapter(mActivity);
		mUnionListAdapter.setUnionType(UnionListAdapter.UNION_RACE_TOPIC);
		return mUnionListAdapter;
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

	protected void saveListToDisk(GetRaceTopicRsp param) {
		String key = Constants.UNION_RACE_TOPIC + mTopicId;
		mSaveCacheTask.execute(key, param, Constants.CACHE_MYFAVOR_DURATION,
				true);
	}

	private class BgTask extends BackgroundTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			mRsp = getResponseFromDisk();
			if (mRsp != null && mRsp.getUnionList() != null) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean needReload) {
			// if (mRsp != null) {
			// mAttachInfo = mRsp.getAttachInfo();
			// refreshHeader(mRsp);
			// }
			if (needReload) {
				requestFinish(RefreshType._REFRESH_TYPE_REFRESH,
						mRsp.getUnionList(), false, true, false);
			}
			super.onPostExecute(needReload);
		}
	}

	protected GetRaceTopicRsp getResponseFromDisk() {
		GetRaceTopicRsp rsp = mPageCache.getJceObject(
				Constants.UNION_RACE_TOPIC + mTopicId, new GetRaceTopicRsp());
		return rsp;
	}

}
