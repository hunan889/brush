package com.duowan.android.base;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.duowan.Comm.UpgradeRsp;
import com.duowan.android.base.event.GuidEvent;
import com.duowan.android.base.event.ToastEvent;
import com.duowan.android.base.event.UniPacketErrorEvent;
import com.duowan.android.base.event.VolleyErrorEvent;
import com.duowan.android.base.model.BaseModel;
import com.duowan.android.base.model.UpgradeModel;
import com.duowan.android.base.net.VolleyErrorHelper;
import com.duowan.android.base.util.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午3:47:46
 */
public abstract class BaseActivity extends FragmentActivity {

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 当com.duowan.android.base.net.VolleyClient请求失败时事件会回调回这里处理，具体方法可重写
	 * 
	 * @see com.duowan.android.base.net.VolleyClient#newRequestQueue
	 * @param event
	 */
	public void onEventMainThread(VolleyErrorEvent event) {
		String errorMsg = VolleyErrorHelper.getMessage(getApplicationContext(), event.error);
		Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
		LogUtils.log(errorMsg);
	}

	/**
	 * 请求成功后返回的UniPacket在解析时错误，事件放在这里处理，具体方法须实现
	 * 
	 * @see com.duowan.android.base.net.VolleyClient#newRequestQueue
	 * @param event
	 */
	public void onEventMainThread(UniPacketErrorEvent event) {
		Toast.makeText(getApplicationContext(), event.msg, Toast.LENGTH_SHORT).show();

	}

	public void onEventMainThread(ToastEvent event) {
		Toast.makeText(getApplicationContext(), event.msg, Toast.LENGTH_SHORT).show();

	}

	public void onEvent(GuidEvent event) {
		BaseModel.setGuid(getApplicationContext(), event.vGuid);
	}

	/**
	 * 版本升级
	 * 
	 * @param upgradeRsp
	 */
	public void onEventMainThread(final UpgradeRsp upgradeRsp) {
		UpgradeModel.showUpgradeDialog(this, upgradeRsp);
	}

}
