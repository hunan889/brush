package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.show.SpecialTagId;
import com.duowan.show.Topic;
import com.yy.android.gamenews.ui.common.DataViewConverterFactory;
import com.yy.android.gamenews.ui.common.ImageAdapter;

public class SquareTopicListFragment extends BaseTopicListFragment<Topic> {

	private static final String KEY_SQUARE_TOPIC_LIST = "square_topic_list";

	public SquareTopicListFragment() {
		setType(DataViewConverterFactory.TYPE_LIST_WATERFALL);
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
	protected void requestData(int refreType) {
		ArrayList<Integer> tags = new ArrayList<Integer>();
		tags.add(SpecialTagId._SPECIAL_TAGID_SQUARE);
		super.requestData(refreType, tags);
	}

	@Override
	protected ImageAdapter<Topic> initAdapter() {
		return initAdapter(TopicListAdapter.TYPE_WATERFALL);
	}

	@Override
	protected String getKey() {
		return KEY_SQUARE_TOPIC_LIST;
	}
}
