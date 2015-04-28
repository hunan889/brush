package com.yy.android.gamenews.util;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.duowan.gamenews.ArticleInfo;
import com.duowan.gamenews.ArticleType;
import com.yy.android.gamenews.ui.ArticleDetailActivity;
import com.yy.android.gamenews.ui.ArticleGalleryActivity;
import com.yy.android.sportbrush.R;

public class ArticleDetailSwitcher {

	private static ArticleDetailSwitcher mInstance;

	public static ArticleDetailSwitcher getInstance() {
		if (mInstance == null) {
			synchronized (ArticleDetailSwitcher.class) {
				if (mInstance == null) {
					mInstance = new ArticleDetailSwitcher();
				}
			}
		}
		return mInstance;
	}

	private ArrayList<ArticleInfo> articleInfos;

	/**
	 * 设置跳转列表，切换上一篇下一篇时会以该列表为
	 * 
	 * @see {@link #switchToNextArticle(Activity, long, int, Bundle)}
	 * @see {@link #switchToPreArticle(Activity, long, int, Bundle)}
	 * @param articleInfos
	 */
	public void setArticleInfos(ArrayList<ArticleInfo> articleInfos) {
		this.articleInfos = articleInfos;
	}
	
	public ArrayList<ArticleInfo> getArticleInfos() {
		return articleInfos;
	}

	/**
	 * 跳转到列表中的下一篇文章，如果列表中有下一篇，返回true，否则返回false并弹出提示
	 * 
	 * @see {@link #setArticleInfos(ArrayList)}
	 * @see {@link #switchToPreArticle(Activity, long, int, Bundle)}
	 * @param activity
	 *            即context
	 * @param currentArticleId
	 *            当前文章id，用于搜索在列表中的位置
	 * @param articleType
	 *            文章类型，用于跳转至相同类型的文章
	 * @param params
	 *            跳转文章所需传递的bundle信息
	 * @return true if success, false otherwise
	 */
	public boolean switchToNextArticle(Activity activity,
			long currentArticleId, int articleType, Bundle params) {
		ArticleInfo articleInfo = getNextArticleInfo(currentArticleId,
				articleType);
		if (articleInfo != null) {
			startActivity(activity, articleInfo, DIR_NEXT, params);
			return true;
		} else {
			ToastUtil.showToast("没有下一篇了");
			return false;
		}
	}

	/**
	 * 跳转到列表中的上一篇文章，如果列表中有上一篇，返回true，否则返回false并弹出提示
	 * 
	 * @see {@link #setArticleInfos(ArrayList)}
	 * @see {@link #switchToNextArticle(Activity, long, int, Bundle)}
	 * @param activity
	 *            即context
	 * @param currentArticleId
	 *            当前文章id，用于搜索在列表中的位置
	 * @param articleType
	 *            文章类型，用于跳转至相同类型的文章
	 * @param params
	 *            跳转文章所需传递的bundle信息
	 * @return
	 */
	public boolean switchToPreArticle(Activity mActivity,
			long currentArticleId, int articleType, Bundle params) {
		ArticleInfo articleInfo = getPreArticleInfo(currentArticleId,
				articleType);
		if (articleInfo != null) {
			startActivity(mActivity, articleInfo, DIR_PREV, params);
			return true;
		} else {
			ToastUtil.showToast("没有上一篇了");
			return false;
		}
	}

	private static final int DIR_NEXT = 1;
	private static final int DIR_PREV = 2;

	private void startActivity(Activity mActivity, ArticleInfo info, int dir,
			Bundle params) {
		if (info != null) {

			switch (info.articleType) {
			case ArticleType._ARTICLE_TYPE_ARTICLE: {
				mActivity.onBackPressed();
				ArticleDetailActivity.startArticleDetailActivityFromNotice(mActivity,
						info.getId());

				if (DIR_NEXT == dir) {
					mActivity.overridePendingTransition(R.anim.slide_in_bottom,
							R.anim.slide_out_top);
				} else if (DIR_PREV == dir) {
					mActivity.overridePendingTransition(R.anim.slide_in_top,
							R.anim.slide_out_bottom);
				}

				break;
			}

			case ArticleType._ARTICLE_TYPE_IMAGE: {

				int pos = ArticleGalleryActivity.POSITION_FIRST;
				if (DIR_PREV == dir) {
					pos = ArticleGalleryActivity.POSITION_LAST;
				}

				boolean autoPlay = false;
				if (params != null) {
					autoPlay = params.getBoolean(
							ArticleGalleryActivity.KEY_AUTO_PLAY, false);
				}
				mActivity.onBackPressed();
				ArticleGalleryActivity.startActivity(mActivity, info.getId(),
						info.getTitle(), pos, ArticleType._ARTICLE_TYPE_IMAGE,
						autoPlay);
				if (DIR_NEXT == dir) {
					mActivity.overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else if (DIR_PREV == dir) {
					mActivity.overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_right);
				}
				break;
			}
			}
		}
	}

	private ArticleInfo getPreArticleInfo(long currentArticleId, int type) {
		ArticleInfo articleInfo = null;
		int index = getIndexByArticleId(currentArticleId);
		if (index > 0) {
			for (int i = index - 1; i >= 0; i--) {
				if (articleInfos.get(i).getArticleType() == type) {
					articleInfo = articleInfos.get(i);
					break;
				}
			}

		}
		return articleInfo;
	}

	private ArticleInfo getNextArticleInfo(long currentArticleId, int type) {
		ArticleInfo articleInfo = null;
		int index = getIndexByArticleId(currentArticleId);
		if (index >= 0 && index < articleInfos.size() - 1) {
			for (int i = index + 1; i < articleInfos.size(); i++) {
				if (articleInfos.get(i).getArticleType() == type) {
					articleInfo = articleInfos.get(i);
					break;
				}
			}
		}
		return articleInfo;
	}

	private int getIndexByArticleId(long currentArticleId) {
		int index = -1;
		if (articleInfos != null && articleInfos.size() > 0) {
			for (int i = 0; i < articleInfos.size(); i++) {
				ArticleInfo article = articleInfos.get(i);
				if (article.getId() == currentArticleId) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
}
