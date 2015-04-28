package com.yy.android.gamenews.plugin.show;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.show.Tag;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;

public class HotTagListFragment extends BaseTagListFragment<Tag> {

	private static final String KEY_TAG_LIST = "hot_tag_list";

	public HotTagListFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_NORMAL);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected ImageAdapter<Tag> initAdapter() {
		return new TagGridAdapter(getActivity());
	}

	@Override
	protected String getKey() {
		return KEY_TAG_LIST;
	}
}
