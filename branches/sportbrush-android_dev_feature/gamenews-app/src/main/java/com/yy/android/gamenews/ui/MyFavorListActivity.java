package com.yy.android.gamenews.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.android.base.event.UniPacketErrorEvent;
import com.duowan.android.base.event.VolleyErrorEvent;
import com.duowan.android.base.net.VolleyErrorHelper;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

import de.greenrobot.event.EventBus;

public class MyFavorListActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String TAG_NAME_FRAGMENT = "ArticleListFragment";

	public static final String ACTION_BRUSH_CLICKED = "action_brush_clicked";
	private MyFavorListFragment mInfoFragment;
	private ActionBar mActionBar;
	public static final String KEY_ARTICLE_LIST = "article_list";
	public static final String KEY_CHANNEL = "channel";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate");

		setContentView(R.layout.activity_myfavor_article_list);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		if (savedInstanceState != null) { // onSaveInstanceState里保存的当前选择的tab
			mInfoFragment = (MyFavorListFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_NAME_FRAGMENT);
		}

	}

	@Override
	protected void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		if (mInfoFragment == null) {

			mInfoFragment = MyFavorListFragment.newInstance();
			transaction.add(R.id.container, mInfoFragment, TAG_NAME_FRAGMENT);
		} 

		transaction.show(mInfoFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}

	public void onEventMainThread(VolleyErrorEvent event) {
		String errorMsg = VolleyErrorHelper.getMessage(this, event.error);
		ToastUtil.showToast(errorMsg);
	}

	public void onEventMainThread(UniPacketErrorEvent event) {
		ToastUtil.showToast(event.msg);
	}

	@Override
	protected void onStart() {
		EventBus.getDefault().register(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}
}
