package com.yy.android.gamenews.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.android.base.model.BaseModel.ResponseListener;
import com.duowan.gamenews.Channel;
import com.duowan.gamenews.Column;
import com.duowan.gamenews.SearchChannelRsp;
import com.yy.android.gamenews.model.ChannelModel;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.Preference;
import com.yy.android.gamenews.util.StatsUtil;
import com.yy.android.gamenews.util.ToastUtil;
import com.yy.android.sportbrush.R;

public class ChannelSearchActivity extends BaseActivity implements
		OnItemClickListener {
	private static final String FG_TAG_CHANNEL_GRID = "FG_TAG_CHANNEL_GRID";
	private static final String FG_TAG_NO_RESULT = "FG_TAG_NO_RESULT";
	private ActionBar mActionBar;
	private AutoCompleteTextView mSearch;
	private TextView mSearchGo;
	private LinearLayout mSearchContainer;
	private AutoCompleteAdapter mAutoCompleteAdapter;
	private ChannelMoreFragment mChannelMoreFragment;

	private ChannelSearchNoResultFragment mChannelSearchNoResultFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_search);
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setRightVisibility(View.INVISIBLE);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mSearch = (AutoCompleteTextView) findViewById(R.id.search_view);
		mSearchGo = (TextView) findViewById(R.id.search_go);
		mSearchContainer = (LinearLayout) findViewById(R.id.search_container);

		mAutoCompleteAdapter = new AutoCompleteAdapter(
				ChannelSearchActivity.this, R.layout.li_search_suggestion);
		mSearch.setAdapter(mAutoCompleteAdapter);
		mSearch.setOnItemClickListener(this);

		mSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					mSearchGo.performClick();
					return false;
				}
				return false;
			}
		});

		mSearchGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSearch.getText().toString() == null
						|| mSearch.getText().toString().trim().length() == 0) {
					ToastUtil.showToast(R.string.channel_no_keyword_tips);
					return;
				}
				searchChannel(mSearch.getText().toString(), null, 18);

				StatsUtil.statsReport(ChannelSearchActivity.this,
						"stats_search_channel", "serarch_key", mSearch
								.getText().toString());
				StatsUtil.statsReportByHiido("stats_search_channel",
						"serarch_key:" + mSearch.getText().toString());
				hideSoftKeybord();
			}
		});

		if (savedInstanceState != null) {
			mChannelMoreFragment = (ChannelMoreFragment) getSupportFragmentManager()
					.findFragmentByTag(FG_TAG_CHANNEL_GRID);
			mChannelSearchNoResultFragment = (ChannelSearchNoResultFragment) getSupportFragmentManager()
					.findFragmentByTag(FG_TAG_NO_RESULT);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		hideSoftKeybord();
	}

	public void hideSoftKeybord() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String keyWord = (String) adapterView.getItemAtPosition(position);
		if (keyWord != null && keyWord.trim().length() > 0) {
			searchChannel(keyWord, null, 9);
		}
	}

	public void setSearchResultsView(String key, String attachInfo,
			boolean hasMore, ArrayList<Channel> channels) {
		if (mSearchContainer.getVisibility() != View.VISIBLE) {
			mSearchContainer.setVisibility(View.VISIBLE);
		}
		if (mChannelMoreFragment == null) {
			mChannelMoreFragment = ChannelMoreFragment.newInstance(key,
					attachInfo, hasMore, channels);
		}
		if (!isDestroyed()) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			if (!mChannelMoreFragment.isAdded()) {
				transaction.add(R.id.fragment_container, mChannelMoreFragment,
						FG_TAG_CHANNEL_GRID);
			} else {
				mChannelMoreFragment.refreshSearchResults(key, hasMore,
						attachInfo, channels);
			}
			if (mChannelSearchNoResultFragment != null
					&& mChannelSearchNoResultFragment.isAdded()
					&& !mChannelSearchNoResultFragment.isHidden()) {
				transaction.hide(mChannelSearchNoResultFragment);
			}
			transaction.show(mChannelMoreFragment);
			transaction.commitAllowingStateLoss();
		}

	}

	public void setSearchNoResultsView(ArrayList<Column> columns) {
		if (mChannelSearchNoResultFragment == null) {
			mChannelSearchNoResultFragment = ChannelSearchNoResultFragment
					.newInstance(columns);
		}

		if (!isDestroyed()) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			if (!mChannelSearchNoResultFragment.isAdded()) {
				transaction.add(R.id.fragment_container,
						mChannelSearchNoResultFragment, FG_TAG_NO_RESULT);
			} else {
				mChannelSearchNoResultFragment.refreshSearchNoResult(columns);
			}
			if (mChannelMoreFragment != null && mChannelMoreFragment.isAdded()
					&& !mChannelMoreFragment.isHidden()) {
				transaction.hide(mChannelMoreFragment);
			}
			transaction.show(mChannelSearchNoResultFragment);
			transaction.commitAllowingStateLoss();
		}

	}

	public ArrayList<String> getTips(String key) {
		Map<String, ArrayList<String>> map = Preference.getInstance()
				.getSearchSuggestion();
		ArrayList<String> array = new ArrayList<String>();
		Set<String> vals = new HashSet<String>();
		Set<String> keySet = map.keySet();
		for (String item : keySet) {
			if (item.toLowerCase().contains(key.toLowerCase())) {
				vals.addAll(map.get(item));
			}
		}
		if (vals.size() > 0) {
			array.addAll(vals);
		}
		return array;
	}

	class AutoCompleteAdapter extends ArrayAdapter<String> implements
			Filterable {
		private ArrayList<String> resultList;

		public AutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList == null ? 0 : resultList.size();
		}

		@Override
		public String getItem(int index) {
			if (resultList == null || index < 0
					|| index >= resultList.size()) {
				return null;
			}
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null
							&& constraint.toString().trim().length() != 0) {
						resultList = getTips(constraint.toString());
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	private void searchChannel(final String keyWord, String attachInfo,
			int count) {
		ResponseListener<SearchChannelRsp> responseListener = new ResponseListener<SearchChannelRsp>(
				this) {
			public void onResponse(SearchChannelRsp response) {
				ArrayList<Channel> channels = response.getChannelList();
				if (channels == null || channels.size() == 0) {
					ArrayList<Column> columns = response.getColumnList();
					setSearchNoResultsView(columns);
				} else {
					setSearchResultsView(keyWord, response.getAttachInfo(),
							response.getHasMore(), channels);
				}
			}
		};
		ChannelModel
				.searchChannel(responseListener, keyWord, attachInfo, count);
	}
}
