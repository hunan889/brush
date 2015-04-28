package com.yy.android.gamenews.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.SparseArray;

import com.duowan.gamenews.ArticleFlag;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.GetRacePortalReq;
import com.duowan.gamenews.GetRacePortalRsp;
import com.duowan.gamenews.GetUnionInfoReq;
import com.duowan.gamenews.GetUnionInfoRsp;
import com.duowan.gamenews.GetUnionListReq;
import com.duowan.gamenews.GetUnionListRsp;
import com.duowan.gamenews.UnionInfo;
import com.duowan.gamenews.UnionType;
import com.duowan.gamenews.UnionVoteReq;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

/**
 * 游戏刷子赛事
 * 
 * @author lcq, ldx, yyl
 * 
 */
public class UnionModel extends CommonModel {

	public static void getUnionList(
			final ResponseListener<GetUnionListRsp> listener,
			final int unionType, final String attachInfo, int refreType) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetUnionListReq req = new GetUnionListReq();
		req.setUnionType(unionType);
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refreType);
		UniPacket uniPacket = createUniPacket("GetUnionList", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetUnionListRsp rsp = new GetUnionListRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	@SuppressWarnings("unused")
	private static GetUnionListRsp getUnions(int unionType) {
		GetUnionListRsp rsp = new GetUnionListRsp();
		ArrayList<UnionInfo> unionList = new ArrayList<UnionInfo>();
		int count = unionType == UnionType._UNION_TYPE_TOP ? 10 : 20;
		for (int i = 0; i < count; i++) {
			UnionInfo unionInfo = new UnionInfo(
					i + 1,
					"公会" + (i + 1),
					"http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg",
					10000 * (count - i), "公会描述公会描述公会描述公会描述");
			unionList.add(unionInfo);
		}
		rsp.setUnionList(unionList);
		rsp.setAttachInfo(String.valueOf(System.currentTimeMillis() / 1000));
		return rsp;
	}

	public static void getUnionInfo(
			final ResponseListener<GetUnionInfoRsp> listener,
			final long unionId, String attachInfo, int refresh) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetUnionInfoReq req = new GetUnionInfoReq();
		req.setUnionId(unionId);
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refresh);
		UniPacket uniPacket = createUniPacket("GetUnionInfo", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetUnionInfoRsp rsp = new GetUnionInfoRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	@SuppressWarnings("unused")
	private static GetUnionInfoRsp getUnion(long unionId) {
		GetUnionInfoRsp rsp = new GetUnionInfoRsp();
		ArrayList<ArticleInfo> articleInfos = new ArrayList<ArticleInfo>();
		for (int i = 0; i < 15; i++) {
			ArticleInfo model = new ArticleInfo();
			model.setId(i + 1);
			model.setCommentCount(13);
			model.setSourceName("多玩网");
			model.setTime((int) (System.currentTimeMillis() / 1000));
			model.setTitle("这些你都知道？《口袋战争》基本常识课堂");

			ArrayList<String> urlList = new ArrayList<String>();
			if (i % 3 == 0) {
				urlList.add("http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg");
				urlList.add("http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg");
				urlList.add("http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg");
			} else {
				urlList.add("http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg");
			}

			long flag = model.flag;
			switch (i) {
			case 1: {
				flag |= ArticleFlag._ARTICLE_FLAG_ADV;
				break;
			}
			case 2: {
				flag |= ArticleFlag._ARTICLE_FLAG_BIGIMAGE;
				Map<Long, String> extraInfo = model.extraInfo;
				if (extraInfo == null) {
					extraInfo = new HashMap<Long, String>();
					model.extraInfo = extraInfo;
				}
				extraInfo
						.put((long) ArticleFlag._ARTICLE_FLAG_BIGIMAGE,
								"http://mt1.baidu.com/timg?wh_rate=0&wapiknow&quality=100&size=w250&sec=0&di=48a18b80ffa1fc6ade04d224ab555f58&src=http%3A%2F%2Fimg.iknow.bdimg.com%2Fjctuijian%2F0624%2F6.jpg");
				break;
			}
			case 3: {
				flag |= ArticleFlag._ARTICLE_FLAG_HOT;
				break;
			}
			case 4: {
				flag |= ArticleFlag._ARTICLE_FLAG_RECOMM;
				break;
			}
			case 5: {
				// flag |= ArticleFlag._ARTICLE_FLAG_TOP;
				break;
			}
			case 6: {
				break;
			}
			case 7: {
				break;
			}
			case 8: {
				break;
			}
			}
			model.setFlag(flag);
			if (i % 4 == 0) {
				model.setArticleType(ArticleType._ARTICLE_TYPE_SPECIAL);
			} else {
				model.setArticleType(ArticleType._ARTICLE_TYPE_ARTICLE);
			}

			model.setImageList(urlList);
			articleInfos.add(model);
		}
		rsp.setUnionId(unionId);
		rsp.setName("公会" + unionId);
		rsp.setSummary("公会描述公会描述公会描述公会描述什么工会什么描述什么比赛什么成员");
		rsp.setImg("http://mt1.baidu.com/timg?wh_rate=0&wapiknow&quality=100&size=w250&sec=0&di=48a18b80ffa1fc6ade04d224ab555f58&src=http%3A%2F%2Fimg.iknow.bdimg.com%2Fjctuijian%2F0624%2F6.jpg");
		rsp.setHeat((int) (10000 * (10 - unionId)));
		rsp.setRanking((int) unionId);
		rsp.setArticleList(articleInfos);
		return rsp;
	}

	public static void supportUnion(
			final ResponseListener<SparseArray<String>> listener,
			final long unionId) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		UnionVoteReq req = new UnionVoteReq();
		req.setUnionId(unionId);

		UniPacket uniPacket = createUniPacket("UnionVote", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				int code = response.getByClass("subcode", Integer.valueOf(1));
				if (code != 0) {
					code = 1;
				}
				String msg = response.getByClass("msg", String.valueOf(""));
				SparseArray<String> data = new SparseArray<String>();
				data.put(code, msg);
				listener.onResponse(data);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	public static void getRacePortalList(
			final ResponseListener<GetRacePortalRsp> listener,
			String attachInfo, int refresh) {

		// if(true) {
		// GetRacePortalRsp rsp = new GetRacePortalRsp();
		//
		// rsp.setHasMore(true);
		// rsp.setArticleList(ArticleModel.getData());
		// listener.onResponse(rsp);
		//
		// return;
		// }

		GetRacePortalReq req = new GetRacePortalReq();
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refresh);
		UniPacket uniPacket = createUniPacket("GetRacePortal", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetRacePortalRsp rsp = new GetRacePortalRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}
}
