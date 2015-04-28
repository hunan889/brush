package com.yy.android.gamenews.util;

import java.util.List;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.duowan.Comm.ECommAppType;
import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Button;
import com.duowan.gamenews.ButtonType;
import com.duowan.gamenews.CheckInActionRsp;
import com.duowan.gamenews.CheckInButton;
import com.duowan.gamenews.CheckInIndexRsp;
import com.yy.android.gamenews.Constants;
import com.yy.android.gamenews.model.SignModel;
import com.yy.android.gamenews.ui.AppWebActivity;
import com.yy.android.gamenews.ui.LoginYYActivity;
import com.yy.android.gamenews.ui.MyHomeActivity;
import com.yy.android.gamenews.ui.common.UiUtils;
import com.yy.android.gamenews.ui.view.AppDialog;
import com.yy.android.gamenews.ui.view.AppDialog.OnClickListener;
import com.yy.android.gamenews.ui.view.CustomDialogView.Builder;
import com.yy.android.sportbrush.R;

public class SignUtil {
	private FragmentActivity mContext;
	private Resources mRes;
	private Preference mPref;

	public SignUtil(FragmentActivity context) {
		mContext = context;
		mRes = context.getResources();
		mPref = Preference.getInstance();
	}

	/**
	 * 检查是否每天第一次进入，如果是，则请求签到
	 */
	public void requestSignDaily() {
		return;
//		暂时去掉每日签到弹窗功能
//		//只有游戏刷子有签到
//		if (!Constants.isFunctionEnabled(ECommAppType._Comm_APP_GAMENEWS)) {
//			return;
//		}
//		if (mPref.isFirstLaunchDaily()) {
//			requestSign();
//		}
	}

	public void requestSign() {
		// 请求后台接口获取显示
		SignModel.sendSignReq(new ResponseListener<CheckInIndexRsp>(mContext) {

			@Override
			public void onResponse(CheckInIndexRsp rsp) {

				if(mPref.isFirstLaunchDaily()) {
					mPref.finishFirstLaunchDaily();
				}
				showSignDialog(rsp.checkInButton, rsp.button, rsp.desc);
			}

			@Override
			public void onError(Exception e) {
				if (e != null) {
					ToastUtil.showToast(R.string.http_error);
				}
			}
		});

	}

	private AppDialog mDialog;

	public void showSignDialog(List<CheckInButton> signBtnList,
			List<Button> btnList, String desc) {

		String title = mRes.getString(R.string.my_event_sign);
		Builder builder = new Builder(mContext);
		if (signBtnList != null && signBtnList.size() > 0) {
			if (signBtnList != null) {
				for (CheckInButton btn : signBtnList) {
					String url = btn.icon;
					String text = btn.name;
					builder.addButton(btn.checkInType, url, text,
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									sendCheckInAction(v.getId());
								}
							}, btn.isValid);
				}
			}
			builder.addTextView(desc).setTitleText(
					mRes.getString(R.string.sign_choose_title));
		} else {
			title = "";
			builder.setCaption(desc);
		}

		showDialogWithButtons(title, builder, btnList, R.string.global_cancel);
	}

	private void showDialogWithButtons(String title, Builder builder,
			List<Button> btnList, int defaultRes) {

		String btn1 = "";
		String btn2 = "";

		if (btnList != null && btnList.size() > 0) {
			btn1 = btnList.get(0).name;
			if (btnList.size() > 1) {
				btn2 = btnList.get(1).name;
			}
		} else {
			btn1 = mRes.getString(defaultRes);
		}
		mListener.setButtonList(btnList);
		showDialog(title, builder, btn1, btn2, mListener);
	}

	private void showDialog(String title, Builder builder, String ok,
			String cancel, OnClickListener listener) {
		if (mDialog != null) {
			mDialog.dismissWithoutNotify();
		}
		View view = null;
		if (builder != null) {
			view = builder.create();
		}
		mDialog = UiUtils.showDialog(mContext, title, view, ok, cancel,
				listener);
	}

	private void sendCheckInAction(int type) {
		SignModel.sendCheckInActionReq(new ResponseListener<CheckInActionRsp>(
				mContext) {

			@Override
			public void onResponse(CheckInActionRsp response) {
				if (response != null) {
					showResultAfterSign(response);
				}
			}

			@Override
			public void onError(Exception e) {
				if (e != null) {
					ToastUtil.showToast(R.string.http_error);
				}
			}

		}, type);

		StatsUtil.statsReport(mContext, "stats_checkin_action_type:" + type);
		StatsUtil.statsReportByMta(mContext, "stats_checkin_action_type",
				String.valueOf(type));
		StatsUtil.statsReportByHiido("stats_checkin_action_type",
				String.valueOf(type));
	}

	private void showResultAfterSign(CheckInActionRsp response) {
		Builder builder = new Builder(mContext);
		if (!TextUtils.isEmpty(response.getGiftCode())) {
			builder.addTextView(response.getGiftCode(), true);
		}
		if (!TextUtils.isEmpty(response.getCopyValue())) {
			builder.setCopyViewText(response.getCopyValue());
			builder.showCopyView(true);
		}

		builder.setCaption(response.getDesc());
		showDialogWithButtons("", builder, response.getButton(),
				R.string.global_ok);
	}

	private OnDialogBtnClickListener mListener = new OnDialogBtnClickListener();

	private class OnDialogBtnClickListener implements OnClickListener {

		private List<Button> mBtnList;

		public void setButtonList(List<Button> btnList) {
			mBtnList = btnList;
		}

		@Override
		public void onDismiss() {
			Log.d("", "onDismiss");

			notifyEnd();
		}

		@Override
		public void onDialogClick(int nButtonId) {
			try {
				if (mBtnList == null || mBtnList.size() == 0) {
					return;
				}
				int idx = 0;
				if (nButtonId == AppDialog.BUTTON_POSITIVE) {
					idx = 0;
				} else if (nButtonId == AppDialog.BUTTON_NEGATIVE) {
					idx = 1;
				}

				processBtnEvent(mBtnList.get(idx));
			} finally {
				notifyEnd();
			}
		}

	}

	private void notifyEnd() {
		if (mOnSignEndListener != null) {
			mOnSignEndListener.onEnd();
		}
	}

	private OnSignEndListener mOnSignEndListener;

	public void setOnSignEndListener(OnSignEndListener listener) {
		mOnSignEndListener = listener;
	}

	public interface OnSignEndListener {
		public void onEnd();
	}

	private void processBtnEvent(Button btn) {
		if (btn == null) {
			return;
		}
		switch (btn.buttonType) {
		case ButtonType._GO_CANCEL: {
			break;
		}
		case ButtonType._GO_USER_CENTER: {
			MyHomeActivity.startMyHomeActivityForResult(mContext);
			break;
		}
		case ButtonType._GO_WEB: {
			AppWebActivity.startWebActivityWithYYToken(mContext, btn.url,true);
			break;
		}
		case ButtonType._GO_YY_LOGIN: {
			LoginYYActivity.startLoginActivityForResult(mContext);
			break;
		}
		case ButtonType._GO_CANCEL_MSG: {
			ToastUtil.showToast(btn.desc);
			break;
		}

		}
	}

}
