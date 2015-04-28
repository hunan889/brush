package com.yy.android.gamenews.plugin.show;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.show.AdvInfo;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class NoticeListBannerAdapter extends ImageAdapter<AdvInfo> {

	public NoticeListBannerAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.show_notice_item_view, null);
			viewHolder = new ViewHolder();
			viewHolder.noticeTextView = (TextView) convertView.findViewById(R.id.tv_notice_title);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		AdvInfo advInfo = getItem(position);
		viewHolder.noticeTextView.setText(advInfo.getTitle());
		return convertView;
	}
	
	static class ViewHolder{
		TextView noticeTextView;
	}

}
