package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class WelcomeView extends FrameLayout {

	private View mTextTop;
	private View mTextMid;
	private View mTextBottom;
	private View mRefreshLayout;
	private View mRefreshView;
	private View mBrushView;

	private View mParent;

	public WelcomeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WelcomeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WelcomeView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		LayoutInflater inflater = LayoutInflater.from(context);

		mParent = inflater.inflate(R.layout.welcome_layout, null);

		mTextTop = mParent.findViewById(R.id.welcome_text_top);
		mTextMid = mParent.findViewById(R.id.welcome_text_mid);
		mTextBottom = mParent.findViewById(R.id.welcome_text_bottom);
		mBrushView = mParent.findViewById(R.id.welcome_brush);
		mRefreshLayout = mParent.findViewById(R.id.welcome_refresh_layout);
		mRefreshView = mParent.findViewById(R.id.welcome_refresh_circle);
		
//		setOnTouchListener(mOnTouchListener);

		initAnimation();

		addView(mParent);
	}
	
//	private OnTouchListener mOnTouchListener = new OnTouchListener() {
//		private float mStartY;
//		private float mStartX;
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//
//			float y = event.getY();
//
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN: {
//				if (mStartY == 0) {
//					mStartY = y;
//				}
//				
//				break;
//			}
//			case MotionEvent.ACTION_UP: {
//				onFinish();
//				mStartY = 0;
//				break;
//			}
//			case MotionEvent.ACTION_MOVE: {
//				if (mStartY - y > 20) {
//					onFinish();
//				}
//				break;
//			}
//			}
//			return true;
//		}
//	};

	private Animation mAnimBrushOpen1;
	private Animation mAnimBrushOpen2;
	private Animation mAnimBrushOpen3;
	private Animation mAnimTextOpen;
	private Animation mAnimaRefresh;
	private Animation mAnimTextOut;
	private Animation mAnimation7;
	
	private Animation mFinishAnimation;

	private AnimationListener mAnimationListener = new AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			if(mIsFinished && animation == mFinishAnimation) {
				if(mOnCompletedListener != null) {
					mOnCompletedListener.onCompleted();
				}
				return;
			}
			if (animation == mAnimBrushOpen1) {
				startAnimation2();
			} else if (animation == mAnimBrushOpen2) {
				startAnimation3();
			} else if (animation == mAnimBrushOpen3) {
				mCanCancel = true;
				startAnimation4();
			} else if (animation == mAnimTextOpen) {
				startAnimation5();
			} else if (animation == mAnimaRefresh) {
				startAnimation6();
			} else if (animation == mAnimTextOut) {
				startAnimation7();
			}
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		};
	};

	public void start() {
		startAnimation1();
	}

	private void startAnimation1() {
		mBrushView.setVisibility(View.VISIBLE);
		mBrushView.clearAnimation();
		mBrushView.startAnimation(mAnimBrushOpen1);
	}

	private void startAnimation2() {
		mBrushView.clearAnimation();
		mBrushView.startAnimation(mAnimBrushOpen2);
	}

	private void startAnimation3() {
		mBrushView.clearAnimation();
		mBrushView.startAnimation(mAnimBrushOpen3);
	}

	private void startAnimation4() {
		mTextTop.setVisibility(View.VISIBLE);
		mTextMid.setVisibility(View.VISIBLE);
		mTextBottom.setVisibility(View.VISIBLE);

		mTextTop.clearAnimation();
		mTextMid.clearAnimation();
		mTextBottom.clearAnimation();
		mTextTop.startAnimation(mAnimTextOpen);
		mTextMid.startAnimation(mAnimTextOpen);
		mTextBottom.startAnimation(mAnimTextOpen);
	}

	private void startAnimation5() {
		mRefreshView.setVisibility(View.VISIBLE);
		mRefreshView.clearAnimation();
		mRefreshView.startAnimation(mAnimaRefresh);
	}

	private void startAnimation6() {
		mRefreshLayout.clearAnimation();
		mTextMid.clearAnimation();
		mRefreshLayout.startAnimation(mAnimTextOut);
		mTextMid.startAnimation(mAnimTextOut);
	}

	private void startAnimation7() {

		float midPos = (float)(Util.getAppHeight() / 2);

		float viewMidPos = (float)(mTextTop.getHeight() / 2);

		float currentPos = 0;
		Animation anim = new TranslateAnimation(0, 0, currentPos, midPos
				- viewMidPos);
		anim.setDuration(300);
		anim.setFillAfter(true);
		anim.setAnimationListener(mAnimationListener);
		mAnimation7 = anim;

		mTextTop.clearAnimation();
		mTextTop.startAnimation(mAnimation7);
	}

	private void initAnimation() {

		Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(500);
		anim.setFillAfter(true);

		anim.setAnimationListener(mAnimationListener);
		mAnimBrushOpen1 = anim;

		anim = new ScaleAnimation(1.0f, 0.85f, 1.0f, 0.85f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setAnimationListener(mAnimationListener);
		mAnimBrushOpen2 = anim;

		anim = new ScaleAnimation(0.85f, 1.0f, 0.85f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setAnimationListener(mAnimationListener);
		mAnimBrushOpen3 = anim;

		anim = new AlphaAnimation(0, 1f);
		anim.setDuration(300);
		anim.setFillAfter(true);
		anim.setAnimationListener(mAnimationListener);
		mAnimTextOpen = anim;

		anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(1000);
		anim.setFillAfter(true);
		anim.setRepeatCount(1);
		anim.setAnimationListener(mAnimationListener);
		mAnimaRefresh = anim;

		anim = new AlphaAnimation(1, 0f);
		anim.setDuration(300);
		anim.setFillAfter(true);
		anim.setAnimationListener(mAnimationListener);
		mAnimTextOut = anim;
	}

	private boolean mIsFinished;
	private boolean mCanCancel;
	public void onFinish() {
		if(!mCanCancel) {
			return;
		}
		if(mIsFinished) {
			return;
		}
		mIsFinished = true;
		mFinishAnimation = new TranslateAnimation(0, 0, 0, -(float)Util.getAppHeight());
		mFinishAnimation.setDuration(500);
		mFinishAnimation.setFillAfter(true);
		mFinishAnimation.setAnimationListener(mAnimationListener);
		mFinishAnimation.setInterpolator(new LinearInterpolator());
		startAnimation(mFinishAnimation);
	}
	
	private OnCompletedListener mOnCompletedListener;
	public void setOnCompletedListener(OnCompletedListener listener) {
		mOnCompletedListener = listener;
	}
	public interface OnCompletedListener {
		public void onCompleted();
	}
	
}
