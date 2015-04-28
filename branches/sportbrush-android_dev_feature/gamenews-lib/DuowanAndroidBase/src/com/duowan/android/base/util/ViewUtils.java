package com.duowan.android.base.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-10 下午4:32:18
 */
public final class ViewUtils {
	/**
	 * 
	 * 描述：dip转换为px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 * @throws
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 
	 * 描述：px转换为dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 * @throws
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 描述：根据分辨率获得字体大小.
	 * 
	 * @param screenWidth
	 *            the screen width
	 * @param screenHeight
	 *            the screen height
	 * @param textSize
	 *            the text size
	 * @return the int
	 */
	public static int resizeTextSize(int screenWidth, int screenHeight,
			int textSize) {
		float ratio = 1;
		try {
			float ratioWidth = (float) screenWidth / 480;
			float ratioHeight = (float) screenHeight / 800;
			ratio = Math.min(ratioWidth, ratioHeight);
		} catch (Exception e) {
		}
		return Math.round(textSize * ratio);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		setListViewHeightBasedOnChildren(listView, -1);
	}

	/**
	 * 显示整个listview
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView,
			int size) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int count = 0;
		if (size <= 0) {
			count = listAdapter.getCount();
		} else {
			count = size;
		}

		int totalHeight = 0;
		for (int i = 0; i < count; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (count - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}
