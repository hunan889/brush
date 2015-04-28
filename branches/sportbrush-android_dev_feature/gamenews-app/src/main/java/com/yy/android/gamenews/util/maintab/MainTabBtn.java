package com.yy.android.gamenews.util.maintab;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.yy.android.gamenews.ui.view.tab.FrameButton;
import com.yy.android.sportbrush.R;

public class MainTabBtn extends FrameButton {
	public MainTabBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MainTabBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MainTabBtn(Context context) {
		super(context);
	}

	private TextView mTextView;

	private Resources mRes;

	@Override
	protected void inflateView(LayoutInflater inflater) {
		inflater.inflate(R.layout.main_tab_bottom_tab_btn, this);
		mRes = getContext().getResources();

		mTextView = (TextView) findViewById(R.id.info_textview);
	}

	public void setDrawableResource(int resId) {
		Drawable d = mRes.getDrawable(resId);
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		mTextView.setCompoundDrawables(null, d, null, null);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTextView.setText(title);
	}
}
