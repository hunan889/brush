package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.SingleFragmentActivity;
import com.yy.android.sportbrush.R;

public class BrandDetailActivity extends BaseActivity {

	private static final String TAG = SingleFragmentActivity.class
			.getSimpleName();
	public static final String TAG_NAME_FRAGMENT = "brandDetaiFragment";
	public static final String TAG_BRANDID = "tag_brandid";
	public static final String IMG_URL = "img_url";
	public static final String BRAND_NAME = "brand_name";

	private BrandSecondPagerlFragment mFragment;

	public static void startActivity(Context context, int brandId,
			String brandName, String imgUrl) {
		Intent intent = new Intent(context, BrandDetailActivity.class);
		intent.putExtra(TAG_BRANDID, brandId);
		intent.putExtra(BRAND_NAME, brandName);
		intent.putExtra(IMG_URL, imgUrl);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_brand_detail);
		mFragment = (BrandSecondPagerlFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_NAME_FRAGMENT);
	}

	@Override
	public void onResume() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mFragment == null) {
			mFragment = new BrandSecondPagerlFragment();
			mFragment.setArguments(getIntent().getExtras());
			transaction.add(R.id.container, mFragment, TAG_NAME_FRAGMENT);
		}
		transaction.show(mFragment);
		transaction.commitAllowingStateLoss();
		super.onResume();
	}
}
