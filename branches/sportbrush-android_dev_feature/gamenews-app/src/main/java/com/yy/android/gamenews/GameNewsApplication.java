package com.yy.android.gamenews;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.duowan.android.base.model.BaseModel;
import com.duowan.android.base.net.VolleyClient;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.umeng.analytics.MobclickAgent;
import com.yy.android.gamenews.ui.common.SwitchImageLoader;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;
import com.yy.hiidostatis.api.HiidoSDK;
import com.yy.hiidostatis.api.OnStatisListener;
import com.yy.udbsdk.UICalls;

public class GameNewsApplication extends Application {
	private static final String TAG = GameNewsApplication.class.getSimpleName();
	private static GameNewsApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate");
		mInstance = this;

		VolleyClient.init(this, Constants.DEFAULT_DISK_USAGE_BYTES);
		SwitchImageLoader.init(this);
		Preference.getInstance().init(this);
		UICalls.setAppid(Constants.YY_APP_ID);
		UICalls.setTestMode(false);
		initMTAConfig(false);// 腾讯云统计
		MobclickAgent.setDebugMode(false);// 友盟统计
		// MobclickAgent.openActivityDurationTrack(false);
		// MobclickAgent.updateOnlineConfig(this);
		initHiido();// 海度统计初始化
		// (测试环境)崩溃日志
		String channelName = getString(R.string.channelname);
		if (Util.inTest(channelName)) {
			String hostUrl = Preference.getInstance().getTestUrl();
			if (!TextUtils.isEmpty(hostUrl)) {
				BaseModel.HOST = hostUrl;
			}
			if (!Util.inDev(channelName)) {
				Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler());
			}
		}
	}

	public static GameNewsApplication getInstance() {
		return mInstance;
	}

	private void initHiido() {
		// Options options =new Options();
		// options.testServer = "http://tylog.hiido.com/c.gif";
		// HiidoSDK.instance().setOptions(options);
		HiidoSDK.instance().appStartLaunchWithAppKey(this,
				getMetaValue("HIIDO_APPKEY"), getMetaValue("HIIDO_APPID"),
				getMetaValue("HIIDO_FROM"), new OnStatisListener() {
					@Override
					public long getCurrentUid() {
						return Constants.UID;
					}
				});
	}

	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public String getMetaValue(String key) {
		if (key == null) {
			return null;
		}
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			if (ai != null && ai.metaData != null) {
				return ai.metaData.getString(key);
			}
		} catch (NameNotFoundException e) {
		}
		return null;
	}

	private void initMTAConfig(boolean isDebugMode) {
		if (isDebugMode) {
			StatConfig.setDebugEnable(true);
			StatConfig.setStatSendStrategy(StatReportStrategy.INSTANT);
		} else {
			StatConfig.setDebugEnable(false);
			// 根据情况，决定是否开启MTA对app未处理异常的捕获
			StatConfig.setAutoExceptionCaught(false);
			// 选择默认的上报策略
			StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		}
		try {
			com.tencent.stat.StatService.startStatService(this, null,
					com.tencent.stat.common.StatConstants.VERSION);
		} catch (Exception e) {
		}
	}

}
