package com.yy.android.gamenews.event;

import com.duowan.gamenews.ArticleCategory;
import com.duowan.gamenews.Channel;

public class RefreshEvent {

	public Channel mChannel;
	public ArticleCategory mCategory;

	public Channel getChannel() {
		return mChannel;
	}

	public void setChannel(Channel channel) {
		mChannel = channel;
	}

	public ArticleCategory getCategory() {
		return mCategory;
	}

	public void setCategory(ArticleCategory category) {
		mCategory = category;
	}
	
}
