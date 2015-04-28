package com.yy.android.gamenews.model;

import java.util.Map;

import com.duowan.gamenews.GetChannelArticleListReq;
import com.duowan.gamenews.GetChannelArticleListRsp;
import com.duowan.gamenews.GetFavArticleListReq;
import com.duowan.gamenews.GetFavArticleListRsp;
import com.duowan.gamenews.GetSpecialArticleListReq;
import com.duowan.gamenews.GetSpecialArticleListRsp;
import com.duowan.gamenews.GetVideoUrlReq;
import com.duowan.gamenews.GetVideoUrlRsp;
import com.duowan.gamenews.MeRsp;
import com.duowan.jce.wup.UniPacket;
import com.duowan.taf.jce.JceStruct;

/**
 * @author yy:909012970@liuchaoqun
 * @version 创建时间：2014-3-20 下午2:44:18
 */
public class ArticleModel extends CommonModel {

	public static void getMeRsp(final ResponseListener<MeRsp> responseListener) {
		UniPacket uniPacket = createUniPacket("Me", (JceStruct) null);
		new CommonRequest<MeRsp>(responseListener.get(), uniPacket)
				.setup(responseListener, new MeRsp()).setShowErrorMsg(false)
				.setShowProgressDialog(false).execute();
	}

	public static void getFavArticleList(
			final ResponseListener<GetFavArticleListRsp> responseListener,
			int refreshType, String attachInfo) {

		GetFavArticleListReq req = new GetFavArticleListReq();

		req.setRefreshType(refreshType);
		req.setCount(10); // 默认10个
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetFavArticleList", req);

		new CommonRequest<GetFavArticleListRsp>(responseListener.get(),
				uniPacket).setup(responseListener, new GetFavArticleListRsp())
				.setShowErrorMsg(true).setShowProgressDialog(false).execute();
	}

	public static void getSpecialArticleList(
			final ResponseListener<GetSpecialArticleListRsp> responseListener,
			int refreshType, long specialId, Map<Integer, String> attachInfo) {
		GetSpecialArticleListReq req = new GetSpecialArticleListReq();

		req.setRefreshType(refreshType);
		req.setCount(10); // 默认10个
		req.setSpecialId(specialId);
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetSpecialArticleList", req);

		new CommonRequest<GetSpecialArticleListRsp>(responseListener.get(),
				uniPacket)
				.setup(responseListener, new GetSpecialArticleListRsp())
				.setShowErrorMsg(true).setShowProgressDialog(false).execute();
	}

	public static void getVideoUrlReq(
			final ResponseListener<GetVideoUrlRsp> responseListener,
			long articleId) {
		GetVideoUrlReq req = new GetVideoUrlReq();
		req.setArticleId(articleId);
		UniPacket uniPacket = createUniPacket("GetVideoUrl", req);

		new CommonRequest<GetVideoUrlRsp>(responseListener.get(), uniPacket)
				.setup(responseListener, new GetVideoUrlRsp())
				.setShowErrorMsg(true).setShowProgressDialog(true).execute();
	}

	public static void getArticleList(
			final ResponseListener<GetChannelArticleListRsp> responseListener,
			final int refreshType, int channelId, int subChannelId,
			int subType, Map<Integer, String> attachInfo, boolean showDialog) {
		GetChannelArticleListReq req = new GetChannelArticleListReq();

		req.setRefreshType(refreshType);
		req.setChannelId(channelId);
		req.setSubChannelId(subChannelId);
		req.setSubType(subType);
		req.setAttachInfo(attachInfo);
		UniPacket uniPacket = createUniPacket("GetChannelArticleList", req);

		new CommonRequest<GetChannelArticleListRsp>(responseListener.get(),
				uniPacket)
				.setup(responseListener, new GetChannelArticleListRsp())
				.setShowErrorMsg(true).setShowProgressDialog(showDialog)
				.execute();

	}
}
