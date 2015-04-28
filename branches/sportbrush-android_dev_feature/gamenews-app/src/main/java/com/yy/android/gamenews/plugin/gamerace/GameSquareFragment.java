package com.yy.android.gamenews.plugin.gamerace;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.GetRacePortalRsp;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.jcewrapper.GetRacePortalRspLocal;
import com.yy.android.gamenews.model.UnionModel;
import com.yy.android.gamenews.ui.ArticleListFragment;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class GameSquareFragment extends
		ArticleListFragment<GetRacePortalRsp, GetRacePortalRspLocal> {

	@Override
	protected void requestDataImpl(final int refresh, Object attachInfo) {

		UnionModel.getRacePortalList(
				new ResponseListener<GetRacePortalRsp>(getActivity()) {

					@Override
					public void onResponse(GetRacePortalRsp response) {
						requestFinish(refresh, response, false);
					}

					@Override
					public void onError(Exception e) {

						requestFinish(refresh, null, false);

						if (e != null) {
							ToastUtil.showToast(R.string.http_error);
						}
						super.onError(e);
					}
				}, (String) attachInfo, refresh);
	}

	@Override
	protected GetRacePortalRsp newRspObject() {
		return new GetRacePortalRsp();
	}

	@Override
	protected GetRacePortalRspLocal initRspWrapper() {
		return new GetRacePortalRspLocal();
	}

	@Override
	protected String getCacheKey() {
		return "gamesquare";
	}

	@Override
	protected void onItemClick(ArticleInfo model, int position, int type) {

		if (model != null) {
			switch (model.getArticleType()) {
			case ArticleType._ARTICLE_TYPE_UNION_RACE: {

				UnionRaceTopicActivity.startRaceTopicActivity(getActivity(),
						model.getId(), UnionRaceTopicActivity._RACE_TOPIC_ID);
				return;
			}
			case ArticleType._ARTICLE_TYPE_PERSON_RACE: {
				PersonalRaceTopicActivity.startActivity(getActivity(),
						model.getId());
				break;
			}
			}
			
			MainTabStatsUtil.statistics(getActivity(),
					MainTabEvent.TAB_GAMERACE_INFO,
					MainTabEvent.CLICK_RACEPORTAL, model.getTitle());
		}

		super.onItemClick(model, position, type);
	}
	
	@Override
	protected boolean needShowUpdatedCount() {

		return false;
	}
}
