package com.yy.android.gamenews.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yy.android.gamenews.ui.GalleryAdapter.GalleryItem;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.ui.view.BaseBannerView.Direction;
import com.yy.android.gamenews.ui.view.BaseBannerView.OnBannerItemClickListener;
import com.yy.android.gamenews.ui.view.BaseBannerView.OnFlingListener;
import com.yy.android.gamenews.ui.view.ScrollBannerView;
import com.yy.android.gamenews.ui.view.ScrollBannerView.OnPageScrollListener;
import com.yy.android.gamenews.util.AnimationHelper;
import com.yy.android.gamenews.util.DropDownHelper;
import com.yy.android.gamenews.util.DropDownHelper.DropDownItem;
import com.yy.android.gamenews.util.DropDownHelper.OnDropDownClickListener;
import com.yy.android.gamenews.util.FileUtil;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class GalleryFragment extends BaseFragment implements
		OnPageChangeListener, OnBannerItemClickListener, OnFlingListener,
		OnPageScrollListener {

	private ScrollBannerView mBanner;
	private View mBannerParent;
	private GalleryAdapter mAdapter;
	private ArrayList<GalleryItem> mDatasource;
	private ActionBar mActionBar;
	private TextView mTitleView;
	private TextView mIndexView;
	private ViewGroup mFooterParent;
	private ViewGroup mFooter;

	// private ArrayList<String> mImageUrls;
	private int mSelectPos;
	private String mTitle;

	protected LayoutInflater mInflater;
	public static final String KEY_SELECT_POS = "select_pos";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URL_LIST = "url_list";

	public static final int POSITION_FIRST = 0;
	public static final int POSITION_LAST = -1;

	protected void readDataFromBundle(Bundle bundle) {
		if (bundle != null) {
			ArrayList<String> mImageUrls = bundle
					.getStringArrayList(GalleryFragment.KEY_URL_LIST);
			mDatasource = convert(mImageUrls);
			mTitle = bundle.getString(GalleryFragment.KEY_TITLE);
			mSelectPos = bundle.getInt(GalleryFragment.KEY_SELECT_POS);
		}
	}

	public GalleryAdapter getAdapter() {
		return mAdapter;
	}

	public ActionBar getActionBar() {
		return mActionBar;
	};

	public ViewGroup getFooterParent() {
		return mFooterParent;
	}

	public ArrayList<GalleryItem> getItemList() {
		return mDatasource;
	}

	public void setItemList(ArrayList<GalleryItem> list) {
		mDatasource = list;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public final void refresh() {
		mAdapter.setDataSource(mDatasource);
		if (mDatasource != null) {
			if (mSelectPos >= mDatasource.size()) {
				mSelectPos = 0;
			}
			if (mSelectPos == POSITION_LAST) {
				mSelectPos = getImageSum() - 1;
			}
		}
		mTitleView.setText(mTitle);
		mBanner.setCurrentItem(mSelectPos, false);
		updateCurrentPos(mSelectPos);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		ViewGroup parentView = (ViewGroup) inflater.inflate(
				R.layout.activity_article_gallery, container, false);

		mTextShare = getResources().getString(R.string.global_share);
		mTextKeep = getResources().getString(R.string.global_keep);
		mTextKept = getResources().getString(R.string.cancel_collect);
		mTextSave = getResources().getString(R.string.global_save);

		mBanner = (ScrollBannerView) parentView.findViewById(R.id.pager);
		mBanner.setOnPageScrollListener(this);
		mBannerParent = parentView.findViewById(R.id.pager_layout);
		setContainer((ViewGroup) parentView.findViewById(R.id.data_layout));
		setEmptyLayoutBg(android.R.color.transparent);
		mActionBar = (ActionBar) parentView.findViewById(R.id.actionbar);
		initActionBar(mActionBar);
		initAmin();

		mAdapter = initAdapter();

		mBanner.setListAdapter(mAdapter);
		mBanner.setOnItemClickListener(this);
		mBanner.setOnFlingListener(this);
		mBanner.setOnPageChangeListener(this);
		mBanner.setDuration(3000);

		mTitleView = (TextView) parentView.findViewById(R.id.page_title);
		mIndexView = (TextView) parentView.findViewById(R.id.page_number);
		mFooterParent = (ViewGroup) parentView
				.findViewById(R.id.page_footer_layout);
		mFooter = (ViewGroup) parentView.findViewById(R.id.page_footer);
		initFooter(mFooter);
		super.onCreateView(inflater, container, savedInstanceState);
		return parentView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		pauseScroll();
		super.onPause();
	}

	protected ArrayList<GalleryItem> convert(ArrayList<String> urlList) {
		ArrayList<GalleryItem> list = new ArrayList<GalleryItem>();
		if (urlList != null) {
			for (String str : urlList) {
				GalleryItem item = new GalleryItem();
				item.url = str;
				list.add(item);
			}
		}

		return list;
	}

	protected GalleryAdapter initAdapter() {
		return new GalleryAdapter(getActivity());
	}

	@Override
	protected View getDataView() {
		return mBannerParent;
	}

	public final ScrollBannerView getBannerView() {
		return mBanner;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		refreshData();
		super.onViewCreated(view, savedInstanceState);
	}

	protected void refreshData() {
		if (mDatasource != null && mDatasource.size() > 0) {
			showView(VIEW_TYPE_DATA);
		} else {
			showView(VIEW_TYPE_EMPTY);
		}
		refresh();
	}

	protected void initFooter(ViewGroup footer) {

	}

	private void updateCurrentPos(int position) {
		mIndexView.setText(String.format("%d/%d", getImagePos(position),
				getImageSum()));
	}

	protected int getImagePos(int pagePos) {
		return pagePos + 1;
	}

	/**
	 * 显示有几张图片
	 * 
	 * @return
	 */
	protected int getImageSum() {
		return mAdapter.getCount();
	}

	private static final int FILT_DURATION = 200;
	private boolean isAnimating;
	private boolean mIsRadioVisible = true;

	protected void clearHeaderFooterAnimation() {
		mActionBar.clearAnimation();
		mFooterParent.clearAnimation();
		mIsRadioVisible = true;
	}

	public void showHideHeaderFooter() {
		if (mIsRadioVisible) {
			hideMainRadio(FILT_DURATION);
		} else {
			showMainRadio(FILT_DURATION);
		}
	}

	public void checkShowMainRadio() {
		if (!mIsRadioVisible) {
			showMainRadio(FILT_DURATION);
		}
	}

	public void checkHideMainRadio() {
		if (mIsRadioVisible) {
			hideMainRadio(FILT_DURATION);
		}
	}

	private void showMainRadio(int delay) {
		if (!mHandler.hasMessages(MSG_SHOW_RADIO)) {
			mHandler.removeMessages(MSG_HIDE_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_RADIO, delay);
		}
	}

	private void hideMainRadio(int delay) {
		if (!mHandler.hasMessages(MSG_HIDE_RADIO)) {
			mHandler.removeMessages(MSG_SHOW_RADIO);
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_RADIO, delay);
		}
	}

	private Animation mAnimRadioUpToDownIn;
	private Animation mAnimRadioUpToDownOut;
	private Animation mAnimRadioDownToUpIn;
	private Animation mAnimRadioDownToUpOut;

	private void showMainRadioNow() {
		if (isAnimating) {
			showMainRadio(10);
			return;
		}
		if (!mIsRadioVisible) {
			mAnimRadioUpToDownIn.cancel();
			mAnimRadioDownToUpIn.cancel();
			mActionBar.startAnimation(mAnimRadioUpToDownIn);
			mFooterParent.startAnimation(mAnimRadioDownToUpIn);
		}
	}

	private void initAmin() {
		mAnimRadioUpToDownIn = AnimationHelper.createAnimUpToDownIn(
				getActivity(), null);
		mAnimRadioUpToDownOut = AnimationHelper.createAnimUpToDownOut(
				getActivity(), null);
		mAnimRadioDownToUpIn = AnimationHelper.createAnimDownToUpIn(
				getActivity(), mAnimListener);
		mAnimRadioDownToUpOut = AnimationHelper.createAnimDownToUpOut(
				getActivity(), mAnimListener);
	}

	private AnimationListener mAnimListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnimating = true;
			if (!mIsRadioVisible) {
				onHeaderFooterShow();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == mAnimRadioDownToUpIn) {
				mIsRadioVisible = true;
			} else {
				onHeaderFooterHide();
				mIsRadioVisible = false;
			}
			isAnimating = false;
		}
	};

	protected void onHeaderFooterShow() {
		mActionBar.setRightClickable(true);
		mActionBar.setLeftClickable(true);
	}

	protected void onHeaderFooterHide() {
		mActionBar.setRightClickable(false);
		mActionBar.setLeftClickable(false);
	}

	private void hideMainRadioNow() {
		if (isAnimating) {
			hideMainRadio(10);
			return;
		}
		if (mIsRadioVisible) {
			mAnimRadioUpToDownOut.cancel();
			mAnimRadioDownToUpOut.cancel();
			mActionBar.startAnimation(mAnimRadioUpToDownOut);
			mFooterParent.startAnimation(mAnimRadioDownToUpOut);
		}
	}

	private static final int MSG_SHOW_RADIO = 1001;
	private static final int MSG_HIDE_RADIO = 1002;
	private Handler mHandler = new UIHandler(GalleryFragment.this);

	private static class UIHandler extends Handler {
		private WeakReference<GalleryFragment> mRef;

		public UIHandler(GalleryFragment activity) {
			mRef = new WeakReference<GalleryFragment>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			GalleryFragment activity = mRef.get();
			if (activity == null) {
				return;
			}
			switch (msg.what) {
			case MSG_SHOW_RADIO: {
				activity.showMainRadioNow();
				break;
			}
			case MSG_HIDE_RADIO: {
				activity.hideMainRadioNow();
				break;
			}
			}
		}
	}

	protected void initActionBar(ActionBar actionbar) {
		actionbar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		actionbar.setRightVisibility(View.VISIBLE);
		actionbar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DropDownHelper.showDropDownList(getActivity(),
						mActionBar.getRightImageView(), getDropDownItemList(),
						new OnDropDownClickListener() {

							@Override
							public void onClick(int position, String text) {
								onMenuClicked(position, text);
							}
						});
			}
		});

	}

	public String mTextShare;
	public String mTextKeep;
	public String mTextKept;
	public String mTextSave;

	public ArrayList<DropDownItem> getDropDownItemList() {

		ArrayList<DropDownItem> list = new ArrayList<DropDownItem>();
		list.add(new DropDownItem(mTextShare,
				R.drawable.btn_show_topic_detail_share_selector, false, false));
		list.add(new DropDownItem(mTextSave,
				R.drawable.image_download_selector, false, false));
		return list;
	}

	protected void onMenuClicked(int pos, String text) {

		if (text == null) {
			return;
		}
		if (text.equals(mTextSave)) {
			saveCurrentImage();
		} else if (text.equals(mTextShare)) {
			shareCurrentImage();
		}
	}

	public static final String TAG_SOCIAL_DIALOG = "social_dialog";

	public void shareCurrentImage() {
		int position = mBanner.getCurrentItem();
		if (mDatasource != null) {

			String url = mDatasource.get(position).url;
			if (ImageLoader.getInstance().getDiscCache().get(url).exists()) {

				DialogFragment fs = ArticleSocialDialog.newInstance(url, "",
						url, ArticleSocialDialog.SHARED_FROM_ARTICLE);
				Util.showDialog(getActivity(), fs, TAG_SOCIAL_DIALOG);
				StatsUtil.statsReportAllData(getActivity(),
						"into_cart_image_share", "desc",
						"into_cart_image_share");

				return;
			}
		}

		ToastUtil.showToast(R.string.download_not_ready);
	}

	public void saveCurrentImage() {
		int position = mBanner.getCurrentItem();
		if (mDatasource != null) {
			String url = mDatasource.get(position).url;
			ImageLoader loader = ImageLoader.getInstance();
			File file = loader.getDiscCache().get(url);
			if (file.exists()) {

				ContentResolver cr = mContext.getContentResolver();

				try {
					MediaStore.Images.Media.insertImage(cr,
							file.getAbsolutePath(), file.getName(), "");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				String saveFileName = FileUtil.saveImage(url);
				if (saveFileName != null) {
					ToastUtil.showToast(getResources().getString(
							R.string.download_success, saveFileName));

					return;
				}
			}
		}

		ToastUtil.showToast(R.string.download_not_ready);
	}

	@Override
	protected void onEmptyViewClicked() {
		refreshData();
		super.onEmptyViewClicked();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		mSelectPos = position;
		updateCurrentPos(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onBannerItemClick(View view, ListAdapter adapter, int position) {

		onActivityBackPressed();
	}

	public void startScroll() {
		ScrollBannerView bannerView = getBannerView();
		if (bannerView != null) {
			bannerView.startScroll();
		}
	}

	public void pauseScroll() {

		ScrollBannerView bannerView = getBannerView();
		if (bannerView != null) {
			bannerView.pauseScroll();
		}
	}

	@Override
	public void onFling(Direction dir, int curPos) {
		if (dir == Direction.LEFT && curPos == 0) {
			getActivity().onBackPressed();
		}
	}

	public void hideActionbarMenu() {
		mActionBar.getCustomizeView().setVisibility(View.GONE);
		mActionBar.setRightVisibility(View.INVISIBLE);
		mActionBar.setRightClickable(false);
	}

	public void showActionbarMenu() {
		mActionBar.getCustomizeView().setVisibility(View.VISIBLE);
		mActionBar.setRightVisibility(View.VISIBLE);
		mActionBar.setRightClickable(true);
	}

	@Override
	public void onScroll(int curPage, int pageCount) {

	}
}
