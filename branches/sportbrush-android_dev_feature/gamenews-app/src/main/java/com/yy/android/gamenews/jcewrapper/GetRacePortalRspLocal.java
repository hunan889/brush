package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetRacePortalRsp;

public class GetRacePortalRspLocal extends
		GetArticleListRspLocal<GetRacePortalRsp> {

	public ArrayList<ArticleInfo> getArticleList() {
		GetRacePortalRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetRacePortalRsp clone() {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			return (GetRacePortalRsp) rsp.clone();
		}
		return null;
	}

	@Override
	public void setPictopList(ArrayList<ArticleInfo> pictopList) {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			rsp.setPictopList(pictopList);
		}
	}

	@Override
	public ArrayList<ArticleInfo> getPictopList() {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getPictopList();
		}
		return null;
	}

	@Override
	public Object getAttachInfo() {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetRacePortalRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}
}
