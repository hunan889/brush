package com.yy.android.gamenews.ui.channeldetail;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetMyFavChannelListRsp;
import com.duowan.gamenews.RefreshType;
import com.yy.android.gamenews.event.FragmentCallbackEvent;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.event.UpdateChannelListEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.BaseListFragment;
import com.yy.android.gamenews.ui.ChannelDepotActivity;
import com.yy.android.gamenews.ui.ViewPagerActivity;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory.PageType;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ChannelListFragment extends BaseListFragment<Channel> {

	private ArrayList<Channel> mChannelList = new ArrayList<Channel>();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refreshData();

		View dataView = getDataView();
		if (dataView instanceof ListView) {
			ListView listView = (ListView) dataView;
			Drawable drawable = getActivity().getResources().getDrawable(
					R.color.global_divider_color_2);
			listView.setDivider(drawable);
			listView.setDividerHeight(Util.dip2px(getActivity(), 10));
			listView.setHeaderDividersEnabled(true);
			listView.setFooterDividersEnabled(true);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected void requestData(final int refreType) {
		mChannelList.clear();
		List<Channel> channelList = Preference.getInstance()
				.getMyFavorChannelList();
		if (channelList != null) {
			mChannelList.addAll(channelList);
		}
		if (mChannelList.size() == 0) {
			setEmptyText(strEmptyAddChannel);
			requestFinish(refreType, mChannelList, false, true, false);
			notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD,
					null);
			return;
		}

		requestFinish(refreType, mChannelList, false, true, false);
		ChannelModel
				.getMyFavChannelList(new ResponseListener<GetMyFavChannelListRsp>(
						(FragmentActivity) getActivity()) {

					@Override
					public void onResponse(GetMyFavChannelListRsp rsp) {
						if (rsp != null) {
							mChannelList.clear();
							mChannelList.addAll(rsp.getChannelList());
						}
						updateChannel(mChannelList);
						requestFinish(refreType, mChannelList, false, true, false);
					}
				});
	}

	private void updateChannel(List<Channel> channelList) {
		Preference.getInstance().saveMyFavorChannelList(channelList);
	}

	@Override
	public void onResume() {
		if (mEvent != null) {
			boolean hasChanged = mEvent.isSubscribeChanged;
			if (hasChanged) {
				refreshData();
			}
			mEvent = null;
		}
		super.onResume();
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
	protected ImageAdapter<Channel> initAdapter() {
		return new ChannelListAdapter(getActivity());
	}

	@Override
	public void onItemClick(View parent, Adapter adapter, View view,
			int position, long id) {

		Channel channel = getAdapter().getItem(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ChannelDetailFragment.KEY_CHANNEL, channel);
		ViewPagerActivity.startViewPagerActivity(getActivity(),
				PageType.CHANNEL_DETAIL, channel.getName(), bundle);
	}

	private SubscribeEvent mEvent;

	public void onEvent(SubscribeEvent event) {
		mEvent = event;
	}

	public void onEvent(UpdateChannelListEvent event) {
		mChannelList.clear();
		List<Channel> channelList = Preference.getInstance()
				.getMyFavorChannelList();
		if (channelList != null) {
			mChannelList.addAll(channelList);
		}
		if (mChannelList.size() == 0) {
			setEmptyText(strEmptyAddChannel);
			requestFinish(RefreshType._REFRESH_TYPE_REFRESH, mChannelList,
					false, true, false);
			notifyListener(FragmentCallbackEvent.FRGMT_LIST_SCROLL_TO_HEAD,
					null);
			return;
		}

		requestFinish(RefreshType._REFRESH_TYPE_REFRESH, mChannelList, false,
				true, false);
	}

	@Override
	protected void onEmptyViewClicked() {
		Intent intent = new Intent(getActivity(), ChannelDepotActivity.class);
		startActivity(intent);
	}
}
