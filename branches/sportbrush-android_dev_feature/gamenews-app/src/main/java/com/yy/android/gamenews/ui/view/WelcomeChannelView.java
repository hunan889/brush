package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.UpdateMyFavChannelListRsp;
import com.duowan.gamenews.WelcomeChannel;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.TipsHelper;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class WelcomeChannelView extends FrameLayout implements OnClickListener {

	private View mParent;

	private ListView mListView;
	private Button mSubmitBtn;
	private GridItemViewAdapter mAdapter;
	private TextView mHintView;

	private View mHintLayout;
	private Context mContext;

	public WelcomeChannelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WelcomeChannelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WelcomeChannelView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);

		mParent = inflater.inflate(R.layout.welcome_channel_view, null);
		mSubmitBtn = (Button) mParent.findViewById(R.id.welcome_channel_submit);
		mSubmitBtn.setOnClickListener(this);

		mListView = (ListView) mParent.findViewById(R.id.welcome_channel_list);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		View footer = inflater.inflate(R.layout.welcome_channel_list_footer,
				null);
		mListView.addFooterView(footer, null, false);
		mAdapter = new GridItemViewAdapter(getContext());
		mListView.setAdapter(mAdapter);

		mHintView = (TextView) mParent.findViewById(R.id.welcome_channel_hint);
		mHintLayout = mParent.findViewById(R.id.welcome_channel_hint_layout);
		setClickable(true);
		addView(mParent);
	}

	private OnClickListener mOnSaveClickListener;

	public void setOnSaveClickListener(OnClickListener listener) {
		mOnSaveClickListener = listener;
	}

	public void setChannelList(List<WelcomeChannel> channelList) {
		mAdapter.setDatasource(channelList);
	}

	public class GridItemViewAdapter extends ImageAdapter<WelcomeChannel> {
		private LayoutInflater mInflater;
		private ArrayList<WelcomeChannel> mData;

		public GridItemViewAdapter(Context c) {
			super(c);
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mData = new ArrayList<WelcomeChannel>();
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

		// public WelcomeChannel getItem(int position) {
		// return position;
		// }

		// public long getItemId(int position) {
		// return position;
		// }

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		public void clearAll() {
			mData.clear();
			notifyDataSetChanged();
		}

		public void setDatasource(List<WelcomeChannel> datas) {
			for (WelcomeChannel data : datas) {
				Util.validChannelData(data.getChannel());
			}
			mData.addAll(datas);
			notifyDataSetChanged();
		}

		public ArrayList<WelcomeChannel> getDataSource() {
			return mData;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.welcome_channel_item,
						null);
			}

			View channel1 = convertView.findViewById(R.id.channel_1);
			View channel2 = convertView.findViewById(R.id.channel_2);
			View channel3 = convertView.findViewById(R.id.channel_3);

			final int count = mData.size();
			int startIndex = position * 3;

			if (startIndex < count) {
				WelcomeChannel channelData = mData.get(startIndex);
				channel1.setVisibility(View.VISIBLE);
				setChannelsView(channel1, channelData);
				startIndex++;
			} else {
				channel1.setVisibility(View.INVISIBLE);
			}
			if (startIndex < count) {
				WelcomeChannel channelData = mData.get(startIndex);
				channel2.setVisibility(View.VISIBLE);
				setChannelsView(channel2, channelData);
				startIndex++;
			} else {
				channel2.setVisibility(View.INVISIBLE);
			}
			if (startIndex < count) {
				WelcomeChannel channelData = mData.get(startIndex);
				channel3.setVisibility(View.VISIBLE);
				setChannelsView(channel3, channelData);
			} else {
				channel3.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		public void setChannelsView(View channelView,
				WelcomeChannel welcomeChannel) {
			if (welcomeChannel == null) {
				return;
			}

			Channel channel = welcomeChannel.getChannel();

			ImageView channelImage = (ImageView) channelView
					.findViewById(R.id.welcome_channel_image);
			TextView channelName = (TextView) channelView
					.findViewById(R.id.welcome_channel_name);
			ImageView maskImage = (ImageView) channelView
					.findViewById(R.id.welcome_channel_mask);

			maskImage.setOnClickListener(WelcomeChannelView.this);
			maskImage.setSelected(welcomeChannel.getIsSelected());
			maskImage.setTag(welcomeChannel);

			channelView.setOnClickListener(WelcomeChannelView.this);
			if (welcomeChannel.fromServer) {
				displayImage(welcomeChannel.getIconPath(), channelImage);
			} else {
				channelImage.setImageBitmap(Util.getAssetBitmap(welcomeChannel
						.getIconPath()));
			}

			channelName.setText(channel.getName());
		}
	}

	@Override
	public void onClick(final View view) {
		switch (view.getId()) {
		case R.id.welcome_channel_column_layout: {

			break;
		}
		case R.id.welcome_channel_mask: {
			boolean status = !view.isSelected();
			WelcomeChannel channel = (WelcomeChannel) view.getTag();
			channel.setIsSelected(status);
			view.setSelected(status);
			checkHint();
			break;
		}

		case R.id.welcome_channel_submit: {
			final ArrayList<Channel> channelList = new ArrayList<Channel>();
			List<WelcomeChannel> welcomeChannelList = mAdapter.getDataSource();
			if (welcomeChannelList != null) {
				for (WelcomeChannel welcomeChannel : welcomeChannelList) {
					if (welcomeChannel.getIsSelected()) {
						channelList.add(welcomeChannel.getChannel());
					}
				}
			}
			if (channelList.size() > 0) {
				List<Channel> oldChannels = Preference.getInstance()
						.getMyFavorChannelList();
				// 删除上一次信鸽保存的Tag
				PushUtil.deleteChannelTag(mContext, oldChannels);//
				PushUtil.addChannelTag(mContext, channelList);
				ChannelModel.updateMyFavChannelList(
						(FragmentActivity) mContext,
						new ResponseListener<UpdateMyFavChannelListRsp>(
								(FragmentActivity) mContext) {

							@Override
							public void onResponse(UpdateMyFavChannelListRsp rsp) {
								if (rsp != null) {

									updateChannel(rsp.getChannelList());
								} else {

									updateChannel(channelList);
								}

								if (mOnSaveClickListener != null) {
									mOnSaveClickListener.onClick(view);
								}
							}

							@Override
							public void onError(Exception e) {
								updateChannel(channelList);

								if (mOnSaveClickListener != null) {
									mOnSaveClickListener.onClick(view);
								}
								super.onError(e);
							}
						}, channelList, true);
			} else {
				if (mOnSaveClickListener != null) {
					mOnSaveClickListener.onClick(view);
				}
			}

			break;
		}
		}
	}

	private void updateChannel(List<Channel> channelList) {
		// List<Channel> oldChannels = Preference.getInstance()
		// .getMyFavorChannelList();
		// PushUtil.deleteChannelTag(mContext, oldChannels);// 删除上一次信鸽保存的Tag
		Preference.getInstance().saveMyFavorChannelList(channelList);
		// PushUtil.addChannelTag(mContext, channelList);
	}

	private TipsHelper mTipsHelper;

	public void checkHint() {

		// if (mTipsHelper == null) {
		// mTipsHelper = new TipsHelper(mContext, mHintLayout, mHintView);
		// }
		// int step = Preference.getInstance().getCurrentGuideStep();
		// if (Preference.STEP_0 == step || Preference.STEP_1 == step) {
		// mTipsHelper.checkHint(step, true);
		// }

	}
}
