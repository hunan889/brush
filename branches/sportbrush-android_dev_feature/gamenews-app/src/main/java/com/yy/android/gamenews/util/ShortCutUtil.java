package com.yy.android.gamenews.util;

import java.util.List;

import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.ui.BaseActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

/**
 * 创建桌面快捷方式
 * 
 * @author yeyuelai
 *
 */
public class ShortCutUtil {

	private static Context context;
	private static String label;
	static {
		context = GameNewsApplication.getInstance().getApplicationContext();
		label = context.getPackageName();
	}

	public static void addShortcutToDesktop(
			Class<? extends BaseActivity> activityClass) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		BitmapDrawable iconBitmapDrawabel = null;

		PackageManager packageManager = context.getPackageManager();
		try {
			iconBitmapDrawabel = (BitmapDrawable) packageManager
					.getApplicationIcon(context.getPackageName());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				iconBitmapDrawabel.getBitmap());
		// ShortcutIconResource iconRes =
		// Intent.ShortcutIconResource.fromContext(this, R.drawable.user);
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		shortcut.putExtra("duplicate", false);

		// ComponentName comp = new ComponentName(label, "."
		// + this.getLocalClassName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
		// new Intent(Intent.ACTION_MAIN).setComponent(comp));

		Intent respondIntent = new Intent(context, activityClass);
		 /*以下两句是为了在卸载应用的时候同时删除桌面快捷方式*/
		respondIntent.setAction(Intent.ACTION_MAIN);
		respondIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);

		context.sendBroadcast(shortcut);
	}

	public static void removeShortcutFromDesktop(
			Class<? extends BaseActivity> startClass) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);

		Intent respondIntent = new Intent(context, startClass);
		respondIntent.setAction(Intent.ACTION_MAIN);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);

		// String appClass = "." + this.getLocalClassName();
		// ComponentName comp = new ComponentName(label, appClass);
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
		// Intent.ACTION_MAIN).setComponent(comp));
		// Intent intent =
		// getPackageManager().getLaunchIntentForPackage(getPackageName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		context.sendBroadcast(shortcut);
	}

	public static boolean isInstallShortcut() {
		boolean isInstallShortcut = false;
		final ContentResolver cr = context.getContentResolver();
		String AUTHORITY = getAuthorityFromPermission("com.android.launcher.permission.READ_SETTINGS");
		if (AUTHORITY != null) {
			Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
					+ "/favorites?notify=true");

			Cursor c = cr.query(CONTENT_URI, new String[] { "title",
					"iconResource" }, "title=?", new String[] { label }, null);
			if (c != null && c.getCount() > 0) {
				isInstallShortcut = true;
			}

			if (c != null) {
				c.close();
			}
		}
		Log.v("isInstallShortcut", isInstallShortcut + "");
		return isInstallShortcut;
	}

	public static String getAuthorityFromPermission(String permission) {
		if (permission == null) {
			return null;
		}
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packageInfos != null) {
			for (PackageInfo packageInfo : packageInfos) {
				ProviderInfo[] providerInfos = packageInfo.providers;
				if (providerInfos != null) {
					for (ProviderInfo providerInfo : providerInfos) {
						if (permission.equals(providerInfo.readPermission)) {
							return providerInfo.authority;
						}
						if (permission.equals(providerInfo.writePermission)) {
							return providerInfo.authority;
						}
					}
				}
			}
		}
		return null;
	}
}
