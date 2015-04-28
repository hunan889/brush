package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class GridItemViewCache extends View {

	private Bitmap mBitmap;
	private Paint mPaint;
	private int mPointToLeft;
	private int mPointToTop;
	private android.view.WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;

	public GridItemViewCache(Context context, Bitmap bitmap, int i, int j,
			int x, int y, int width, int height) {
		super(context);
		mWindowManager = (WindowManager) context.getSystemService("window");
		Matrix matrix = new Matrix();
		float sx = width;
		float sy = (20F + sx) / sx;
		matrix.setScale(sy, sy);
		mBitmap = Bitmap
				.createBitmap(bitmap, x, y, width, height, matrix, true);
		mPointToLeft = i + 10;
		mPointToTop = j + 10;
		mPaint = new Paint();
		mPaint.setAlpha(180);
	}

	public void removeSelf() {
		mWindowManager.removeView(this);
	}

	public void moveToCoordinate(int i, int j) {
		LayoutParams layoutparams = mParams;
		layoutparams.x = i - mPointToLeft;
		layoutparams.y = j - mPointToTop;
		// Log.e("event", String
		// .format("ori(%d,%d)-modify(%d,%d)-update(%d,%d)", i, j,
		// mPointToLeft, mPointToTop, layoutparams.x,
		// layoutparams.y));
		mWindowManager.updateViewLayout(this, layoutparams);
	}

	public void bindToWindow(IBinder token, int i, int j) {
		LayoutParams layoutparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, i - mPointToLeft, j - mPointToTop,
				WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		layoutparams.gravity = Gravity.TOP | Gravity.LEFT;
		layoutparams.token = token;
		layoutparams.setTitle("DragView");
		
		
		layoutparams.x = i +10;
		layoutparams.y = j +10;
		
		
		mParams = layoutparams;
		mWindowManager.addView(this, layoutparams);
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mBitmap.recycle();
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
	}

	public static Bitmap getBitmapByView(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	protected void onMeasure(int i, int j) {
		setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
	}

	public void setPaint(Paint paint) {
		mPaint = paint;
		invalidate();
	}
}
