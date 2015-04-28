package com.yy.android.gamenews.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;

import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.sportbrush.R;

public class CommentListActivity extends SingleFragmentActivity {

	public static final String KEY_ARTICLE_ID = "article_id";

	public static void startActivity(Context context, long articleId, String title) {
		Intent intent = new Intent(context, CommentListActivity.class);
		intent.putExtra(KEY_ARTICLE_ID, articleId);

		context.startActivity(intent);
		
		String eventId = "stats_view_comment_list";
		String key = "article_title";
		String value = String.valueOf(title + "(" + articleId + ")");
		StatsUtil.statsReport(context, eventId, key, value);
		StatsUtil.statsReportByMta(context, eventId, key, value);
		StatsUtil.statsReportByHiido(eventId, key + value);
	}

	@Override
	protected Fragment initFragment() {
		return new CommentListFragment();
	}

	private int[] mAttribute = new int[2];
	private boolean intercept = false;
	// for slip out
	// 手指向右滑动时的最小速度
	private static final int YDISTANCE_MAX = 50;
	// 手指向右滑动时的最小距离
	private int XDISTANCE_MIN = 0;
	// 记录手指按下时的横坐标。
	private float xDown;
	private float yDown;
	// 记录手指移动时的横坐标。
	private float xMove;
	private float yMove;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "intercept = " + intercept);
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle bundle) {

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		super.onCreate(bundle);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	private static final String TAG = "CommentListActivity";

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.d(TAG, "intercept = " + intercept);
		// if (intercept) {
		// return super.dispatchTouchEvent(ev);
		// }
		if (XDISTANCE_MIN == 0) {
			if (mAttribute.length > 0 && mAttribute[0] != 0) {
				XDISTANCE_MIN = mAttribute[0] / 8;
			} else {
				XDISTANCE_MIN = 30;
			}
			if (XDISTANCE_MIN == 0) {
				return false;
			}
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			xMove = ev.getRawX();
			yMove = ev.getRawY();
			Log.d(TAG, "yMove = " + yMove + "   xMove = " + xMove);
			// 活动的距离
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) Math.abs(yDown - yMove);
			// 当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
			if (distanceX > XDISTANCE_MIN && distanceY < YDISTANCE_MAX) {
				onBackPressed();
				StatsUtil.statsReportAllData(this, "go_back_article",
						"go_back_article_info", "go_back_article");
				// intercept = true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			xDown = ev.getRawX();
			yDown = ev.getRawY();
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* Release the drag */
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		}

		return super.dispatchTouchEvent(ev);
	}
}
