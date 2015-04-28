package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yy.android.gamenews.ui.SingleFragmentActivity;

public class CartDetailActivity extends SingleFragmentActivity {

	public static void startCartDetailActivity(Context context, int brandId,
			String brandName) {

		startActivity(context, brandId, brandName);
	}

	protected static void startActivity(Context context, int cartId,
			String brandName) {

		Intent intent = new Intent(context, CartDetailActivity.class);
		intent.putExtra(CartDetailActivity.TAG_FGMT_CARTID, cartId);
		intent.putExtra(CartDetailActivity.TAG_FGMT_BRANDNAME, brandName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static final String TAG_FGMT_CARTID = "brandId";
	public static final String TAG_FGMT_BRANDNAME = "brandName";

	@Override
	protected Fragment initFragment() {
		Fragment fgmt = new CartDetailFragment();
		return fgmt;
	}

}
