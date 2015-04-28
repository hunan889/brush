package com.yy.android.gamenews.jcewrapper;

import java.util.ArrayList;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.taf.jce.JceStruct;
/**
 * 对所有使用ArticleInfo作为列表显示元素的对象进行封装
 * @param <E> 远程JCE对象
 */
public abstract class GetArticleListRspLocal<E extends JceStruct> extends
		GetDataRsp<E> {

	public abstract Object getAttachInfo();

	public abstract boolean hasMore();

	public abstract void setPictopList(ArrayList<ArticleInfo> pictopList);

	public abstract ArrayList<ArticleInfo> getPictopList();

	public abstract ArrayList<ArticleInfo> getArticleList();

	public abstract void setArticleList(ArrayList<ArticleInfo> list);
}
