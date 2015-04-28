package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.duowan.jce.wup.UniPacket;
import com.duowan.show.Message;
import com.duowan.show.NotificationReq;
import com.duowan.show.NotificationRsp;
import com.duowan.show.User;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class MessageModel extends CommonModel {

	private static final boolean TEST_DATA = false;
	private static final Handler sHandler;
	static {
		sHandler = new Handler(Looper.getMainLooper());
	}

	private static String MESSAGE_NAME = "show";

	public static void getPersonMessage(
			final ResponseListener<NotificationRsp> listener, int refreshType,
			int noteCallType, String attachInfo) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		if (TEST_DATA) {
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					NotificationRsp rsp = new NotificationRsp();
					rsp.setUnreadCount(10);
					rsp.setHasMore(true);
					ArrayList<Message> noteList = new ArrayList<Message>();
					for (int i = 0; i < 10; i++) {
						Message msg = new Message();
						msg.setId(i);
						User user = new User();
						user.setIcon("http://www.pyinfo.com/UploadFiles/2010-05/admin/2010556162876001.jpg");
						user.setName("达达");
						msg.setAuthor(user);
						msg.setNote("一二三四五六七八九十");
						msg.setImg("http://www.pyinfo.com/UploadFiles/2010-05/admin/2010556162876001.jpg");
						noteList.add(msg);
					}
					rsp.setNoteList(noteList);

					listener.onResponse(rsp);
				}
			}, 1000);

			return;
		}

		NotificationReq req = new NotificationReq();
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refreshType);
		req.setNoteCallType(noteCallType);
		UniPacket uniPacket = createUniPacket(MESSAGE_NAME, "Notification", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				NotificationRsp rsp = new NotificationRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();

	}

}
