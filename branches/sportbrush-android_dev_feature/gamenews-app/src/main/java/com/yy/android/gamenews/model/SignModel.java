package com.yy.android.gamenews.model;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.duowan.gamenews.Button;
import com.duowan.gamenews.ButtonType;
import com.duowan.gamenews.CheckInActionReq;
import com.duowan.gamenews.CheckInActionRsp;
import com.duowan.gamenews.CheckInIndexReq;
import com.duowan.gamenews.CheckInIndexRsp;
import com.duowan.jce.wup.UniPacket;
import com.yy.android.gamenews.ServerConstants;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

/**
 * 签到功能
 * 
 * @author liuchaoqun
 * 
 */
public class SignModel extends CommentModel {

	public static void sendSignReq(
			final ResponseListener<CheckInIndexRsp> listener) {

		CheckInIndexReq req = new CheckInIndexReq();
		req.setPlatType(Preference.getInstance().getLoginType());
		UniPacket packet = createUniPacket("CheckInIndex", req);

		new Request(listener.get(), packet) {

			@Override
			public void onResponse(UniPacket response) {
				CheckInIndexRsp rsp = new CheckInIndexRsp();
				rsp = response.getByClass("result", rsp);

				if (rsp == null) {
					int subCode = response.get(ServerConstants.SUBCODE.KEY, 0);
					String desc = response.get(ServerConstants.MESSAGE.KEY, "");
					rsp = getErrorIndexRsp(listener.get(), subCode, desc);
				}

				listener.onResponse(rsp);
			}

			@Override
			public void onError(Exception e) {
				listener.onError(e);
				super.onError(e);
			}
		}.setShowProgressDialog(false).execute();
	}

	public static void sendCheckInActionReq(
			final ResponseListener<CheckInActionRsp> listener, int type) {

		CheckInActionReq req = new CheckInActionReq();
		req.setCheckInType(type);
		req.setHash(Util.getKey());
		req.setPlatType(Preference.getInstance().getLoginType());
		UniPacket packet = createUniPacket("CheckInAction", req);

		new Request(listener.get(), packet) {

			@Override
			public void onResponse(UniPacket response) {
				CheckInActionRsp rsp = new CheckInActionRsp();
				rsp = response.getByClass("result", rsp);

				if (rsp == null) {
					int subCode = response.get(ServerConstants.SUBCODE.KEY, 0);
					String desc = response.get(ServerConstants.MESSAGE.KEY, "");
					rsp = getErrorActionRsp(listener.get(), subCode, desc);
				}

				listener.onResponse(rsp);
			}

			@Override
			public void onError(Exception e) {
				listener.onError(e);
				super.onError(e);
			}
		}.execute();
	}

	public static CheckInActionRsp getErrorActionRsp(Context context,
			int subCode, String desc) {
		CheckInActionRsp rsp = new CheckInActionRsp();

		rsp.setButton(getErrorBtns(context, subCode));
		rsp.setDesc(getErrorDesc(context, subCode, desc));
		return rsp;
	}

	public static CheckInIndexRsp getErrorIndexRsp(Context context,
			int subCode, String desc) {
		CheckInIndexRsp rsp = new CheckInIndexRsp();

		rsp.setButton(getErrorBtns(context, subCode));
		rsp.setDesc(getErrorDesc(context, subCode, desc));
		return rsp;
	}

	private static String getErrorDesc(Context context, int subCode, String desc) {

		if (TextUtils.isEmpty(desc)) {
			Resources res = context.getResources();
			switch (subCode) {
			case ServerConstants.SUBCODE.LOGIN_FAIL: {
				desc = res.getString(R.string.sign_please_login);
				break;
			}
			case ServerConstants.SUBCODE.USER_SIGNED: {
				desc = res.getString(R.string.sign_already_signed);
				break;
			}
			default: {
				desc = res.getString(R.string.sign_fail);
				break;
			}
			}
		}
		return desc;
	}

	private static ArrayList<Button> getErrorBtns(Context context, int subCode) {
		ArrayList<Button> btnList = new ArrayList<Button>();
		Resources res = context.getResources();

		switch (subCode) {
		case ServerConstants.SUBCODE.LOGIN_FAIL: {
			btnList.add(new Button(res.getString(R.string.global_login),
					ButtonType._GO_USER_CENTER, "", ""));
			btnList.add(new Button(res.getString(R.string.global_cancel),
					ButtonType._GO_CANCEL, "", ""));

			break;
		}
		case ServerConstants.SUBCODE.USER_SIGNED: {
			btnList.add(new Button(res.getString(R.string.global_ok),
					ButtonType._GO_CANCEL, "", ""));
			break;
		}
		default: {

			btnList.add(new Button(res.getString(R.string.global_ok),
					ButtonType._GO_CANCEL, "", ""));
			break;
		}
		}

		return btnList;
	}
}
