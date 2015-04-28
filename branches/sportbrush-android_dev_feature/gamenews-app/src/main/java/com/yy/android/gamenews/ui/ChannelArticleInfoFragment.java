package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleCategory;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetChannelArticleListRsp;
import com.duowan.gamenews.HotChannel;
import com.duowan.gamenews.RefreshType;
import com.duowan.gamenews.SubType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.CheckExpireEvent;
import com.yy.android.gamenews.event.ClickHotChannelEvent;
import com.yy.android.gamenews.event.RefreshEvent;
import com.yy.android.gamenews.jcewrapper.GetChannelArticleListRspLocal;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ChannelArticleInfoFragment
		extends
		ArticleListFragment<GetChannelArticleListRsp, GetChannelArticleListRspLocal> {
	private Channel mChannel;
	private ArticleCategory mCategory; // mCategory是游戏刷子里面的频道分类
	private boolean mIsSubChannel;
	public static final String KEY_CHANNEL = "channel";
	private static final String KEY_IS_SUB_CHANNEL = "is_sub_channel";
	private static final String KEY_TYPE = "type";
	public static final String KEY_ARTICLE_CATEGORY = "article_category";
	private int mType;
	private View hotChannelView;

	public static ChannelArticleInfoFragment newInstance(Channel channel,
			ArticleCategory category) {
		return newInstance(channel, Constants.CACH_SIZE_HOME_ARTI_LIST,
				NewsFragment.TYPE_MY_FAVOR, category);
	}

	public static ChannelArticleInfoFragment newInstance(Channel channel,
			int cacheSize, int viewType, int type, ArticleCategory category) {
		ChannelArticleInfoFragment fragment = new ChannelArticleInfoFragment();
		fragment.setType(viewType);
		Bundle args = new Bundle();
		args.putSerializable(KEY_CHANNEL, channel);
		args.putInt(KEY_CACHE_SIZE, cacheSize);
		args.putInt(KEY_TYPE, type);
		args.putSerializable(KEY_ARTICLE_CATEGORY, category);
		fragment.setArguments(args);
		return fragment;
	}

	public static ChannelArticleInfoFragment newInstance(Channel channel,
			int cacheSize, int type, ArticleCategory category) {
		return newInstance(channel, cacheSize, getType(channel, category),
				type, category);
	}

	public static ChannelArticleInfoFragment newInstance(Channel channel,
			int viewType, ArticleCategory category) {
		return newInstance(channel, Constants.CACH_SIZE_HOME_ARTI_LIST,
				viewType, NewsFragment.TYPE_MY_FAVOR, category);
	}

	private static int getType(Channel channel, ArticleCategory category) {
		int type = DataViewConverterFactory.TYPE_LIST_NORMAL;
		if (category != null) {
			switch (category.getId()) {
			case SubType._SUBTYPE_FALL: {
				type = DataViewConverterFactory.TYPE_LIST_WATERFALL;
				break;
			}
			}
		}
		return type;
	}

	public static ChannelArticleInfoFragment newInstance(Channel channel,
			boolean isSubChannel, boolean isDark) {
		int type = DataViewConverterFactory.TYPE_LIST_NORMAL;
		ChannelArticleInfoFragment fragment = new ChannelArticleInfoFragment();
		fragment.setIsDarkTheme(isDark);
		fragment.setType(type);
		Bundle args = new Bundle();
		args.putBoolean(KEY_IS_SUB_CHANNEL, isSubChannel);
		args.putSerializable(KEY_CHANNEL, channel);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (bundle != null) {
			mIsSubChannel = bundle.getBoolean(KEY_IS_SUB_CHANNEL);
			mChannel = (Channel) bundle.getSerializable(KEY_CHANNEL);
			mCategory = (ArticleCategory) bundle
					.getSerializable(KEY_ARTICLE_CATEGORY);
			mType = bundle.getInt(KEY_TYPE);
		} else {
			mIsSubChannel = getArguments().getBoolean(KEY_IS_SUB_CHANNEL);
			mChannel = (Channel) getArguments().getSerializable(KEY_CHANNEL);
			mCategory = (ArticleCategory) getArguments().getSerializable(
					KEY_ARTICLE_CATEGORY);
			mType = getArguments().getInt(KEY_TYPE);
		}

		Log.d(LOG_TAG, "[onCreate], channelId = " + mChannel.getName());
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return !mIsSubChannel;
	}

	@Override
	protected void showUpdatedToast(int count) {

		if (mRes == null) {
			return;
		}
		if (mChannel != null
				&& mType == NewsFragment.TYPE_HEADLINES
				&& Constants
						.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
			if (count < 0) {
				return;
			}
			if (isAdded()) {
				setStrUpdatedCount(mRes
						.getString(R.string.global_sport_info_update_tip));
				showUpdateTv(count);
			}
		} else if (mCategory != null
				&& mCategory.getId() == SubType._SUBTYPE_VIDEO) {
			setStrUpdatedCount(mRes
					.getString(R.string.global_update_count_video));
			setStrUpdatedCountZero(mRes
					.getString(R.string.global_update_count_zero_video));
			showUpdateTv(count);
		} else {
			super.showUpdatedToast(count);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// 如果是我的最爱的频道，且用户没有添加喜欢的频道，则清空我的最爱的缓存
		// 并提示用户去添加喜欢的频道
		if (!needLoadData()) {
			mPageCache.setJceObject(getCacheKey(), null,
					Constants.CACHE_DURATION_HOMELIST);
			requestFinish(0, null, false);
		}

		GetChannelArticleListRsp rsp = getRspInstance();
		if (rsp != null) {
			updateHotChannelView(rsp.getHotChannel(), rsp.getArticleList());
		}
	}

	@Override
	protected boolean needLoadData() {
		if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
			List<Channel> channelList = mPref.getMyFavorChannelList();
			if (channelList == null || channelList.size() == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected ImageAdapter<ArticleInfo> initAdapter() {
		ArticleListAdapter adapter = (ArticleListAdapter) super.initAdapter();
		adapter.setChannel(mChannel);
		adapter.setCategory(mCategory);
		return adapter;
	}

	public Channel getChannel() {
		return mChannel;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_CHANNEL, mChannel);
		outState.putSerializable(KEY_ARTICLE_CATEGORY, mCategory);
		outState.putSerializable(KEY_IS_SUB_CHANNEL, mIsSubChannel);
		outState.putInt(KEY_TYPE, mType);
		super.onSaveInstanceState(outState);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void requestDataImpl(final int refresh, Object attachInfo) {

		int channelId = 0;
		int subChannelId = 0;
		int subType = 0;
		if (mChannel == null) {
			requestFinish(refresh, null, true);
			return;
		}

		if (mIsSubChannel) {
			subChannelId = mChannel.getId();
		} else {
			channelId = mChannel.getId();
		}

		if (mCategory != null) {
			subType = mCategory.getId();
		}

		ArticleModel.getArticleList(
				new ResponseListener<GetChannelArticleListRsp>(getActivity()) {
					@Override
					public void onResponse(GetChannelArticleListRsp data) {

						if (refresh == RefreshType._REFRESH_TYPE_REFRESH
								&& data != null) {
							ArrayList<ArticleInfo> articleList = data
									.getArticleList();
							if (articleList != null) {
								for (int j = 0; j < articleList.size(); j++) {
									articleList
											.get(j)
											.setTime(
													(int) (System
															.currentTimeMillis() / 1000));
								}
							}

						}

						requestFinish(refresh, data, false);
					}

					@Override
					public void onError(Exception e) {
						requestFinish(refresh, null, true);
						super.onError(e);
					}
				}, // Listener
				refresh, channelId, subChannelId, subType,
				(Map<Integer, String>) attachInfo, false);
	}

	@Override
	protected GetChannelArticleListRsp newRspObject() {
		return new GetChannelArticleListRsp();
	}

	protected String getCacheKey() {
		if (mChannel == null) {
			return null;
		}
		String key = "";
		if (mIsSubChannel) {
			key = Constants.CACHE_KEY_HOME_LIST + "subchannel"
					+ mChannel.getId();
		} else {
			key = Constants.CACHE_KEY_HOME_LIST + mChannel.getId();
		}

		if (mCategory != null) {
			key += "subtype" + String.valueOf(mCategory.getId());
		}

		return key;
	}

	@Override
	protected boolean needCheckRefresh(RefreshEvent event) {

		Channel channel = event.mChannel;
		ArticleCategory category = event.mCategory;
		if (mChannel == channel // 考虑到为空的情况
				|| (channel != null && channel.getId() == mChannel.getId())) {
			if (category != null) {
				if (category.getId() == mCategory.getId()) {
					return true;
				} else {
					return false;
				}
			}
			return true;
		}

		return super.needCheckRefresh(event);
	}

	@Override
	protected boolean needCheckExpire(CheckExpireEvent event) {
		Channel channel = event.mChannel;
		if (mChannel == channel // 考虑到为空的情况
				|| (channel != null && channel.getId() == mChannel.getId())) {
			return true;
		}
		return super.needCheckExpire(event);
	}

	@Override
	protected boolean isExpire() {
		if (mChannel != null) {
			List<ArticleInfo> dataList = getDataSource();
			if (dataList != null && dataList.size() > 0) { // 如果列表为空，不需要刷新
				if (mPageCache.isExpire(getCacheKey())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void onEmptyViewClicked() {
		if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
			List<Channel> channelList = mPref.getMyFavorChannelList();
			if (channelList == null || channelList.size() == 0) {
				Intent intent = new Intent(getActivity(),
						ChannelDepotActivity.class);
				startActivity(intent);
			} else {
				super.onEmptyViewClicked();
			}

		} else {
			super.onEmptyViewClicked();
		}
	}

	@Override
	protected String getLastRefreshTimeKey() {
		int id = 0;
		if (mChannel != null) {
			id = mChannel.getId();
		}
		String key = Constants.CACHE_KEY_LAST_REFRSH_TIME_ARTICLE + id;
		if (mCategory != null) {
			key += "subtype" + mCategory.getId();
		}
		return key;
	}

	@Override
	protected String getEmptyText(boolean error) {
		String emptyText = "";
		if (Constants.MY_FAVOR_CHANNEL_ID == mChannel.getId()) {
			List<Channel> channelList = mPref.getMyFavorChannelList();
			if (channelList == null || channelList.size() == 0) {
				emptyText = strEmptyAddChannel;
			}
		}
		return emptyText;
	}

	@Override
	protected void customizeView(ViewGroup viewGroup) {
		super.customizeView(viewGroup);

	}

	@Override
	protected RefreshableViewWrapper<?> getViewWrapper() {
		if (mChannel.getId() == Constants.RECOMMD_ID
				&& Constants
						.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)) {
			hotChannelView = mInflater.inflate(
					R.layout.hot_channel_header_view, null);
			hotChannelView.setVisibility(View.GONE);
			return mDataViewConverter.getViewWrapper(hotChannelView);
		}
		return super.getViewWrapper();
	}

	private void updateHotChannelView(List<HotChannel> hotChannels,
			ArrayList<ArticleInfo> dataList) {
		if (!Constants.isFunctionEnabled(ECommAppType._Comm_APP_SPORTBRUSH)
				|| mChannel.getId() != Constants.RECOMMD_ID) {
			return;
		}
		if (dataList == null || dataList.size() <= 0) {
			dataList = getDataSource();
		}
		hotChannelView.setVisibility(View.VISIBLE);
		if (hotChannels == null || hotChannels.size() <= 0) {
			hotChannelView.findViewById(R.id.ll_hot_channel).setVisibility(
					View.GONE);
			hotChannelView.findViewById(R.id.ll_channels_view).setVisibility(
					View.GONE);
			if (dataList == null || dataList.size() <= 0) {
				hotChannelView.findViewById(R.id.ll_hot_news).setVisibility(
						View.GONE);
			} else {
				hotChannelView.findViewById(R.id.ll_hot_news).setVisibility(
						View.VISIBLE);
			}
			return;
		}
		hotChannelView.findViewById(R.id.ll_hot_channel).setVisibility(
				View.VISIBLE);
		hotChannelView.findViewById(R.id.ll_channels_view).setVisibility(
				View.VISIBLE);
		if (dataList == null || dataList.size() <= 0) {
			hotChannelView.findViewById(R.id.ll_hot_news).setVisibility(
					View.GONE);
		} else {
			hotChannelView.findViewById(R.id.ll_hot_news).setVisibility(
					View.VISIBLE);
		}

		LinearLayout layout = (LinearLayout) hotChannelView
				.findViewById(R.id.ll_channels_view);
		layout.removeAllViews();
		SwitchImageLoader mImageLoader = SwitchImageLoader.getInstance();
		DisplayImageOptions displayImageOptions;
		for (int i = 0; i < hotChannels.size(); i++) {
			final HotChannel hotChannel = hotChannels.get(i);
			View view = mInflater.inflate(R.layout.list_item_channel_hot, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_order);
			String url = hotChannel.getIcon();
			if (i == 0) {
				displayImageOptions = SwitchImageLoader
						.getDisplayOptions(R.drawable.first);
				mImageLoader.displayImage(url, imageView, displayImageOptions);
			} else if (i == 1) {
				displayImageOptions = SwitchImageLoader
						.getDisplayOptions(R.drawable.second);
				mImageLoader.displayImage(url, imageView, displayImageOptions);
			} else if (i == 2) {
				displayImageOptions = SwitchImageLoader
						.getDisplayOptions(R.drawable.third);
				mImageLoader.displayImage(url, imageView, displayImageOptions);
			} else {
				displayImageOptions = SwitchImageLoader
						.getDisplayOptions(R.drawable.fouth);
				mImageLoader.displayImage(url, imageView, displayImageOptions);
			}
			((TextView) view.findViewById(R.id.channel)).setText(hotChannel
					.getChannel().getName());
			((TextView) view.findViewById(R.id.comment_count)).setText(String
					.valueOf(hotChannel.getComments()));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ClickHotChannelEvent event = new ClickHotChannelEvent();
					event.setChannel(hotChannel.getChannel());
					EventBus.getDefault().post(event);
				}
			});
			layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
	}

	@Override
	protected void requestFinishImpl(int refresh,
			GetChannelArticleListRsp data, boolean error) {

		if (data != null && refresh != RefreshType._REFRESH_TYPE_LOAD_MORE) {
			ArrayList<ArticleInfo> dataList = data.getArticleList();
			updateHotChannelView(data.getHotChannel(), dataList);
		}
		super.requestFinishImpl(refresh, data, error);
	}

	@Override
	protected boolean hasData() {
		if (mChannel != null && mChannel.getId() == Constants.RECOMMD_ID) {
			GetChannelArticleListRsp rsp = getRspInstance();
			return super.hasData()
					|| (rsp != null && rsp.getHotChannel() != null && rsp
							.getHotChannel().size() > 0);
		}
		return super.hasData();
	}

	@Override
	protected boolean needShowUpdatedBubble() {
		if (mCategory != null) {
			return false;
		}
		return super.needShowUpdatedBubble();
	}

	@Override
	protected GetChannelArticleListRspLocal initRspWrapper() {
		return new GetChannelArticleListRspLocal();
	}

}
