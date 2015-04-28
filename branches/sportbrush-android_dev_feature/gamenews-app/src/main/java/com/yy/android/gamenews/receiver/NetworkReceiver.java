package com.yy.android.gamenews.receiver;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.UpdateMyFavChannelListRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.event.MessageEvent;
import com.yy.android.gamenews.event.NetWorkChangeEvent;
import com.yy.android.gamenews.event.UpdateChannelListEvent;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.PushUtil;
import com.yy.android.gamenews.util.Util;

import de.greenrobot.event.EventBus;

public class NetworkReceiver extends BroadcastReceiver {

	private Preference mPref = Preference.getInstance();

	public void onReceive(Context context, Intent intent) {
		// 网络状态改变
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			EventBus.getDefault().post(new NetWorkChangeEvent());
			{
				if (GameNewsApplication.getInstance() != null
						&& Util.isNetworkConnected()) {
					// 删除Tag
					String deleteData = mPref
							.getXinGeData(PushUtil.DELETE_XINGE_PUSH_DATA);
					if (deleteData != null && TextUtils.isEmpty(deleteData)) {
						String[] split = deleteData.split(",");
						for (int i = 1; i < split.length; i = i + 2) {
							PushUtil.deleteItemTag(context, split[i]);
						}
						mPref.setXinGeData(PushUtil.DELETE_XINGE_PUSH_DATA, "");
					}
					// 增加Tag
					String addData = mPref
							.getXinGeData(PushUtil.ADD_XINGE_PUSH_DATA);
					if (addData != null && TextUtils.isEmpty(addData)) {
						String[] split = addData.split(",");
						for (int i = 1; i < split.length; i = i + 2) {
							PushUtil.addItemTag(context, split[i]);
						}
						mPref.setXinGeData(PushUtil.ADD_XINGE_PUSH_DATA, "");
					}
					// 同步我的最爱频道
					if (mPref.getRetryState()) {
						ArrayList<Channel> channels = (ArrayList<Channel>) mPref
								.getMyFavorChannelList();
						ChannelModel
								.updateMyFavChannelList(
										null,
										new ResponseListener<UpdateMyFavChannelListRsp>(
												null) {

											@Override
											public void onResponse(
													UpdateMyFavChannelListRsp response) {
												// TODO Auto-generated method
												// stub
												if (response != null) {
													mPref.saveMyFavorChannelList(response
															.getChannelList());
												}
												EventBus.getDefault()
														.post(new UpdateChannelListEvent());
												mPref.setRetryState(false);
											}
										}, channels, false);
					}

				}
			}

			// 网络状态改变
//			if (intent.getAction().equals(
//					ConnectivityManager.CONNECTIVITY_ACTION)) {
//				if (GameNewsApplication.getInstance() != null
//						&& Util.isNetworkConnected()&&Constants
//						.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//					MessageEvent msg  =	new MessageEvent();
//					msg.setNetworkChangeStatus(MessageEvent.STATUS_SUCESS);
//					EventBus.getDefault().post(msg);
//				}
//			}
		}

	}
}
