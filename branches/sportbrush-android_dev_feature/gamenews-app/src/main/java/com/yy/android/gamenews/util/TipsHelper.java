package com.yy.android.gamenews.util;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yy.android.sportbrush.R;

public class TipsHelper {
	private Context mContext;
	private View mLayout;
	private TextView mTipsView;
	private boolean mIsCancelable;
	
	private Handler mHandler = new Handler();
	
	private Runnable mDismissRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(mLayout == null) {
				return;
			}
			Animation anim = mLayout.getAnimation();
			if(anim != null) {
				anim.cancel();
			}
			mLayout.clearAnimation();
		}
	};

	public TipsHelper(Context context, View layout, TextView tipsView) {
		mContext = context;
		mLayout = layout;
		mTipsView = tipsView;

		mLayout.setOnTouchListener(mOnTouchListener);
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mIsCancelable) {
				mHandler.postDelayed(mDismissRunnable, 100);
			}
			return false;
		}
	};

	private Animation mFadeAnimation;

	private Animation getFadeAnimation() {
		if (mFadeAnimation == null) {
			mFadeAnimation = AnimationUtils.loadAnimation(mContext,
					R.anim.welcome_hint_fadeout);
		}
		mFadeAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLayout.setVisibility(View.GONE);
				mTipsView.setClickable(false);
			}
		});

		return mFadeAnimation;
	}

	private boolean needShowHint = false;
	public void checkHint(int step, boolean cancelable) {
//		if(!needShowHint) {
//			return;
//		}
//		if (Preference.STEP_0 == step) {
//			showHint(R.string.welcom_hint_0, cancelable);
//			Preference.getInstance().setGuideStep(Preference.STEP_1);
//		} else if (Preference.STEP_1 == step) {
//			showHint(R.string.welcom_hint_1, cancelable);
//			Preference.getInstance().setGuideStep(Preference.STEP_2);
//		} else if (Preference.STEP_2 == step) {
//			showHint(R.string.welcom_hint_2, cancelable);
//			Preference.getInstance().setGuideStep(Preference.STEP_3);
//		} else if (Preference.STEP_3 == step) {
//			showHint(R.string.welcom_hint_3, cancelable);
//			Preference.getInstance().setGuideStep(Preference.STEP_DONE);
//		}
	}

	private void showHint(int stringId, boolean cancelable) {
		mHandler.removeCallbacks(mDismissRunnable);
		mIsCancelable = cancelable;
		mTipsView.setText(stringId);
		mTipsView.setClickable(true);
		mLayout.clearAnimation();
		mLayout.setVisibility(View.VISIBLE);
		mLayout.startAnimation(getFadeAnimation());
	}
}
