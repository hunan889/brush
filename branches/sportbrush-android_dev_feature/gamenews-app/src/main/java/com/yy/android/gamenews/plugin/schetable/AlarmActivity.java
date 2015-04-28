package com.yy.android.gamenews.plugin.schetable;

import java.io.IOException;
import java.util.List;

import android.app.KeyguardManager;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.WindowManager;

import com.duowan.gamenews.RaceInfo;
import com.duowan.gamenews.Team;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.ui.view.AppDialog.OnClickListener;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class AlarmActivity extends BaseActivity {

	public static final String KEY_ID = "info_id";
	public static final String ACTION_ALARM = "com.yy.android.gamenews.alarm_dialog";
	private static final String TAG = "AlarmActivity";

	private Ringtone mRingtone;
	private WakeLock mWakelock;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		acquire();
		/**
		 * 忽略用户按home键进入闹钟界面
		 */
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			gotoMainApp();
			onBackPressed();
			return;
		}
		showDialog(intent);

	};

	private void acquire() {

		if (mWakelock == null) {
			mWakelock = ((PowerManager) getSystemService(POWER_SERVICE))
					.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
							| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		}

		mWakelock.acquire();
		// // 初始化键盘锁，可以锁定或解开键盘锁
		// mKeyguardLock = mKeyguardManager.newKeyguardLock("");
		// // 禁用显示键盘锁定
		// mKeyguardLock.disableKeyguard();
	}

	private void release() {
		if (mWakelock != null && mWakelock.isHeld()) {
			mWakelock.release();
		}
	}

	private void gotoMainApp() {
		Util.startMainApp(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private boolean mIsStoppedByAMSOnce;

	private void stop() {
		stopRing();
		if (!isFinishing()) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {

		release();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		if (keyguardManager.inKeyguardRestrictedInputMode()
				&& !mIsStoppedByAMSOnce) {
			mIsStoppedByAMSOnce = true;
			return;
		}
		stop();
	}

	private void startRing() {
		if (mPlayer == null) {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);

			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			try {
				mPlayer.setDataSource(this, notification);
				mPlayer.prepare();
				mPlayer.setLooping(true);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mPlayer.start();
		}
		// if (mRingtone == null) {
		//
		// Uri notification = RingtoneManager
		// .getDefaultUri(RingtoneManager.TYPE_ALARM);
		// mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
		// notification);
		// }
		// if (!mRingtone.isPlaying()) {
		// mRingtone.play();
		// }
	}

	/**
	 * handler 用于循环播放
	 */
	private int mLoopCount;
	private MediaPlayer mPlayer;

	private void stopRing() {
		if (mPlayer != null) {
			mPlayer.stop();
		}
		// if (mRingtone != null) {
		// mRingtone.stop();
		// }
	}

	private int mDialogCount = 0;

	private void showDialog(Intent intent) {
		String id = "";
		if (intent != null) {
			id = intent.getStringExtra(KEY_ID);
			intent.putExtra(KEY_ID, "");
		}

		if (TextUtils.isEmpty(id)) {
			onBackPressed();
			return;
		}

		RaceInfo currentInfo = null;
		List<RaceInfo> infoList = Preference.getInstance().getAlarmRaceList();
		List<RaceInfo> schedInfoList = Preference.getInstance()
				.getSchedAlarmRaceList();
		if (infoList == null || infoList.size() == 0) {
			onBackPressed();
			return;
		}
		boolean hasData = false;
		for (RaceInfo info : infoList) {
			if (info.getId().equals(id)) {
				hasData = true;
				infoList.remove(info);
				currentInfo = info;
				break;
			}
		}
		for (RaceInfo info : schedInfoList) {
			if (info.getId().equals(id)) {
				schedInfoList.remove(info);
				break;
			}
		}
		if (!hasData) {

			// 如果闹钟已经响过了，则不再处理闹钟，直接跳转到刷子app
			onBackPressed();
			return;
		} else {

			Preference.getInstance().saveAlarmRaceList(infoList);
			Preference.getInstance().saveSchedAlarmRaceList(schedInfoList);
		}

		startRing();

		String title = getString(R.string.app_name);
		String message = "";
		List<Team> teamList = currentInfo.getTeamList();

		if (teamList != null) {
			if (teamList.size() > 1) {

				message = getResources().getString(R.string.alarm_message_vs,
						teamList.get(0).getName(), teamList.get(01).getName());
			} else {
				message = getResources().getString(
						R.string.alarm_message_single,
						teamList.get(0).getName());
			}
		}

		mDialogCount++;
		UiUtils.showDialog(this, title, message.toString(),
				getString(R.string.view_schedule),
				getString(R.string.global_cancel), new OnClickListener() {

					@Override
					public void onDialogClick(int nButtonId) {
						if (AppDialog.BUTTON_POSITIVE == nButtonId) {
							KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
							if (keyguardManager.inKeyguardRestrictedInputMode()) {
								getWindow()
										.addFlags(
												WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
							}
							mDialogCount = 0;
							gotoMainApp();
							onBackPressed();
						}
					}

					@Override
					public void onDismiss() {
						mDialogCount--;
						if (mDialogCount <= 0) {
							stop();
						}
					}

				}).setCancelable(false);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		showDialog(intent);
	}
}
