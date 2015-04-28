package com.yy.android.gamenews.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CustomDurationScroller extends Scroller {
	
	private double mScrollFactor = 1;
	private int screenWidth;

    public CustomDurationScroller(Context context) {
        super(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();  
        screenWidth = dm.widthPixels;  
    }

    public CustomDurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();  
        screenWidth = dm.widthPixels; 
    }

    @SuppressLint("NewApi")
    public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, Math.abs(dx) < screenWidth ? duration : (int) (duration * mScrollFactor));
    }
}
