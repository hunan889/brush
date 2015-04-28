package com.yy.android.gamenews.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.duowan.jce.wup.UniPacket;
import com.duowan.show.AddCommentReq;
import com.duowan.show.AddTopicReq;
import com.duowan.show.AddtopicRsp;
import com.duowan.show.AdvInfo;
import com.duowan.show.AppInitReq;
import com.duowan.show.AppInitRsp;
import com.duowan.show.AppOption;
import com.duowan.show.GetCommentListReq;
import com.duowan.show.GetCommentListRsp;
import com.duowan.show.GetTagListRsp;
import com.duowan.show.GetTopicDetailReq;
import com.duowan.show.GetTopicDetailRsp;
import com.duowan.show.GetTopicListReq;
import com.duowan.show.GetTopicListRsp;
import com.duowan.show.Image;
import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.duowan.show.Topic;
import com.duowan.show.TopicLikeReq;
import com.duowan.show.User;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

/**
 * 秀场网络交互model
 * 
 * @author liuchaoqun
 * 
 */
public class ShowModel extends CommonModel {

	/**
	 * show的servant name与gamenews要区分开来
	 */
	private static String SHOW_SERVANT_NAME = "show";

	/**
	 * //初始化时请求
	 * 
	 * @param listener
	 * @param serviceType
	 */
	public static void getAdvInfo(final ResponseListener<AppInitRsp> listener,
			final int serviceType) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}
		AppInitReq req = new AppInitReq();
		req.setServiceType(serviceType);

		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME, "AppInit", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				AppInitRsp rsp = new AppInitRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	private static AppInitRsp getAppInit() {
		AppInitRsp rsp = new AppInitRsp();
		rsp.setFlag(AppOption._FLAG_APP_OPTION_NOT_DISPLAY_SHOW);
		ArrayList<AdvInfo> advInfos = new ArrayList<AdvInfo>();
		advInfos.add(new AdvInfo("每周日话题第一名用户将获得奖品", "http://www.baidu.com"));
		rsp.setAdvInfo(advInfos);
		return rsp;
	}

	/**
	 * 获取广场、分类post列表
	 * 
	 * @param listener
	 * @param tags
	 *            分类id列表
	 * @param attachInfo
	 * @param refreType
	 */
	public static void getTopicList(
			final ResponseListener<GetTopicListRsp> listener,
			final ArrayList<Integer> tags, final String attachInfo,
			int refreType) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetTopicListReq req = new GetTopicListReq();
		if (tags != null && tags.size() > 0) {
			req.setTagIdList(tags);
		}
		req.setAttachInfo(attachInfo);
		req.setRefreshType(refreType);
		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME,
				"GetTopicList", req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetTopicListRsp rsp = new GetTopicListRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	private static GetTopicListRsp getTopics() {
		GetTopicListRsp rsp = new GetTopicListRsp();
		ArrayList<Topic> topics = new ArrayList<Topic>();
		User user = new User("1", "ABCD",
				"http://img1.gtimg.com/gd/pics/hv1/194/27/1745/113475704.jpg");
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(getTags().getTagList().get(0));
		for (int i = 0; i < 25; i++) {
			Map<Integer, PicInfo> picMap = new HashMap<Integer, PicInfo>();
			PicInfo picInfo;
			if (i % 2 == 0) {
				picInfo = new PicInfo(
						"http://ww3.sinaimg.cn/bmiddle/a20a9b80jw1emju2kitxkj20cs0pt767.jpg",
						0, 0);
			} else {
				picInfo = new PicInfo(
						"http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg",
						640, 480);
			}
			picMap.put(ImageType._IMAGE_TYPE_NORMAL, picInfo);
			Image image = new Image(picMap);
			Topic topic = new Topic(i + 1, 1582634566, user, "文章内容", image,
					tags, 10, 10, 10, false);
			topic.setAuthor(user);
			topic.setImage(image);
			topics.add(topic);
		}
		rsp.setTopicList(topics);
		rsp.setHasMore(true);
		return rsp;
	}

	/**
	 * 获取文章分类列表
	 * 
	 * @param listener
	 * @param tags
	 * @param attachInfo
	 * @param refreType
	 */
	public static void getTagList(final ResponseListener<GetTagListRsp> listener) {
		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME, "GetTagList",
				null);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				GetTagListRsp rsp = new GetTagListRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}

	private static GetTagListRsp getTags() {
		GetTagListRsp rsp = new GetTagListRsp();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (int i = 0; i < 20; i++) {
			Map<Integer, PicInfo> picMap = new HashMap<Integer, PicInfo>();
			PicInfo picInfo;
			if (i % 2 == 0) {
				picInfo = new PicInfo(
						"http://ww3.sinaimg.cn/bmiddle/a20a9b80jw1emju2kitxkj20cs0pt767.jpg",
						0, 0);
			} else {
				picInfo = new PicInfo(
						"http://news.baidu.com/z/resource/r/image/2014-05-16/f465f3ca5010ac13f077853a3156dfe8.jpg",
						640, 480);
			}
			picMap.put(ImageType._IMAGE_TYPE_NORMAL, picInfo);
			Image image = new Image(picMap);
			Tag tag = new Tag(i + 1, "tag" + (i + 1), image);
			tags.add(tag);
		}
		rsp.setTagList(tags);
		return rsp;
	}

	private static final long cacheHitButRefreshed = 0;
	private static final long cacheExpired = 7 * 24 * 60 * 60 * 1000;

	public static void getTopicDetail(
			final ResponseListener<GetTopicDetailRsp> listener, int id) {

		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}

		GetTopicDetailReq req = new GetTopicDetailReq();

		req.setTopicId(id);
		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME,
				"GetTopicDetail", req);

		String cacheKey = String.format("%s-%s-%d", uniPacket.getServantName(),
				uniPacket.getFuncName(), id);

		new CommonRequest<GetTopicDetailRsp>(listener.get(), uniPacket,
				cacheKey).setup(listener, new GetTopicDetailRsp())
				.setShowErrorMsg(true)
				.setCacheHitButRefreshed(cacheHitButRefreshed)
				.setCacheExpired(cacheExpired).setShowProgressDialog(false)
				.execute();

	}

	public static void getCommentList(
			final ResponseListener<GetCommentListRsp> listener, int id,
			String attachInfo) {

		GetCommentListReq req = new GetCommentListReq();
		req.setAttachInfo(attachInfo);
		req.setTopicId(id);
		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME,
				"GetCommentList", req);

		new CommonRequest<GetCommentListRsp>(listener.get(), uniPacket)
				.setup(listener, new GetCommentListRsp()).setShowErrorMsg(true)
				.setShowProgressDialog(false).execute();
	}

	public static void sendComment(final ResponseListener<Object> listener,
			String comment, String replyCommentId, int id) {

		AddCommentReq req = new AddCommentReq();
		req.setTopicId(id);
		req.setReplyCommentId(replyCommentId);
		req.setContent(comment);
		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME, "AddComment",
				req);

		new CommonRequest<Object>(listener.get(), uniPacket)
				.setup(listener, new Object()).setShowErrorMsg(true)
				.setShowProgressDialog(false).execute();
	}

	public static void sendTopicLikeReq(
			final ResponseListener<Object> listener, int topicId) {

		TopicLikeReq req = new TopicLikeReq();
		req.setTopicId(topicId);
		UniPacket packet = createUniPacket(SHOW_SERVANT_NAME, "TopicLike", req);

		new CommonRequest<Object>(listener.get(), packet)
				.setup(listener, new Object()).setShowErrorMsg(true)
				.setShowProgressDialog(false).execute();
	}

	public static void getSubmitTopic(
			final ResponseListener<AddtopicRsp> listener, String content,
			PicInfo picInfo, ArrayList<Integer> tagList) {

		if (!Util.isNetworkConnected()) {
			ToastUtil.showToast(R.string.http_not_connected);
			listener.onError(null);
			return;
		}
		AddTopicReq req = new AddTopicReq();
		req.setContent(content);
		req.setImage(picInfo);
		req.setTagIdList(tagList);
		UniPacket uniPacket = createUniPacket(SHOW_SERVANT_NAME, "AddTopic",
				req);

		new Request(listener.get(), uniPacket) {
			@Override
			public void onResponse(UniPacket response) {
				AddtopicRsp rsp = new AddtopicRsp();
				rsp = response.getByClass("result", rsp);
				listener.onResponse(rsp);
			}

			public void onError(Exception e) {
				listener.onError(e);
			};
		}.setShowProgressDialog(false).execute();
	}
}
