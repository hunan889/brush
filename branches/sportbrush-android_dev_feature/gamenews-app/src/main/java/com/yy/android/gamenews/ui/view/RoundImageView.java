package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yy.android.sportbrush.R;

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 * 
 * @author Alan
 */
public class RoundImageView extends ImageView {
	private int mBorderOutsideThickness = 0;
	private int mSavedBorderOutsideThickness = 0;
	private int mBorderInsideThickness = 0;
	private Context mContext;
	private int defaultColor = 0xFFFFFFFF;
	// 如果只有其中一个有值，则只画一个圆形边框
	private int mBorderOutsideColor = 0;
	private int mBorderInsideColor = 0;
	// 控件默认长、宽
	private int defaultWidth = 0;
	private int defaultHeight = 0;

	public RoundImageView(Context context) {
		super(context);
		mContext = context;
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setCustomAttributes(attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setCustomAttributes(attrs);
	}

	private void setCustomAttributes(AttributeSet attrs) {
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.gamenews);
		mBorderOutsideThickness = a.getDimensionPixelSize(
				R.styleable.gamenews_border_outside_thickness, 0);
		mSavedBorderOutsideThickness = mBorderOutsideThickness;
		mBorderInsideThickness = a.getDimensionPixelSize(
				R.styleable.gamenews_border_inside_thickness, 0);
		mBorderOutsideColor = a.getColor(
				R.styleable.gamenews_border_outside_color, defaultColor);
		mBorderInsideColor = a.getColor(
				R.styleable.gamenews_border_inside_color, defaultColor);
		
		a.recycle();
	}

	public void showOutsideBorder(boolean show) {
		if (show) {

			mBorderOutsideThickness = mSavedBorderOutsideThickness;
		} else {
			mBorderOutsideThickness = 0;
		}
		invalidate();
	}

	public void setBorderColor(int colorValue) {
		mBorderOutsideColor = colorValue;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		this.measure(0, 0);
		if (drawable.getClass() == NinePatchDrawable.class)
			return;

		if (defaultWidth == 0) {
			defaultWidth = getWidth();

		}
		if (defaultHeight == 0) {
			defaultHeight = getHeight();
		}
		// 保证重新读取图片后不会因为图片大小而改变控件宽、高的大小（针对宽、高为wrap_content布局的imageview，但会导致margin无效）
		// if (defaultWidth != 0 && defaultHeight != 0) {
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// defaultWidth, defaultHeight);
		// setLayoutParams(params);
		// }
		int radiusInside = 0;
		int radiusOutSide = 0;
		if (mBorderInsideThickness > 0 && mBorderOutsideThickness > 0) {// 定义画两个边框，分别为外圆边框和内圆边框
			radiusInside = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - 2 * mBorderInsideThickness;
			radiusOutSide = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - 2 * mBorderOutsideThickness;
			// 画内圆
			drawCircleBorder(canvas, radiusInside + mBorderInsideThickness / 2,
					mBorderInsideColor);
			// 画外圆
			drawCircleBorder(canvas, radiusOutSide + mBorderOutsideThickness
					+ mBorderOutsideThickness / 2, mBorderOutsideColor);
		} else if (mBorderInsideThickness > 0 && mBorderOutsideThickness == 0) {// 定义画内框
			radiusInside = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderInsideThickness;
			drawCircleBorder(canvas, radiusInside + mBorderInsideThickness / 2,
					mBorderInsideColor);
		} else if (mBorderInsideThickness == 0 && mBorderOutsideThickness > 0) {// 定义画一个边框
			radiusOutSide = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2 - mBorderOutsideThickness;
			drawCircleBorder(canvas, radiusOutSide + mBorderOutsideThickness
					/ 2, mBorderOutsideColor);
		} else {// 没有边框
			radiusInside = (defaultWidth < defaultHeight ? defaultWidth
					: defaultHeight) / 2;
		}

		if (radiusOutSide == 0) {
			super.onDraw(canvas);
			return;
		}
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
	
		Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radiusOutSide);
		canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radiusOutSide,
				defaultHeight / 2 - radiusOutSide, null);
	}

	/**
	 * 获取裁剪后的圆形图片
	 * 
	 * @param radius
	 *            半径
	 */
	public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if ((squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter)
				&& diameter != 0) {
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);
		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle((float)(scaledSrcBmp.getWidth() / 2),
				(float)(scaledSrcBmp.getHeight() / 2), (float)(scaledSrcBmp.getWidth() / 2),
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

	/**
	 * 边缘画圆
	 */
	private void drawCircleBorder(Canvas canvas, int radius, int color) {
		Paint paint = new Paint();
		/* 去锯齿 */
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setColor(color);
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.STROKE);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(mBorderOutsideThickness);
		canvas.drawCircle((float) (defaultWidth / 2),
				(float) (defaultHeight / 2), radius, paint);
	}

}