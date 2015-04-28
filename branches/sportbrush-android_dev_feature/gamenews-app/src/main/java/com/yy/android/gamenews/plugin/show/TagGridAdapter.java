package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.show.ImageType;
import com.duowan.show.PicInfo;
import com.duowan.show.Tag;
import com.yy.android.gamenews.event.MainTabEvent;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.gamenews.util.MainTabStatsUtil;
import com.yy.android.sportbrush.R;

public class TagGridAdapter  extends ImageAdapter<Tag> {

	private Context mContext;
	
	public TagGridAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mDataSource == null ? 0 : (mDataSource.size() / 2 + mDataSource.size() % 2);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.show_tag_gridview_item_view, null);
			viewHolder = new ViewHolder();
			viewHolder.firstItemView = convertView.findViewById(R.id.first_tag_item);
			viewHolder.firstTagImageView = (ImageView) convertView
					.findViewById(R.id.iv_first_tag_icon);
			viewHolder.firstTagNameTextView = (TextView) convertView
					.findViewById(R.id.tv_first_tag_name);
			viewHolder.secondItemView = convertView.findViewById(R.id.second_tag_item);
			viewHolder.secondTagImageView = (ImageView) convertView
					.findViewById(R.id.iv_second_tag_icon);
			viewHolder.secondTagNameTextView = (TextView) convertView
					.findViewById(R.id.tv_second_tag_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Tag firstTag = getItem(2 * position);
		viewHolder.firstTagNameTextView.setText(firstTag.getName());
		PicInfo picInfo = TopicUtils.getImageFromTag(firstTag, ImageType._IMAGE_TYPE_NORMAL);
		if(picInfo != null){
			displayImage(picInfo.getUrl(), viewHolder.firstTagImageView);
		}
		
		if(2 * position + 1 < mDataSource.size()){
			viewHolder.secondItemView.setVisibility(View.VISIBLE);
			Tag secondTag = getItem(2 * position + 1);
			viewHolder.secondTagNameTextView.setText(secondTag.getName());
			PicInfo picInfo1 = TopicUtils.getImageFromTag(secondTag, ImageType._IMAGE_TYPE_NORMAL);
			if(picInfo1 != null){
				displayImage(picInfo1.getUrl(), viewHolder.secondTagImageView);
			}
		}else{
			viewHolder.secondItemView.setVisibility(View.INVISIBLE);
		}
		setItemClickListener(viewHolder, position);
		return convertView;
	}
	
	private void setItemClickListener(ViewHolder viewHolder, final int position){
		viewHolder.firstItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(2 * position);
			}
		});
		viewHolder.secondItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(2 * position + 1);
			}
		});
	}
	
	public void onItemClick(int position) {
		Tag tag = getItem(position);
		TopicListActivity.startTopicListActivity(mContext, tag);
		
		MainTabStatsUtil.statistics(mContext, MainTabEvent.TAB_COMMUNITY,
				MainTabEvent.CLICK_HOT_TAG,tag.getName());
	}
	
	static class ViewHolder {
		View firstItemView;
		ImageView firstTagImageView;
		TextView firstTagNameTextView;
		View secondItemView;
		ImageView secondTagImageView;
		TextView secondTagNameTextView;
	}
}
