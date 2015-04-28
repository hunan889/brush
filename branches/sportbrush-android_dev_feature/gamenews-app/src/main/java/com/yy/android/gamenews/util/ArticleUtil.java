package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.List;

import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.util.AsyncIPageCache.OnCacheListener;

public class ArticleUtil {
	public static void maskAsViewed(final long articleId) {
		AsyncIPageCache.getInstance().readAsync(
				Constants.CACHE_KEY_VIEWED_ARTICLE_LIST, null, false,
				new OnCacheListener<List<Long>>() {

					@Override
					public void onRead(List<Long> idList) {
						List<Long> mViewedList = new ArrayList<Long>();
						if (idList != null) {

							mViewedList.clear();
							mViewedList.addAll(idList);
						}
						if (!mViewedList.contains(articleId)) {
							if (mViewedList.size() >= Constants.CACHE_SIZE_VIEWED_ARTI_LIST) {
								mViewedList.remove(0);
							}
							mViewedList.add(articleId);

							AsyncIPageCache.getInstance().writeAsync(
									Constants.CACHE_KEY_VIEWED_ARTICLE_LIST,
									mViewedList,
									Constants.CACHE_DURATION_FOREVER, false,
									null);
						}
					}

					@Override
					public void onWrite() {

					}
				});
	}
}
