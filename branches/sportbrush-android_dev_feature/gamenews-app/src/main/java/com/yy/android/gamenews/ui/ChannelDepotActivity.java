package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.Column;
import com.duowan.gamenews.GetColumnListRsp;
import com.duowan.gamenews.GetSearchSuggestionListRsp;
import com.duowan.gamenews.UpdateMyFavChannelListRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.CategoryFlowLayout;
import com.yy.android.gamenews.ui.view.CategoryFlowLayout.OnRearrangeListener;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.TipsHelper;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ChannelDepotActivity extends BaseActivity {

	public static final int HOT_CHANNEL_CODE = 100;
	private ActionBar mActionBar;
	private CategoryFlowLayout mCategoryFlowLayout;
	private LinearLayout mLayout;
	private View mGridItem;
	private TextView mChannelManageTips;
	private TextView mChannelEdit;
	private boolean mIsAnimationProcessing = false;
	private Channel mLastAdded;
	private ArrayList<Channel> mSubscribeChannels;
	private ArrayList<Channel> mOriginalChannels = new ArrayList<Channel>();
	private int mTips = 0;
	private boolean mSubscribeChanged = false;
	// private TextView mCompleteTextView;
	private String mGetColumnListAttach = null;
	private TextView mHintView;
	private View mHintLayout;
	private TipsHelper mTipsHelper;
	private HashMap<String, Object> mDeleteChannels = new HashMap<String, Object>();
	private StringBuffer mDeleteData = new StringBuffer();
	private Preference mPref;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			mGridItem.clearAnimation();
			mGridItem.setVisibility(View.INVISIBLE);
			mCategoryFlowLayout.addView(getGridsItemView(mLastAdded));
			refreshTips();
			mIsAnimationProcessing = false;
		}
	};

	private OnClickListener mSelectChannelListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mCategoryFlowLayout.isFloatingViewReady()) {
				return;
			}
			if (mIsAnimationProcessing) {
				ToastUtil.showToast(R.string.channel_manage_too_fast);
				return;
			}
			mLastAdded = (Channel) v.getTag();
			View view = v.findViewById(R.id.channel_chosen);
			if (view.getVisibility() == View.VISIBLE) {
				int index = mCategoryFlowLayout
						.getIndexOfItemByChannelFrame(mLastAdded.getId());
				if (index != -1) {
					mCategoryFlowLayout.removeViewAt(index);
					refreshTips();
					view.setVisibility(View.INVISIBLE);
					StatsUtil.statsReport(ChannelDepotActivity.this,
							"stats_delete_channel", "channel_name",
							mLastAdded.getName());
					StatsUtil.statsReportByMta(ChannelDepotActivity.this,
							"stats_delete_channel", "channel_name",
							mLastAdded.getName());
					StatsUtil.statsReportByHiido("stats_delete_channel",
							"channel_name:" + mLastAdded.getName());
				}
			} else if (view.getVisibility() == View.INVISIBLE) {
				if (mCategoryFlowLayout.getSubscribeChannelCount() >= Constants.SUBSCRIBE_MOST_LIMIT) {
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

				addAnim(v);
				view.setVisibility(View.VISIBLE);
				StatsUtil.statsReport(ChannelDepotActivity.this,
						"stats_subscribe_channel", "channel_name",
						mLastAdded.getName());
				StatsUtil.statsReportByMta(ChannelDepotActivity.this,
						"stats_subscribe_channel", "channel_name",
						mLastAdded.getName());
				StatsUtil.statsReportByHiido("stats_subscribe_channel",
						"channel_name:" + mLastAdded.getName());
			}
			updateChangedChannels(mLastAdded);
		}
	};

	// private OnClickListener mCompleteClick = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (mIsAnimationProcessing) {
	// mGridItem.clearAnimation();
	// mGridItem.setVisibility(View.INVISIBLE);
	// mCategoryFlowLayout.addView(getGridsItemView(mLastAdded));
	// refreshTips();
	// mIsAnimationProcessing = false;
	// }
	// mChannelManageTips.setVisibility(View.VISIBLE);
	// refreshTips();
	// mCategoryFlowLayout.setNormalMode();
	// mSubscribeChannels = mCategoryFlowLayout.getSubscribeChannelList();
	// Preference.getInstance().saveMyFavorChannelList(mSubscribeChannels);
	// refreshColumnView();
	// mCompleteTextView.setVisibility(View.INVISIBLE);
	// mActionBar.setRightVisibility(View.VISIBLE);
	// }
	// };

	private OnClickListener mMorelistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Column column = (Column) v.getTag();
			Intent intent = new Intent(ChannelDepotActivity.this,
					ChannelsMoreActivity.class);
			intent.putExtra(Constants.EXTRA_COLUMN_ID, column.getId());
			intent.putExtra(Constants.EXTRA_COLUMN_NAME, column.getName());
			startActivity(intent);
		}
	};

	private OnClickListener mDellistener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Channel channel = (Channel) v.getTag();
			int index = mCategoryFlowLayout.getIndexOfItemByDelBtn(v);
			if (index != -1) {
				mCategoryFlowLayout.removeViewAt(index);
				refreshColumnView();
				updateChangedChannels(channel);

				StatsUtil.statsReport(ChannelDepotActivity.this,
						"stats_delete_channel", "channel_name",
						channel.getName());
				StatsUtil.statsReportByMta(ChannelDepotActivity.this,
						"stats_delete_channel", "channel_name",
						channel.getName());
				StatsUtil.statsReportByHiido("stats_delete_channel",
						"channel_name" + channel.getName());
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_depot);
		mPref = Preference.getInstance();
		mHintView = (TextView) findViewById(R.id.welcome_depot_hint);
		mHintLayout = findViewById(R.id.welcome_hint_layout);

		mCategoryFlowLayout = (CategoryFlowLayout) findViewById(R.id.category_flow_layout);
		mLayout = (LinearLayout) findViewById(R.id.layout);
		mGridItem = findViewById(R.id.grid_item);
		mGridItem.setVisibility(View.INVISIBLE);
		mChannelManageTips = (TextView) findViewById(R.id.channel_manage_tips);
		mChannelEdit = (TextView) findViewById(R.id.action_complete);
		mChannelEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String txt = mChannelEdit.getText().toString().trim();
				if (txt.equals(getResources().getString(
						R.string.channel_depot_complete))) {
					editChannelComplete();
				} else {
					mChannelEdit.setText(getResources().getString(
							R.string.channel_depot_complete));
					mChannelManageTips.setText(getResources().getString(
							R.string.channel_manage_edit_tips));
					mCategoryFlowLayout.editChannel();
				}
			}
		});
		mCategoryFlowLayout.setOnRearrangeListener(new OnRearrangeListener() {

			@Override
			public void onRearrange(int oldIndex, int newIndex) {
			}
		});
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editChannelComplete();
				Intent intent = new Intent(ChannelDepotActivity.this,
						ChannelSearchActivity.class);
				startActivity(intent);
			}
		});
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// mCompleteTextView = mActionBar.getRightTextView();
		// mCompleteTextView.setText(R.string.channel_depot_complete);
		// mCompleteTextView.setOnClickListener(mCompleteClick);

		mCategoryFlowLayout.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// mActionBar.setRightVisibility(ViewGroup.INVISIBLE);
				// mCompleteTextView.setVisibility(View.VISIBLE);
				mChannelManageTips.setText(getResources().getString(
						R.string.channel_manage_edit_tips));
				mChannelEdit.setText(getResources().getString(
						R.string.channel_depot_complete));
				return false;
			}
		});

		initCategoryView();
		getColumnList(100);

		long lastQueryTime = Preference.getInstance()
				.getLastGetSuggestionTime();
		long days = (Calendar.getInstance().getTimeInMillis() - lastQueryTime)
				/ (1000 * 60 * 60 * 24);
		if (days >= 7) {
			getSearchSuggestionList(null, 10000);
		}
		refreshTips();
		// checkXinGeDeleteData();
		EventBus.getDefault().register(this);
	}

	/**
	 * 完成编辑频道
	 */
	private void editChannelComplete() {
		mChannelEdit.setText(getResources().getString(
				R.string.channel_depot_edit));
		if (mIsAnimationProcessing) {
			mGridItem.clearAnimation();
			mGridItem.setVisibility(View.INVISIBLE);
			mCategoryFlowLayout.addView(getGridsItemView(mLastAdded));
			refreshTips();
			mIsAnimationProcessing = false;
		}
		refreshTips();
		mCategoryFlowLayout.setNormalMode();
		mSubscribeChannels = mCategoryFlowLayout.getSubscribeChannelList();
		Preference.getInstance().saveMyFavorChannelList(mSubscribeChannels);
		refreshColumnView();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mSubscribeChanged) {
			mSubscribeChanged = false;
			initCategoryView();
			refreshColumnView();
		}
		checkHint();
	}

	public void onBackPressed() {
		ArrayList<Channel> channels = mCategoryFlowLayout
				.getSubscribeChannelList();
		final SubscribeEvent event = Util.getSubscribeEvent(mOriginalChannels,
				channels);
		if (event != null) {
			saveCategorySubscribe();
			ChannelModel.updateMyFavChannelList(ChannelDepotActivity.this,
					new ResponseListener<UpdateMyFavChannelListRsp>(
							ChannelDepotActivity.this) {

						@Override
						public void onResponse(
								UpdateMyFavChannelListRsp response) {
							EventBus.getDefault().post(event);
							finish();
						}

						@Override
						public void onError(Exception e) {
							EventBus.getDefault().post(event);
							finish();
							super.onError(e);
						}
					}, channels, true);
		} else {
			finish();
		}
	};

	// public void checkXinGeDeleteData() {
	// String xinGeData = mPref.getXinGeData(PushUtil.DELETE_XINGE_PUSH_DATA);
	// if (xinGeData != null && !TextUtils.isEmpty(xinGeData)) {
	// String[] split = xinGeData.split(",");
	// for (int i = 0; i < split.length - 1; i = i + 2) {
	// mDeleteChannels.put(split[i], split[i + 1]);
	// }
	// }
	// }

	public void checkHint() {

		// if (mTipsHelper == null) {
		// mTipsHelper = new TipsHelper(this, mHintLayout, mHintView);
		// }
		// int step = Preference.getInstance().getCurrentGuideStep();
		// if (Preference.STEP_3 == step) {
		// mTipsHelper.checkHint(step, true);
		// }
	}

	@Override
	public void onPause() {
		super.onPause();
		// if (mCompleteTextView.getVisibility() == View.VISIBLE) {
		// mCompleteTextView.performClick();
		// }
	}

	@Override
	public void onDestroy() {

		if (mSubscribeChannels != null && mOriginalChannels != null) {
			for (int i = 0; i < mOriginalChannels.size(); i++) {
				boolean isDelete = true;
				Channel channel = mOriginalChannels.get(i);
				for (int j = 0; j < mSubscribeChannels.size(); j++) {
					if (channel.getId() == mSubscribeChannels.get(j).getId()) {
						isDelete = false;
						break;
					}
				}
				if (isDelete) {
					mDeleteChannels.put(String.valueOf(channel.getId()),
							channel.getName() + "_" + channel.getId());
				}
			}
		} else if (mSubscribeChannels == null && mOriginalChannels.size() > 0) {
			for (int i = 0; i < mOriginalChannels.size(); i++) {
				Channel channel = mOriginalChannels.get(i);
				mDeleteChannels.put(String.valueOf(channel.getId()),
						channel.getName() + "_" + channel.getId());
			}
		}
		if (mDeleteChannels != null) {
			if (Util.isNetworkConnected()) {
				PushUtil.deleteChannelTag(ChannelDepotActivity.this,
						mDeleteChannels);// 删除信鸽推送的Tag
			} else {
				Set<String> keySet = mDeleteChannels.keySet();
				for (String key : keySet) {
					String value = (String) mDeleteChannels.get(key);
					mDeleteData.append(key + "," + value);
					mDeleteData.append(",");
				}
				String mDelete = "";
				if (mDeleteData.length() > 0) {
					mDelete = mDeleteData.toString().substring(0,
							mDeleteData.toString().length() - 1);
				}
				mPref.setXinGeData(PushUtil.DELETE_XINGE_PUSH_DATA, mDelete);
			}
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public void onEvent(SubscribeEvent event) {
		if (event.isSubscribeChanged) {
			mSubscribeChanged = true;
		} else {
			mSubscribeChanged = false;
		}
	}

	private void addAnim(View view) {
		int location[] = new int[2];
		int location1[] = new int[2];
		view.getLocationOnScreen(location);
		mGridItem.getLocationOnScreen(location1);
		Point oldPos = new Point(location[0] - location1[0], location[1]
				- location1[1]);
		Point newPos = mCategoryFlowLayout.getNewLocationOnScreen();
		newPos.x = newPos.x - location1[0]
				+ mCategoryFlowLayout.getItemToColumnOffset();
		newPos.y -= location1[1];
		mGridItem.setVisibility(View.INVISIBLE);
		mGridItem.bringToFront();
		translateAnim(mGridItem, oldPos, newPos);
	}

	protected void translateAnim(View view, Point oldPos, Point newPos) {
		TranslateAnimation translateanimation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, oldPos.x,
				TranslateAnimation.ABSOLUTE, newPos.x,
				TranslateAnimation.ABSOLUTE, oldPos.y,
				TranslateAnimation.ABSOLUTE, newPos.y);
		translateanimation.setDuration(300);
		translateanimation.setFillEnabled(true);
		// translateanimation.setAnimationListener(mAnimationListener);
		mIsAnimationProcessing = true;
		view.clearAnimation();
		view.startAnimation(translateanimation);
		mHandler.postDelayed(mRunnable, translateanimation.getDuration());
	}

	private View getGridsItemView(Channel channel) {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.channel_grid_item, null);
		ImageView channelImage = (ImageView) view
				.findViewById(R.id.channel_image);
		TextView channelName = (TextView) view.findViewById(R.id.channel_name);
		View del = view.findViewById(R.id.channel_del);
		SwitchImageLoader.getInstance().displayImage(channel.getIcon(),
				channelImage,
				SwitchImageLoader.DEFAULT_CHANNEL_SMALL_DISPLAYER, true);
		channelName.setText(channel.getName());
		del.setTag(channel);
		del.setOnClickListener(mDellistener);
		return view;
	}

	private void initCategoryView() {
		List<Channel> channels = Preference.getInstance()
				.getMyFavorChannelList();

		mCategoryFlowLayout.removeAllViews();
		mOriginalChannels.clear();
		if (channels != null) {
			for (Channel channel : channels) {
				mOriginalChannels.add(channel);
				mCategoryFlowLayout.addView(getGridsItemView(channel), false);
			}
		}
	}

	private void saveCategorySubscribe() {
		ArrayList<Channel> channels = mCategoryFlowLayout
				.getSubscribeChannelList();
		Preference.getInstance().saveMyFavorChannelList(channels);
	}

	public void refreshTips() {
		if (mChannelManageTips.getVisibility() == View.VISIBLE
				&& mCategoryFlowLayout.getSubscribeChannelCount() == 0) {
			mChannelManageTips.setText(R.string.channel_manage_empty_tips);
		} else {
			mChannelManageTips.setText(R.string.channel_manage_tips);
		}
	}

	public void refreshColumnView() {
		mSubscribeChannels = mCategoryFlowLayout.getSubscribeChannelList();
		final int columnsCount = mLayout.getChildCount();
		for (int i = 0; i < columnsCount; i++) {
			ViewGroup column = (ViewGroup) mLayout.getChildAt(i);
			if (column == null)
				continue;
			ViewGroup channels = (ViewGroup) column
					.findViewById(R.id.channel_container);
			int channelCount = channels.getChildCount();
			for (int j = 0; j < channelCount; j++) {
				if (channels.getChildAt(j) instanceof ViewGroup) {
					ViewGroup channel = (ViewGroup) channels.getChildAt(j);
					ViewGroup frameLayout = (ViewGroup) channel
							.findViewById(R.id.channel_framelayout);
					if (frameLayout != null) {
						View chosen = frameLayout
								.findViewById(R.id.channel_chosen);
						if (chosen != null) {
							if (chosen.getVisibility() == View.VISIBLE) {
								Channel channelData = (Channel) frameLayout
										.getTag();
								if (!Util.isSubscribedChannel(
										mSubscribeChannels, channelData)) {
									chosen.setVisibility(View.INVISIBLE);
								}
							} else {
								Channel channelData = (Channel) frameLayout
										.getTag();
								if (Util.isSubscribedChannel(
										mSubscribeChannels, channelData)) {
									chosen.setVisibility(View.VISIBLE);
								}
							}
						}
					}
				}
			}
		}
	}

	public void updateColumnView(ArrayList<Column> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		LayoutInflater inflater = getLayoutInflater();
		mLayout.removeAllViews();
		for (Column item : list) {
			View view = inflater.inflate(R.layout.channel_columns, null);
			LinearLayout lltItem = (LinearLayout) view
					.findViewById(R.id.channel_item);
			TextView columnName = (TextView) view
					.findViewById(R.id.column_name);
			View moreAction = view.findViewById(R.id.action_more);
			View channel1 = view.findViewById(R.id.channel_1);
			View channel2 = view.findViewById(R.id.channel_2);
			View channel3 = view.findViewById(R.id.channel_3);

			columnName.setText(item.getName());
			moreAction.setTag(item);
			moreAction.setOnClickListener(mMorelistener);

			ArrayList<Channel> channels = item.getChannelList();
			mSubscribeChannels = mCategoryFlowLayout.getSubscribeChannelList();
			
			if(item.getId() == HOT_CHANNEL_CODE){
				lltItem.setBackgroundResource(R.color.hot_channel_bg);
			}
			if (channels != null) {
				int channelCount = channels.size();

				int i = 0;
				if (++i <= channelCount) {
					channel1.setVisibility(View.VISIBLE);
					updateColumnChannelsView(channel1, channels.get(i - 1));
				}
				if (++i <= channelCount) {
					channel2.setVisibility(View.VISIBLE);
					updateColumnChannelsView(channel2, channels.get(i - 1));
				}
				if (++i <= channelCount) {
					channel3.setVisibility(View.VISIBLE);
					updateColumnChannelsView(channel3, channels.get(i - 1));
				}
				mLayout.addView(view);
			}

		}
	}

	public void updateColumnChannelsView(View channelView, Channel channelData) {
		if (channelData == null) {
			return;
		}
		// mSubscribeChannels = mCategoryFlowLayout.getSubscribeChannelList();
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
		if (Util.isSubscribedChannel(mSubscribeChannels, channelData)) {
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

	private void getColumnList(int count) {
		ResponseListener<GetColumnListRsp> responseListener = new ResponseListener<GetColumnListRsp>(
				this) {
			public void onResponse(GetColumnListRsp response) {
				mGetColumnListAttach = response.getAttachInfo();
				ArrayList<Column> list = response.getColumnList();
				updateColumnView(list);
			}
		};
		ChannelModel.getColumnList(responseListener, mGetColumnListAttach,
				count);
	}

	private void getSearchSuggestionList(String attachInfo, int count) {
		final ResponseListener<GetSearchSuggestionListRsp> responseListener = new ResponseListener<GetSearchSuggestionListRsp>(
				this) {
			public void onResponse(GetSearchSuggestionListRsp response) {
				Map<String, ArrayList<String>> map = response
						.getSuggestionList();
				if (map != null && map.size() > 0) {
					Preference.getInstance().saveSearchSuggestion(map);
					Preference.getInstance().saveLastGetSuggestionTime(
							Calendar.getInstance().getTimeInMillis());
				}
			}

			public void onError(Exception e) {
			};
		};
		ChannelModel.getSearchSuggestionList(responseListener, attachInfo,
				count);
	}

}
