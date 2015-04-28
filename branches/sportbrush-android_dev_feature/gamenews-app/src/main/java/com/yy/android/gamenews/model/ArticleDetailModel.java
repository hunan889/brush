package com.yy.android.gamenews.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

import com.duowan.gamenews.ArticleDetail;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.Count;
import com.duowan.gamenews.GetArticleDetailReq;
import com.duowan.gamenews.GetArticleDetailRsp;
import com.duowan.gamenews.Image;
import com.duowan.gamenews.ImageType;
import com.duowan.gamenews.PicInfo;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.sportbrush.R;

/**
 * @author yy:909011172@liangbing
 * @version 创建时间：2014-3-20 下午2:44:18
 */
public class ArticleDetailModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;
	private static final long cacheHitButRefreshed = 0;
	// in 10 minutes
	// cache
	// will be hit, but
	// also refreshed on
	// background
	private static final long cacheExpired = 7 * 24 * 60 * 60 * 1000;
	// in 7*24 hours this
	// cache
	// entry expires
	// completely
	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void getArticleDetail(
			final ResponseListener<GetArticleDetailRsp> responseListener,
			long articleId) {

		if (TEST_DATA) {
			final long id = articleId;
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					GetArticleDetailRsp rsp = new GetArticleDetailRsp();
					rsp.setArticleDetail(getData(id));

					responseListener.onResponse(rsp);
				}
			}, 1000);

			return;
			// articleId = 5136;
		}

		GetArticleDetailReq req = new GetArticleDetailReq();

		req.setArticleId(articleId);
		UniPacket uniPacket = createUniPacket("GetArticleDetail", req);

		String cacheKey = String.format("%s-%s-%d", uniPacket.getServantName(),
				uniPacket.getFuncName(), articleId);

		new CommonRequest<GetArticleDetailRsp>(responseListener.get(),
				uniPacket, cacheKey)
				.setup(responseListener, new GetArticleDetailRsp())
				.setShowErrorMsg(false).setShowProgressDialog(false)
				.setCacheHitButRefreshed(cacheHitButRefreshed)
				.setCacheExpired(cacheExpired).execute();

		// new Request(responseListener.get(), uniPacket, cacheKey) {
		// @Override
		// public void onError(Exception e) {
		// // TODO Auto-generated method stub
		// super.onError(e);
		// responseListener.onError(e);
		// }
		//
		// @Override
		// public void onResponse(UniPacket response) {
		// String strType = "";
		// Integer intType = 0;
		// String msg = response.getByClass("msg", strType);
		// int code = response.getByClass("code", intType);
		// int subcode = response.getByClass("subcode", intType);
		// if (code == 0 && subcode == 0) {
		// GetArticleDetailRsp rsp = new GetArticleDetailRsp();
		// rsp = response.getByClass("result", rsp);
		// responseListener.onResponse(rsp);
		// } else {
		// responseListener.onError(new Exception(msg));
		// }
		// }
		// }.setCacheHitButRefreshed(cacheHitButRefreshed)
		// .setCacheExpired(cacheExpired).setShowProgressDialog(false)
		// .execute();
	}

	private static ArticleDetail getData(long articleId) {
		ArticleDetail res = new ArticleDetail();

		InputStream inputStream = GameNewsApplication.getInstance()
				.getResources().openRawResource(R.raw.article_detail);
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "gbk");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		res.setContent(sb.toString());
		ArrayList<Channel> channels = new ArrayList<Channel>();
		for (int i = 0; i < 3; ++i) {
			Channel c = new Channel(i, String.format(
					"name%dnamenamenamenamename", i), null, null, 0, null, null);
			channels.add(c);
		}
		res.setChannelList(channels);

		ArrayList<Image> images = new ArrayList<Image>();
		{
			Image image = new Image();
			PicInfo info = new PicInfo();
			info.setUrl("http://i0.sinaimg.cn/gm/2014/0515/U10650P115DT20140515141413.jpeg");
			info.setWidth(550);
			info.setHeight(310);
			Map<Integer, PicInfo> urls = new HashMap<Integer, PicInfo>();
			urls.put(ImageType._IMAGE_TYPE_BIG, info);
			image.setUrls(urls);
			images.add(image);
		}
		{
			Image image = new Image();
			PicInfo info = new PicInfo();
			info.setUrl("http://img.dwstatic.com/iphone/1310/246638195733/1382683076002.jpg");
			info.setWidth(550);
			info.setHeight(310);
			Map<Integer, PicInfo> urls = new HashMap<Integer, PicInfo>();
			urls.put(ImageType._IMAGE_TYPE_BIG, info);
			image.setUrls(urls);
			images.add(image);
		}
		{
			Image image = new Image();
			PicInfo info = new PicInfo();
			info.setUrl("http://img.dwstatic.com/ka/1404/262622667919/1398680140408.jpg");
			info.setWidth(550);
			info.setHeight(310);
			Map<Integer, PicInfo> urls = new HashMap<Integer, PicInfo>();
			urls.put(ImageType._IMAGE_TYPE_BIG, info);
			image.setUrls(urls);
			images.add(image);
		}
		{
			Image image = new Image();
			PicInfo info = new PicInfo();
			info.setUrl("http://img6.yxtq.yy.com/upload/2012/09/134664281588044.jpg");
			info.setWidth(550);
			info.setHeight(310);
			Map<Integer, PicInfo> urls = new HashMap<Integer, PicInfo>();
			urls.put(ImageType._IMAGE_TYPE_BIG, info);
			image.setUrls(urls);
			images.add(image);
		}

		res.setImageList(images);
		res.setCount(new Count(100, 20));
		res.setCommentCount(50);
		res.setId(articleId);
		res.setSourceUrl("http://games.sina.com.cn/m/n/2014-05-15/1728784552.shtml");
		res.setTitle("Neemo《唤醒来自星星的它》图文通关攻略大全 第4关");
		res.setTimeStamp(1400147751);
		res.setRefer("新浪");

		return res;
	}
}
