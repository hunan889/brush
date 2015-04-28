package com.yy.android.gamenews.plugin.cartport;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yy.android.gamenews.ui.SingleFragmentActivity;
import com.yy.android.gamenews.util.StatsUtil;

public class CartDetailImageActivity extends SingleFragmentActivity {

	public static void startCartDetailActivity(Context context, long mId,
			String brandName) {

		startActivity(context, mId, brandName);
	}

	protected static void startActivity(Context context, long id, String title) {

		Intent intent = new Intent(context, CartDetailImageActivity.class);
		intent.putExtra(CartDetailActivity.TAG_FGMT_CARTID, id);
		intent.putExtra(CartDetailImageActivity.TAG_FGMT_CAR_COLUMN, title);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		StatsUtil.statsReport(context, "into_cart_image", "desc",
				"into_cart_image");
		StatsUtil
				.statsReportByHiido("into_cart_image", "into_cart_image");
		StatsUtil.statsReportByMta(context, "into_cart_image",
				"into_cart_image");
		
	}

	public static final String TAG_FGMT_CAR_COLUMN = "carColumn";

	@Override
	protected Fragment initFragment() {
		return new CartDetailImageFragment();
	}

}
