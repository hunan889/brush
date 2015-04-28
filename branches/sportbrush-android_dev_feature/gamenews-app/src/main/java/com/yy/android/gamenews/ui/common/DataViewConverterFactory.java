package com.yy.android.gamenews.ui.common;

import android.content.Context;

/**
 * 工厂类，通过type获取不同的dataview converter
 * 
 * 暂时支持两种： 原生listview和瀑布流的listview 已知子类： </br> 1. 原生listview:
 * {@link DataListViewConverter} for Type {@link TYPE_LIST_NORMAL} </br> 2.
 * 瀑布流的listview: {@link DataPlaListViewConverter} for Type
 * {@link TYPE_LIST_WATERFALL}
 * 
 * @see DataViewConverter
 * @author liuchaoqun
 * 
 */
public class DataViewConverterFactory {

	/**
	 * 安卓原生的listview
	 */
	public static final int TYPE_LIST_NORMAL = 1001;

	/**
	 * 瀑布流listview
	 */
	public static final int TYPE_LIST_WATERFALL = 1002;
	
	/**
	 * GridView
	 */
	public static final int TYPE_LIST_GRIDVIEW = 1003;

	public static DataViewConverter<?> getDataViewWrapper(Context context,
			int type) {
		DataViewConverter<?> ret = null;
		switch (type) {
		case TYPE_LIST_NORMAL: {
			ret = new DataListViewConverter(context);
			break;
		}
		case TYPE_LIST_WATERFALL: {
			ret = new DataPlaListViewConverter(context);
			break;
		}
		case TYPE_LIST_GRIDVIEW: {
			ret = new DataGridViewConverter(context);
			break;
		}
		default: {
			break;
		}
		}
		return ret;
	}
}
