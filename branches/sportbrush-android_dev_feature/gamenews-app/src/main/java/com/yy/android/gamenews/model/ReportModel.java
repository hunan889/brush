package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.gamenews.AddFavArticleReq;
import com.duowan.gamenews.Comment;
import com.duowan.gamenews.Count;
import com.duowan.gamenews.FavType;
import com.duowan.gamenews.LikeReq;
import com.duowan.gamenews.LikeType;
import com.duowan.gamenews.NotInterestedArticleReq;
import com.duowan.gamenews.ReportArticleReq;
import com.duowan.gamenews.ShareArticleReq;
import com.duowan.gamenews.User;
import com.duowan.jce.wup.UniPacket;

/**
 * @author yy:909011172@liangbing
 * @version 创建时间：2014-3-20 下午2:44:18
 */
public class ReportModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;
	private static int testCount = 0;
	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void NotInterestedArticle(
			final ResponseListener<Boolean> responseListener, long articleId) {
		if (TEST_DATA) {
			final long id = articleId;
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					responseListener.onResponse(true);
				}
			}, 5000);

			return;
			// articleId = 5136;
		}

		NotInterestedArticleReq req = new NotInterestedArticleReq();

		req.setArticleId(articleId);

		UniPacket uniPacket = CommonModel.createUniPacket(
				"NotInterestedArticle", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				String strType = "";
				Integer intType = 0;
				String msg = response.getByClass("msg", strType);
				int code = response.getByClass("code", intType);
				int subcode = response.getByClass("subcode", intType);
				if (code == 0 && subcode == 0) {
					responseListener.onResponse(true);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	public static void ReportArticle(
			final ResponseListener<Boolean> responseListener, long articleId) {
		ReportArticleReq req = new ReportArticleReq();

		req.setArticleId(articleId);
		UniPacket uniPacket = CommonModel.createUniPacket("ReportArticle", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				String strType = "";
				Integer intType = 0;
				String msg = response.getByClass("msg", strType);
				int code = response.getByClass("code", intType);
				int subcode = response.getByClass("subcode", intType);
				if (code == 0 && subcode == 0) {
					responseListener.onResponse(true);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	public static void LikeArticle(
			final ResponseListener<Boolean> responseListener, long articleId,
			LikeType likeType) {
		LikeReq req = new LikeReq();

		req.setLikeType(likeType.value());
		req.setArticleId(articleId);

		UniPacket uniPacket = CommonModel.createUniPacket("Like", req);

		new CommonRequest<Boolean>(responseListener.get(), uniPacket)
				.setup(responseListener, Boolean.valueOf(false))
				.setShowErrorMsg(true).setShowProgressDialog(false).execute();

		// new Request(responseListener.get(), uniPacket) {
		// @Override
		// public void onError(Exception e) {
		// // TODO Auto-generated method stub
		// super.onError(e);
		// responseListener.onError(e);
		// }
		// @Override
		// public void onResponse(UniPacket response) {
		// String strType = "";
		// Integer intType = 0;
		// String msg = response.getByClass("msg", strType);
		// int code = response.getByClass("code", intType);
		// int subcode = response.getByClass("subcode", intType);
		// if (code == 0 && subcode == 0) {
		// responseListener.onResponse(true);
		// } else {
		// responseListener.onError(new Exception(msg));
		// }
		// }
		// }.setShowProgressDialog(false).execute();
	}

	public static void LikeComment(
			final ResponseListener<Boolean> responseListener, long articleId,
			String commentId, LikeType likeType) {
		LikeReq req = new LikeReq();

		req.setLikeType(likeType.value());
		req.setCommentId(commentId);
		req.setArticleId(articleId);
		UniPacket uniPacket = CommonModel.createUniPacket("Like", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				super.onError(e);
				responseListener.onError(e);
			}

			@Override
			public void onResponse(UniPacket response) {
				String strType = "";
				Integer intType = 0;
				String msg = response.getByClass("msg", strType);
				int code = response.getByClass("code", intType);
				int subcode = response.getByClass("subcode", intType);
				if (code == 0 && subcode == 0) {
					responseListener.onResponse(true);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	public static void AddFavArticle(
			final ResponseListener<Boolean> responseListener, long articleId,
			FavType type) {
		AddFavArticleReq req = new AddFavArticleReq();

		req.setArticleId(articleId);
		req.setType(type.value());
		UniPacket uniPacket = CommonModel.createUniPacket("AddFavArticle", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				super.onError(e);
				responseListener.onError(e);
			}

			@Override
			public void onResponse(UniPacket response) {
				String strType = "";
				Integer intType = 0;
				String msg = response.getByClass("msg", strType);
				int code = response.getByClass("code", intType);
				int subcode = response.getByClass("subcode", intType);
				if (code == 0 && subcode == 0) {
					responseListener.onResponse(true);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	public static void ShareArticle(
			final ResponseListener<Boolean> responseListener, long articleId) {
		ShareArticleReq req = new ShareArticleReq();

		req.setArticleId(articleId);
		UniPacket uniPacket = CommonModel.createUniPacket("ShareArticle", req);

		new Request(responseListener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				String strType = "";
				Integer intType = 0;
				String msg = response.getByClass("msg", strType);
				int code = response.getByClass("code", intType);
				int subcode = response.getByClass("subcode", intType);
				if (code == 0 && subcode == 0) {
					responseListener.onResponse(true);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	private static ArrayList<Comment> getData() {
		// TODO: for fake data, to be removed
		ArrayList<Comment> modelList = new ArrayList<Comment>();

		for (int i = 0; i < 10; i++) {
			Comment model = new Comment();
			model.setId(Integer.toString(testCount * 100 + i));
			User user = new User();
			user.setId(String.format("%d,%d", testCount, i));
			user.setName(String.format("name%d,%d", testCount, i));
			model.setUser(user);
			model.setContent(String.format("content%d,%d", testCount, i));
			model.setId(Integer.toString(testCount * 100 + i));
			model.setCount(new Count(testCount, i));
			model.setTime(0);
			modelList.add(model);
		}

		return modelList;
	}
}
