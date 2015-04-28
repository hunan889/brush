package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetRaceTopicRsp;

public class GetRaceTopicRspLocal extends
		GetArticleListRspLocal<GetRaceTopicRsp> {
	public ArrayList<ArticleInfo> getArticleList() {
		GetRaceTopicRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetRaceTopicRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetRaceTopicRsp clone() {
		GetRaceTopicRsp rsp = getObject();
		if (rsp != null) {
			return (GetRaceTopicRsp) rsp.clone();
		}
		return null;
	}

	@Override
	public void setPictopList(ArrayList<ArticleInfo> pictopList) {
	}

	@Override
	public ArrayList<ArticleInfo> getPictopList() {
		return null;
	}

	@Override
	public Object getAttachInfo() {
		GetRaceTopicRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetRaceTopicRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}
}