package com.yy.android.gamenews.ui;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.android.gamenews.event.NetWorkChangeEvent;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.ui.view.AppDialog.OnClickListener;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class VideoPlayerActivity extends Activity implements
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener,
		OnBufferingUpdateListener, SurfaceHolder.Callback {
	public static final String TAG = "test";
	public static final String EXTRA_VIDEO_URL = "video_rul";
	public static final String EXTRA_VIDEO_TITLE = "video_title";

	private boolean isExit = false;
	private int currentMusicPosition; // 当前音量
	String url = null;
	private Uri uri;
	private String name;
	private ImageButton playBtn = null;// 播放、暂停
	private TextView playtime = null;// 已播放时间
	private TextView durationTime = null;// 总时间
	private TextView VideoName = null;
	private SeekBar seekbar = null;// 进度
	private SeekBar soundBar = null;// 音量调节
	private TextView progress = null;
	private Handler handler = new MyHandler(this);

	private static final int MSG_REFRESH = 1;
	private static final int MSG_HIDE_CONTROLLER = 2;

	private static class MyHandler extends Handler {
		private WeakReference<VideoPlayerActivity> mRef;

		public MyHandler(VideoPlayerActivity activity) {
			mRef = new WeakReference<VideoPlayerActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			VideoPlayerActivity activity = mRef.get();

			if (activity != null) {

				switch (msg.what) {
				case MSG_REFRESH: {
					activity.refresh();
					break;
				}
				case MSG_HIDE_CONTROLLER: {
					activity.hideController();
					break;
				}
				}
			}
			super.handleMessage(msg);
		}
	}

	private void refresh() {
		if (mediaPlayer != null) {

			int duration = mediaPlayer.getDuration();
			if (seekbar.getMax() == 0) {
				seekbar.setMax(duration);
			}
			if (toTime(0).equals(durationTime.getText())) {
				durationTime.setText(toTime(duration));// 设置时间
			}
			currentPosition = mediaPlayer.getCurrentPosition();

			// adjustDisplay();
		}

		seekbar.setProgress(currentPosition);
		playtime.setText(toTime(currentPosition));

		if (handler != null) {
			handler.sendEmptyMessage(1);
		}
	}

	public static void startVideoPlayerActivity(Context context, String title,
			String url) {
		Intent intent = new Intent(context, VideoPlayerActivity.class);
		intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, url);
		intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, title);
		context.startActivity(intent);
	}

	private int currentPosition;// 当前播放位置
	// private ProgressDialog dialog; // 加载等待框
	private AudioManager mAudioManager = null;
	private View video_contrlbar, titlebar;
	private boolean isControlBarShow = true;
	private boolean isSeekbarPressed;
	private Display currentDisplay;
	private Button mBackBtn;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer mediaPlayer;// 使用的是MediaPlayer来播放视频
	private int ScurrentPosition; // 音量
	private Timer showController = new Timer();
	private TimerTask timerTask;

	private void hideController() {
		video_contrlbar.setVisibility(View.GONE);
		titlebar.setVisibility(View.GONE);
		isControlBarShow = false;
	}

	boolean readyToPlayer = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate :1");

		mAudioManager = (AudioManager) VideoPlayerActivity.this
				.getSystemService(AUDIO_SERVICE);
		EventBus.getDefault().register(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 设置屏幕常亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置满屏
		setContentView(R.layout.activity_video_player);

		name = getIntent().getStringExtra(EXTRA_VIDEO_TITLE);
		url = getIntent().getStringExtra(EXTRA_VIDEO_URL);
		Log.d(TAG, " url :" + url);

		// dialog = new ProgressDialog(this); // 设置等待
		// dialog.setMessage("加载中...");
		// dialog.show();
		progress = (TextView) findViewById(R.id.progress);
		mBackBtn = (Button) findViewById(R.id.back);
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doExit();
			}
		});

		// 控制台
		video_contrlbar = (View) findViewById(R.id.video_contrlbar);
		titlebar = (View) findViewById(R.id.video_titlebar);
		showControlBar();
		/* 时间 */
		playtime = (TextView) findViewById(R.id.video_playtime);// 已经播放的时间
		durationTime = (TextView) findViewById(R.id.video_duration);// 总时间
		VideoName = (TextView) findViewById(R.id.play_movie_name);
		VideoName.setText(name);

		/* 播放、暂停、停止按钮设置 */
		playBtn = (ImageButton) findViewById(R.id.video_playBtn);// 开始播放
		playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer == null) {
					return;
				}
				if (mediaPlayer.isPlaying()) {
					pause();
				} else {
					play();
				}
			}
		});

		// 进度条
		seekbar = (SeekBar) findViewById(R.id.video_seekbar);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				play();
				isSeekbarPressed = false;
				repostHideControllerEvent();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				if (mediaPlayer != null) {
					mediaPlayer.pause();
				}
				isSeekbarPressed = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser)
					mediaPlayer.seekTo(progress);
			}
		});

		seekbar.setEnabled(false);
		playBtn.setEnabled(false);

		/* 音量控制条 */
		soundBar = (SeekBar) findViewById(R.id.video_sound);
		soundBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					int ScurrentPosition = soundBar.getProgress();
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							ScurrentPosition, 0);

				}
			}
		});

		surfaceView = (SurfaceView) findViewById(R.id.SurfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		findViewById(R.id.MainView).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showControlBar(); // 点击屏幕时，调出控制台；
					}
				});
		Log.d(TAG, "onCreate :2");
		setup();
		currentDisplay = getWindowManager().getDefaultDisplay();
		Log.d(TAG, "onCreate :3");
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		handler.removeMessages(MSG_HIDE_CONTROLLER);
		handler.removeMessages(MSG_REFRESH);
		releaseMediaPlayer();
		super.onDestroy();
	}

	private void showControlBar() { // 控制台的显隐
		video_contrlbar.setVisibility(View.VISIBLE);
		titlebar.setVisibility(View.VISIBLE);
		isControlBarShow = true;
		repostHideControllerEvent();
	}

	private void repostHideControllerEvent() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		timerTask = new TimerTask() {

			@Override
			public void run() {
				if (isControlBarShow && !isSeekbarPressed) {
					handler.sendEmptyMessage(MSG_HIDE_CONTROLLER);
				}
			}

		};
		showController.schedule(timerTask, 5000); // 5秒后隐藏
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void loadClip() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mediaPlayer = new MediaPlayer();// 创建多媒体对象
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnVideoSizeChangedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);

		// ListEntity entity = dbDao.find(position);
		// Log.d(TAG,"list entity object :"+entity);
		// String url = entity.getUrl();
		Log.d(TAG, "url :" + url);
		uri = Uri.parse(url);
		Log.d(TAG, "media url :" + url);
		// uri =
		// Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		// "" + pos);
		try {
			mediaPlayer.setDataSource(this, uri);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setup() {
		loadClip();
		mediaPlayer.prepareAsync();
	}

	private void play() {
		mediaPlayer.start();
		playBtn.setBackgroundResource(R.drawable.video_pause_selector);
		adjustDisplay();
	}

	private void adjustDisplay() {
		int tmpWidth = currentDisplay.getWidth();
		int tmpHeight = currentDisplay.getHeight();
		int videoHeight = mediaPlayer.getVideoHeight();
		int videoWidth = mediaPlayer.getVideoWidth();

		int width = tmpWidth;
		int height = tmpHeight;
		if (videoHeight != 0 && videoWidth != 0) {
			float videoRate = (float) videoWidth / (float) videoHeight;
			float screenRate = (float) tmpWidth / (float) tmpHeight;

			if (videoRate <= screenRate) {
				width = (int) (tmpHeight * videoRate);
			} else {
				height = (int) (tmpWidth / videoRate);
			}
		}

		if (width == surfaceView.getWidth()
				&& height == surfaceView.getHeight()) {
			return;
		}

		surfaceView
				.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		if (width == 0 || height == 0) {
			surfaceView.setVisibility(View.INVISIBLE);
		} else {
			surfaceView.setVisibility(View.VISIBLE);
		}
	}

	private void pause() {
		mediaPlayer.pause();
		playBtn.setBackgroundResource(R.drawable.video_play_selector);
	}

	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surafce created");
		mediaPlayer.setDisplay(surfaceHolder);// 若无次句，将只有声音而无图像
		// play();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { // surface销毁时结束播放，防止按下home键后仍有声音,但无法播放图像。

		finish();
	}

	private void releaseMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
		Log.d(TAG, "onVideoSizeChanged");
		adjustDisplay();
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		Log.d(TAG, "onSeekComplete");
		seekbar.setEnabled(true);
		playBtn.setEnabled(true);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared");
		seekbar.setEnabled(true);
		playBtn.setEnabled(true);

		mp.seekTo(currentPosition);// 初始化MediaPlayer播放位置
		play();

		int duration = mp.getDuration();
		seekbar.setMax(duration);// 设置播放进度条最大值
		durationTime.setText(toTime(mp.getDuration()));// 设置时间
		playtime.setText(toTime(mp.getCurrentPosition()));// 初始化播放时间
		// adjustDisplay();
		handler.sendEmptyMessage(1);// 向handler发送消息，启动播放进度条
		/* 获得最大音量 */

		int maxSound = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		/* 获得当前音量 */
		int currentSound = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		soundBar.setMax(maxSound);
		soundBar.setProgress(currentSound);

	}

	@Override
	public boolean onInfo(MediaPlayer arg0, int whatInfo, int extra) {
		Log.d(TAG, "onInfo" + whatInfo);
		return false;
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		Log.d(TAG, "onError");
		if (arg2 == MediaPlayer.MEDIA_ERROR_IO) {
			ToastUtil.showToast(R.string.http_error);
		} else {
			ToastUtil.showToast("播放出错，请重试！");
		}
		finish();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) { // 播放完后自动退出
		finish();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onBufferingUpdate");
		if (percent == 100) {
			progress.setVisibility(View.INVISIBLE);
		} else {
			if (progress.getVisibility() == View.INVISIBLE) {

				progress.setVisibility(View.VISIBLE);
			}
			progress.setText("" + percent + "%");
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			exitBy2Click();
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN: // 音量键的控制，调出控制台
			Log.d(TAG, "to pressed voice down");
			showControlBar();
			currentMusicPosition = soundBar.getProgress();
			currentMusicPosition--;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentMusicPosition, 0);
			soundBar.setProgress(currentMusicPosition);
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			Log.d(TAG, "to pressed voice up");
			showControlBar();
			currentMusicPosition = soundBar.getProgress();
			currentMusicPosition++;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					currentMusicPosition, 0);
			soundBar.setProgress(currentMusicPosition);
			break;
		default:
			super.onKeyDown(keyCode, event);
		}
		return true;
	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出播放", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			doExit();
		}
	}
	
	private void doExit() {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		finish();
	}

	private boolean mIsNotified;

	public void onEvent(NetWorkChangeEvent event) {
		if (mIsNotified) {
			return;
		}
		boolean isWifi = Util.isWifiConnected();
		boolean isNetwork = Util.isNetworkConnected();
		boolean isData = isNetwork && !isWifi;

		if (isData && mediaPlayer != null && mediaPlayer.isPlaying()) {
			pause();
			UiUtils.showDialog(this, R.string.global_caption,
					R.string.play_video_no_wifi, R.string.global_ok,
					R.string.global_cancel, new OnClickListener() {

						@Override
						public void onDismiss() {
							mIsNotified = true;
						}

						@Override
						public void onDialogClick(int nButtonId) {
							if (nButtonId == AppDialog.BUTTON_POSITIVE) {
								play();
							}
							mIsNotified = true;
						}
					});
		}
	}

}