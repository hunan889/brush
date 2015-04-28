package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.support.v4.app.FragmentActivity;

import com.duowan.gamenews.Channel;
import com.duowan.gamenews.FeedBackReq;
import com.duowan.gamenews.GetChannelDetailReq;
import com.duowan.gamenews.GetChannelDetailRsp;
import com.duowan.gamenews.GetColumnChannelListReq;
import com.duowan.gamenews.GetColumnChannelListRsp;
import com.duowan.gamenews.GetColumnListReq;
import com.duowan.gamenews.GetColumnListRsp;
import com.duowan.gamenews.GetMyFavChannelListReq;
import com.duowan.gamenews.GetMyFavChannelListRsp;
import com.duowan.gamenews.GetSearchSuggestionListReq;
import com.duowan.gamenews.GetSearchSuggestionListRsp;
import com.duowan.gamenews.PushInitReq;
import com.duowan.gamenews.SearchChannelReq;
import com.duowan.gamenews.SearchChannelRsp;
import com.duowan.gamenews.UpdateMyFavChannelListReq;
import com.duowan.gamenews.UpdateMyFavChannelListRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class ChannelModel extends CommonModel {

	public static void getColumnList(
			final ResponseListener<GetColumnListRsp> responseListener,
			String attachInfo, final int count) {
		GetColumnListReq req = new GetColumnListReq();
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("GetColumnList", req);

		String cacheKey = String.format("%s-%s-%s-%s",
				uniPacket.getServantName(), uniPacket.getFuncName(),
				attachInfo, String.valueOf(count));

		new CommonRequest<GetColumnListRsp>(responseListener.get(), uniPacket,
				cacheKey).setup(responseListener, new GetColumnListRsp())
				.setShowErrorMsg(true).execute();
	}

	public static void getColumnChannelList(
			final ResponseListener<GetColumnChannelListRsp> responseListener,
			int columnId, String attachInfo, final int count) {
		GetColumnChannelListReq req = new GetColumnChannelListReq();
		req.setId(columnId);
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("GetColumnChannelList", req);

		new CommonRequest<GetColumnChannelListRsp>(responseListener.get(),
				uniPacket)
				.setup(responseListener, new GetColumnChannelListRsp())
				.setShowErrorMsg(true).setShowProgressDialog(false).execute();
	}

	public static void searchChannel(
			final ResponseListener<SearchChannelRsp> responseListener,
			final String keyWord, String attachInfo, final int count) {
		SearchChannelReq req = new SearchChannelReq();
		req.setKeyword(keyWord);
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("SearchChannel", req);

		new CommonRequest<SearchChannelRsp>(responseListener.get(), uniPacket)
				.setup(responseListener, new SearchChannelRsp())
				.setShowErrorMsg(true).setShowProgressDialog(true).execute();
	}

	public static void getSearchSuggestionList(
			final ResponseListener<GetSearchSuggestionListRsp> responseListener,
			String attachInfo, int count) {
		GetSearchSuggestionListReq req = new GetSearchSuggestionListReq();
		req.setAttachInfo(attachInfo);
		req.setCount(count);
		UniPacket uniPacket = createUniPacket("GetSearchSuggestionList", req);

		new CommonRequest<GetSearchSuggestionListRsp>(responseListener.get(),
				uniPacket)
				.setup(responseListener, new GetSearchSuggestionListRsp())
				.setShowErrorMsg(false).setShowProgressDialog(false).execute();
	}

	public static void getMyFavChannelList(
			final ResponseListener<GetMyFavChannelListRsp> listener) {

		GetMyFavChannelListReq req = new GetMyFavChannelListReq();
		UniPacket uniPacket = createUniPacket("GetMyFavChannelList", req);

		new CommonRequest<GetMyFavChannelListRsp>(listener.get(), uniPacket)
				.setup(listener, new GetMyFavChannelListRsp())
				.setShowErrorMsg(false).setShowProgressDialog(false).execute();
	}

	public static void updateMyFavChannelList(FragmentActivity activity,
			ArrayList<Channel> channels) {

		updateMyFavChannelList(activity, null, channels, false);
	}

	public static void updateMyFavChannelList(FragmentActivity activity,
			final ResponseListener<UpdateMyFavChannelListRsp> responseListener,
			ArrayList<Channel> channels, boolean showDialog) {
		ArrayList<Integer> channelIds = new ArrayList<Integer>();
		if (channels != null) {
			for (Channel channel : channels) {
				channelIds.add(channel.id);
			}
		}
		UpdateMyFavChannelListReq req = new UpdateMyFavChannelListReq();
		req.setChannelList(channelIds);
		UniPacket uniPacket = createUniPacket("UpdateMyFavChannelList", req);

		Request request = new Request(activity, uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				UpdateMyFavChannelListRsp rsp = new UpdateMyFavChannelListRsp();
				rsp = response.getByClass("result", rsp);
				if (responseListener != null) {
					responseListener.onResponse(rsp);
				}
			}

			public void onError(Exception e) {
				Preference pref = Preference.getInstance();
				pref.setRetryState(true);
				e.getCause();

				if (responseListener != null) {
					responseListener.onError(null);
				}
			};
		};
		request.setShowProgressDialog(showDialog);
		request.execute();
	}

	public static void pushInit(String userId) {
		PushInitReq req = new PushInitReq();
		req.setUserId(userId);
		UniPacket uniPacket = createUniPacket("PushInit", req);

		Request request = new Request(uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				response.get("code");
			}

			public void onError(Exception e) {
				e.getCause();
			};
		};
		request.execute();
	}

	public static void sendFeedBack(
			final ResponseListener<Boolean> responseListener, String msg,
			String platformInfo, String appVersion) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			return;
		}
		FeedBackReq req = new FeedBackReq();
		req.setAppVersion(appVersion);
		req.setPlatformInfo(platformInfo);
		req.setContent(msg);
		UniPacket uniPacket = createUniPacket("FeedBack", req);

		Request request = new Request(uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				int code = -1;
				code = response.get("code", Integer.class);
				if (code == 0) {
					responseListener.onResponse(true);
				}
			}

			public void onError(Exception e) {
				responseListener.onError(e);
			};
		};
		request.execute();
	}

	public static void getChannelById(
			final ResponseListener<GetChannelDetailRsp> listener, int id) {

		GetChannelDetailReq req = new GetChannelDetailReq();
		req.setId(id);
		UniPacket packet = createUniPacket("GetChannelDetail", req);

		new CommonRequest<GetChannelDetailRsp>(listener.get(), packet).setup(
				listener, new GetChannelDetailRsp()).execute();
	}
}
