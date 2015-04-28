package com.yy.android.gamenews.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.event.XGPushEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.WelcomeActivity;
import com.yy.android.gamenews.util.Preference;

import de.greenrobot.event.EventBus;

public class CustomPushReceiver extends XGPushBaseReceiver {
	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
	}

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
		if (message.getActionType() != XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			return;
		}
		String customContentString = message.getCustomContent();
		handleMsg(context, customContentString);
	}

	protected void handleMsg(Context context, String customContentString) {
		if (TextUtils.isEmpty(customContentString)) {
			return;
		}
		try {
			int type = -1;
			long id = -1;
			String url = null;

			JSONObject json = new JSONObject(customContentString);
			if (!json.isNull(Constants.PUSH_TYPE)) {
				String str = json.getString(Constants.PUSH_TYPE);
				type = Integer.valueOf(str);
			}
			if (!json.isNull(Constants.PUSH_ID)) {
				String str = json.getString(Constants.PUSH_ID);
				id = Long.valueOf(str);
			}

			if (!json.isNull(Constants.PUSH_URL)) {
				url = json.getString(Constants.PUSH_URL);
			}

			if (type != -1 && (id != -1 || url != null)) {
				Intent intent = new Intent(context, WelcomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);

				intent.putExtra(Constants.PUSH_TYPE, type);
				if (id != -1) {
					intent.putExtra(Constants.PUSH_ID, id);
				}
				if (url != null) {
					intent.putExtra(Constants.PUSH_URL, url);
				}

				context.getApplicationContext().startActivity(intent);
			}
		} catch (JSONException e) {
		} catch (Exception e) {
		}
	}

	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult registerMessage) {
		if (context == null || registerMessage == null) {
			return;
		}
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			ChannelModel.pushInit(registerMessage.getToken());
		}
	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		String msg = message.getContent();
		Preference pref = Preference.getInstance();
		if (Constants.PUSH_MESSAGE_NEW_TDOU.equals(msg)) {
			//pref.setHasNewTdou(true);
		} else if (Constants.PUSH_MESSAGE_NEW_EVENT.equals(msg)) {
			pref.setHasNewEvent(true);
			XGPushEvent event = new XGPushEvent();
			event.customContent = message.getCustomContent();
			event.content = message.getContent();
			event.title = message.getTitle();
			
			EventBus.getDefault().post(event);
		}
	}

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
	}
}
