package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetSpecialArticleListRsp;

public class GetSpecialArticleRspLocal extends
		GetArticleListRspLocal<GetSpecialArticleListRsp> {

	public ArrayList<ArticleInfo> getArticleList() {
		GetSpecialArticleListRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetSpecialArticleListRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetSpecialArticleListRsp clone() {
		GetSpecialArticleListRsp rsp = getObject();
		if (rsp != null) {
			return (GetSpecialArticleListRsp) rsp.clone();
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
		GetSpecialArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetSpecialArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}
}
