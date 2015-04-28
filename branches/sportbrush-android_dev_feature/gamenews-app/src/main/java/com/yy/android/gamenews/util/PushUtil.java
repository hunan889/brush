package com.yy.android.gamenews.util;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.duowan.gamenews.Channel;
import com.tencent.android.tpush.XGPushActivity;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushReceiver;
import com.tencent.android.tpush.service.XGPushService;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.receiver.CustomPushReceiver;

public class PushUtil {
	// 信鸽推送（add）
	public static final String ADD_XINGE_PUSH_DATA = "add_xinge_push_data";

	// 信鸽推送（delete）
	public static final String DELETE_XINGE_PUSH_DATA = "delete_xinge_push_data";

	public static void start(Context context) {
		// registerPush之前调用
		// 防止被安全软件等禁止掉组件，导致广播接收不了或service无法启动
		enableComponentIfNeeded(context, XGPushService.class.getName());
		enableComponentIfNeeded(context, XGPushReceiver.class.getName());
		// 2.30及以上版本
		enableComponentIfNeeded(context, XGPushActivity.class.getName());
		// CustomPushReceiver改为自己继承XGPushBaseReceiver的类，若有的话
		enableComponentIfNeeded(context, CustomPushReceiver.class.getName());

		XGPushManager.registerPush(context);

	}

	public static void stop(Context context) {
		XGPushManager.unregisterPush(context);
		ChannelModel.pushInit("");
	}

	// 启用被禁用组件方法
	private static void enableComponentIfNeeded(Context context,
			String componentName) {
		PackageManager pmManager = context.getPackageManager();
		if (pmManager != null) {
			ComponentName cnComponentName = new ComponentName(
					context.getPackageName(), componentName);
			int status = pmManager.getComponentEnabledSetting(cnComponentName);
			if (status != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
				pmManager.setComponentEnabledSetting(cnComponentName,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			}
		}
	}

	public static void addItemTag(Context context, String tag) {
		if (tag != null && !TextUtils.isEmpty(tag)) {
			XGPushManager.setTag(context, tag);
		}
	}

	public static void addArrayTag(Context context, List<String> arrayTag) {
		if (arrayTag != null && arrayTag.size() > 0) {
			for (int i = 0; i < arrayTag.size(); i++) {
				addItemTag(context, arrayTag.get(i));
			}
		}
	}

	public static void addChannelTag(Context context, List<Channel> channels) {
		if (channels != null && channels.size() > 0) {
			for (int i = 0; i < channels.size(); i++) {
				Channel channel = channels.get(i);
				if (channel != null) {
					String name = channel.getName();
					int id = channel.getId();
					addItemTag(context, name + "_" + id);
				}
			}
		}
	}

	public static void deleteItemTag(Context context, String tag) {
		if (tag != null && !TextUtils.isEmpty(tag)) {
			XGPushManager.deleteTag(context, tag);
		}
	}

	public static void deleteArrayTag(Context context, List<String> arrayTag) {
		if (arrayTag != null && arrayTag.size() > 0) {
			for (int i = 0; i < arrayTag.size(); i++) {
				deleteItemTag(context, arrayTag.get(i));
			}
		}
	}
	
	public static void deleteChannelTag(Context context, Channel channel) {
		if (channel != null) {
			String name = channel.getName();
			int id = channel.getId();
			deleteItemTag(context, name + "_" + id);
		}
	}

	public static void deleteChannelTag(Context context, List<Channel> channels) {
		if (channels != null && channels.size() > 0) {
			for (int i = 0; i < channels.size(); i++) {
				Channel channel = channels.get(i);
				deleteChannelTag(context, channel);
			}
		}
	}
	
	public static void deleteChannelTag(Context context, HashMap<String,Object> channels) {
		if (channels != null && channels.size() > 0) {
			Set<String> keySet = channels.keySet();
			for (String key : keySet) {
				deleteItemTag(context, channels.get(key).toString());
			}
		}
	}
}
