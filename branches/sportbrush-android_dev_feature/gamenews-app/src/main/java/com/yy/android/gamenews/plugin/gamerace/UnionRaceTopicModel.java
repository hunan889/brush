package com.yy.android.gamenews.plugin.gamerace;

import com.duowan.gamenews.GetRaceTopicReq;
import com.duowan.gamenews.GetRaceTopicRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.model.CommonModel;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class UnionRaceTopicModel extends CommonModel {

	// private static final Handler sHandler;
	// private static final boolean TEST_DATA = false;
	//
	// static {
	// sHandler = new Handler(Looper.getMainLooper());
	// }

	public static void getRaceTopicList(
			final ResponseListener<GetRaceTopicRsp> responseListener,
			long topicId, String attachInfo, int refreshType) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			responseListener.onError(null);
			return;
		}
		// if (TEST_DATA) {
		// sHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// GetRaceTopicRsp rsp = new GetRaceTopicRsp();
		// ArrayList<UnionInfo> unionList = new ArrayList<UnionInfo>();
		// for (int i = 0; i < 10; i++) {
		// UnionInfo unionitem = new UnionInfo();
		// unionitem.setId(i);
		// unionitem.setName("lol盒子");
		// unionitem.setHeat(1000);
		// unionitem.setDesc("工会惠东阿斯顿合法工会惠东阿斯顿合法");
		// unionitem
		// .setImg("http://pic10.nipic.com/20100929/2548970_082246081816_2.jpg");
		// unionList.add(unionitem);
		// }
		// rsp.setUnionList(unionList);
		// rsp.setArticleList(ArticleModel.getData());
		// rsp.setTitle("比赛专题");
		// rsp.setImg("http://img.funshion.com/pictures/145/093/145093.jpg");
		// rsp.setHeat(100000);
		// rsp.setSummary("工会惠东阿斯顿合法工会惠东阿斯顿合法工会惠东阿斯顿合法工会惠东阿斯顿合法工会惠东阿斯顿合法工会惠东阿斯顿合法");
		// rsp.setSubTitle("工会积分排名的title");
		// rsp.setUnderDesc("赶紧抢沙发");
		// rsp.setHasMore(true);
		// rsp.setAttachInfo("下一页");
		// responseListener.onResponse(rsp);
		// }
		// }, 1000);
		// return;
		// }

		GetRaceTopicReq req = new GetRaceTopicReq();
		req.setAttachInfo(attachInfo);
		req.setTopicId(topicId);
		req.setRefreshType(refreshType);
		UniPacket uniPacket = createUniPacket("GetRaceTopic", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetRaceTopicRsp rsp = new GetRaceTopicRsp();
				rsp = response.getByClass("result", rsp);
				responseListener.onResponse(rsp);
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}
}
