package com.yy.android.gamenews.plugin.gamerace;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.BaseFragment;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class UnionTabFragment extends BaseFragment implements OnClickListener{
 
	protected final static int TOP_TAB = 0;
	protected final static int OTHER_TAB = 1;
	protected final static String TAB_KEY = "tab_key";
	
	private View topUnionView;
	private View otherUnionView;
	
	private UnionListFragment topUnionListFragment;
	private UnionListFragment otherUnionListFragment;
	
	private int currentTabIndex = -1;
	
	private FragmentTransaction fragmentTransaction;
	
	public static UnionTabFragment newInstance(){
		UnionTabFragment unionTabFragment = new UnionTabFragment();
		return unionTabFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.union_tab, container, false);
		
		topUnionView = view.findViewById(R.id.ll_top_ten_union);
		otherUnionView = view.findViewById(R.id.ll_other_union);
		
		topUnionView.setTag(TOP_TAB);
		otherUnionView.setTag(OTHER_TAB);
		
		topUnionView.setOnClickListener(this);
		otherUnionView.setOnClickListener(this);
		
		topUnionView.performClick();
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		int index = (Integer) v.getTag();
		switchTab(index);
	}
	
	private void switchTab(int index){
		if(currentTabIndex == index){
			return;
		}
		String eventId = MainTabEvent.TAB_GAMERACE_INFO;
		String key = null;
		String value = null;
		currentTabIndex = index;
		fragmentTransaction = getChildFragmentManager().beginTransaction();
		if(topUnionListFragment == null){
			 topUnionListFragment = UnionListFragment.newInstance(TOP_TAB);
			 fragmentTransaction.add(R.id.fragment_container, topUnionListFragment, String.valueOf(TOP_TAB));
		}
		if(otherUnionListFragment == null){
			otherUnionListFragment = UnionListFragment.newInstance(OTHER_TAB);
			fragmentTransaction.add(R.id.fragment_container, otherUnionListFragment, String.valueOf(OTHER_TAB));
		}
		
		if(currentTabIndex == TOP_TAB ){
			fragmentTransaction.show(topUnionListFragment).hide(otherUnionListFragment);
			topUnionView.setSelected(true);
			otherUnionView.setSelected(false);
			key = MainTabEvent.INTO_TOP_UNION_TAB;
			value = MainTabEvent.INTO_TOP_UNION_TAB_NAME;
		}else{
			fragmentTransaction.show(otherUnionListFragment).hide(topUnionListFragment);
			otherUnionView.setSelected(true);
			topUnionView.setSelected(false);
			key = MainTabEvent.INTO_OTHER_UNION_TAB;
			value = MainTabEvent.INTO_OTHER_UNION_TAB_NAME;
		}
		fragmentTransaction.commit();
		
		MainTabStatsUtil.statistics(getActivity(), eventId, key, value);
	}
}
