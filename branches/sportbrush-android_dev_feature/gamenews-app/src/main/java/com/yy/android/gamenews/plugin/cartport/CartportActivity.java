package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yy.android.gamenews.ui.SingleFragmentActivity;

public class CartportActivity extends SingleFragmentActivity {

	public static void startCarportActivity(Context context) {

		startActivity(context);
	}

	protected static void startActivity(Context context) {

		Intent intent = new Intent(context, CartportActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	protected Fragment initFragment() {
		return new CartportFragment();
	}

}
