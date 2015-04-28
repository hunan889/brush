package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.gamenews.AddCommentReq;
import com.duowan.gamenews.Comment;
import com.duowan.gamenews.Count;
import com.duowan.gamenews.GetCommentListReq;
import com.duowan.gamenews.GetCommentListRsp;
import com.duowan.gamenews.User;
import com.duowan.jce.wup.UniPacket;

/**
 * @author yy:909011172@liangbing
 * @version 创建时间：2014-3-20 下午2:44:18
 */
public class CommentModel extends CommonModel {

	private static final Handler sHandler;
	private static final boolean TEST_DATA = false;
	private static int testCount = 0;
	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static void addComment(
			final ResponseListener<Boolean> responseListener, long articleId,
			String comment, boolean showDialog) {

		AddCommentReq req = new AddCommentReq();

		req.setArticleId(articleId);
		req.setComment(comment);

		UniPacket uniPacket = createUniPacket("AddComment", req);

		new CommonRequest<Boolean>(responseListener.get(), uniPacket)
				.setup(responseListener, Boolean.valueOf(false))
				.setShowErrorMsg(true).setShowProgressDialog(true).execute();

	}

	public static void getCommentList(
			final ResponseListener<GetCommentListRsp> responseListener,
			long articleId, String attachInfo) {

		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					GetCommentListRsp rsp = new GetCommentListRsp();

					rsp.setCommentList(getData());
					rsp.setHasMore(testCount < 3 ? true : false);
					if (testCount >= 3) {
						testCount = 0;
					} else {
						testCount++;
					}

					responseListener.onResponse(rsp);
				}
			}, 2000);

			return;
		}
		GetCommentListReq req = new GetCommentListReq();

		req.setArticleId(articleId);
		req.setCount(20); // 默认10个
		req.setAttachInfo(attachInfo);

		UniPacket uniPacket = createUniPacket("GetCommentList", req);

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
					GetCommentListRsp rsp = new GetCommentListRsp();
					rsp = response.getByClass("result", rsp);
					responseListener.onResponse(rsp);
				} else {
					responseListener.onError(new Exception(msg));
				}
			}
		}.setShowProgressDialog(false).execute();
	}

	private static ArrayList<Comment> getData() {
		// TODO: for fake data, to be removed
		ArrayList<Comment> modelList = new ArrayList<Comment>();

		for (int i = 0; i < 4; i++) {
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
