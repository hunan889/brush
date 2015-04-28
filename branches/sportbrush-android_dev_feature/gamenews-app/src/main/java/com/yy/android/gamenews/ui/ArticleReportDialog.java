package com.yy.android.gamenews.ui;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.yy.android.gamenews.model.ReportModel;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class ArticleReportDialog extends DialogFragment implements
		OnClickListener {
	public static final String KEY_ARTICLE_ID = "article_id";
	public static final String KEY_ARTICLE_NAME = "article_name";
	private long mArticleId;
	private String mArticleTitle;

	public static ArticleReportDialog newInstance(long articleId, String title) {
		ArticleReportDialog fragment = new ArticleReportDialog();
		Bundle args = new Bundle();
		args.putLong(KEY_ARTICLE_ID, articleId);
		args.putString(KEY_ARTICLE_NAME, title);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mArticleId = getArguments().getLong(KEY_ARTICLE_ID);
		mArticleTitle = getArguments().getString(KEY_ARTICLE_NAME);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity(),
				R.style.articleReportDialog);
		dialog.setContentView(R.layout.article_report_dialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.cancel).setOnClickListener(this);
		dialog.findViewById(R.id.report_not_interested)
				.setOnClickListener(this);
		dialog.findViewById(R.id.report_bad).setOnClickListener(this);
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

		case R.id.report_not_interested:
			ReportModel.NotInterestedArticle(new ResponseListener<Boolean>(
					getActivity()) {

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					super.onError(e);
					ToastUtil.showToast(R.string.http_not_connected);
				}

				@Override
				public void onResponse(Boolean arg0) {
					// TODO Auto-generated method stub
					if (arg0) {
						ToastUtil.showToast(R.string.report_success);
					}

				}

			}, mArticleId);
			StatsUtil.statsReport(getActivity(), "stats_not_interest",
					"article_title", mArticleTitle);
			StatsUtil.statsReportByMta(getActivity(), "stats_not_interest", "article_title",mArticleTitle);
			StatsUtil.statsReportByHiido("stats_not_interest", "article_title:"
					+ mArticleTitle);
			dismiss();
			break;
		case R.id.report_bad:
			ReportModel.ReportArticle(new ResponseListener<Boolean>(
					getActivity()) {

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					super.onError(e);
					ToastUtil.showToast(R.string.http_not_connected);
				}

				@Override
				public void onResponse(Boolean arg0) {
					// TODO Auto-generated method stub
					if (arg0) {
						ToastUtil.showToast(R.string.report_success);
					}

				}

			}, mArticleId);
			
			StatsUtil.statsReport(getActivity(), "stats_report",
					"article_title", mArticleTitle);
			StatsUtil.statsReportByMta(getActivity(), "stats_report","article_title", mArticleTitle);
			StatsUtil.statsReportByHiido("stats_report", "article_title"
					+ mArticleTitle);
			dismiss();
			break;
		default:
			break;
		}

	}

}