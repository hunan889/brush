/**
 * Copyright (C) 2013 Tencent Inc.
 * All rights reserved, for internal usage only.
 * 
 * Project: SosoNovel
 * FileName: AppDialog.java
 * 
 * Description: 
 * Author: lorenchen (lorenchen@tencent.com)
 * Created: Jul 11, 2012
 */
package com.yy.android.gamenews.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yy.android.sportbrush.R;

/**
 * @author lorenchen
 */
public class AppDialog extends Dialog implements View.OnClickListener {
	/**
	 * interface for OnButtonClickListener
	 * 
	 * @author lorenchen
	 * 
	 */
	public interface OnClickListener {
		/**
		 * onDialogClick
		 * 
		 * @param nButtonId
		 */
		public abstract void onDialogClick(int nButtonId);

		public abstract void onDismiss();
	}

	/**
	 * Create a new instance of AppDialog
	 * 
	 * @param aContext
	 */
	public AppDialog(Context aContext, OnClickListener aListener) {
		super(aContext, R.style.Dialog);
		mListener = aListener;
	}

	public AppDialog(Context aContext, OnClickListener aListener, int aTheme) {
		super(aContext, aTheme);
		mListener = aListener;
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((null != mListener) && (keyCode == KeyEvent.KEYCODE_BACK)) {
	// mListener.onDialogClick(BUTTON_NEGATIVE);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	/**
	 * setProperty
	 * 
	 * @param strCaption
	 * @param strMessage
	 * @param nPostiveBtnTextId
	 */
	public void setProperty(String strCaption, String strMessage,
			int nPostiveBtnTextId) {
		setProperty(strCaption, strMessage, nPostiveBtnTextId, 0);
	}

	/**
	 * setProperty
	 * 
	 * @param strCaption
	 * @param strMessage
	 * @param nPostiveBtnTextId
	 * @param nNegativeBtnTextId
	 */
	public void setProperty(String strCaption, String strMessage,
			int nPostiveBtnTextId, int nNegativeBtnTextId) {
		Context pContext = getContext();

		setProperty(
				strCaption,
				strMessage,
				pContext.getString(nPostiveBtnTextId),
				(nNegativeBtnTextId > 0 ? pContext
						.getString(nNegativeBtnTextId) : ""));
	}

	/**
	 * @param strCaption
	 * @param strMessage
	 * @param strPostiveBtnText
	 * @param strNegativeBtnText
	 */
	public void setProperty(String strCaption, String strMessage,
			String strPostiveBtnText, String strNegativeBtnText,
			String strCheckboxText) {
		(mCaption = getComponent(mCaption)).mText = strCaption;
		(mMessage = getComponent(mMessage)).mText = strMessage;

		(mPositive = getComponent(mPositive)).mText = strPostiveBtnText;
		(mNegative = getComponent(mNegative)).mText = strNegativeBtnText;

		(mSingle = getComponent(mSingle)).mText = strPostiveBtnText;
		mCheckBoxText = strCheckboxText;

	}

	/**
	 * 
	 * @param strCaption
	 * @param strMessage
	 * @param strPostiveBtnText
	 * @param strNegativeBtnText
	 */
	public void setProperty(String strCaption, String strMessage,
			String strPostiveBtnText, String strNegativeBtnText) {
		(mCaption = getComponent(mCaption)).mText = strCaption;
		(mMessage = getComponent(mMessage)).mText = strMessage;

		(mPositive = getComponent(mPositive)).mText = strPostiveBtnText;
		(mNegative = getComponent(mNegative)).mText = strNegativeBtnText;
		(mSingle = getComponent(mSingle)).mText = strPostiveBtnText;

	}

	/**
	 * setProperty
	 * 
	 * @param nCaptionId
	 * @param nMessageId
	 * @param nPostiveBtnTextId
	 * @param nNegativeBtnTextId
	 */
	public void setProperty(int nCaptionId, int nMessageId,
			int nPostiveBtnTextId, int nNegativeBtnTextId) {
		Context pContext = getContext();
		String strCaption = pContext.getString(nCaptionId);
		String strMessage = pContext.getString(nMessageId);

		setProperty(strCaption, strMessage, nPostiveBtnTextId,
				nNegativeBtnTextId);
		strCaption = null;
		strMessage = null;
	}

	/**
	 * 
	 * @param mCaptionId
	 * @param customizeView
	 * @param nPositiveBtnTextId
	 * @param nNegativeBtnTextId
	 */
	public void setProperty(String strCaption, View customizeView,
			String strPositive, String strNegative) {

		setProperty(strCaption, "", strPositive, strNegative);

		(mCustomize = getComponent(mCustomize)).mCustView = customizeView;
	}

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setOnDismissListener(mDismissListener);
		// Load the default configuration.
		setContentView(R.layout.dialog_common);

		(mCaption = getComponent(mCaption)).mView = (TextView) findViewById(R.id.dialog_caption);
		(mMessage = getComponent(mMessage)).mView = (TextView) findViewById(R.id.dialog_message);
		(mCustomize = getComponent(mCustomize)).mCustViewLayout = (ViewGroup) findViewById(R.id.dialog_customize);
		if (mCustomize.mCustView != null) {
			(mCustomize = getComponent(mCustomize)).mCustViewLayout
					.removeAllViews();
			(mCustomize = getComponent(mCustomize)).mCustViewLayout
					.addView(mCustomize.mCustView);
		}
		(mSingle = getComponent(mSingle)).mView = (TextView) findViewById(R.id.dialog_btn_single);
		mSingle.mView.setOnClickListener(this);

		(mPositive = getComponent(mPositive)).mView = (TextView) findViewById(R.id.dialog_btn_positive);
		mPositive.mView.setOnClickListener(this);

		(mNegative = getComponent(mNegative)).mView = (TextView) findViewById(R.id.dialog_btn_negative);
		mNegative.mView.setOnClickListener(this);

		mSeparator = findViewById(R.id.dialog_btn_separator);

		// Set UI configuration.
		int visibleBtns = 0;
		if (mNegative.mText != null && mNegative.mText.length() > 0) {
			visibleBtns++;
		}
		if (mPositive.mText != null && mPositive.mText.length() > 0) {
			visibleBtns++;
		}

		if (visibleBtns == 1) {
			mSingle.mView.setVisibility(View.VISIBLE);
			mNegative.mView.setVisibility(View.GONE);
			mSeparator.setVisibility(View.GONE);
			mPositive.mView.setVisibility(View.GONE);
			mPositive.mText = "";
		} else {
			mSingle.mView.setVisibility(View.GONE);
			mNegative.mView.setVisibility(View.VISIBLE);
			mSeparator.setVisibility(View.VISIBLE);
			mPositive.mView.setVisibility(View.VISIBLE);
			mSingle.mText = "";
		}

		// if(!TextUtils.isEmpty(this.mCheckBoxText))
		// {
		// mCheckBoxView = (CheckBox)findViewById(R.id.checkbox);
		// mCheckBoxView.setVisibility(View.VISIBLE);
		// mCheckBoxView.setOnClickListener(this);
		// }
		// Update ui configuration.
		this.updateUi();
	}

	/**
	 * onStart
	 */
	@Override
	protected void onStart() {
		// Call the base implementation.
		super.onStart();

		TextView pMessage = (null != mMessage ? mMessage.mView : null);
		if (null != pMessage) {
			// Measure the text length.
			TextPaint pPaint = pMessage.getPaint();
			final int nWidth = (null != mMessage.mText && mMessage.mText
					.length() > 0) ? (int) (pPaint.measureText(mMessage.mText))
					: 0;
			pPaint = null;

			final int nMsgWidth = mWinWidth - (10 << 1); // 10 pixels in both
															// sides.
			pMessage.setGravity(nMsgWidth > nWidth ? Gravity.CENTER_HORIZONTAL
					: Gravity.LEFT);
		}
	}

	@Override
	public void onClick(View aView) {
		if (null != mListener) {
			final int nId = aView.getId();
			switch (nId) {
			case R.id.dialog_btn_single:
			case R.id.dialog_btn_positive:
				mListener.onDialogClick(BUTTON_POSITIVE);
				break;

			case R.id.dialog_btn_negative:
				mListener.onDialogClick(BUTTON_NEGATIVE);
				break;
			default:
				break;
			}
		}

		// Dismiss the dialog.
		if (this.isShowing()) {
			mIsDismissWithoutNotify = true;
			dismiss();
		}
	}

	public void dismissWithoutNotify() {
		mIsDismissWithoutNotify = true;
		dismiss();
	}
	private boolean mIsDismissWithoutNotify;
	private OnDismissListener mDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {

			if (mIsDismissWithoutNotify) {
				mIsDismissWithoutNotify = false;
				return;
			}
			if (mListener != null) {
				mListener.onDismiss();
			}
		}
	};

	/**
	 * updateUi Update the UI configuration.
	 */
	protected void updateUi() {
		// Update the text.
		Component aComponents[] = { mCaption, mMessage, mPositive, mNegative,
				mSingle };
		for (int nIdx = 0; nIdx < aComponents.length; nIdx++) {
			Component pComponent = aComponents[nIdx];

			if (TextUtils.isEmpty(pComponent.mText)) {
				pComponent.mView.setVisibility(View.GONE);
			} else {
				pComponent.mView.setVisibility(View.VISIBLE);
				pComponent.mView.setText(pComponent.mText);
			}
		}

		mWinWidth = this.setAttributes();

		// Update the width for buttons.
		this.initButtons(mWinWidth);
	}

	/**
	 * setAttributes
	 */
	protected int setAttributes() {
		Window pWindow = getWindow();
		if (null == pWindow)
			return 0;

		DisplayMetrics pMetrics = new DisplayMetrics();
		pWindow.getWindowManager().getDefaultDisplay().getMetrics(pMetrics);

		WindowManager.LayoutParams pParams = pWindow.getAttributes();
		pParams.gravity = Gravity.CENTER_HORIZONTAL;
		pParams.width = (int) (pMetrics.widthPixels * 0.9);
		pWindow.setAttributes(pParams);

		// Clean up.
		pMetrics = null;
		pWindow = null;

		return pParams.width;
	}

	/**
	 * initButtons
	 * 
	 * @param nWidth
	 */
	protected void initButtons(int nWidth) {
		if (0 >= nWidth)
			return;

		final int nButtonWidth = (nWidth * 2 / 5);
		(mPositive = getComponent(mPositive)).mView.setWidth(nButtonWidth);
		(mNegative = getComponent(mNegative)).mView.setWidth(nButtonWidth);
	}

	/**
	 * getComponent
	 * 
	 * @param aComponent
	 * @return
	 */
	protected Component getComponent(Component aComponent) {
		return (null != aComponent ? aComponent : new Component());
	}

	/**
	 * Component
	 * 
	 * @author lorenchen
	 */
	class Component {
		/**
		 * Default constructor of Component
		 */
		public Component() {
			mView = null;
			mText = null;
		}

		public void setColor(int nTextColor) {
			if (null != mView) {
				mView.setTextColor(nTextColor);
			}
		}

		public ViewGroup mCustViewLayout;
		public View mCustView;
		public TextView mView;
		public String mText;
	}

	public boolean isChecked() {
		if (null != mCheckBoxView)
			return mCheckBoxView.isChecked();
		return false;
	}

	// Member instances.
	protected int mWinWidth;
	protected View mSeparator;
	protected Component mCaption;
	protected Component mMessage;
	protected Component mCustomize;
	protected Component mPositive;
	protected Component mSingle;
	protected Component mNegative;
	protected String mCheckBoxText;
	protected CheckBox mCheckBoxView;
	protected OnClickListener mListener;

	// Constant members.
	public static final int BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE;

	// Field descriptor #17 I
	public static final int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;

	// Field descriptor #17 I
	public static final int BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

}
