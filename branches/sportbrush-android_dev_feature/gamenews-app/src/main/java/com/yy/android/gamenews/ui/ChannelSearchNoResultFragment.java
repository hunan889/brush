package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.gamenews.Channel;
import com.duowan.gamenews.Column;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ChannelSearchNoResultFragment extends BaseFragment {

	private LinearLayout mLayout;
	private int mTips = 0;
	private List<Channel> mChannels = new ArrayList<Channel>();
	private ArrayList<Channel> mOriginalChannels = new ArrayList<Channel>();
//	private boolean mHasChanged = false;

	public static ChannelSearchNoResultFragment newInstance(
			ArrayList<Column> columns) {
		ChannelSearchNoResultFragment fragment = new ChannelSearchNoResultFragment();
		Bundle args = new Bundle();
		args.putSerializable(Constants.EXTRA_SEARCH_FG_COLUMN, columns);
		fragment.setArguments(args);
		return fragment;
	}

	public void refreshSearchNoResult(ArrayList<Column> columns) {
		if (columns != null && columns.size() > 0) {
			updateColumnView(columns);
		}
	}

	public static ChannelSearchNoResultFragment newInstance() {
		ChannelSearchNoResultFragment fragment = new ChannelSearchNoResultFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChannels = Preference.getInstance().getMyFavorChannelList();
		if (mChannels == null) {
			mChannels = new ArrayList<Channel>();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.channel_search_no_result,
				container, false);
		mLayout = (LinearLayout) view.findViewById(R.id.channel_container);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		if (arguments != null) {
			ArrayList<Column> columns = (ArrayList<Column>) getArguments()
					.getSerializable(Constants.EXTRA_SEARCH_FG_COLUMN);
			if (columns != null && columns.size() > 0) {
				updateColumnView(columns);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SubscribeEvent event = Util.getSubscribeEvent(mOriginalChannels, mChannels);
		if(event != null) {
			Preference.getInstance().saveMyFavorChannelList(mChannels);
			ChannelModel.updateMyFavChannelList(getActivity(),
					(ArrayList<Channel>) mChannels);
			EventBus.getDefault().post(event);
		}
	}

	public void updateColumnView(ArrayList<Column> list) {
		if (list.size() == 0) {
			return;
		}

		mLayout.removeAllViews();

		LayoutInflater inflater = getLayoutInflater(null);

		for (Column item : list) {
			View view = inflater.inflate(R.layout.channel_columns_recommends,
					null);
			TextView columnName = (TextView) view
					.findViewById(R.id.column_name);

			View channel1 = view.findViewById(R.id.channel_1);
			View channel2 = view.findViewById(R.id.channel_2);
			View channel3 = view.findViewById(R.id.channel_3);
			View channel4 = view.findViewById(R.id.channel_4);
			View channel5 = view.findViewById(R.id.channel_5);
			View channel6 = view.findViewById(R.id.channel_6);

			columnName.setText(item.getName());

			ArrayList<Channel> channels = item.getChannelList();
			int i = 0;
			if (++i <= channels.size()) {
				channel1.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel1, channels.get(i - 1));
			}
			if (++i <= channels.size()) {
				channel2.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel2, channels.get(i - 1));
			}
			if (++i <= channels.size()) {
				channel3.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel3, channels.get(i - 1));
			}
			if (++i <= channels.size()) {
				channel4.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel4, channels.get(i - 1));
			}
			if (++i <= channels.size()) {
				channel5.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel5, channels.get(i - 1));
			}
			if (++i <= channels.size()) {
				channel6.setVisibility(View.VISIBLE);
				updateColumnChannelsView(channel6, channels.get(i - 1));
			}
			mLayout.addView(view);
		}
	}

	public void updateColumnChannelsView(View channelView, Channel channelData) {
		if (channelData == null) {
			return;
		}
		FrameLayout frameLayout = (FrameLayout) channelView
				.findViewById(R.id.channel_framelayout);
		ImageView channelImage = (ImageView) channelView
				.findViewById(R.id.channel_image);
		TextView channelName = (TextView) channelView
				.findViewById(R.id.channel_name);
		SwitchImageLoader.getInstance().displayImage(channelData.getImage(),
				channelImage, SwitchImageLoader.DEFAULT_CHANNEL_BIG_DISPLAYER,
				true);
		FrameLayout channelChosen = (FrameLayout) channelView
				.findViewById(R.id.channel_chosen);
		if (Util.isSubscribedChannel(mChannels, channelData)) {
			channelChosen.setVisibility(View.VISIBLE);
		} else {
			channelChosen.setVisibility(View.INVISIBLE);
		}
		channelName.setText(channelData.getName());
		frameLayout.setTag(channelData);
		frameLayout.setOnClickListener(mSelectChannelListener);
	}

	OnClickListener mSelectChannelListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Channel channelData = (Channel) v.getTag();
			if (channelData == null) {
				return;
			}
			View view = v.findViewById(R.id.channel_chosen);
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.INVISIBLE);
				Util.removeChannelIfExist(mChannels, channelData);
			} else if (view.getVisibility() == View.INVISIBLE) {
				if (mChannels.size() >= Constants.SUBSCRIBE_MOST_LIMIT) {
					String tips;
					switch (mTips) {
					case 0:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy0);
						mTips++;
						break;
					case 1:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy1);
						mTips++;
						break;
					case 2:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy2);
						mTips++;
						break;
					case 3:
						tips = getResources().getString(
								R.string.channel_manage_too_mandy3);
						mTips++;
						break;

					default:
						mTips = 1;
						tips = getResources().getString(
								R.string.channel_manage_too_mandy0);
						break;
					}
					ToastUtil.showToast(tips);
					return;
				}
				if (!Util.isSubscribedChannel(mChannels, channelData)) {
					Util.validChannelData(channelData);
					mChannels.add(channelData);
				}
				view.setVisibility(View.VISIBLE);
			}
			updateChangedChannels(channelData);
//			mHasChanged = true;
		}
	};

	private void updateChangedChannels(Channel channel) {
//		if (Util.isSubscribedChannel(mChangedChannels, channel)) {
//			mChangedChannels.remove(channel);
//		} else {
//			Util.validChannelData(channel);
//			mChangedChannels.add(channel);
//		}
	}

}
