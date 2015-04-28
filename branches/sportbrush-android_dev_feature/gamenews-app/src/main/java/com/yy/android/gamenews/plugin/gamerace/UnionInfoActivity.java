package com.yy.android.gamenews.plugin.gamerace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.duowan.gamenews.UnionInfo;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.BaseActivity;
import com.yy.android.gamenews.ui.view.ActionBar;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class UnionInfoActivity extends BaseActivity {
	
	private static final String TAG = UnionInfoActivity.class.getSimpleName();
	public static final String UNION = "union";
	private ActionBar mActionBar;
	private UnionInfoFragment unionInfoFragment;
	
	public static void startActivity(Context context, UnionInfo unionInfo) {
		Intent intent = new Intent(context, UnionInfoActivity.class);
		intent.putExtra(UNION, unionInfo);
		context.startActivity(intent);
		
		MainTabStatsUtil.statistics(context,
				MainTabEvent.INTO_UNION_DETAIL, "union", unionInfo.getName());
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.union_info_view);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setOnLeftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		UnionInfo unionInfo = (UnionInfo) getIntent().getSerializableExtra(UNION);
		
		mActionBar.setTitle(unionInfo.getName());
		if (savedInstanceState != null) {
			unionInfoFragment = (UnionInfoFragment) getSupportFragmentManager().findFragmentByTag(TAG);
		} else {
			unionInfoFragment = UnionInfoFragment.newInstance(unionInfo);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, unionInfoFragment, TAG).commit();
		}
	}
}
