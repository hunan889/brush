package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetColumnChannelListRsp;
import com.duowan.gamenews.SearchChannelRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.XListView;
import com.yy.android.gamenews.ui.view.XListView.IXListViewListener;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ChannelMoreFragment extends BaseFragment {

	// private TextView mLable;
	private XListView mListView;
	private GridItemViewAdapter mAdapter;
	private String mType;
	private String mAttachInfo;
	private boolean mHasMore = true;
	private int mColumnId = -1;
	private String mKeyWord;
	private int mTips = 0;
	private List<Channel> mChannels = new ArrayList<Channel>();
	private ArrayList<Channel> mOriginalChannels = new ArrayList<Channel>();

	IXListViewListener mIxListViewListener = new IXListViewListener() {
		public void onRefresh() {

		}

		public void onLoadMore() {
			if (Constants.EXTRA_GRID_FG_TYPE_MORE.equals(mType)) {
				if (mHasMore) {
					getColumnChannelList(mColumnId, mAttachInfo, 18);
				} else {
					mListView.stopLoadMore();
					mListView.setFooterEmpty(true);
				}
			} else if (Constants.EXTRA_GRID_FG_TYPE_SEARCH.equals(mType)) {
				if (mHasMore) {
					searchChannel(mKeyWord, mAttachInfo, 18);
				} else {
					mListView.stopLoadMore();
					mListView.setFooterEmpty(true);
				}
			}
		}
	};

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
		}
	};

	public static ChannelMoreFragment newInstance(String key,
			String attachInfo, boolean hasMore, ArrayList<Channel> channels) {
		ChannelMoreFragment fragment = new ChannelMoreFragment();
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_GRID_FG_TYPE,
				Constants.EXTRA_GRID_FG_TYPE_SEARCH);
		args.putString(Constants.EXTRA_GRID_FG_KEY_WORD, key);
		args.putBoolean(Constants.EXTRA_HAS_MORE, hasMore);
		args.putString(Constants.EXTRA_ATTACHINFO, attachInfo);
		args.putSerializable(Constants.EXTRA_GRID_FG_CHANNELS, channels);
		fragment.setArguments(args);
		return fragment;
	}

	public static ChannelMoreFragment newInstance(int columnId,
			String columnName) {
		ChannelMoreFragment fragment = new ChannelMoreFragment();
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_GRID_FG_TYPE,
				Constants.EXTRA_GRID_FG_TYPE_MORE);
		args.putInt(Constants.EXTRA_COLUMN_ID, columnId);
		args.putString(Constants.EXTRA_COLUMN_NAME, columnName);
		fragment.setArguments(args);
		return fragment;
	}

	public void refreshSearchResults(String key, boolean hasMore,
			String attachInfo, ArrayList<Channel> channels) {
		if (mAdapter == null) {
			return;
		}
		mAdapter.clearAll();
		if (channels != null && channels.size() > 0) {
			// mLable.setVisibility(View.GONE);
			mKeyWord = key;
			mHasMore = hasMore;
			mAttachInfo = attachInfo;
			mAdapter.append(channels);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChannels = Preference.getInstance().getMyFavorChannelList();
		if (mChannels == null) {
			mChannels = new ArrayList<Channel>();
		}
		mOriginalChannels.addAll(mChannels);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.channel_more_fragment, container,
				false);
		// mLable = (TextView) view.findViewById(R.id.channel_more_lable);
		mListView = (XListView) view.findViewById(R.id.listview);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		SubscribeEvent event = Util.getSubscribeEvent(mOriginalChannels,
				mChannels);
		if (event != null) {
			Preference.getInstance().saveMyFavorChannelList(mChannels);
			ChannelModel.updateMyFavChannelList(getActivity(),
					(ArrayList<Channel>) mChannels);
			EventBus.getDefault().post(event);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mAdapter = new GridItemViewAdapter(getActivity());
		// mGridView.setAdapter(mAdapter);
		mListView.setAdapter(mAdapter);
		mListView.setFooterEmpty(false);
		mListView.setPullLoadEnable(true);
		mListView.setFooterAutoLoad(true);
		mListView.setXListViewListener(mIxListViewListener);

		Bundle arguments = getArguments();
		if (arguments != null) {
			mType = arguments.getString(Constants.EXTRA_GRID_FG_TYPE);
			if (Constants.EXTRA_GRID_FG_TYPE_SEARCH.equals(mType)) {
				// mLable.setVisibility(View.GONE);
				mKeyWord = arguments
						.getString(Constants.EXTRA_GRID_FG_KEY_WORD);

				mHasMore = arguments.getBoolean(Constants.EXTRA_HAS_MORE);
				mAttachInfo = arguments.getString(Constants.EXTRA_ATTACHINFO);
				ArrayList<Channel> channels = (ArrayList<Channel>) arguments
						.getSerializable(Constants.EXTRA_GRID_FG_CHANNELS);
				if (channels != null && channels.size() > 0) {
					mAdapter.append(channels);
				}
			} else if (Constants.EXTRA_GRID_FG_TYPE_MORE.equals(mType)) {
				mColumnId = arguments.getInt(Constants.EXTRA_COLUMN_ID);
				String columnName = arguments
						.getString(Constants.EXTRA_COLUMN_NAME);
				// mLable.setVisibility(View.INVISIBLE);
				// mLable.setText(columnName);

				mListView.manualLoadMore();
				// getColumnChannelList(mColumnId, null, 18);
			}
		}
	}

	public class GridItemViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<Channel> mData;

		public GridItemViewAdapter(Context c) {
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mData = new ArrayList<Channel>();
		}

		public int getCount() {
			if (mData.size() == 0) {
				return 0;
			}
			int count = (int) (mData.size() / 3);
			if (mData.size() > 3 * count) {
				count++;
			}
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public void clearAll() {
			mData.clear();
		}

		public void append(ArrayList<Channel> datas) {
			for (Channel data : datas) {
				Util.validChannelData(data);
			}
			mData.addAll(datas);
			notifyDataSetChanged();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.channel_more_items,
						null);
			}

			View channel1 = convertView.findViewById(R.id.channel_1);
			View channel2 = convertView.findViewById(R.id.channel_2);
			View channel3 = convertView.findViewById(R.id.channel_3);

			final int count = mData.size();
			int startIndex = position * 3;

			if (startIndex < count) {
				Channel channelData = mData.get(startIndex);
				channel1.setVisibility(View.VISIBLE);
				setChannelsView(channel1, channelData);
				startIndex++;
			} else {
				channel1.setVisibility(View.INVISIBLE);
			}
			if (startIndex < count) {
				Channel channelData = mData.get(startIndex);
				channel2.setVisibility(View.VISIBLE);
				setChannelsView(channel2, channelData);
				startIndex++;
			} else {
				channel2.setVisibility(View.INVISIBLE);
			}
			if (startIndex < count) {
				Channel channelData = mData.get(startIndex);
				channel3.setVisibility(View.VISIBLE);
				setChannelsView(channel3, channelData);
			} else {
				channel3.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	public void setChannelsView(View channelView, Channel channelData) {
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

	private void updateChangedChannels(Channel channel) {
		// if (Util.isSubscribedChannel(mChangedChannels, channel)) {
		// mChangedChannels.remove(channel);
		// } else {
		// Util.validChannelData(channel);
		// mChangedChannels.add(channel);
		// }
	}

	private void getColumnChannelList(int columnId, String attachInfo, int count) {
		if (getActivity() == null) {
			return;
		}
		ResponseListener<GetColumnChannelListRsp> responseListener = new ResponseListener<GetColumnChannelListRsp>(
				getActivity()) {
			public void onResponse(GetColumnChannelListRsp response) {
				if (mListView.isPullLoading()) {
					mListView.stopLoadMore();
				}
				mHasMore = response.getHasMore();
				mAttachInfo = response.getAttachInfo();
				ArrayList<Channel> list = response.getChannelList();
				mAdapter.append(list);
				// mLable.setVisibility(View.VISIBLE);
				if (!mHasMore) {
					mListView.setFooterEmpty(true);
				}
			}
		};
		ChannelModel.getColumnChannelList(responseListener, columnId,
				attachInfo, count);
	}

	private void searchChannel(String keyWord, String attachInfo, int count) {
		if (getActivity() == null) {
			return;
		}
		ResponseListener<SearchChannelRsp> responseListener = new ResponseListener<SearchChannelRsp>(
				getActivity()) {
			public void onResponse(SearchChannelRsp response) {

				if (mListView.isPullLoading()) {
					mListView.stopLoadMore();
				}
				mHasMore = response.getHasMore();
				mAttachInfo = response.getAttachInfo();
				ArrayList<Channel> list = response.getChannelList();
				mAdapter.append(list);
				if (!mHasMore) {
					mListView.setFooterEmpty(true);
				}
			}
		};
		ChannelModel
				.searchChannel(responseListener, keyWord, attachInfo, count);
	}

}
