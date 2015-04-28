package com.yy.android.gamenews.ui;

import java.util.ArrayList;

import com.yy.android.gamenews.util.StatsUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ArticleGalleryActivity extends SingleFragmentActivity {

	public static final String KEY_ARTICLE_ID = "article_id";
	public static final String KEY_SOURCE = "source";
	public static final String KEY_COMMENT_COUNT = "comment_count";
	public static final String KEY_ARTICLE_TYPE = "article_type";
	public static final String KEY_AUTO_PLAY = "auto_play";

	public static final int POSITION_FIRST = GalleryFragment.POSITION_FIRST;
	public static final int POSITION_LAST = GalleryFragment.POSITION_LAST;
	/**
	 * 通过articleId进行查询
	 */
	public static final int SOURCE_QUERY = 1;
	/**
	 * 使用urlList进行显示 而不查询
	 */
	public static final int SOURCE_GIVEN = 2;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public static void startActivity(Context context,
			ArrayList<String> urlList, long articleId, String title,
			int selectPos, int source, int commentCount, int articleType,
			boolean autoPlay) {
		Intent intent = new Intent(context, ArticleGalleryActivity.class);

		intent.putExtra(GalleryFragment.KEY_URL_LIST, urlList);
		intent.putExtra(KEY_ARTICLE_ID, articleId);
		intent.putExtra(GalleryFragment.KEY_TITLE, title);
		intent.putExtra(GalleryFragment.KEY_SELECT_POS, selectPos);
		intent.putExtra(KEY_SOURCE, source);
		intent.putExtra(KEY_COMMENT_COUNT, commentCount);
		intent.putExtra(KEY_ARTICLE_TYPE, articleType);
		intent.putExtra(KEY_AUTO_PLAY, autoPlay);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		String eventId = "stats_view_image_gallery";
		String key = "article_title";
		String value = String.valueOf(title + "(" + articleId + ")");
		StatsUtil.statsReport(context, eventId, key, value);
		StatsUtil.statsReportByMta(context, eventId, key, value);
		StatsUtil.statsReportByHiido(eventId, key + value);
	}

	public static void startActivity(Context context, long articleId,
			String title, int selectPos, int articleType, boolean autoPlay) {
		startActivity(context, null, articleId, title, selectPos, SOURCE_QUERY,
				0, articleType, autoPlay);
	}

	@Override
	protected Fragment initFragment() {
		return new ArticleGalleryFragment();
	}
}
