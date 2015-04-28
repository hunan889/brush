package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class CommentView extends RelativeLayout {

	private Context mContext;
	private EditText mInputBox;
	private Button mSubmit;
	private View mDivider;

	public CommentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CommentView(Context context) {
		super(context);
		init(context);
	}

	protected void init(Context context) {
		this.mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.global_inputbox, this);

		mInputBox = (EditText) findViewById(R.id.global_comment_inputbox);
		mInputBox.addTextChangedListener(mTextWatcher);
		mSubmit = (Button) findViewById(R.id.global_comment_submit);

		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String comment = mInputBox.getText().toString().trim();
				if (TextUtils.isEmpty(comment)) {
					ToastUtil.showToast(R.string.comment_empty_hint);
					return;
				}

				hideInputMethod();
				if (mOnCommentListener != null) {
					mOnCommentListener.onComment(mInputBox.getEditableText()
							.toString());
				}
			}
		});

		mDivider = findViewById(R.id.divider);

		setBackgroundResource(R.drawable.global_inputbox_parent_bg_dark);
	}

	public void showDivider(boolean show) {
		mDivider.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mInputBox.getWindowToken(), 0);
	}

	public void showInputMethod() {
		if (mInputBox.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public void setEnabled(boolean enabled) {
		mInputBox.setEnabled(enabled);
		mInputBox.setClickable(enabled);
		mSubmit.setEnabled(enabled);
		mSubmit.setClickable(enabled);
		super.setEnabled(enabled);
	}

	private OnCommentListener mOnCommentListener;

	public void setOnCommentListener(OnCommentListener listener) {
		mOnCommentListener = listener;
	}

	public void setViewBackground(int resource) {
		setBackgroundResource(resource);
	}

	public void setInputBoxBackground(int resource) {
		mInputBox.setBackgroundResource(resource);
	}

	public void setInputBoxTextColor(int color) {
		mInputBox.setTextColor(color);
	}

	public void clearInputBoxText() {
		mInputBox.clearFocus();
		mInputBox.setText("");
	}

	public interface OnCommentListener {
		public void onComment(String text);
	}

	private static final int MAX_LENGTH = 140;
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() >= MAX_LENGTH) {
				ToastUtil.showToast(R.string.comment_length_max);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

}
