package com.yy.android.gamenews.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.GetChannelDetailRsp;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.ViewPagerActivity;
import com.yy.android.gamenews.ui.ViewPagerFragmentFactory.PageType;
import com.yy.android.gamenews.ui.channeldetail.ChannelDetailFragment;

public enum PageCaller {
	;
	public interface Definition {
		public static final int TYPE_CHANNEL_ARTICLE_LIST = 1;

	}

	public static void open(final Context context, int type, int id) {
		switch (type) {
		case Definition.TYPE_CHANNEL_ARTICLE_LIST: {

			ChannelModel.getChannelById(
					new ResponseListener<GetChannelDetailRsp>(
							(FragmentActivity) context) {

						@Override
						public void onResponse(GetChannelDetailRsp response) {

							Channel channel = response.getChannelInfo();
							Bundle bundle = new Bundle();
							bundle.putSerializable(
									ChannelDetailFragment.KEY_CHANNEL, channel);
							ViewPagerActivity.startViewPagerActivity(context,
									PageType.CHANNEL_DETAIL, channel.getName(),
									bundle);
						}
					}, id);

			break;
		}
		}
	}
}
