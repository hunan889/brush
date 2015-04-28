package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.GetSpecialArticleListRsp;
import com.duowan.gamenews.PicInfo;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.jcewrapper.GetSpecialArticleRspLocal;
import com.yy.android.gamenews.model.ArticleModel;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.RefreshableViewWrapper;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class SpecialArticleFragment
		extends
		ArticleListFragment<GetSpecialArticleListRsp, GetSpecialArticleRspLocal> {
	private long mSpecialId;
	private ImageView mImageView;
	private TextView mDescriptionView;
	private View mDesLayout;
	private View mHeader;
	private ArticleInfo mSpecialInfo;
	public static final String KEY_SPECIAL_INFO = "special_info";
	public static final String KEY_SPECIAL_ID = "special_id";
	private SwitchImageLoader mImageLoader;

	public static SpecialArticleFragment newInstance(long specialId) {
		SpecialArticleFragment fragment = new SpecialArticleFragment();
		fragment.setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
		Bundle args = new Bundle();
		args.putLong(KEY_SPECIAL_ID, specialId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle data) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mSpecialInfo = (ArticleInfo) bundle
					.getSerializable(KEY_SPECIAL_INFO);

			mSpecialId = bundle.getLong(KEY_SPECIAL_ID, 0);

			if (mSpecialInfo != null) {
				mSpecialId = mSpecialInfo.getId();
			}
		}

		mImageLoader = SwitchImageLoader.getInstance();
		super.onCreate(bundle);
	}

	@Override
	public void onResume() {

		refreshData();
		super.onResume();
	}

	@Override
	protected RefreshableViewWrapper<?> getViewWrapper() {

		mHeader = getActivity().getLayoutInflater().inflate(
				R.layout.special_article_header, null);
		mImageView = (ImageView) mHeader.findViewById(R.id.special_image_view);
		mDescriptionView = (TextView) mHeader.findViewById(R.id.special_desc);
		mDesLayout = mHeader.findViewById(R.id.special_desc_layout);
		return mDataViewConverter.getViewWrapper(mHeader);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void requestDataImpl(final int refresh, Object attachInfo) {

		ArticleModel.getSpecialArticleList(
				new ResponseListener<GetSpecialArticleListRsp>(getActivity()) {
					@Override
					public void onResponse(GetSpecialArticleListRsp data) {
						setEmptyText(strEmptyNoData);
						requestFinish(refresh, data, false);
						setCloudStatics(getActivity(), mSpecialId,
								data.getName());
					}

					@Override
					public void onError(Exception e) {
						setEmptyText(strEmptyReload);
						requestFinish(refresh, null, true);
						super.onError(e);
					}
				}, // Listener
				refresh, mSpecialId, (Map<Integer, String>) attachInfo);
	}

	@Override
	protected GetSpecialArticleListRsp newRspObject() {
		return new GetSpecialArticleListRsp();
	}

	@Override
	protected boolean needSaveToDisk() {
		return false;
	}

	@Override
	protected String getLastRefreshTimeKey() {
		return Constants.CACHE_KEY_LAST_REFRSH_TIME_SPECIAL + mSpecialId;
	}

	@Override
	protected void requestFinishImpl(int refresh,
			GetSpecialArticleListRsp data, boolean error) {
		ArrayList<ArticleInfo> dataList = null;
		boolean hasMore = false;
		if (data != null) {
			dataList = data.getArticleList();
			hasMore = data.hasMore;
			displayHeader(data);
		}
		requestFinish(refresh, dataList, hasMore, false, error);
	}

	private void displayHeader(GetSpecialArticleListRsp data) {
		List<PicInfo> coverList = data.getCover();
		if (coverList != null) {

			if (coverList.size() > 0) {
				PicInfo cover = coverList.get(0);
				String coverUrl = cover.getUrl();
				int imgWidth = cover.getWidth();
				int imgHeight = cover.getHeight();

				int viewWidth = Util.getAppWidth();
				float scale = (float) viewWidth / (float) imgWidth;
				int viewHeight = (int) (scale * imgHeight);
				mImageView.getLayoutParams().height = viewHeight;
				mImageView.invalidate();

				mImageLoader.displayImage(coverUrl, mImageView);
				mImageView.setVisibility(View.VISIBLE);
			} else {
				mImageView.setVisibility(View.GONE);
			}
		}
		String description = data.getDesc();

		if (description != null && !"".equals(description)) {
			SpannableStringBuilder desc = new SpannableStringBuilder();
			desc.append("　　　　");
			desc.append(description);

			mDescriptionView.setText(desc);
			mDesLayout.setVisibility(View.VISIBLE);
		} else {
			mDesLayout.setVisibility(View.GONE);
		}
	}

	@Override
	protected boolean needShowUpdatedCount() {
		return false;
	}

	public void setCloudStatics(Context context, long mSpecialId2, String title) {
		StatsUtil.statsReport(context, "stats_special_article",
				"article_special_name", title);
		StatsUtil.statsReportByMta(context, "stats_special_article",
				"article_special_name", mSpecialId2 + title);
		StatsUtil.statsReportByHiido("stats_special_article",
				"article_special_name:" + title);
	}

	@Override
	protected GetSpecialArticleRspLocal initRspWrapper() {
		return new GetSpecialArticleRspLocal();
	}
}
