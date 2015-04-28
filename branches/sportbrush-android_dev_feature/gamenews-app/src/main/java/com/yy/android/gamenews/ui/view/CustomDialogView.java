package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class CustomDialogView extends FrameLayout {

	private CustomDialogView(Context context) {
		super(context);
		init(context);
	}

	private ViewGroup mImageViewGroup;
	private ViewGroup mTextViewGroup;
	private TextView mCopyView;
	private TextView mCaptionView;
	private View mCopyBtn;
	private ViewGroup mCopyLayout;
	private TextView mTitleTextView;
	private DisplayImageOptions mOptions = SwitchImageLoader
			.getDisplayOptions(R.drawable.sign_image_default);
	private LayoutInflater mInflater;

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		mInflater = inflater;

		View container = inflater.inflate(R.layout.dialog_sign_layout, this);
		mImageViewGroup = (ViewGroup) container
				.findViewById(R.id.custom_image_layout);
		mTextViewGroup = (ViewGroup) container
				.findViewById(R.id.custom_desc_layout);
		mCaptionView = (TextView) container
				.findViewById(R.id.custom_caption_tv);
		mCopyView = (TextView) container
				.findViewById(R.id.custom_copy_textview);
		mCopyLayout = (ViewGroup) container
				.findViewById(R.id.custom_copy_layout);

		mTitleTextView = (TextView) container
				.findViewById(R.id.custom_title_tv);

		mCopyBtn = container.findViewById(R.id.custom_copy_btn);
		mCopyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.copyText(mCopyView.getText().toString());

				Toast.makeText(getContext(), "已复制到粘贴板", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void addButton(int id, String url, String text,
			OnClickListener listener, boolean enabled) {

		if (mImageViewGroup.getVisibility() != View.VISIBLE) {
			mImageViewGroup.setVisibility(View.VISIBLE);
		}
		View view = mInflater.inflate(R.layout.sign_image_btn_layout, null);
		view.setId(id);
		view.setOnClickListener(listener);
		view.setEnabled(enabled);

		((LinearLayout) mImageViewGroup).setWeightSum(mImageViewGroup
				.getChildCount() + 1);
		mImageViewGroup.addView(view);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1));

		ImageView btn = (ImageView) view.findViewById(R.id.sign_image_btn);

		SwitchImageLoader.getInstance().displayImage(url, btn, mOptions);

		TextView tv = (TextView) view.findViewById(R.id.sign_image_title);
		tv.setText(text);
	}

	private void addTextView(String text, boolean center) {

		if (mTextViewGroup.getVisibility() != View.VISIBLE) {
			mTextViewGroup.setVisibility(View.VISIBLE);
		}

		TextView tv = (TextView) mInflater.inflate(
				R.layout.dialog_sign_desc_tv, null)
				.findViewById(R.id.sign_desc);
		tv.setText(text);
		if (center) {
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
		}
		mTextViewGroup.addView(tv);
	}

	private void setCaption(String text) {

		if (!TextUtils.isEmpty(text)) {
			if (mCaptionView.getVisibility() != View.VISIBLE) {

				mCaptionView.setVisibility(View.VISIBLE);
			}
		} else {

			mCaptionView.setVisibility(View.GONE);
		}

		mCaptionView.setText(text);
	}

	private void setTitleText(String text) {

		if (mTitleTextView.getVisibility() != View.VISIBLE) {
			mTitleTextView.setVisibility(View.VISIBLE);
		}
		mTitleTextView.setText(text);
	}

	private void setCopyViewText(String text) {
		mCopyView.setText(text);
	}

	private void showCopyView(boolean show) {
		if (show) {
			mCopyLayout.setVisibility(View.VISIBLE);
		} else {
			mCopyLayout.setVisibility(View.GONE);
		}
	}

	public static class Builder {
		public Builder(Context context) {
			mView = new CustomDialogView(context);
		}

		private CustomDialogView mView;

		public Builder setTitleText(String text) {
			mView.setTitleText(text);
			return this;
		}

		public Builder addButton(int id, String url, String text,
				OnClickListener listener, boolean enabled) {
			mView.addButton(id, url, text, listener, enabled);
			return this;
		}

		public Builder addTextView(String text) {
			mView.addTextView(text, false);
			return this;
		}

		public Builder addTextView(String text, boolean center) {
			mView.addTextView(text, center);
			return this;
		}

		public Builder setCopyViewText(String text) {
			mView.setCopyViewText(text);
			return this;
		}

		public Builder showCopyView(boolean show) {
			mView.showCopyView(show);
			return this;
		}

		public Builder setCaption(String text) {
			mView.setCaption(text);
			return this;
		}

		public CustomDialogView create() {
			return mView;
		}

	}
}
