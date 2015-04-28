package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetFavArticleListRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.jcewrapper.GetFavArticleListRspLocal;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;

public class MyFavorListFragment extends
		ArticleListFragment<GetFavArticleListRsp, GetFavArticleListRspLocal> {

	public static MyFavorListFragment newInstance() {
		MyFavorListFragment fragment = new MyFavorListFragment();
		fragment.setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {

		refreshData();
		super.onResume();
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	@Override
	protected void requestDataImpl(final int refresh, Object attachInfo) {
		ArticleModel.getFavArticleList(
				new ResponseListener<GetFavArticleListRsp>(getActivity()) {
					@Override
					public void onResponse(GetFavArticleListRsp data) {
						setEmptyText(strEmptyNoData);
						requestFinish(refresh, data, false);
					}

					@Override
					public void onError(Exception e) {
						setEmptyText(strEmptyReload);
						requestFinish(refresh, null, true);

						super.onError(e);
					}
				}, // Listener
				refresh, (String) attachInfo);
	}

	@Override
	protected GetFavArticleListRsp newRspObject() {
		return new GetFavArticleListRsp();
	}

	@Override
	protected boolean needShowViewedArticle() {
		return false;
	}

	@Override
	protected boolean needSaveToDisk() {
		return false;
	}

	@Override
	protected String getLastRefreshTimeKey() {
		return Constants.CACHE_KEY_LAST_REFRSH_TIME_MYFAVOR;
	}

	@Override
	protected void requestFinishImpl(int refresh, GetFavArticleListRsp data,
			boolean error) {
		ArrayList<ArticleInfo> dataList = null;
		boolean hasMore = false;
		if (data != null) {
			dataList = data.getArticleList();
			hasMore = data.hasMore;
		}
		requestFinish(refresh, dataList, hasMore, true, error);
	}

	@Override
	protected GetFavArticleListRspLocal initRspWrapper() {
		return new GetFavArticleListRspLocal();
	}
}
