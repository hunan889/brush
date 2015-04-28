package com.yy.android.gamenews.plugin.gamerace;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.gamenews.RaceTopicInfo;
import com.duowan.gamenews.RaceTopicType;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class WonderfulRaceAdapter extends ImageAdapter<RaceTopicInfo> {
	private Context mContext;

	public WonderfulRaceAdapter(Context context) {
		super(context);
		this.mContext = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.wonderful_race_body_fragment, null);
			holder.race_img = (ImageView) convertView
					.findViewById(R.id.wondlerful_race_img);
			holder.race_flag = (ImageView) convertView
					.findViewById(R.id.wondlerful_race_union_flag);
			holder.race_title = (TextView) convertView
					.findViewById(R.id.wondlerful_race_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RaceTopicInfo item = getItem(position);
		displayImage(item.getImg(), holder.race_img);
		if (item.getRaceTopicType() == RaceTopicType._RACE_TYPE_UNION) {
			holder.race_flag
					.setImageResource(R.drawable.wondlerful_race_union_game);
		} else if (item.getRaceTopicType() == RaceTopicType._RACE_TYPE_PERSON) {
			holder.race_flag
					.setImageResource(R.drawable.wondlerful_race_person_game);
		}
		holder.race_title.setText(item.getName());
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item.getRaceTopicType() == RaceTopicType._RACE_TYPE_UNION) {
					// 跳转工会赛
					UnionRaceTopicActivity.startRaceTopicActivity(mContext,
							item, UnionRaceTopicActivity._RACE_TOPIC);
				} else if (item.getRaceTopicType() == RaceTopicType._RACE_TYPE_PERSON) {
					// 跳转个人赛
					PersonalRaceTopicActivity.startActivity(mContext, item.getId());
				}
				MainTabStatsUtil.statistics(mContext,
						MainTabEvent.TAB_GAMERACE_INFO,
						MainTabEvent.CLICK_RACE_TOPIC, item.getName());
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		ImageView race_img;
		ImageView race_flag;
		TextView race_title;
	}
}
