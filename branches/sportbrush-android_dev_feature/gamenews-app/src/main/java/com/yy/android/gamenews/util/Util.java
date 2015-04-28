package com.yy.android.gamenews.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.duowan.android.base.util.LocalLog;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.UserInitRsp;
import com.duowan.gamenews.WelcomeChannel;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.event.SubscribeEvent;
import com.yy.android.gamenews.ui.ArticleDetailActivity;
import com.yy.android.gamenews.ui.WelcomeActivity;
import com.yy.android.gamenews.util.maintab.MainTab;
import com.yy.android.gamenews.util.maintab.MainTab1;
import com.yy.android.gamenews.util.maintab.MainTab2;
import com.yy.android.gamenews.util.maintab.MainTab3;
import com.yy.android.gamenews.util.maintab.MainTab4;
import com.yy.android.gamenews.util.maintab.MainTab5;
import com.yy.android.sportbrush.R;

public class Util {

	public static final String getDeviceUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();

		return uniqueId;
	}

	public static boolean isWifiConnected() {
		ConnectivityManager conMan = (ConnectivityManager) GameNewsApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED.equals(wifi)) {
			return true;
		}
		return false;
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) GameNewsApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		boolean isConnected = false;
		if (mNetworkInfo != null) {
			isConnected = mNetworkInfo.isAvailable();
		}
		LocalLog.d("", "[isNetworkConnected]network connected? " + isConnected);
		return isConnected;
	}

	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		return android.util.Log.getStackTraceString(tr);

		// return tr.getMessage();
	}

	public static boolean isSDExists() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private static final int INSTALL_GAME_COUNT = 2;

	/**
	 * 选取用户安装的游戏用于首页推荐
	 * 
	 * @return
	 */
	public static ArrayList<String> getInitChannelList() {
		List<GameName> installedGameChannels = new ArrayList<GameName>(); // 保存手机中装的游戏
		List<ApplicationInfo> appInfos = getInstalledApplication();
		List<GameName> defaultGameChannels = getDefaultGameList();

		PackageManager pManager = GameNewsApplication.getInstance()
				.getPackageManager();
		for (ApplicationInfo info : appInfos) {
			String name = info.loadLabel(pManager).toString();

			for (GameName game : defaultGameChannels) {
				if (game.name.equals(name)) {
					int index = installedGameChannels.size();
					for (int i = 0; i < installedGameChannels.size(); i++) {
						if (game.index < installedGameChannels.get(i).index) {
							index = i;
						}
					}

					installedGameChannels.add(index, game);
					break;
				}
			}

		}

		ArrayList<String> installedGameName = new ArrayList<String>();
		for (GameName gamename : installedGameChannels) {
			installedGameName.add(gamename.name);
		}
		// int dataSize = installedGameChannels.size();
		// List<Channel> randomList = new ArrayList<Channel>(); //
		// 从手机中装的游戏中随机选取两款
		// // 如果安装游戏数量少于需返回的数量，则直接返回。否则随机选取需返回数量的游戏
		// if (dataSize < INSTALL_GAME_COUNT) {
		// randomList = installedGameChannels;
		// } else {
		// Random random = new Random();
		// while (randomList.size() < INSTALL_GAME_COUNT) {
		// int randomPos = random.nextInt(dataSize);
		// Channel channel = installedGameChannels.remove(randomPos);
		// randomList.add(channel);
		// dataSize = installedGameChannels.size();
		// }
		// }
		// // 推荐两款热门
		// // TODO: 推荐两款热门频道，需要产品提供
		// Channel channel = new Channel();
		// channel.setName("手游");
		// randomList.add(channel);
		//
		// channel = new Channel();
		// channel.setName("美女");
		// randomList.add(channel);
		// -

		return installedGameName;
	}

	/**
	 * 拿到所有安装的应用程序
	 * 
	 * @return
	 */
	public static List<ApplicationInfo> getInstalledApplication() {
		PackageManager pManager = GameNewsApplication.getInstance()
				.getPackageManager();
		List<ApplicationInfo> infos = pManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		return infos;
	}

	/**
	 * 用于排序
	 * 
	 * @author Administrator
	 * 
	 */
	private static class GameName {
		private String name;
		private int index;
	}

	// private static final int[] sChannelIds = { R.drawable.welcome_channel_1,
	// R.drawable.welcome_channel_2, R.drawable.welcome_channel_3,
	// R.drawable.welcome_channel_4, R.drawable.welcome_channel_5,
	// R.drawable.welcome_channel_6, R.drawable.welcome_channel_7,
	// R.drawable.welcome_channel_8, R.drawable.welcome_channel_9,
	// R.drawable.welcome_channel_10, R.drawable.welcome_channel_11,
	// R.drawable.welcome_channel_12, R.drawable.welcome_channel_13,
	// R.drawable.welcome_channel_14, R.drawable.welcome_channel_15,
	// R.drawable.welcome_channel_16, R.drawable.welcome_channel_17,
	// R.drawable.welcome_channel_18, R.drawable.welcome_channel_19,
	//
	// };

	private static final int DEFAULT_ADD_COUNT = 6;

	public static List<WelcomeChannel> getWelcomeChannelList() {
		List<WelcomeChannel> list = Preference.getInstance()
				.getWelcomeChannelList();// new ArrayList<WelcomeChannel>();
		if (list != null) {
			return list;
		}

		list = new ArrayList<WelcomeChannel>();
		AssetManager asm = GameNewsApplication.getInstance().getAssets();
		InputStream is = null;
		try {

			is = asm.open(Constants.FILE_NAME_WELCOME_CHANNEL);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String value = null;
			while ((value = reader.readLine()) != null) {

				Channel channel = new Channel();
				String[] channelValue = value.split(",");
				String channelName = channelValue[0].trim();
				int channelId = Integer.valueOf(channelValue[1].trim());
				String url = channelValue[2].trim();
				String iconPath = channelValue[3].trim();
				boolean checked = false;
				if (channelValue.length > 4) {
					checked = Boolean.valueOf(channelValue[4].trim());
				}
				channel.setName(channelName);
				channel.setId(channelId);
				channel.setIcon(url);
				WelcomeChannel welcomeChannel = new WelcomeChannel();
				welcomeChannel.setChannel(channel);
				welcomeChannel.setFromServer(false);
				welcomeChannel.setIsSelected(checked);
				welcomeChannel.setIconPath(iconPath);
				list.add(welcomeChannel);

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static Bitmap getAssetBitmap(String path) {
		Bitmap bmp = null;
		AssetManager asm = GameNewsApplication.getInstance().getAssets();
		InputStream is = null;
		try {
			is = asm.open(path);
			bmp = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return bmp;
	}

	private static List<GameName> getDefaultGameList() {
		List<GameName> list = new ArrayList<GameName>();

		AssetManager asm = GameNewsApplication.getInstance().getAssets();

		InputStream is = null;
		int index = 0;
		try {
			is = asm.open(Constants.FILE_NAME_GAME_LIST);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String value = null;
			while ((value = reader.readLine()) != null) {

				// TODO:当前只读取了游戏名字，需要读取游戏id和icon
				GameName channel = new GameName();
				channel.name = value;
				channel.index = index++;
				list.add(channel);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	private static Object sLock = new Object();
	private static String sVersionName;
	private static int sVersionCode;

	public static String getVersionName() {
		if (sVersionName == null) {
			synchronized (sLock) {
				sVersionName = GameNewsApplication.getInstance()
						.getPackageInfo().versionName;
			}
		}
		return sVersionName;
	}

	public static int getVersionCode() {
		if (sVersionCode == 0) {
			synchronized (sLock) {
				sVersionCode = GameNewsApplication.getInstance()
						.getPackageInfo().versionCode;
			}
		}
		return sVersionCode;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void copyText(String text) {
		Context context = GameNewsApplication.getInstance();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", text);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
	}

	public static boolean isSubscribedChannel(List<Channel> channels,
			Channel channel) {
		boolean flag = false;
		if (channel != null && channels != null) {
			for (Channel item : channels) {
				if (channel.id == item.id) {
					flag = true;
					break;

				}

			}
		}
		return flag;
	}

	public static int removeChannelIfExist(List<Channel> channels,
			Channel channel) {
		if (channels == null || channel == null) {
			return -1;
		}
		for (int i = 0; i < channels.size(); i++) {
			if (channel.getId() == channels.get(i).getId()) {
				channels.remove(i);
				return i;
			}
		}
		return -1;
	}

	public static void validChannelData(Channel channel) {
		if (channel.icon == null) {
			channel.icon = "";
		}
		if (channel.image == null) {
			channel.image = "";
		}
		if (channel.name == null) {
			channel.name = "";
		}
	}

	private static float mDensity;
	private static int mAppWidthDip;
	private static int mAppWidth;
	private static int mAppHeight;
	private static int mAppHeightDip;

	public static float getDensity() {
		if (mDensity != 0)
			return mDensity;

		mDensity = GameNewsApplication.getInstance().getResources()
				.getDisplayMetrics().density;

		return mDensity;
	}

	public static int getAppHeightDip() {
		if (mAppHeightDip != 0)
			return mAppHeightDip;

		final Context context = GameNewsApplication.getInstance();
		mAppHeightDip = Util.px2dip(context, getEquipmentHeight(context));

		return mAppHeightDip;
	}

	public static int getAppHeight() {
		if (mAppHeight != 0)
			return mAppHeight;

		mAppHeight = getEquipmentHeight(GameNewsApplication.getInstance());

		return mAppHeight;
	}

	public static int getAppWidthDip() {
		if (mAppWidthDip != 0)
			return mAppWidthDip;

		final Context context = GameNewsApplication.getInstance();
		mAppWidthDip = Util.px2dip(context, getEquipmentWidth(context));

		return mAppWidthDip;
	}

	public static int getAppWidth() {
		if (mAppWidth != 0)
			return mAppWidth;

		mAppWidth = getEquipmentWidth(GameNewsApplication.getInstance());

		return mAppWidth;
	}

	public static int[] getDisplayAttribute(Context context) {
		int[] nums = null;
		DisplayMetrics dm = null;
		if (context != null) {
			dm = context.getResources().getDisplayMetrics();
		} else {
			dm = GameNewsApplication.getInstance().getResources()
					.getDisplayMetrics();
		}
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		nums = new int[] { w_screen, h_screen };
		return nums;

	}

	public static float getDensityDpi() {
		return GameNewsApplication.getInstance().getResources()
				.getDisplayMetrics().densityDpi;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = getDensity();
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getEquipmentWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getEquipmentHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static void showMainHelpTips(Context context, MainTab mainTab,
			OnDismissListener dismissListener) {
		final PopupWindow tipsView = new PopupWindow(context);
		tipsView.setOutsideTouchable(true);
		tipsView.setTouchable(true);
		tipsView.setFocusable(true);

		tipsView.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		tipsView.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		tipsView.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				tipsView.dismiss();
				return true;
			}
		});
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View helpView = inflater.inflate(R.layout.help_view, null);
		tipsView.setContentView(helpView);
		TextView tips = (TextView) helpView.findViewById(R.id.help_tips);
		tipsView.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.bg_help_down));
		if (dismissListener != null) {
			tipsView.setOnDismissListener(dismissListener);
		}
		switch (mainTab.getId()) {
		case MainTab1.INDEX:
			tips.setText(R.string.tips_new_info);
			break;
		case MainTab2.INDEX:
			tips.setText(R.string.tips_brush);
			break;
		case MainTab3.INDEX:
			tips.setText(R.string.tips_mine);
			tipsView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bg_help_down1));
			break;
		case MainTab4.INDEX:
			tips.setText(R.string.tips_subscribe);
			tipsView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bg_help_up));
			break;

		case MainTab5.INDEX:
			tips.setText(R.string.tips_extra_btn1);
			tipsView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.bg_help_down1));
			break;
		default:
			break;
		}

		int[] location = new int[2];

		View button = mainTab.getButton();
		button.getLocationOnScreen(location);
		Rect tempRect = new Rect(location[0], location[1], location[0]
				+ button.getWidth(), location[1] + button.getHeight());
		helpView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int helpViewWidth = helpView.getMeasuredWidth();
		int helpViewHeight = helpView.getMeasuredHeight();

		if (MainTab5.INDEX == mainTab.getId()) {
			int xPos = (tempRect.left + tempRect.right) / 2 - helpViewWidth
					+ dip2px(context, 25);
			int yPos = (int) (tempRect.top - helpViewHeight - dip2px(context,
					10));
			tipsView.showAtLocation(button, Gravity.NO_GRAVITY, xPos, yPos);
		} else {
			int xPos = (tempRect.left + tempRect.right) / 2 - helpViewWidth / 2;
			int yPos = (int) (tempRect.top - helpViewHeight - dip2px(context,
					10));
			tipsView.showAtLocation(button, Gravity.NO_GRAVITY, xPos, yPos);
		}

		int step = Preference.getInstance().getCurrentGuideStep();
		if (step < Preference.STEP_DONE) {
			Preference.getInstance().setGuideStep(step + 1);
		}
	}

	public static String getUrlDomainName(String url) throws URISyntaxException {
		if (url == null) {
			return null;
		}
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	public static String getUrlQuery(String url) throws URISyntaxException {
		if (url == null) {
			return null;
		}
		URI uri = new URI(url);
		String query = uri.getQuery();
		return query;
	}

	public static SubscribeEvent getSubscribeEvent(List<Channel> originalList,
			List<Channel> changedList) {
		boolean changed = false;
		boolean isMulti = false;
		if (originalList != null && changedList != null) {

			if (!isEquals(originalList, changedList)) {
				changed = true;
				List<Channel> subList = null;

				if (changedList.size() > 0) {
					subList = changedList.subList(0, changedList.size() - 1);
				}

				if (isEquals(originalList, subList)) {
					isMulti = false;
				} else {
					isMulti = true;
				}
			}
		}

		if (changed) {
			SubscribeEvent event = new SubscribeEvent();
			event.isSubscribeChanged = true;
			event.isSubscribeMultiple = isMulti;
			return event;
		}
		return null;
	}

	private static boolean isEquals(List<Channel> list1, List<Channel> list2) {
		if (list2 == null || list1 == null) {
			return false;
		}
		if (list2.size() != list1.size()) {
			return false;
		} else {
			for (int i = 0; i < list2.size(); i++) {
				Channel channel = list2.get(i);
				Channel originalChannel = list1.get(i);
				if (channel.getId() != originalChannel.getId()) {
					return false;
				}
			}
		}
		return true;
	}

	public static void showDialog(FragmentActivity activity, DialogFragment f,
			String tag) {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		Fragment prev = fm.findFragmentByTag(tag);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		f.show(ft, tag);
	}

	/**
	 * 清除cookie
	 * 
	 * @param context
	 */
	public static void removeCookie(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

	public static void addCookie(Context context, String url, String value) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);

		String cookie = cookieManager.getCookie(url);
		cookieManager.setCookie(url, value + ","
				+ (cookie == null ? "" : cookie));
		CookieSyncManager.getInstance().sync();
	}

	public static void ensureAccesstokenForCookie(Context context) {
		Preference pref = Preference.getInstance();
		UserInitRsp rsp = pref.getInitRsp();

		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		String url = "shua.duowan.com";

		String cookie = cookieManager.getCookie(url);
		if (cookie == null || !cookie.contains("accessToken="))

			if (rsp != null) {
				addCookie(context, url, "accessToken=" + rsp.getAccessToken());
			}
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(Context context) {
		InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// manager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
		if (((Activity) context).getCurrentFocus() != null) {
			manager.hideSoftInputFromWindow(((Activity) context)
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 控制软键盘的显示隐藏
	 */
	public static void showSoftInput(Context context) {
		InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	public static boolean isFloatEquals(float f1, float f2, float r) {
		return Math.abs(f1 - f2) < r;
	}

	public static boolean isFloatEquals(float f1, float f2) {
		return isFloatEquals(f1, f2, 0.0001f);
	}

	// md5( md5("2014-11-07") + checkin ) + md5(#user_token)
	public static String getKey() {
		String date = TimeUtil.parseTimeToYMD(new Date());
		String str = "checkin";
		String token = getAccessToken();

		String key = "";
		try {
			key = FileUtil.md5Str2Str(FileUtil.md5Str2Str(date) + str)
					+ FileUtil.md5Str2Str(token);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return key;
	}

	public static String getIPFromInet(String host) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("获取失败");
		}

		String ip = "";
		if (address != null) {
			ip = address.getHostAddress();
		}
		return ip;
	}

	private static String getIPFromLocal(String host) {

		Preference pref = Preference.getInstance();
		Map<String, String> ipMap = pref.getIpMap();

		if (ipMap != null && ipMap.containsKey(host)) {
			return ipMap.get(host);
		}

		return "";
	}

	public static String ensureIp(String domain) {
		Set<String> list = new HashSet<String>();
		list.add(domain);

		Map<String, String> map = ensureIpMap(list);
		if (map == null) {
			return null;
		}
		return map.get(domain);
	}

	public static Map<String, String> ensureIpMap(Set<String> domainList) {
		Map<String, String> map = new HashMap<String, String>();

		Preference pref = Preference.getInstance();
		long lastUpdateTime = pref.getLastHostMapUpdateTime();
		long currentTime = System.currentTimeMillis();

		String ip = "";
		for (String domain : domainList) {
			if (currentTime - lastUpdateTime >= Constants.CACHE_DURATION_BS2_IP_MAP) {
				ip = syncIPFromInet(domain);
			} else {
				ip = getIPFromLocal(domain);
				if (TextUtils.isEmpty(ip)) {
					ip = syncIPFromInet(domain);
				}
			}

			if (TextUtils.isEmpty(ip)) {
				ip = domain; // 最后保障：如果无法获取到ip地址，则返回原host
			}

			map.put(domain, ip);
		}
		return map;
	}

	/**
	 * 从网络获取ip地址，并保存到本地
	 * 
	 * @param host
	 * @param ipMap
	 * @return 该host对应的ip地址
	 */
	private static String syncIPFromInet(String host) {
		Preference pref = Preference.getInstance();
		Map<String, String> ipMap = pref.getIpMap();
		String ip;
		ip = getIPFromInet(host);
		if (!TextUtils.isEmpty(ip)) {
			if (ipMap == null) {
				ipMap = new HashMap<String, String>();
			}
			ipMap.put(host, ip);
		}

		pref.setIpMap(ipMap);
		return ip;
	}

	public static String getDomainName(String url) {
		if (TextUtils.isEmpty(url)) {
			return "";
		}

		url = url.replace("https://", "");
		url = url.replace("http://", "");

		int index = url.indexOf("/");
		if (index == -1) {
			return url;
		}

		return url.substring(0, index);
	}

	/**
	 * 判断所给的url是否是一个ip地址：如10.33.3.3
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isIpUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}

		url = url.replaceAll("\\.", "");
		if (TextUtils.isDigitsOnly(url)) {
			return true;
		}

		return false;
	}

	public static String replaceDomainWithIP(String url, String ip) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(ip)) {
			return url;
		}
		String domain = getDomainName(url);
		String newUrl = url.replace(domain, ip);
		return newUrl;
	}

	public static void startMainApp(Context context) {
		context.startActivity(getMainAppIntent(context));
	}

	public static Intent getMainAppIntent(Context context) {
		Intent intent = new Intent(context, WelcomeActivity.class);

		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		return intent;
	}

	public static void restartApp(Activity activity) {

		AlarmManager am = (AlarmManager) activity
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = activity.getIntent();

		if (ArticleDetailActivity.class.getName().equals(
				intent.getComponent().getClassName())) {

			intent.putExtra(ArticleDetailActivity.KEY_ARTICLE_LIST,
					ArticleDetailSwitcher.getInstance().getArticleInfos());
		}

		PendingIntent pi = PendingIntent.getActivity(activity, 1, intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pi);

		Process.killProcess(Process.myPid());
	}

	public static boolean inTest(String channelName) {
		return "test".equals(channelName) || inDev(channelName);
	}

	public static boolean inDev(String channelName) {
		return "dev".equals(channelName);
	}

	public static byte[] combineArray(byte[]... params) {

		if (params == null || params.length == 0) {
			return null;
		}

		int length = 0;
		for (byte[] bytearray : params) {
			if (bytearray == null) {
				continue;
			}
			length += bytearray.length;
		}
		byte[] returnValue = new byte[length];
		int currentLength = 0;
		for (int i = 0; i < params.length; i++) {
			byte[] bytearray = params[i];

			if (bytearray == null) {
				continue;
			}

			System.arraycopy(bytearray, 0, returnValue, currentLength,
					bytearray.length);

			currentLength += bytearray.length;
		}

		return returnValue;
	}

	public static String getVersionCodeForServer() {
		return "android-" + getVersionName();
	}

	public static String getAccessToken() {
		UserInitRsp rsp = Preference.getInstance().getInitRsp();
		String accessToken = "";
		if (rsp != null) {
			accessToken = rsp.getAccessToken();
		}
		return accessToken;
	}

	public static Set<String> getQueryParameterNames(Uri uri) {
		if (uri.isOpaque()) {
			throw new UnsupportedOperationException(
					"This isn't a hierarchical URI.");
		}

		String query = uri.getEncodedQuery();
		if (query == null) {
			return Collections.emptySet();
		}

		Set<String> names = new LinkedHashSet<String>();
		int start = 0;
		do {
			int next = query.indexOf('&', start);
			int end = (next == -1) ? query.length() : next;

			int separator = query.indexOf('=', start);
			if (separator > end || separator == -1) {
				separator = end;
			}

			String name = query.substring(start, separator);
			names.add(Uri.decode(name));

			// Move start to end of name.
			start = end + 1;
		} while (start < query.length());

		return Collections.unmodifiableSet(names);
	}
}
