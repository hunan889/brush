package com.yy.android.gamenews.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class AppWebOperatorDialog extends DialogFragment implements
		OnClickListener {
	public static final String KEY_ARTICLE_URL = "article_url";
	private String mUrl;

	public static AppWebOperatorDialog newInstance(String url) {
		AppWebOperatorDialog fragment = new AppWebOperatorDialog();
		Bundle args = new Bundle();
		args.putString(KEY_ARTICLE_URL, url);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mUrl = getArguments().getString(KEY_ARTICLE_URL);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity(),
				R.style.articleReportDialog);
		dialog.setContentView(R.layout.app_web_operator_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.cancel).setOnClickListener(this);
		dialog.findViewById(R.id.out).setOnClickListener(this);
		dialog.findViewById(R.id.copy).setOnClickListener(this);
		dialog.findViewById(R.id.back).setOnClickListener(this);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		case R.id.out:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
			startActivity(intent);
			dismiss();
			break;
		case R.id.copy:
			Util.copyText(mUrl);
			Toast.makeText(getActivity(), "已复制到粘贴板", Toast.LENGTH_SHORT).show();
			dismiss();
			break;
		default:
			break;
		}
	}

}