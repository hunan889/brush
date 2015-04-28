package com.yy.android.gamenews.plugin.show;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.show.Tag;
import com.yy.android.gamenews.event.TagSuccessEvent;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class SelectPhotoActivity extends BaseActivity implements
		OnClickListener {
	public static final int PIC_FROM_CAMERA = 1;
	public static final int PIC_FROM＿LOCALPHOTO = 0;
	private static final String TOPIC_TAG = "topic_Tag";

	private Handler mUIHandler;
	private ActionBar mActionBar;
	private Uri photoUri;
	private Tag mTag;
	private Dialog mSavePicDialog;

	public static void startSelectPhotoActivity(Context context, Tag tag) {
		Intent intent = new Intent(context, SelectPhotoActivity.class);
		intent.putExtra(TOPIC_TAG, tag);
		context.startActivity(intent);
		StatsUtil.statsReport(context, "into_upload_pic", "tag", tag.getName());
		StatsUtil.statsReportByHiido("into_upload_pic", tag.getName());
		StatsUtil.statsReportByMta(context, "into_upload_pic", tag.getName());
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		EventBus.getDefault().register(this);
		mUIHandler = new Handler(getMainLooper());
		setContentView(R.layout.show_select_photo_activity);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle(getResources().getString(R.string.upload_pic));
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mTag = (Tag) extras.getSerializable(TOPIC_TAG);
		}
		findViewById(R.id.ll_take_picture).setOnClickListener(this);
		findViewById(R.id.ll_select_picture).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_take_picture:
			doHandlerPhoto(PIC_FROM_CAMERA);
			break;
		case R.id.ll_select_picture:
			doHandlerPhoto(PIC_FROM＿LOCALPHOTO);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据不同方式选择图片设置ImageView
	 * 
	 * @param type
	 *            0-本地相册选择，非0为拍照
	 */
	private void doHandlerPhoto(int type) {
		if (type == PIC_FROM＿LOCALPHOTO) {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
		} else if (type == PIC_FROM_CAMERA) {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String photoName = ImageUtil
					.getPhoneIMEIInfo(SelectPhotoActivity.this)
					+ String.valueOf(System.currentTimeMillis());
			String mPicName = photoName + ImageUtil.PIC_TYPE_JPG;
			String mLoactionPath = ImageUtil.getDCIM().getAbsolutePath() + "/"
					+ mPicName;
			photoUri = Uri.fromFile(new File(mLoactionPath));
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PIC_FROM_CAMERA: // 拍照
			if (resultCode == 0) {
				return;
			}
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				ToastUtil.showToast(R.string.sd_absent);
				return;
			}
			try {
				String realFilePath = ImageUtil.getRealFilePath(this, photoUri);
				if (TextUtils.isEmpty(realFilePath)
						|| !new File(realFilePath).exists()) {
					ToastUtil.showToast(R.string.take_pic_fail);
					return;
				}
				SubmitTopicActivity.startSubmitTopicActivity(
						SelectPhotoActivity.this, realFilePath, mTag);
			} catch (Exception e) {
				UiUtils.dialogDismiss(mSavePicDialog);
			}
			break;
		case PIC_FROM＿LOCALPHOTO:// 本地相册
			try {
				if (data == null || data.getData() == null || resultCode == 0) {
					return;
				}
				Uri originalUri = data.getData();
				String realFilePath = ImageUtil.getRealFilePath(this,
						originalUri);
				SubmitTopicActivity.startSubmitTopicActivity(
						SelectPhotoActivity.this, realFilePath, mTag);
			} catch (Exception e) {
				UiUtils.dialogDismiss(mSavePicDialog);
			}
			break;
		}
	}

	public void onEvent(TagSuccessEvent event) {
		if (event != null) {
			boolean sendState = event.isSuccess();
			if (sendState) {
				finish();
			}
			boolean picSizeState = event.isPicSize();
			if (picSizeState) {
				if (mUIHandler != null) {
					mUIHandler.post(new Runnable() {

						@Override
						public void run() {
							ToastUtil.showToast(getResources().getString(
									R.string.show_pic_size));
						}
					});
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
