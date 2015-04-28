package com.yy.android.gamenews.util.maintab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.plugin.show.CommunityFragment;
import com.yy.android.gamenews.ui.MainActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class MainTab4Gamenews extends MainTab4 {

	public MainTab4Gamenews(MainActivity context, ActionBar actionbar,
			Bundle savedInstance) {
		super(context, actionbar, MainTabEvent.TAB_COMMUNITY, savedInstance);
	}

	@Override
	protected void custActionbarRight() {
		mActionBar.setRightTextVisibility(View.GONE);
		mActionBar.getRightImageView().setImageResource(
				R.drawable.show_new_post_selector);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.setOnRightClickListener(mOnRightClickListener);
	}

	private OnClickListener mOnRightClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			UiUtils.sendTopicCheckLogin(mContext);

			MainTabStatsUtil.statistics(mContext, MainTabEvent.TAB_COMMUNITY,
					MainTabEvent.INTO_SELECT_TOPIC_TAG,
					MainTabEvent.INTO_SELECT_TOPIC_TAG_NAME);
		}
	};

	@Override
	protected Fragment initFragment() {
		CommunityFragment communityFragment = CommunityFragment.newInstance();
		return communityFragment;
	}

}
