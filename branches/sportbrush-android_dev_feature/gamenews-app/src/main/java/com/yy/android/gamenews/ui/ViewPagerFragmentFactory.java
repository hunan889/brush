package com.yy.android.gamenews.ui;

import android.os.Bundle;

import com.yy.android.gamenews.plugin.gamerace.AssociationEntryFragment;
import com.yy.android.gamenews.ui.channeldetail.ChannelDetailFragment;

public class ViewPagerFragmentFactory {

	public enum PageType {
		NEWS, CHANNEL_DETAIL, ASSOCIATION_ENTRY
	}

	public static final String KEY_BUNDLE = "ViewPagerFragment_bundle";

	public static ViewPagerFragment createViewPagerFragment(PageType type,
			Bundle bundle) {
		ViewPagerFragment fragment = null;

		switch (type) {
		case CHANNEL_DETAIL: {
			fragment = new ChannelDetailFragment();
			break;
		}
		case NEWS: {
			fragment = new NewsFragment();
			break;
		}
		case ASSOCIATION_ENTRY: {
			fragment = new AssociationEntryFragment();
			break;
		}
		}
		fragment.setArguments(bundle);

		return fragment;
	}
}
