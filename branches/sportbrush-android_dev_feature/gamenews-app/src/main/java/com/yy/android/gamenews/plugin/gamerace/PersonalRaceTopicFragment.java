package com.yy.android.gamenews.plugin.gamerace;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetRaceTopicRsp;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.jcewrapper.GetRaceTopicRspLocal;
import com.yy.android.gamenews.ui.ArticleListFragment;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class PersonalRaceTopicFragment extends
		ArticleListFragment<GetRaceTopicRsp, GetRaceTopicRspLocal> {

	private long raceTopicId;
	private View headerView;
	private OnDataLoadedListener onDataLoadedListener;

	public interface OnDataLoadedListener {
		void onDataLoaded(String title);
	}

	public static PersonalRaceTopicFragment newInstance(long raceTopicId) {
		PersonalRaceTopicFragment fragment = new PersonalRaceTopicFragment();
		Bundle args = new Bundle();
		args.putLong(PersonalRaceTopicActivity.RACE_TOPIC, raceTopicId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		raceTopicId = getArguments().getLong(
				PersonalRaceTopicActivity.RACE_TOPIC);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup parentView = (ViewGroup) super.onCreateView(inflater,
				container, savedInstanceState);
		return parentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GetRaceTopicRsp rsp = getRspInstance();
		if (rsp != null) {
			refreshHeader();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		checkExpire();
	}

	@SuppressLint("InflateParams")
	@Override
	protected RefreshableViewWrapper<?> getViewWrapper() {
		headerView = mInflater.inflate(R.layout.race_topic_header, null);
		headerView.findViewById(R.id.race_topic_title_flt).setVisibility(
				View.GONE);
		headerView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
		return mDataViewConverter.getViewWrapper(headerView);
	}

	private void refreshHeader() {
		GetRaceTopicRsp mRsp = getRspInstance();
		if (mRsp == null) {
			return;
		}

		SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
		mImageLoader
				.displayImage(mRsp.getImg(), (ImageView) headerView
						.findViewById(R.id.race_topic_banner_img),
						SwitchImageLoader.DEFAULT_ARTICLE_ITEM_BIG_DISPLAYER);

		if (mRsp.getHeat() > 0) {
			String heat = String.format(
					getResources().getString(R.string.race_join_count),
					mRsp.getHeat());
			int endPos = heat.lastIndexOf("人");
			SpannableString str = new SpannableString(heat);
			str.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.actionbar_bg)), 2, endPos,
					SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
			((TextView) headerView.findViewById(R.id.race_topic_banner_txt))
					.setText(str);
		}
		String summary = mRsp.getSummary();
		if (summary != null && !summary.equals("")) {
			SpannableStringBuilder desc = new SpannableStringBuilder();
			desc.append("　　　    ");
			desc.append(summary);

			((TextView) headerView.findViewById(R.id.race_topic_desc))
					.setText(desc);
			headerView.findViewById(R.id.race_topic_desc_rlt).setVisibility(
					View.VISIBLE);
		} else {
			headerView.findViewById(R.id.race_topic_desc_rlt).setVisibility(
					View.GONE);
		}
	}

	@Override
	protected void requestDataImpl(final int refresh, Object attachInfo) {
		if (raceTopicId < 0) {
			return;
		}
		UnionRaceTopicModel.getRaceTopicList(
				new ResponseListener<GetRaceTopicRsp>(getActivity()) {

					@Override
					public void onResponse(GetRaceTopicRsp data) {
						requestFinish(refresh, data, false);
						if (data != null && data.getTitle() != null
								&& onDataLoadedListener != null) {
							onDataLoadedListener.onDataLoaded(data.getTitle());
						}
					}

					@Override
					public void onError(Exception e) {
						requestFinish(refresh, null, true);
						if (e != null) {
							ToastUtil.showToast(R.string.http_error);
						}
						super.onError(e);
					}
				}, raceTopicId, (String)attachInfo, refresh);
	}

	@Override
	protected void requestFinishImpl(int refresh, GetRaceTopicRsp data,
			boolean error) {
		if (data != null && refresh != RefreshType._REFRESH_TYPE_LOAD_MORE) {
			refreshHeader();
		}

		super.requestFinishImpl(refresh, data, error);
	}

	@Override
	protected GetRaceTopicRsp newRspObject() {
		return new GetRaceTopicRsp();
	}

	protected String getCacheKey() {
		if (raceTopicId < 0) {
			return null;
		}
		String key = Constants.CACHE_KEY_PERSONAL_RACETOPIC_LIST + raceTopicId;

		return key;
	}

	@Override
	protected boolean isExpire() {
		List<ArticleInfo> dataList = getDataSource();
		if (dataList != null && dataList.size() > 0) { // 如果列表为空，不需要刷新
			if (mPageCache.isExpire(getCacheKey())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		return false;
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected GetRaceTopicRspLocal initRspWrapper() {
		return new GetRaceTopicRspLocal();
	}

	public void setOnDataLoadedListener(
			OnDataLoadedListener onDataLoadedListener) {
		this.onDataLoadedListener = onDataLoadedListener;
	}
}
