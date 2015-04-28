package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ListAdapter;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleDetail;
import com.duowan.gamenews.ArticleType;
import com.duowan.gamenews.FavType;
import com.duowan.gamenews.GetArticleDetailRsp;
import com.duowan.gamenews.Image;
import com.duowan.gamenews.ImageType;
import com.duowan.gamenews.PicInfo;
import com.yy.android.gamenews.event.CommentEvent;
import com.yy.android.gamenews.model.ArticleDetailModel;
import com.yy.android.gamenews.model.ReportModel;
import com.yy.android.gamenews.ui.GalleryAdapter.GalleryItem;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.ArticleCommentView;
import com.yy.android.gamenews.ui.view.ArticleGalleryAdapter;
import com.yy.android.gamenews.ui.view.ArticleGalleryAdapter.ArticleGalleryItem;
import com.yy.android.gamenews.ui.view.BaseBannerView.Direction;
import com.yy.android.gamenews.ui.view.CommentView;
import com.yy.android.gamenews.util.ArticleDetailSwitcher;
import com.yy.android.gamenews.util.ArticleUtil;
import com.yy.android.gamenews.util.DropDownHelper.DropDownItem;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class ArticleGalleryFragment extends CommentGalleryFragment {

	private long mArticleId;
	private int mCommentCount;
	private int mSource;
	private boolean mKept;
	private int mCustomizeCount;
	private int mArticleType;
	private boolean mAutoPlay;
	private String mTextPlay;
	private String mTextPause;
	private String mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);

		mTextPlay = getString(R.string.global_play);
		mTextPause = getString(R.string.global_pause);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getBannerView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				pauseScroll();
				return false;
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void readDataFromBundle(Bundle bundle) {
		if (bundle != null) {
			mArticleId = bundle.getLong(ArticleGalleryActivity.KEY_ARTICLE_ID);
			mSource = bundle.getInt(ArticleGalleryActivity.KEY_SOURCE);
			mCommentCount = bundle
					.getInt(ArticleGalleryActivity.KEY_COMMENT_COUNT);
			mArticleType = bundle
					.getInt(ArticleGalleryActivity.KEY_ARTICLE_TYPE);
			mAutoPlay = bundle.getBoolean(ArticleGalleryActivity.KEY_AUTO_PLAY);
		}

		super.readDataFromBundle(bundle);
	}

	protected void refreshData() {
		if (mSource == ArticleGalleryActivity.SOURCE_GIVEN) {
			refresh(getTitle(), mCommentCount, getItemList());
			return;
		}

		showView(VIEW_TYPE_LOADING);
		hideActionbarMenu();
		ArticleDetailModel.getArticleDetail(
				new ResponseListener<GetArticleDetailRsp>(getActivity()) {

					@Override
					public void onResponse(GetArticleDetailRsp response) {
						if (response != null) {
							showView(VIEW_TYPE_DATA);
							showActionbarMenu();
							ArticleDetail detail = response.getArticleDetail();
							if (detail != null) {
								mTitle = detail.getTitle();
								ArticleUtil.maskAsViewed(detail.getId());
								mKept = detail.hasFav;

								int commentCount = detail.getCommentCount();

								ArrayList<Image> imageList = detail.imageList;
								ArrayList<String> imageUrlList = new ArrayList<String>();
								if (imageList != null) {
									for (Image image : imageList) {
										Map<Integer, PicInfo> picMap = image.urls;
										imageUrlList
												.add(picMap
														.get(ImageType._IMAGE_TYPE_BIG).url);
									}
								}

								ArrayList<GalleryItem> itemList = convert(imageUrlList);
								ArticleGalleryItem item = new ArticleGalleryItem();

								if (response.recommendList != null
										&& response.recommendList.size() > 0) {
									mCustomizeCount = 1;
									item.recommList = response.recommendList;
									itemList.add(item);
								} else {
									mCustomizeCount = 0;
								}

								refresh(detail.getTitle(), commentCount,
										itemList);
							}
						} else {
							setEmptyText(strEmptyNoData);
							showView(VIEW_TYPE_EMPTY);
						}

					}

					@Override
					public void onError(Exception e) {
						if (getItemList() != null && getItemList().size() > 0) {
							return;
						}
						setEmptyText(strEmptyReload);
						showView(VIEW_TYPE_EMPTY);
						super.onError(e);
					}
				}, mArticleId);
	}

	private void refresh(String title, int commentCount,
			ArrayList<GalleryItem> itemList) {

		setTitle(title);
		mCommentCountView.setText(String.valueOf(commentCount));

		ArrayList<GalleryItem> oldList = getItemList();
		if (itemList != null && !itemList.equals(oldList)) {
			setItemList(itemList);
			refresh();

			if (mAutoPlay) {
				startScroll();
			}
		}
	}

	@Override
	protected void onCommentCountViewClicked() {

		CommentListActivity.startActivity(getActivity(), mArticleId, mTitle);
	}

	@Override
	protected CommentView initCommentView() {
		ArticleCommentView view = new ArticleCommentView(getActivity());
		view.setArticleId(mArticleId);
		return view;
	}

	@Override
	public ArrayList<DropDownItem> getDropDownItemList() {
		ArrayList<DropDownItem> list = new ArrayList<DropDownItem>();

		list.add(new DropDownItem(mTextPlay,
				R.drawable.article_gallery_play_selector, false, false));

		list.add(new DropDownItem(mTextShare,
				R.drawable.btn_show_topic_detail_share_selector, false, false));
		list.add(new DropDownItem(mTextSave,
				R.drawable.image_download_selector, false, false));
		list.add(new DropDownItem(mKept ? mTextKept : mTextKeep,
				R.drawable.btn_article_collect_selector, mKept, false));
		return list;
	}

	@Override
	protected void onMenuClicked(int pos, String text) {
		if (text == null) {
			return;
		}
		if (text.equals(mTextShare)) {
			shareCurrentArticle();
			return;
		} else if (text.equals(mTextKeep) || text.equals(mTextKept)) {
			keepCurrentArticle();
			return;
		} else if (text.equals(mTextPlay)) {
			ToastUtil.showToast(R.string.article_image_detail_play_start);
			startScroll();
		} else if (text.equals(mTextPause)) {
			pauseScroll();
		}
		super.onMenuClicked(pos, text);
	}

	private void shareCurrentArticle() {
		List<GalleryItem> items = getItemList();
		String url = "";
		if (items != null) {
			for (GalleryItem image : items) {
				if (image != null && !TextUtils.isEmpty(image.url)) {
					url = image.url;
					break;
				}
			}
		}

		DialogFragment fs = ArticleSocialDialog.newInstance(mArticleId,
				getTitle(), url, ArticleSocialDialog.SHARED_FROM_ARTICLE);
		Util.showDialog(getActivity(), fs, TAG_SOCIAL_DIALOG);

	}

	private void keepCurrentArticle() {

		FavType type = mKept ? FavType.FAV_TYPE_DEL : FavType.FAV_TYPE_ADD;
		ReportModel.AddFavArticle(new ResponseListener<Boolean>(getActivity()) {
			@Override
			public void onError(Exception e) {
				super.onError(e);
				ToastUtil.showToast(R.string.load_failed);
			}

			@Override
			public void onResponse(Boolean arg0) {
				mKept = !mKept; //收藏状态取反
				int text = mKept ? R.string.article_keep_success
						: R.string.article_keep_cancel;

				Preference pref = Preference.getInstance();
				int oldCount = pref.getMyFavCount();
				int newCount = mKept ? oldCount + 1 : oldCount - 1;
				pref.saveMyFavCount(newCount);
				ToastUtil.showToast(text);

			}
		}, mArticleId, type);
	}

	@Override
	protected GalleryAdapter initAdapter() {
		return new ArticleGalleryAdapter(getActivity());
	}

	@Override
	public void onPageSelected(int position) {

		if (mCustomizeCount > 0) {
			View footer = getFooterParent();
			ActionBar actionbar = getActionBar();
			boolean isLastItem = position == getAdapter().getCount() - 1;
			if (isLastItem) {

				actionbar.setTitle(R.string.article_image_detail_more);
				footer.setVisibility(View.GONE);
				clearHeaderFooterAnimation();

				onHeaderFooterShow();
				hideActionbarMenu();
			} else {
				footer.setVisibility(View.VISIBLE);
				actionbar.setTitle("");
				showActionbarMenu();
			}
		}

		super.onPageSelected(position);
	}

	@Override
	protected int getImageSum() {
		return getAdapter().getCount() - mCustomizeCount;
	}

	@Override
	public void onBannerItemClick(View view, ListAdapter adapter, int position) {
		if (mAutoPlay) {
			pauseScroll();
		}
		if (position == getAdapter().getCount() - mCustomizeCount) {
			return;
		}

		showHideHeaderFooter();
	}

	@Override
	public void pauseScroll() {
		if (mAutoPlay) {
			checkShowMainRadio();
		}
		mAutoPlay = false;

		getActivity().getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getActivity().getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.pauseScroll();

	}

	@Override
	public void startScroll() {
		checkHideMainRadio();
		mAutoPlay = true;
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 设置屏幕常亮
		getActivity().getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.startScroll();
	}

	public void onEvent(CommentEvent event) {
		int currentCount = Integer.parseInt(mCommentCountView.getText()
				.toString());
		mCommentCountView.setText(String.valueOf(currentCount + 1));
	}

	public void onFling(Direction dir, int curPos) {

		if (mArticleType == ArticleType._ARTICLE_TYPE_IMAGE) {
			if (dir == Direction.RIGHT && curPos == getAdapter().getCount() - 1) {

				Bundle bundle = new Bundle();
				bundle.putBoolean(ArticleGalleryActivity.KEY_AUTO_PLAY,
						mAutoPlay);
				ArticleDetailSwitcher.getInstance().switchToNextArticle(
						getActivity(), mArticleId, mArticleType, bundle);
			} else if (dir == Direction.LEFT && curPos == 0) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(ArticleGalleryActivity.KEY_AUTO_PLAY,
						mAutoPlay);
				ArticleDetailSwitcher.getInstance().switchToPreArticle(
						getActivity(), mArticleId, mArticleType, bundle);
			}

			return;
		}

		super.onFling(dir, curPos);

	};

	@Override
	public void onScroll(int curPage, int pageCount) {
		if (curPage == pageCount
				&& mArticleType == ArticleType._ARTICLE_TYPE_IMAGE) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(ArticleGalleryActivity.KEY_AUTO_PLAY, mAutoPlay);
			boolean switchToNext = ArticleDetailSwitcher.getInstance()
					.switchToNextArticle(getActivity(), mArticleId,
							mArticleType, bundle);

			if (!switchToNext) {
				pauseScroll();
			}
		}

		super.onScroll(curPage, pageCount);
	}
}
