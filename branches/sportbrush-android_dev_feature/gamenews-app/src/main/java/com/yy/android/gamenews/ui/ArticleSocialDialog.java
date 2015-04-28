package com.yy.android.gamenews.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.CircleShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.WeiXinShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class ArticleSocialDialog extends DialogFragment implements
		OnClickListener {
	public static final String KEY_ARTICLE_ID = "article_id";
	public static final String KEY_ARTICLE_CONTENT = "article_content";
	public static final String KEY_ARTICLE_IMAGE = "article_image";
	public static final String KEY_SHARED_IMAGE = "shared_image";
	public static final String KEY_SHARED_TITLE = "shared_title";
	public static final String KEY_SHARED_CONTENT = "shared_content";
	public static final String KEY_SHARED_URL = "shared_url";
	public static final String KEY_SHARED_FROM = "shared_from";
	public static final String KEY_SHARE_TYPE = "key_share_type";

	public static final String SHARED_FROM_HD = "from_huodong";
	public static final String SHARED_FROM_ARTICLE = "from_article";
	public static final String SHARED_FROM_LIST = "from_list";
	public static final String SHARED_FROM_MYHOME = "from_myhome";
	
	public static final String TAG_SOCIAL_DIALOG = "article_social_dialog";
	public static final String TAG_REPORT_DIALOG = "article_report_dialog";
	
	private String mSharedFrom;
	private long mArticleId;
	private String mSharedUrl;
	private String mSharedTitle;
	private String mSharedContent;
	private String mSharedImage;
	private UMImage mSinaSharedUMImage;
	private UMImage mSharedUMImage;

	private GridView mList;
	private GridAdapter mListAdapter;
	private ArrayList<SocialItem> mSocialItem;
	private Activity mContext;
	private UMSocialService mController;

	public static ArticleSocialDialog newInstance(long articleId,
			String content, String imgUrl, String from) {
		ArticleSocialDialog fragment = new ArticleSocialDialog();
		Bundle args = new Bundle();
		args.putLong(KEY_ARTICLE_ID, articleId);
		args.putString(KEY_ARTICLE_CONTENT, content);
		args.putString(KEY_ARTICLE_IMAGE, imgUrl);
		args.putString(KEY_SHARED_FROM, from);
		fragment.setArguments(args);
		return fragment;
	}

	public static ArticleSocialDialog newInstance(String image, String content,
			String url, String from) {
		ArticleSocialDialog fragment = new ArticleSocialDialog();
		Bundle args = new Bundle();
		args.putString(KEY_SHARED_IMAGE, image);
		args.putString(KEY_SHARED_CONTENT, content);
		args.putString(KEY_SHARED_URL, url);
		args.putString(KEY_SHARED_FROM, from);
		fragment.setArguments(args);
		return fragment;
	}

	public static ArticleSocialDialog newInstanceForShareApp(String title,
			String content, String url, String from) {
		ArticleSocialDialog fragment = new ArticleSocialDialog();
		Bundle args = new Bundle();
		args.putInt(KEY_SHARE_TYPE, SHARE_APP);
		args.putString(KEY_SHARED_TITLE, title);
		args.putString(KEY_SHARED_CONTENT, content);
		args.putString(KEY_SHARED_URL, url);
		args.putString(KEY_SHARED_FROM, from);
		fragment.setArguments(args);
		return fragment;
	}

	// public static ArticleSocialDialog newInstanceShareApp(String image,
	// String title,
	// String url, String from) {
	// ArticleSocialDialog fragment = new ArticleSocialDialog();
	// Bundle args = new Bundle();
	// args.putString(KEY_SHARED_IMAGE, image);
	// args.putString(KEY_SHARED_TITLE, title);
	// args.putString(KEY_SHARED_URL, url);
	// args.putString(KEY_SHARED_FROM, from);
	// fragment.setArguments(args);
	// return fragment;
	// }

	private static final int SHARE_APP = 1001;
	private int mType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mArticleId = getArguments().getLong(KEY_ARTICLE_ID, -1);
		mSharedTitle = getResources().getString(R.string.app_name);
		mSharedFrom = getArguments().getString(KEY_SHARED_FROM);
		mType = getArguments().getInt(KEY_SHARE_TYPE, 0);
		if (SHARE_APP == mType) {
			mSharedContent = getArguments().getString(KEY_SHARED_CONTENT);
			String title = getArguments().getString(KEY_SHARED_TITLE);
			if (!TextUtils.isEmpty(title)) {
				mSharedTitle = title;
			}
			mSharedUrl = getArguments().getString(KEY_SHARED_URL);
			mSharedUMImage = new UMImage(getActivity(), R.drawable.ic_launcher);
		} else {
			if (mArticleId > 0) {
				mSharedContent = getArguments().getString(KEY_ARTICLE_CONTENT);
				mSharedImage = getArguments().getString(KEY_ARTICLE_IMAGE);
				mSharedUrl = String.format(Constants.ARTICLE_URL_FORMATTER,
						mArticleId);
				File cache = ImageLoader.getInstance().getDiscCache()
						.get(mSharedImage);
				if (cache.exists()) {
					mSinaSharedUMImage = new UMImage(getActivity(), cache);
				} else {
					mSinaSharedUMImage = new UMImage(getActivity(),
							R.drawable.ic_launcher);
				}
				mSharedUMImage = new UMImage(getActivity(),
						R.drawable.ic_launcher);
			} else if (mArticleId < 0) {
				try {

					String from = getArguments().getString(KEY_SHARED_FROM);
					String image = getArguments().getString(KEY_SHARED_IMAGE);
					if (from.equals(SHARED_FROM_ARTICLE)) {
						File cache = ImageLoader.getInstance().getDiscCache()
								.get(image);
						if (image == null || TextUtils.isEmpty(image)
								|| cache == null || !cache.exists()) {
							mSinaSharedUMImage = new UMImage(getActivity(),
									R.drawable.ic_launcher);
						} else {
							mSinaSharedUMImage = new UMImage(getActivity(),
									cache);
						}
					} else {
						if (image == null || TextUtils.isEmpty(image)) {
							dismiss();
							return;
						}
						byte[] bitmapArray = image.getBytes();
						bitmapArray = Base64.decode(image.trim(),
								Base64.DEFAULT);
						mSinaSharedUMImage = new UMImage(getActivity(),
								bitmapArray);
					}
					mSharedUMImage = mSinaSharedUMImage;
				} catch (Exception e) {
					dismiss();
					return;
				}
				mSharedContent = getArguments().getString(KEY_SHARED_CONTENT);
				mSharedUrl = getArguments().getString(KEY_SHARED_URL);
			}
		}

		mController = UMServiceFactory.getUMSocialService("com.umeng.share",
				RequestType.SOCIAL);

		// 新浪 SSO
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		// QQ SSO
		mController.getConfig().supportQQPlatform(mContext,
				Constants.QQ_APP_ID, Constants.QQ_APP_KEY, mSharedUrl);

		// QQ空间 SSO
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(mContext, Constants.QQ_APP_ID,
						Constants.QQ_APP_KEY));

		// 微信 SSO
		UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(
				mContext, Constants.WEIXIN_APP_KEY, mSharedUrl);
		wxHandler.setWXTitle(getString(R.string.app_name));
		UMWXHandler circleHandler = mController.getConfig()
				.supportWXCirclePlatform(mContext, Constants.WEIXIN_APP_KEY,
						mSharedUrl);
		circleHandler.setCircleTitle(mSharedContent);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity(),
				R.style.articleReportDialog);
		dialog.setContentView(R.layout.article_social_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.cancel).setOnClickListener(this);
		dialog.findViewById(R.id.back).setOnClickListener(this);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		mList = (GridView) dialog.findViewById(R.id.grid);
		mList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mListAdapter = new GridAdapter(this.getActivity(),
				R.layout.article_social_list_item);

		mSocialItem = new ArrayList<SocialItem>(5);
		mSocialItem.add(new SocialItem(
				R.drawable.article_social_weixin_selector,
				R.string.article_social_weixin));
		mSocialItem.add(new SocialItem(
				R.drawable.article_social_friend_selector,
				R.string.article_social_friend));
		mSocialItem.add(new SocialItem(R.drawable.article_social_qq_selector,
				R.string.article_social_qq));
		mSocialItem.add(new SocialItem(R.drawable.article_social_sina_selector,
				R.string.article_social_sina));
		mSocialItem.add(new SocialItem(
				R.drawable.article_social_qzone_selector,
				R.string.article_social_qzone));
		mListAdapter.setData(mSocialItem);
		mList.setAdapter(mListAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (view != null && view.getTag() != null) {
					social((Integer) view.getTag());
				}
			}

		});

		return dialog;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		default:
			break;
		}

	}

	private class GridAdapter extends ArrayAdapter<SocialItem> {
		private LayoutInflater mInflater;
		private int mResource;

		public GridAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mResource = textViewResourceId;

		}

		public void setData(List<SocialItem> data) {
			setNotifyOnChange(false);
			if (data != null) {
				for (SocialItem item : data) {
					add(item);
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = mInflater.inflate(mResource, parent, false);
			} else {
				view = convertView;
			}
			ImageView logo = (ImageView) view.findViewById(R.id.social_logo);
			TextView name = (TextView) view.findViewById(R.id.social_name);
			SocialItem item = getItem(position);
			logo.setImageResource(item.logo);
			name.setText(item.name);
			view.setTag(item.name);
			return view;
		}
	}

	private class SocialItem {
		public SocialItem(int logo, int name) {
			this.logo = logo;
			this.name = name;
		}

		int logo;
		int name;
	}

	private void social(int tag) {
		switch (tag) {
		case R.string.article_social_qzone:
			// mController.setShareContent(mSharedTitle + " " + mSharedUrl);
			// mController.setShareMedia(new UMImage(mContext,
			// R.drawable.ic_launcher));
			// 设置QQ空间分享内容
			QZoneShareContent qzone = new QZoneShareContent();
			if (TextUtils.isEmpty(mSharedContent)) {
				qzone.setShareContent(mSharedUrl);
			} else {
				qzone.setShareContent(mSharedContent);
			}
			qzone.setTitle(mSharedTitle);
			qzone.setTargetUrl(mSharedUrl);
			qzone.setShareImage(mSharedUMImage);
			mController.setShareMedia(qzone);
			postShare(SHARE_MEDIA.QZONE);

			StatsUtil.statsReport(getActivity(), "stats_share", "share_type",
					"qzone", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByMta(getActivity(), "stats_share",
					"share_type", "qzone", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByHiido("stats_share", "share_type:qzone");
			break;
		case R.string.article_social_qq:
			QQShareContent qqShareContent = new QQShareContent();
			if (!TextUtils.isEmpty(mSharedContent)) {
				qqShareContent.setTitle(mSharedTitle);
				qqShareContent.setShareContent(mSharedContent);
			}
			qqShareContent.setShareImage(mSharedUMImage);
			qqShareContent.setTargetUrl(mSharedUrl);
			mController.setShareMedia(qqShareContent);
			postShare(SHARE_MEDIA.QQ);

			StatsUtil.statsReport(getActivity(), "stats_share", "share_type",
					"qq", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByMta(getActivity(), "stats_share",
					"share_type", "qq", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByHiido("stats_share", "share_type:qq");
			break;
		case R.string.article_social_sina:
			mController.setShareContent(mSharedContent + " " + mSharedUrl);
			mController.setShareMedia(mSinaSharedUMImage);
			// SinaShareContent sinaContent = new SinaShareContent(
			// mSinaSharedUMImage);
			// sinaContent.setShareContent(mSharedTitle + " " + mSharedUrl);
			// sinaContent.setTargetUrl(mSharedUrl);
			// sinaContent.setTitle(getResources().getString(R.string.app_name));
			// mController.setShareMedia(sinaContent);
			postShare(SHARE_MEDIA.SINA);

			StatsUtil.statsReport(getActivity(), "stats_share", "share_type",
					"sina", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByMta(getActivity(), "stats_share",
					"share_type", "sina", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByHiido("stats_share", "share_type:sina");
			break;
		case R.string.article_social_weixin:
			WeiXinShareContent weixinContent = new WeiXinShareContent(
					mSharedUMImage);
			if (!TextUtils.isEmpty(mSharedContent)) {
				weixinContent.setShareContent(mSharedContent);
			}
			weixinContent.setTitle(mSharedTitle);
			mController.setShareMedia(weixinContent);
			postShare(SHARE_MEDIA.WEIXIN);

			StatsUtil.statsReport(getActivity(), "stats_share", "share_type",
					"weixin", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByMta(getActivity(), "stats_share",
					"share_type", "weixin", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByHiido("stats_share", "share_type:weinxin");
			break;
		case R.string.article_social_friend:
			CircleShareContent circleMedia = new CircleShareContent(
					mSharedUMImage);
			if (!TextUtils.isEmpty(mSharedContent)) {
				circleMedia.setShareContent(mSharedContent);
				// circleMedia.setTitle(mSharedTitle);
			}
			mController.setShareMedia(circleMedia);
			postShare(SHARE_MEDIA.WEIXIN_CIRCLE);

			StatsUtil.statsReport(getActivity(), "stats_share", "share_type",
					"frind", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByMta(getActivity(), "stats_share",
					"share_type", "frind", KEY_SHARED_FROM, mSharedFrom);
			StatsUtil.statsReportByHiido("stats_share", "share_type:friend");
			break;
		}
		dismiss();
	}

	private void postShare(SHARE_MEDIA shareMedia) {
		mController.postShare(mContext, shareMedia, new SnsPostListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				if (eCode == 200) {
				} else {
					String eMsg = "";
					if (eCode == -101) {
						eMsg = "没有授权";
						Toast.makeText(mContext, "分享失败[" + eCode + "] " + eMsg,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
}