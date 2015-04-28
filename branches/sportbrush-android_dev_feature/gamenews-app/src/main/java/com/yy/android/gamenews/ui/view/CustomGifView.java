package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yy.android.gamenews.util.Util;

public class CustomGifView extends ImageView {

	private Movie mMovie;
	private long mMovieStart;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Matrix mMatrix;

	private static final String LOG_TAG = "CustomGifView";

	public CustomGifView(Context context) {
		super(context);
	}

	public CustomGifView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomGifView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void init(String pathName) {
		// mMovie =
		// getResources().getMovie(R.drawable.article_detail_like_normal);
		mMovie = Movie.decodeFile(pathName);
		if (mMovie == null) {
			return;
		}

		int screenHeight = Util.getAppHeight();
		int screenWidth = Util.getAppWidth();
		int gifWidth = mMovie.width();
		int gifHeight = mMovie.height();
		float scaleX = (float) screenWidth / (float) gifWidth;
		float scaleY = (float) screenHeight / (float) gifHeight;
		String format = "screenHeight = %d, screenWidth = %d, gifWidth = %d, gifHeight = %d, scaleX = %f, scaleY = %f";
		String msg = String.format(format, screenHeight, screenWidth, gifWidth,
				gifHeight, scaleX, scaleY);
		Log.d(LOG_TAG, msg);

		int bitmapWidth = scaleX > 1 ? screenWidth : gifWidth;
		int bitmapHeight = scaleY > 1 ? screenHeight : gifHeight;

		mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mMatrix = new Matrix();
		FrameLayout.LayoutParams params = null;
		if (scaleX < scaleY) {
			mMatrix.postScale(scaleX, scaleX);
			params = new FrameLayout.LayoutParams((int) (gifWidth * scaleX),
					(int) (gifHeight * scaleX));
			params.gravity = Gravity.CENTER;
		} else {
			mMatrix.postScale(scaleY, scaleY);
			params = new FrameLayout.LayoutParams((int) (gifWidth * scaleY),
					(int) (gifHeight * scaleY));
			params.gravity = Gravity.CENTER;
		}

		setLayoutParams(params);
	}

	public void start(String pathName) {
		init(pathName);
		invalidate();
		mMovieStart = 0;
	}

	public void stop() {
		if (mMovie != null) {
			mMovie = null;
		}
	}

	public void onDraw(Canvas canvas) {

		if (mMovie == null) {
			return;
		}

		long now = android.os.SystemClock.uptimeMillis();
		if (mMovieStart == 0) { // first time
			mMovieStart = now;
		}
		int timePassed = (int) (now - mMovieStart);
		int dur = mMovie.duration();
		if (timePassed > dur) {
			if (mOnCompletionListener != null) {
				mMovieStart = now;
				mOnCompletionListener.onCompleted();
			}
			return;
		}

		if (dur == 0) {
			dur = 1000;
		}
		int relTime = (int) ((now - mMovieStart) % dur);
		mMovie.setTime(relTime);
		mMovie.draw(mCanvas, 0, 0);

		canvas.drawBitmap(mBitmap, mMatrix, mPaint);

		invalidate();
	}

	public interface OnCompletionListener {
		public void onCompleted();
	}

	private OnCompletionListener mOnCompletionListener;

	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}

}