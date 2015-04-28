package com.yy.android.gamenews.util.maintab;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.gamenews.ActiveInfo;
import com.duowan.gamenews.ActiveInfoType;
import com.duowan.gamenews.Channel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.AppWebActivity;
import com.yy.android.gamenews.ui.ArticleListActivity;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.NewsFragment;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.sportbrush.R;

/**
 * 首页第一个tab，通用为头条，有以下特性： 1. 右上角显示active list，由后台配置可跳转到频道详情或者web view 2. content
 * view为以viewPage来显示头条的列表
 * 
 * @author liuchaoqun
 * 
 */
public class MainTab1 extends MainTab {

	public MainTab1(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, MainTabEvent.TAB_HEAD_INFO, savedInstance);
	}

	public MainTab1(MainActivity context, ActionBar actionbar, String fromTab,
			Bundle savedInstance) {
		super(context, actionbar, fromTab, savedInstance);
	}

	private List<ActiveInfo> mActiveChannelList;

	@Override
	protected void custActionbarRight() {
		if (mActiveBitmap == null) {
			mActiveChannelList = mPref.getActiveChannelList();
			if (mActiveChannelList != null && mActiveChannelList.size() > 0) {
				ActiveInfo info = mActiveChannelList.get(0);
				ImageLoader.getInstance().loadImage(info.getIcon(),
						mActiveImgLoadingListener);
			}
		}
		updateActiveImage();
	}

	private Bitmap mActiveBitmap;
	private ImageLoadingListener mActiveImgLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			updateActiveImage();
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			mActiveBitmap = loadedImage;
			updateActiveImage();
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			updateActiveImage();
		}

	};

	private void updateActiveImage() {
		mActionBar.setRightTextVisibility(View.GONE);
		if (mActiveBitmap != null) {
			mActionBar.getRightImageView().setImageBitmap(mActiveBitmap);
			mActionBar.setRightVisibility(View.VISIBLE);
			mActionBar.setOnRightClickListener(mOnRightClickListener);

		} else {
			mActionBar.setRightVisibility(View.INVISIBLE);
			mActionBar.setOnRightClickListener(null);
		}
	}

	private OnClickListener mOnRightClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mActiveChannelList == null || mActiveChannelList.size() <= 0) {
				v.setVisibility(View.INVISIBLE);
				v.setOnClickListener(null);
				return;
			}

			ActiveInfo info = mActiveChannelList.get(0);
			if (info != null) {
				if (info.getType() == ActiveInfoType._ENUM_ACTIVEINFO_TYPE_CHANNEL) {
					Channel channel = new Channel();
					channel.setIcon(info.getIcon());
					channel.setId(info.getId());
					channel.setName(info.getName());
					ArticleListActivity.startChannelListActivity(mContext,
							channel);
				} else if (info.getType() == ActiveInfoType._ENUM_ACTIVEINFO_TYPE_URL) {
					String url = info.getUrl();

					AppWebActivity.startWebActivityWithYYToken(mContext, url,
							true);
				}
			}
		}
	};

	/**
	 * 第一个tab默认为头条
	 */
	@Override
	public Fragment initFragment() {

		Fragment fragment = new NewsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(NewsFragment.KEY_NEWS_TYPE, NewsFragment.TYPE_HEADLINES);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public String getDisplayName() {
		return mContext.getString(R.string.square);
	}

	@Override
	protected int getButtonDrawableResource() {
		return R.drawable.main_my_info_btn_selector;
	}

	public static final int INDEX = 0;

	@Override
	public int getId() {
		return INDEX;
	}
}
