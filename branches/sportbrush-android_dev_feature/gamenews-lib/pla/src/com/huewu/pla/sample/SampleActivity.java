package com.huewu.pla.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huewu.pla.R;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;

public class SampleActivity extends Activity {

	private class MySimpleAdapter extends ArrayAdapter<String> {

		public MySimpleAdapter(Context context, int layoutRes) {
			super(context, layoutRes, android.R.id.text1);
		}
	}

	private class MyBaseAdapter extends BaseAdapter {

		private List<String> mDataSource;

		public void setDataSource(List<String> ds) {
			mDataSource = ds;
			notifyDataSetChanged();
		}
		
		public void addAll(List<String> ds) {
			if(mDataSource == null) {
				mDataSource = new ArrayList<String>();
			}
			mDataSource.addAll(ds);
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDataSource.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDataSource.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			String item = (String) getItem(position);

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.sample_item,
						null);
				holder = new ViewHolder();

				holder.mBtn = (Button) convertView.findViewById(R.id.like);
				holder.mImageView = (ImageView) convertView
						.findViewById(R.id.thumbnail);
				holder.mTextView = (TextView) convertView
						.findViewById(android.R.id.text1);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			holder.mBtn.setText("like" + position);
			// holder.mImageView.s
			holder.mTextView.setText(item);
			return convertView;
		}

	}

	private static class ViewHolder {
		private TextView mTextView;
		private ImageView mImageView;
		private Button mBtn;
	}

	private MultiColumnListView mAdapterView = null;
	private MyBaseAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample_act);
		// mAdapterView = (PLA_AdapterView<Adapter>) findViewById(R.id.list);

		mAdapterView = (MultiColumnListView) findViewById(R.id.list);

		// {
		// for( int i = 0; i < 3; ++i ){
		// //add header view.
		// TextView tv = new TextView(this);
		// tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// tv.setText("Hello Header!! ........................................................................");
		// mAdapterView.addHeaderView(tv);
		// }
		// }
		// {
		// for( int i = 0; i < 3; ++i ){
		// //add footer view.
		// TextView tv = new TextView(this);
		// tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// tv.setText("Hello Footer!! ........................................................................");
		// mAdapterView.addFooterView(tv);
		// }
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1001, 0, "Load More Contents");
		menu.add(Menu.NONE, 1002, 0, "Launch Pull-To-Refresh Activity");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1001: {
			int startCount = mAdapter.getCount();
			List<String> stringList = new ArrayList<String>();
			for (int i = 0; i < 100; ++i) {
				// generate 100 random items.

				StringBuilder builder = new StringBuilder();
				builder.append("Hello!![");
				builder.append(startCount + i);
				builder.append("] ");

				char[] chars = new char[mRand.nextInt(100)];
				Arrays.fill(chars, '1');
				builder.append(chars);
				stringList.add(builder.toString());
			}
			mAdapter.addAll(stringList);
		}
			break;
		case 1002: {
			Intent intent = new Intent(this, PullToRefreshSampleActivity.class);
			startActivity(intent);
		}
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initAdapter();
		mAdapterView.setAdapter(mAdapter);
		mAdapterView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				System.out.println();

			}
		});
		// mAdapterView.setAdapter(mAdapter);
	}

	private Random mRand = new Random();

	private void initAdapter() {
		mAdapter = new MyBaseAdapter();

		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < 30; ++i) {
			// generate 30 random items.

			StringBuilder builder = new StringBuilder();
			builder.append("Hello!![");
			builder.append(i);
			builder.append("] ");

			char[] chars = new char[mRand.nextInt(500)];
			Arrays.fill(chars, '1');
			builder.append(chars);
			stringList.add(builder.toString());
		}

		mAdapter.setDataSource(stringList);
	}

}// end of class
