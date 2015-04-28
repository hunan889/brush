package com.yy.android.gamenews.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.android.base.util.LocalLog;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class FeedbackActivity extends BaseActivity {
	private static final int mMaxWordsNum = 160;
	private EditText mComment;
	private TextView mWordsNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		actionBar.setOnRightClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String comment = mComment.getText().toString();
				sendFeedBack(comment);
			}
		});

		mComment = (EditText) findViewById(R.id.posts_editor);
		mComment.addTextChangedListener(mTextWatcher);
		mWordsNum = (TextView) findViewById(R.id.words_num);
		mWordsNum.setText(String.format("0/%d", mMaxWordsNum));

	}

	@Override
	public void onPause() {
		super.onPause();
		hideSoftKeybord();
	}

	public void hideSoftKeybord() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mComment.getWindowToken(), 0);
	}

	private static final String KEY_OPEN_LOG = "showLog";
	private static final String KEY_CLOSE_LOG = "closeLog";
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (KEY_OPEN_LOG.equals(s.toString())) {
				if (!LocalLog.FLAG_WRITE_TO_FILE) {

					LocalLog.FLAG_WRITE_TO_FILE = true;
					ToastUtil.showToast("Log已经打开，文件保存在"
							+ LocalLog.LOG_FILE_FOLDER);
					Preference.getInstance().setNeedShowLog(true);
				}
			} else if (KEY_CLOSE_LOG.equals(s.toString())) {
				if (LocalLog.FLAG_WRITE_TO_FILE) {

					ToastUtil.showToast("Log已经关闭");
					LocalLog.FLAG_WRITE_TO_FILE = false;
					Preference.getInstance().setNeedShowLog(false);
				}
			}
			mWordsNum.setText(String.format("%d/%d", s.length(), mMaxWordsNum));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public void sendFeedBack(String msg) {

		if (TextUtils.isEmpty(msg)) {
			ToastUtil.showToast(R.string.feedback_empty_hint);
			return;
		}

		ResponseListener<Boolean> responseListener = new ResponseListener<Boolean>(
				FeedbackActivity.this) {

			public void onResponse(Boolean isSuccess) {
				if (isSuccess) {
					ToastUtil.showToast(R.string.feedback_success);
					finish();
				} else {
					ToastUtil.showToast(R.string.feedback_failed);
				}
			}

			public void onError(Exception e) {

			}
		};

		String platformInfo = String.format("android %s %s %s",
				android.os.Build.VERSION.RELEASE, android.os.Build.BRAND,
				android.os.Build.MODEL);
		String appVersion = "app_version:";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			appVersion += pInfo.versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		ChannelModel.sendFeedBack(responseListener, msg, platformInfo,
				appVersion);
	}
}
