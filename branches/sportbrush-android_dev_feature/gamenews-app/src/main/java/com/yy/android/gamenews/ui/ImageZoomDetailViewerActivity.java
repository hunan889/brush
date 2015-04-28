package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ImageZoomDetailViewerActivity extends SingleFragmentActivity {

	public static final String EXTRA_IMAGE_LIST_ZOOM = "imagelistZoom";
	public static final String EXTRA_CURRENT_IMAGE = "currentimage";
	public static final String EXTRA_TITLE = "title";

	// public static final String TAG_SOCIAL_DIALOG = "article_social_dialog";
	// // 要隐藏时，layout最少要显示的时间
	// private static final int FILT_DURATION = 200; // 在该时间内被发送过来的消息会覆盖之前的消息
	// private static final int MSG_SHOW_RADIO = 1001;
	// private static final int MSG_HIDE_RADIO = 1002;
	// private LinearLayout mHead;
	// private RelativeLayout mBottom;
	// private ViewPager mPager;
	// private ImageAdapter mAdapter;
	// private TextView mPageNum;
	// private TextView mPagetitle;
	// private ImageView mShare;
	// private ImageView mBack;
	// private ImageView mDownload;
	// private ArrayList<String> mImages;
	// private String mTitle;
	// private boolean isAnimating;
	// private boolean mIsRadioVisible = true; // 初始化时为显示状态
	// private Animation mAnimRadioUpToDownIn;
	// private Animation mAnimRadioUpToDownOut;
	// private Animation mAnimRadioDownToUpIn;
	// private Animation mAnimRadioDownToUpOut;

	public static void startZoomDetailActivity(Context context,
			ArrayList<String> picList, int current, String title) {
		Intent intent = new Intent(context, ImageZoomDetailViewerActivity.class);
		intent.putExtra(GalleryFragment.KEY_URL_LIST, picList);
		intent.putExtra(GalleryFragment.KEY_TITLE, title);
		intent.putExtra(GalleryFragment.KEY_SELECT_POS, current);
		context.startActivity(intent);
	}

	@Override
	protected Fragment initFragment() {
		// TODO Auto-generated method stub
		return new GalleryFragment();
	}

	// private ImageLoadingListener mImageLoadingListener = new
	// ImageLoadingListener() {
	//
	// @Override
	// public void onLoadingStarted(String imageUri, View view) {
	// View v = (View) view.getTag();
	// v.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
	// v.findViewById(R.id.download_failed).setVisibility(View.INVISIBLE);
	//
	// }
	//
	// @Override
	// public void onLoadingFailed(String imageUri, View view,
	// FailReason failReason) {
	// View v = (View) view.getTag();
	// v.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
	// v.findViewById(R.id.download_failed).setVisibility(View.VISIBLE);
	//
	// }
	//
	// @Override
	// public void onLoadingComplete(String imageUri, View view,
	// Bitmap loadedImage) {
	// View v = (View) view.getTag();
	// v.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
	// v.findViewById(R.id.download_failed).setVisibility(View.INVISIBLE);
	// v.findViewById(R.id.image).setVisibility(View.VISIBLE);
	//
	// }
	//
	// @Override
	// public void onLoadingCancelled(String imageUri, View view) {
	//
	// }
	//
	// };
	//
	// private Handler mHandler = new
	// UIHandler(ImageZoomDetailViewerActivity.this);
	//
	// private static class UIHandler extends Handler {
	// private WeakReference<ImageZoomDetailViewerActivity> mRef;
	//
	// public UIHandler(ImageZoomDetailViewerActivity activity) {
	// mRef = new WeakReference<ImageZoomDetailViewerActivity>(activity);
	// }
	//
	// @Override
	// public void handleMessage(Message msg) {
	// ImageZoomDetailViewerActivity activity = mRef.get();
	// if (activity == null) {
	// return;
	// }
	// switch (msg.what) {
	// case MSG_SHOW_RADIO: {
	// activity.showMainRadioNow();
	// break;
	// }
	// case MSG_HIDE_RADIO: {
	// activity.hideMainRadioNow();
	// break;
	// }
	// }
	// }
	// }
	//
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_image_detail_zoo_viewer);
	// mImages = (ArrayList<String>) getIntent().getStringArrayListExtra(
	// EXTRA_IMAGE_LIST_ZOOM);
	// int pos = getIntent().getIntExtra(EXTRA_CURRENT_IMAGE, 0);
	// mTitle = getIntent().getStringExtra(
	// ImageZoomDetailViewerActivity.EXTRA_TITLE);
	// mHead = (LinearLayout) findViewById(R.id.head);
	// mBottom = (RelativeLayout) findViewById(R.id.bottom);
	// mPager = (ViewPager) findViewById(R.id.pager);
	// mPageNum = (TextView) findViewById(R.id.page_number);
	// mPagetitle = (TextView) findViewById(R.id.page_title);
	// initAmin();// 初始化头、尾动画
	// DisplayMetrics dm = new DisplayMetrics();
	// this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	// ArrayList<View> views = new ArrayList<View>(mImages.size());
	// LayoutInflater inflater = (LayoutInflater)
	// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// for (String url : mImages) {
	// View view = inflater.inflate(
	// R.layout.article_detail_zoom_image_detail, null);
	// views.add(view);
	// }
	// mAdapter = new ImageAdapter(views);
	// mPager.setAdapter(mAdapter);
	// mPager.setCurrentItem(pos);
	// mPagetitle.setText(mTitle);
	// mPageNum.setText(String.format("%d/%d", pos + 1, mAdapter.getCount()));
	// mPager.setOnPageChangeListener(new OnPageChangeListener() {
	// public void onPageScrolled(int position, float positionOffset,
	// int positionOffsetPixels) {
	// }
	//
	// public void onPageSelected(int position) {
	// mPageNum.setText(String.format("%d/%d", position + 1,
	// mAdapter.getCount()));
	// }
	//
	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	//
	// }
	// });
	// mBack = (ImageView) findViewById(R.id.back);
	// mBack.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// finish();
	// }
	//
	// });
	//
	// mDownload = (ImageView) findViewById(R.id.download);
	// mDownload.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// String url = mImages.get(mPager.getCurrentItem());
	// if (ImageLoader.getInstance().getDiscCache().get(url).exists()) {
	// String saveFileName = FileUtil.saveImage(url);
	// if (saveFileName != null) {
	// Toast.makeText(
	// ImageZoomDetailViewerActivity.this,
	// getResources()
	// .getString(R.string.download_success,
	// saveFileName),
	// Toast.LENGTH_SHORT).show();
	// }
	// } else {
	// Toast.makeText(ImageZoomDetailViewerActivity.this,
	// R.string.download_not_ready, Toast.LENGTH_SHORT)
	// .show();
	// }
	//
	// }
	//
	// });
	//
	// mShare = (ImageView) findViewById(R.id.share);
	// mShare.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// String mSocialImgUrl = mImages.get(mPager.getCurrentItem());
	// if (ImageLoader.getInstance().getDiscCache().get(mSocialImgUrl)
	// .exists()) {
	//
	// DialogFragment fs = ArticleSocialDialog.newInstance(
	// mSocialImgUrl, "", mSocialImgUrl,
	// ArticleSocialDialog.SHARED_FROM_ARTICLE);
	// Util.showDialog(ImageZoomDetailViewerActivity.this, fs,
	// TAG_SOCIAL_DIALOG);
	// StatsUtil.statsReportAllData(
	// ImageZoomDetailViewerActivity.this,
	// "into_cart_image_share", "desc",
	// "into_cart_image_share");
	// } else {
	// Toast.makeText(ImageZoomDetailViewerActivity.this,
	// R.string.download_not_ready, Toast.LENGTH_SHORT)
	// .show();
	// }
	//
	// }
	//
	// });
	// EventBus.getDefault().register(this);
	// }
	//
	// private void initAmin() {
	// mAnimRadioUpToDownIn = AnimationHelper.createAnimUpToDownIn(this, null);
	// mAnimRadioUpToDownOut = AnimationHelper.createAnimUpToDownOut(this,
	// null);
	// mAnimRadioDownToUpIn = AnimationHelper.createAnimDownToUpIn(this,
	// mAnimListener);
	// mAnimRadioDownToUpOut = AnimationHelper.createAnimDownToUpOut(this,
	// mAnimListener);
	// }
	//
	// private AnimationListener mAnimListener = new AnimationListener() {
	//
	// @Override
	// public void onAnimationStart(Animation animation) {
	// isAnimating = true;
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// if (animation == mAnimRadioDownToUpIn) {
	// mIsRadioVisible = true;
	// } else {
	// mIsRadioVisible = false;
	// }
	// isAnimating = false;
	// }
	// };
	//
	// private void showMainRadio(int delay) {
	// if (!mHandler.hasMessages(MSG_SHOW_RADIO)) {
	// mHandler.removeMessages(MSG_HIDE_RADIO);
	// mHandler.sendEmptyMessageDelayed(MSG_SHOW_RADIO, delay);
	// }
	// }
	//
	// private void hideMainRadio(int delay) {
	// if (!mHandler.hasMessages(MSG_HIDE_RADIO)) {
	// mHandler.removeMessages(MSG_SHOW_RADIO);
	// mHandler.sendEmptyMessageDelayed(MSG_HIDE_RADIO, delay);
	// }
	// }
	//
	// private void showMainRadioNow() {
	// if (isAnimating) {
	// showMainRadio(10);
	// return;
	// }
	// if (!mIsRadioVisible) {
	// mAnimRadioUpToDownIn.cancel();
	// mAnimRadioDownToUpIn.cancel();
	// mHead.startAnimation(mAnimRadioUpToDownIn);
	// mBottom.startAnimation(mAnimRadioDownToUpIn);
	// mShare.setClickable(true);
	// mBack.setClickable(true);
	// mDownload.setClickable(true);
	// }
	// }
	//
	// private void hideMainRadioNow() {
	// if (isAnimating) {
	// hideMainRadio(10);
	// return;
	// }
	// if (mIsRadioVisible) {
	// mAnimRadioUpToDownOut.cancel();
	// mAnimRadioDownToUpOut.cancel();
	// mHead.startAnimation(mAnimRadioUpToDownOut);
	// mBottom.startAnimation(mAnimRadioDownToUpOut);
	// mShare.setClickable(false);
	// mBack.setClickable(false);
	// mDownload.setClickable(false);
	// }
	// }
	//
	// public void onEvent(ImageZoomEvent event) {
	// if (mIsRadioVisible) {
	// hideMainRadio(FILT_DURATION);
	// } else {
	// showMainRadio(FILT_DURATION);
	//
	// }
	//
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// EventBus.getDefault().unregister(this);
	// }
	//
	// // 手指向右滑动时的最小速度(for slip out)
	// private static final int YDISTANCE_MAX = 50;
	// // 手指向右滑动时的最小距离
	// private int XDISTANCE_MIN = 100;
	// // 记录手指按下时的横坐标。
	// private float xDown;
	// private float yDown;
	// // 记录手指移动时的横坐标。
	// private float xMove;
	// private float yMove;
	//
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_MOVE: {
	// xMove = ev.getRawX();
	// yMove = ev.getRawY();
	// // 活动的距离
	// int distanceX = (int) (xMove - xDown);
	// int distanceY = (int) Math.abs(yDown - yMove);
	// // 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
	// if (distanceX > XDISTANCE_MIN && distanceY < YDISTANCE_MAX
	// && mPager.getCurrentItem() == 0
	// && mPager.onInterceptTouchEvent(ev)) {
	// finish();
	// return true;
	// }
	// break;
	// }
	//
	// case MotionEvent.ACTION_DOWN: {
	// xDown = ev.getRawX();
	// yDown = ev.getRawY();
	// break;
	// }
	//
	// case MotionEvent.ACTION_CANCEL:
	// case MotionEvent.ACTION_UP:
	// /* Release the drag */
	// break;
	// case MotionEvent.ACTION_POINTER_UP:
	// break;
	// }
	//
	// return super.dispatchTouchEvent(ev);
	//
	// }
	//
	// public class ImageAdapter extends CustomPagerAdapter {
	// private ArrayList<View> Views;// 存放View的ArrayList
	//
	// /*
	// * ViewAdapter构造函数
	// *
	// * @author：Robin
	// */
	// public ImageAdapter(ArrayList<View> Views) {
	// this.Views = Views;
	// }
	//
	// /*
	// * 返回View的个数
	// */
	// @Override
	// public int getCount() {
	// if (Views != null) {
	// return Views.size();
	// }
	// return 0;
	// }
	//
	// /*
	// * 销毁View
	// */
	// @Override
	// public void destroyItem(View container, int position, Object object) {
	// View v = Views.get(position);
	// if (v == null) {
	// return;
	// }
	// ImageView image = (ImageView) v.findViewById(R.id.image);
	// // CustomGifView customGifImage = (CustomGifView) v
	// // .findViewById(R.id.custom_gif_view_image);
	// if (image == null) {
	// return;
	// }
	// image.setImageDrawable(null);
	// ImageLoader.getInstance().cancelDisplayTask(image);
	// // customGifImage.setImageDrawable(null);
	// // ImageLoader.getInstance().cancelDisplayTask(customGifImage);
	// // customGifImage.stop();
	// ((ViewPager) container).removeView(Views.get(position));
	// }
	//
	// /*
	// * 初始化
	// */
	// @Override
	// public Object instantiateItem(View container, int position) {
	// View v = Views.get(position);
	// ImageView image = (ImageView) v.findViewById(R.id.image);
	// // CustomGifView customGifImage = (CustomGifView) v
	// // .findViewById(R.id.custom_gif_view_image);
	// String url = mImages.get(position);
	// image.setTag(v);
	// ImageLoader.getInstance().displayImage(url, image,
	// mImageLoadingListener);
	// // if (url.endsWith("gif")) {
	// // customGifImage.setTag(v);
	// // ImageLoader.getInstance().displayImage(url, customGifImage,
	// // mCustomGifImageLoadingListener);
	// // } else {
	// // image.setTag(v);
	// // ImageLoader.getInstance().displayImage(url, image,
	// // mImageLoadingListener);
	// // }
	// ((ViewPager) container).addView(v, 0);
	// return v;
	//
	// }
	//
	// /*
	// * 判断View是否来自Object
	// */
	// @Override
	// public boolean isViewFromObject(View view, Object object) {
	// return (view == object);
	// }
	// }

}