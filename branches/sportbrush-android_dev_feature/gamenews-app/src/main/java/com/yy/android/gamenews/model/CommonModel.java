package com.yy.android.gamenews.model;

import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.duowan.android.base.model.BaseModel;
import com.duowan.android.base.util.LocalLog;
import com.duowan.jce.wup.UniPacket;
import com.duowan.taf.jce.JceOutputStream;
import com.duowan.taf.jce.JceStruct;
import com.duowan.taf.jce.JceUtil;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.exception.UserException;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.util.FileUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

public class CommonModel extends BaseModel {

	protected static UniPacket createUniPacket(String funcName,
			JceStruct request) {
		return createUniPacket(Constants.APP_SERVANT_NAME, funcName, request);
	}

	protected static UniPacket createUniPacket(String servantName,
			String funcName, JceStruct request) {
		UniPacket uniPacket = new UniPacket();
		uniPacket.useVersion3();
		uniPacket.setServantName(servantName);
		uniPacket.setFuncName(funcName);

		// workaround:
		// 此处需传空字符串，否则后台接收不到
		Object requestObject = request == null ? "" : request;
		uniPacket.put("request", requestObject);

		uniPacket.put("version", Util.getVersionCodeForServer());

		uniPacket.put("accessToken", Util.getAccessToken());

		uniPacket.put("sign", getSign(requestObject));

		return uniPacket;
	}

	public static String getSign(Object request) {
		String sign = null;
		// md5(self::$sign_key . $body . $raw_version . $raw_accesstoken);
		String signKey = "trwerweswerwerte6345.s][]@";

		byte[] signKeyBytes = signKey.getBytes();

		JceOutputStream _out = null;
		if (request != null) {
			_out = new JceOutputStream();
			_out.write(request, 0);
		}

		byte[] bodyBytes = _out == null ? null : JceUtil.getJceBufArray(_out
				.getByteBuffer());
		byte[] versionBytes = Util.getVersionCodeForServer().getBytes();
		byte[] accessTokenBytes = Util.getAccessToken() == null ? null : Util
				.getAccessToken().getBytes();
		byte[] combinedArray = Util.combineArray(signKeyBytes, bodyBytes,
				versionBytes, accessTokenBytes);
		try {
			sign = FileUtil.md5Byte2String(combinedArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sign;
	}

	public static class CommonRequest<T> extends Request {

		private ResponseListener<T> mListener;
		private T mObject;
		private boolean mIsShowErrorMsg;
		private WeakReference<FragmentActivity> mRef;
		private Fragment mFragment;

		public CommonRequest(UniPacket uniPacket) {
			super(uniPacket);
		}

		public CommonRequest(FragmentActivity activity, UniPacket uniPacket,
				String cacheKey) {
			super(activity, uniPacket, cacheKey);

			mRef = new WeakReference<FragmentActivity>(activity);
		}

		public CommonRequest(FragmentActivity activity, UniPacket uniPacket) {
			super(activity, uniPacket);

			mRef = new WeakReference<FragmentActivity>(activity);
		}

		public CommonRequest(Fragment fragment, UniPacket uniPacket) {
			super(fragment.getActivity(), uniPacket);

			mRef = new WeakReference<FragmentActivity>(fragment.getActivity());
			mFragment = fragment;
		}

		public CommonRequest<T> setup(ResponseListener<T> listener, T t) {
			mListener = listener;
			mObject = t;
			return this;
		}

		public CommonRequest<T> setShowErrorMsg(boolean show) {
			mIsShowErrorMsg = show;
			return this;
		}

		/**
		 * 如果code和subcode为0，则返回response对象，否则返回错误信息
		 */
		@Override
		public void onResponse(UniPacket response) {

			if (mListener == null) {
				return;
			}

			if (checkFragmentDetach()) {
				return;
			}

			T rsp = response.getByClass("result", mObject);
			String strType = "";
			Integer intType = 0;
			String msg = response.getByClass("msg", strType);
			int code = response.getByClass("code", intType);
			int subcode = response.getByClass("subcode", intType);
			if (code == 0 && subcode == 0) {
				mListener.onResponse(rsp);
			} else {
				dispatchError(new UserException(code, subcode, msg), true);
			}
		}

		@Override
		public void onError(Exception e) {
			dispatchError(e, false);
		}

		private boolean checkFragmentDetach() {
			if (mFragment != null) {
				if (mFragment.getActivity() == null) {
					return true;
				}
			}
			return false;
		}

		/**
		 * isUserMsg: 是否是后台返回过来的信息，用于判断是否显示toast
		 * 
		 * @param e
		 * @param isUserMsg
		 */
		private void dispatchError(Exception e, boolean isUserMsg) {
			if (checkFragmentDetach()) {
				return;
			}

			if (mListener != null) {
				mListener.onError(e);
			}

			LocalLog.d("CommonModel",
					"[dispatchError], msg = " + e.getMessage()
							+ ", isUserMsg = " + isUserMsg);
			if (mIsShowErrorMsg) {
				if (isUserMsg) {
					String msg = e.getMessage();
					if (!TextUtils.isEmpty(msg)) {
						ToastUtil.showToast(msg);
					}
				} else {
					if (!Util.isNetworkConnected()) {
						ToastUtil.showToast(R.string.http_not_connected);
						return;
					} else {
						ToastUtil.showToast(R.string.http_error);
					}
				}
			}
		}

		@Override
		public void execute() {
			if (mRef != null) {
				FragmentActivity activity = mRef.get();
				BaseActivity baseActivity = null;
				if (activity instanceof BaseActivity) {
					baseActivity = (BaseActivity) activity;
				}
				if (baseActivity != null
						&& baseActivity.isOnSaveInstanceStateCalled()) {
					setShowProgressDialog(false);
				}
			}
			super.execute();
		}
	}
}
