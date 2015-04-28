package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetUnionInfoRsp;

public class GetUnionInfoRspLocal extends
		GetArticleListRspLocal<GetUnionInfoRsp> {
	public ArrayList<ArticleInfo> getArticleList() {
		GetUnionInfoRsp rsp = getObject();
		return rsp == null ? null : rsp.getArticleList();
	}

	public void setArticleList(ArrayList<ArticleInfo> list) {
		GetUnionInfoRsp rsp = getObject();
		if (rsp != null) {
			rsp.setArticleList(list);
		}
	}

	@Override
	public GetUnionInfoRsp clone() {
		GetUnionInfoRsp rsp = getObject();
		if (rsp != null) {
			return (GetUnionInfoRsp) rsp.clone();
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
		GetUnionInfoRsp rsp = getObject();
		if (rsp != null) {
			return rsp.getAttachInfo();
		}
		return null;
	}

	@Override
	public boolean hasMore() {
		GetUnionInfoRsp rsp = getObject();
		if (rsp != null) {
			return rsp.hasMore;
		}
		return false;
	}
}
