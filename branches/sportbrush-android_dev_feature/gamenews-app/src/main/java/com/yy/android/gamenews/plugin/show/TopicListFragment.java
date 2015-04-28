package com.yy.android.gamenews.plugin.show;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.show.Topic;
import com.yy.android.gamenews.ui.common.ImageAdapter;

public class TopicListFragment extends BaseTopicListFragment<Topic> {

	private final static String TAG_KEY = "tagId";
	private static final String KEY_TOPIC_LIST = "topic_list_";
	private int tagId = -1;

	public static TopicListFragment newInstance(int tagId) {
		TopicListFragment topicListFragment = new TopicListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(TopicListFragment.TAG_KEY, tagId);
		topicListFragment.setArguments(bundle);
		return topicListFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null
				&& getArguments().containsKey(TopicListFragment.TAG_KEY)) {
			tagId = getArguments().getInt(TopicListFragment.TAG_KEY);
		}
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
		if (tagId < 0) {
			return;
		}
		ArrayList<Integer> tags = new ArrayList<Integer>();
		tags.add(tagId);
		super.requestData(refreType, tags);
	}

	@Override
	protected ImageAdapter<Topic> initAdapter() {
		return initAdapter(TopicListAdapter.TYPE_LIST);
	}

	@Override
	protected String getKey() {
		return KEY_TOPIC_LIST + tagId;
	}
}
