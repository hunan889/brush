package com.yy.android.gamenews.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.duowan.gamenews.ArticleInfo;
import com.yy.android.gamenews.ui.ArticleClickHandler;
import com.yy.android.gamenews.ui.common.DataGridViewConverter;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.Util;
import com.yy.android.sportbrush.R;

/**
 * 推荐页
 * 
 * @author liuchaoqun
 * 
 */
public class RecommView extends LinearLayout {

	public RecommView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RecommView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecommView(Context context) {
		super(context);
		init(context);
	}

	private GridView mGridView;
	private RecommViewAdapter mAdapter;
	private ArticleClickHandler mListener;

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);

		setOrientation(VERTICAL);
		View view = new View(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) (context.getResources().getDimensionPixelSize(
						R.dimen.actionbar_height) * 1.5));
		addView(view, params);

		DataGridViewConverter converter = (DataGridViewConverter) DataViewConverterFactory
				.getDataViewWrapper(getContext(),
						DataViewConverterFactory.TYPE_LIST_GRIDVIEW);

		View gridParent = converter.createView(inflater, null, null);
		addView(gridParent);
		mGridView = converter.getDataView();
		mGridView.setNumColumns(2);
		int padding5 = Util.dip2px(context, 5);

		mGridView.setPadding(padding5, 0, padding5, 0);
		mGridView.setVerticalSpacing(Util.dip2px(getContext(), 15));
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setSelector(R.drawable.recomm_list_item_selector_dark);
		mAdapter = new RecommViewAdapter(getContext());
		converter.setAdapter(mAdapter);

		mListener = new ArticleClickHandler((FragmentActivity) getContext());
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ArticleInfo model = mAdapter.getItem(position);

				if (model == null) {
					return;
				}
				if (mListener != null) {
					mListener.onArticleItemClick(model);
				}

				String eventId = "stats_click_recomm";
				String key = "article_title";
				String value = String.valueOf(model.getTitle() + "("
						+ model.getId() + ")");
				StatsUtil.statsReport(getContext(), eventId, key, value);
				StatsUtil.statsReportByMta(getContext(), eventId, key, value);
				StatsUtil.statsReportByHiido(eventId, key + value);
			}
		});
	}

	public void setDataSource(ArrayList<ArticleInfo> list) {
		mAdapter.setDataSource(list);
	}

}
