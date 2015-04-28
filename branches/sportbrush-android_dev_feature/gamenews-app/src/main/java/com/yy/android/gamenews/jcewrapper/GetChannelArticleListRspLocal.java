package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetChannelArticleListRsp;

public class GetChannelArticleListRspLocal extends
		GetArticleListRspLocal<GetChannelArticleListRsp> {

	public ArrayList<ArticleInfo> getArticleList() {
		GetChannelArticleListRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetChannelArticleListRsp clone() {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			return (GetChannelArticleListRsp) rsp.clone();
		}
		return null;
	}

	@Override
	public void setPictopList(ArrayList<ArticleInfo> pictopList) {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			rsp.setPictopList(pictopList);
		}
	}

	@Override
	public ArrayList<ArticleInfo> getPictopList() {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getPictopList();
		}
		return null;
	}

	@Override
	public Object getAttachInfo() {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetChannelArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}
}
