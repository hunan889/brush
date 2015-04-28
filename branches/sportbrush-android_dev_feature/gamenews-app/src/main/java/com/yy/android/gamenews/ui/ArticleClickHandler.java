package com.yy.android.gamenews.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleFlag;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.GetVideoUrlRsp;
import com.duowan.gamenews.LoginActionFlag;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.VideoFlag;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.plugin.cartport.CartDetailImageActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.ui.view.AppDialog.OnClickListener;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.gamenews.util.maintab.MainTab1;
import com.yy.android.gamenews.util.maintab.MainTab2;
import com.yy.android.gamenews.util.maintab.MainTab3;
import com.yy.android.gamenews.util.maintab.MainTab4;
import com.yy.android.gamenews.util.maintab.MainTab5;
import com.yy.android.sportbrush.R;

/**
 * 处理文章点击跳转事件
 * 
 * @author liuchaoqun
 * 
 */
public class ArticleClickHandler {

	private FragmentActivity getActivity() {
		return mActivity;
	}

	private FragmentActivity mActivity;

	public ArticleClickHandler(FragmentActivity activity) {
		mActivity = activity;
	}

	public void onArticleItemClick(final ArticleInfo model) {
		boolean isRedirect = (model.getFlag() & ArticleFlag._ARTICLE_FLAG_REDIRECT) != 0;
		if (isRedirect) {
			startWeb(model, "");
			addStatisticsEvent(model.getId(), model.getTitle());
			sendArticlestatics(MainTabEvent.TAB_HEAD_INFO, model.getTitle());
			return;
		}

		switch (model.getArticleType()) {
		case ArticleType._ARTICLE_TYPE_ARTICLE: {

			ArticleDetailActivity.startArticleDetailActivity(getActivity(),
					model);
			addStatisticsEvent(model.getId(), model.getTitle());
			sendArticlestatics(MainTabEvent.TAB_ORDER_INFO, model.getTitle());
			break;
		}
		case ArticleType._ARTICLE_TYPE_CART_IMAGE_COLUMN: {
			CartDetailImageActivity.startCartDetailActivity(getActivity(),
					model.getId(), model.getTitle());
			break;
		}
		case ArticleType._ARTICLE_TYPE_SPECIAL: {
			ArticleListActivity.startSpecialListActivity(getActivity(),
					model.getId());
			addStatisticsEvent(model.getId(), model.getTitle());
			sendArticlestatics(MainTabEvent.TAB_ORDER_INFO, model.getTitle());
			break;
		}
		case ArticleType._ARTICLE_TYPE_IMAGE: {
			ArticleGalleryActivity
					.startActivity(getActivity(), model.getId(),
							model.getTitle(), 0,
							ArticleType._ARTICLE_TYPE_IMAGE, false);
			break;
		}
		case ArticleType._ARTICLE_TYPE_VIDEO: {
			if (Util.isNetworkConnected()) {
				if (!Util.isWifiConnected()) {
					UiUtils.showDialog(getActivity(), R.string.global_caption,
							R.string.play_video_no_wifi, R.string.global_ok,
							R.string.global_cancel, new OnClickListener() {

								@Override
								public void onDismiss() {
									// TODO Auto-generated method stub

								}

								@Override
								public void onDialogClick(int nButtonId) {
									if (nButtonId == AppDialog.BUTTON_POSITIVE) {
										playVideo(model);
									}
								}
							});
				} else {
					playVideo(model);
				}
			} else {
				ToastUtil.showToast(R.string.global_network_error);
			}
		}
		}
	}

	private void playVideo(ArticleInfo model) {

		ArticleModel.getVideoUrlReq(new ResponseListener<GetVideoUrlRsp>(
				getActivity()) {
			public void onResponse(GetVideoUrlRsp rsp) {
				if (rsp != null) {
					String url = rsp.url;
					switch (rsp.videoFlag) {
					case VideoFlag._VIDEO_FLAG_REDIRECT: {
						AppWebActivity.startWebActivityFromNotice(
								getActivity(), url);
						break;
					}
					case VideoFlag._VIDEO_FLAG_SOURCE: {
						VideoPlayerActivity.startVideoPlayerActivity(
								getActivity(), rsp.getTitle(), rsp.getUrl());
						break;
					}
					}
				}
			};
		}, model.getId());
	}

	private void startWeb(ArticleInfo model, String title) {
		Intent intent = new Intent(getActivity(), AppWebActivity.class);
		String url = model.getSourceUrl();
		if (model.getArticleType() == ArticleType._ARTICLE_TYPE_TEQUAN) {
			UserInitRsp rsp = Preference.getInstance().getInitRsp();
			String token = "";
			if (rsp != null) {
				token = rsp.extraInfo
						.get(LoginActionFlag._LOGIN_ACTION_FLAG_YY_TOKEN);
			}
			if (token != null && (!TextUtils.isEmpty(token))) {
				url = url + token;
				intent.putExtra(AppWebActivity.KEY_ENABLE_CACHE, true);
			}
		}
		intent.putExtra(AppWebActivity.KEY_URL, url);
		intent.putExtra(AppWebActivity.KEY_TITLE, title);
		getActivity().startActivity(intent);
	}

	private void sendArticlestatics(String tab, String param) {
		MainTabStatsUtil.statistics(getActivity(), tab,
				MainTabEvent.ARTICLE_INFO, param != null ? param
						: MainTabEvent.ARTICLE_INFO_NAME);
	}

	private void addStatisticsEvent(long id, String title) {

		String tabName = getTabNameByIndex(ArticleDetailActivity.CURRENT_BUTTON_TAB);
		// 下面tab统计（方案一）
		StatsUtil.statsReport(getActivity(), "stats_read_article_tab", tabName,
				title);
		StatsUtil.statsReportByMta(getActivity(), "stats_read_article_tab",
				tabName, "(" + id + ")" + title);
		StatsUtil.statsReportByHiido("stats_read_article_tab", tabName + title);
		// 下面tab统计（方案二）
		StatsUtil.statsReport(getActivity(),
				"stats_read_article_tab_second_method", tabName, title);
		StatsUtil.statsReportByMta(getActivity(),
				"stats_read_article_tab_second_method", "bottom_tab_name",
				tabName);
		StatsUtil.statsReportByHiido("stats_read_article_tab_second_method",
				tabName);
	}

	public static final String TAG_NAME_INFO = "info"; // 头条，第一个tab
	public static final String TAG_NAME_NEWS = "news"; // 频道，第二个tab
	public static final String TAG_NAME_EXTRA1 = "extra1"; // 额外的tab1
	public static final String TAG_NAME_EXTRA2 = "extra2"; // 额外的tab2
	public static final String TAG_NAME_EXTRA3 = "extra3"; // 额外的tab3

	private String getTabNameByIndex(int index) {

		switch (index) {
		case MainTab1.INDEX: {
			return TAG_NAME_INFO;
		}
		case MainTab2.INDEX: {
			return TAG_NAME_NEWS;
		}
		case MainTab3.INDEX: {
			return TAG_NAME_EXTRA1;
		}
		case MainTab4.INDEX: {
			return TAG_NAME_EXTRA2;
		}
		case MainTab5.INDEX: {
			return TAG_NAME_EXTRA3;
		}
		}
		return "";
	}

}
