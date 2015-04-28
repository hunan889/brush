package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetFavArticleListRsp;

public class GetFavArticleListRspLocal extends
		GetArticleListRspLocal<GetFavArticleListRsp> {

	public ArrayList<ArticleInfo> getArticleList() {
		GetFavArticleListRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetFavArticleListRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetFavArticleListRsp clone() {
		GetFavArticleListRsp rsp = getObject();
		if (rsp != null) {
			return (GetFavArticleListRsp) rsp.clone();
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
		GetFavArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetFavArticleListRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}

}
