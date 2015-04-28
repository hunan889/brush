package com.yy.android.gamenews.plugin.show;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.show.AddtopicRsp;
import com.duowan.show.AllowEmptyContent;
import com.duowan.show.GetTopicDetailRsp;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.tencent.mid.util.Util;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.bs2.util.AppInfo;
import com.yy.android.gamenews.bs2.util.Bs2Client;
import com.yy.android.gamenews.bs2.util.OnceRet;
import com.yy.android.gamenews.bs2.util.OnceUploadClient;
import com.yy.android.gamenews.event.SendTopicEvent;
import com.yy.android.gamenews.event.TagSuccessEvent;
import com.yy.android.gamenews.model.ShowModel;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class SubmitTopicActivity extends BaseActivity implements TextWatcher {

	private static final String REAL_FILE_PATH = "real_file_path";
	private static final String TOPIC_TAG = "topic_Tag";

	public static void startSubmitTopicActivity(Context context,
			String realFilePath, Tag tag) {
		Intent intent = new Intent(context, SubmitTopicActivity.class);
		intent.putExtra(REAL_FILE_PATH, realFilePath);
		intent.putExtra(TOPIC_TAG, tag);
		context.startActivity(intent);
	}

	private ActionBar mActionBar;
	private EditText mContentEditText;
	private TextView mTopicTitle;
	private ImageView mTopicCutPic;
	private TextView mTextNums;
	private Dialog mDialog;
	private ArrayList<Integer> mTags;
	private String mPicName;
	private String mFinalUrl;
	private int mOriginalWidth;
	private int mOriginalHeight;
	private String mLoactionPath;
	private int mFromType;
	protected SavePicTask mSavePicTask;
	protected CommitTask mCommitTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		setContentView(R.layout.show_submit_topic_activity);
		mContentEditText = (EditText) findViewById(R.id.submit_topic_comments);
		mTopicTitle = (TextView) findViewById(R.id.submit_topic_title);
		mTopicCutPic = (ImageView) findViewById(R.id.submit_topic_small_pic);
		mTextNums = (TextView) findViewById(R.id.tag_des_nums);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mContentEditText.addTextChangedListener(this);
		mActionBar.setRightTextVisibility(View.VISIBLE);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mActionBar.setRightTextResource(R.string.upload_topic_des);
		mActionBar.setOnRightClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideInputMethod(mContentEditText);
				if (!Util.isNetworkAvailable(SubmitTopicActivity.this)) {
					ToastUtil.showToast(getResources().getString(
							R.string.global_network_error));
					return;
				}
				int allowEmptyTopicContent = Preference.getInstance()
						.getAllowEmptyTopicContent();
				if (allowEmptyTopicContent == AllowEmptyContent._NO_ALLOW_EMPTY
						&& TextUtils.isEmpty(mContentEditText.getText()
								.toString().trim())) {
					ToastUtil.showToast(R.string.upload_topic_content_empty);
					return;
				}
				mCommitTask = new CommitTask();
				mCommitTask.execute();
				mDialog = UiUtils
						.loadingDialogShow(
								SubmitTopicActivity.this,
								getResources().getString(
										R.string.upload_topic_loading));
			}

		});
		mActionBar.setTitle(getResources().getString(R.string.submit_topic));

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String mRealFilePath = extras.getString(REAL_FILE_PATH);
			Tag tag = (Tag) extras.getSerializable(TOPIC_TAG);
			mTopicTitle.setText(tag.getName());
			mTags = new ArrayList<Integer>();
			mTags.add(tag.getId());
			mSavePicTask = new SavePicTask(mRealFilePath, mFromType);
			mSavePicTask.execute();
		}
	}

	private void setLoationPic() {
		File file = new File(mLoactionPath);
		if (file != null && file.exists()) {
			mTopicCutPic.setImageBitmap(ImageUtil.ImageCrop(
					BitmapFactory.decodeFile(file.getAbsolutePath()),
					ImageUtil.SMALL_PIC_SIZE, ImageUtil.SMALL_PIC_SIZE, true));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		mTextNums.setText(String.format(
				getResources().getString(R.string.show_des_nums),
				140 - s.length()));
		if (s.length() >= 140) {
			ToastUtil.showToast(getResources().getString(
					R.string.show_nums_warn));
		}
	}

	public void submitTopicRequest() {
		PicInfo picInfo = new PicInfo();
		picInfo.setUrl(mFinalUrl);
		picInfo.setHeight(mOriginalHeight);
		picInfo.setWidth(mOriginalWidth);
		String content = mContentEditText.getText().toString().trim();
		ShowModel.getSubmitTopic(new ResponseListener<AddtopicRsp>(this) {

			@Override
			public void onResponse(AddtopicRsp addtopicRsp) {
				if (addtopicRsp != null && addtopicRsp.getTopicId() >= 0) {
					requestTopicDetail(addtopicRsp.getTopicId());
				} else {
					UiUtils.dialogDismiss(mDialog);
					ToastUtil.showToast(R.string.upload_topic_fail);
				}
			}

			@Override
			public void onError(Exception e) {
				super.onError(e);
				UiUtils.dialogDismiss(mDialog);
				ToastUtil.showToast(R.string.upload_topic_fail);
			}
		}, content, picInfo, mTags);

	}

	private void requestTopicDetail(final int mTopicId) {
		ShowModel.getTopicDetail(new ResponseListener<GetTopicDetailRsp>(this) {

			@Override
			public void onResponse(GetTopicDetailRsp response) {
				if (response != null && response.getTopicInfo() != null) {
					ImageUtil.deleteDirectory(mLoactionPath);
					UiUtils.dialogDismiss(mDialog);
					SendTopicEvent sendTopicEvent = new SendTopicEvent();
					sendTopicEvent.setTopic(response.getTopicInfo());
					EventBus.getDefault().post(sendTopicEvent);
					TagSuccessEvent event = new TagSuccessEvent();
					event.setSuccess(true);
					EventBus.getDefault().post(event);
					addEventStatis();
					finish();
				} else {
					UiUtils.dialogDismiss(mDialog);
					ToastUtil.showToast(R.string.upload_topic_fail);
				}
			}

			@Override
			public void onError(Exception e) {
				UiUtils.dialogDismiss(mDialog);
				ToastUtil.showToast(R.string.upload_topic_fail);
			}
		}, mTopicId);
	}

	public void addEventStatis() {
		StatsUtil.statsReport(this, "submit_topic", "topic", "submit_topic");
		StatsUtil.statsReportByHiido("submit_topic", "submit_topic");
		StatsUtil.statsReportByMta(this, "submit_topic", "submit_topic");
	}

	public OnceRet upLoad() {
		AppInfo appInfo = new AppInfo();
		appInfo.setAccessKey(Constants.BS2_ACCESS_KEY);
		appInfo.setAccessSecret(Constants.BS2_ACCESS_SECRET);
		appInfo.setBucket(Constants.BS2_BUCKET);
		OnceUploadClient client = Bs2Client.newOnceUploadClient(appInfo);

		File file = new File(mLoactionPath);
		if (file.exists()) {
			OnceRet ret = null;
			try {
				ret = client.UploadFile(file, file.length(), "image/*",
						mPicName, 0);
				if (ret.getCode() == 200) {
					mFinalUrl = mPicName;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("test", "response is " + ret.toString());
			client.close();
			return ret;
		}
		return null;
	}

	class SavePicTask extends AsyncTask<Void, Void, Boolean> {
		String realFilePath;
		int type;

		public SavePicTask(String realFilePath, int type) {
			this.realFilePath = realFilePath;
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Options bitmapOptions = ImageUtil.getBitmapOptions(realFilePath
					.trim());
			int width = bitmapOptions.outWidth;
			int height = bitmapOptions.outHeight;
			if (width < ImageUtil.MIN_WIDTH || height < ImageUtil.MIN_HEIGHT) {
				TagSuccessEvent event = new TagSuccessEvent();
				event.setPicSize(true);
				EventBus.getDefault().post(event);
				finish();
				return;
			}
			mDialog = UiUtils.loadingDialogShow(SubmitTopicActivity.this,
					getResources().getString(R.string.take_pic_processing));
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			File file = new File(realFilePath);
			int degree = ImageUtil.readPictureDegree(file.getAbsolutePath());
			Bitmap myBitmap = ImageUtil.getSmallBitmap(realFilePath.trim());
			/**
			 * 把图片旋转为正的方向
			 */
			Bitmap cameraBitmap = ImageUtil.rotaingImageView(degree, myBitmap);
			Bitmap zoomBitmap = ImageUtil.zoomBitmap(cameraBitmap,
					ImageUtil.PIC_WIDTH, ImageUtil.PIC_HEIGHT, true);
			mOriginalWidth = zoomBitmap.getWidth();
			mOriginalHeight = zoomBitmap.getHeight();
			String photoName = ImageUtil
					.getPhoneIMEIInfo(SubmitTopicActivity.this)
					+ String.valueOf(System.currentTimeMillis());
			mPicName = photoName + ImageUtil.PIC_TYPE_JPG;
			mLoactionPath = ImageUtil.getDCIM().getAbsolutePath() + "/"
					+ mPicName;

			boolean savePicState = ImageUtil.savePhotoToSDCard(zoomBitmap,
					ImageUtil.getDCIM().getAbsolutePath(), photoName,
					ImageUtil.PIC_TYPE_JPG);
			zoomBitmap.recycle();
			return savePicState;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			UiUtils.dialogDismiss(mDialog);
			if (!result) {
				ToastUtil.showToast(R.string.take_pic_fail);
				finish();
			} else {
				setLoationPic();
			}
		}
	}

	class CommitTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			OnceRet upLoad = upLoad();
			if (upLoad != null && upLoad.getCode() == 200) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				if (!Util.isNetworkAvailable(SubmitTopicActivity.this)) {
					UiUtils.dialogDismiss(mDialog);
					ToastUtil.showToast(getResources().getString(
							R.string.global_network_error));
					return;
				}
				// 发表
				submitTopicRequest();
			} else {
				UiUtils.dialogDismiss(mDialog);
				ToastUtil.showToast(R.string.upload_topic_fail);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSavePicTask != null) {
			mSavePicTask.cancel(true);
			mSavePicTask = null;
		}
		if (mCommitTask != null) {
			mCommitTask.cancel(true);
			mCommitTask = null;
		}
	}

}