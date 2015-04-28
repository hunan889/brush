package com.yy.android.gamenews.ui.view;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleVideoPlayerView extends SurfaceView implements
		OnBufferingUpdateListener, OnCompletionListener, OnErrorListener,
		MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

	private MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private int videoWidth;
	private int videoHeight;

	public SimpleVideoPlayerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SimpleVideoPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SimpleVideoPlayerView(Context context) {
		super(context);
		init(context);
	}
	
	private Context mContext;

	public void init(Context context) {
		mContext = context;
		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
	}
	
	public void start() {
//		try {
//			playVideo();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void playVideo() throws IllegalArgumentException,
			IllegalStateException, IOException {
		this.mediaPlayer = new MediaPlayer();
		AssetFileDescriptor fileDescriptor;
		try {
			fileDescriptor = mContext.getAssets().openFd("welcome.gif");
//			fileDescriptor = mContext.getAssets().openFd("welcome.wmv");
			mediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.mediaPlayer.setDisplay(this.surfaceHolder);
		this.mediaPlayer.prepare();
		this.mediaPlayer.setOnBufferingUpdateListener(this);
		this.mediaPlayer.setOnPreparedListener(this);
		this.mediaPlayer.setOnErrorListener(this);
		this.mediaPlayer.setOnCompletionListener(this);
		Log.i("mplayer", ">>>play video");
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.i("cat", ">>>surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			this.playVideo();
		} catch (Exception e) {
			mOnCompletionListener.onCompleted();
			Log.i("cat", ">>>error", e);
		}
		Log.i("cat", ">>>surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("mplayer", ">>>surface destroyed");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if(mOnCompletionListener != null) {
			mOnCompletionListener.onCompleted();
		}
	}
	
	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}
	private OnCompletionListener mOnCompletionListener;
	public interface OnCompletionListener {
		public void onCompleted();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		this.videoWidth = this.mediaPlayer.getVideoWidth();
		this.videoHeight = this.mediaPlayer.getVideoHeight();
		if (this.videoHeight != 0 && this.videoWidth != 0) {
			this.surfaceHolder.setFixedSize(this.videoWidth, this.videoHeight);
			this.mediaPlayer.start();
		} else {
			mOnCompletionListener.onCompleted();
		}
		
		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mOnCompletionListener.onCompleted();
		return false;
	}
}