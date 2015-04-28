package com.yy.android.gamenews.ui.channeldetail;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.gamenews.Channel;
import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class ChannelListAdapter extends ImageAdapter<Channel> {

	public ChannelListAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Channel item = getItem(position);
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.channel_list_item, null);
			holder.brand_detail_item = (LinearLayout) convertView
					.findViewById(R.id.brand_detail_item);
			holder.brand_detail_img = (ImageView) convertView
					.findViewById(R.id.brand_detail_img);
			holder.brand_detail_name = (TextView) convertView
					.findViewById(R.id.brand_detail_name);
			holder.brand_detail_price = (TextView) convertView
					.findViewById(R.id.brand_detail_price);
			holder.channel_list_desc = (TextView) convertView
					.findViewById(R.id.channel_list_desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.brand_detail_name.setText(item.getName());

		Resources res = getContext().getResources();

		String subfix = res.getString(R.string.channel_number);
		SpannableString str = new SpannableString(item.getCollectNumber()
				+ subfix);

		int endPos = str.length() - subfix.length();
		str.setSpan(
				new ForegroundColorSpan(res.getColor(R.color.main_menu_text)),
				0, endPos, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

		str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, endPos,
				SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

		holder.brand_detail_price.setText(str);

		String summary = item.getSummary();
		if (TextUtils.isEmpty(summary)) {
			holder.channel_list_desc.setVisibility(View.GONE);
		} else {
			holder.channel_list_desc.setVisibility(View.VISIBLE);
			holder.channel_list_desc.setText(summary);
		}

		displayImage(item.image, holder.brand_detail_img);

		return convertView;
	}

	class ViewHolder {
		LinearLayout brand_detail_item;

		ImageView brand_detail_img;

		TextView brand_detail_name;

		TextView brand_detail_price;
		TextView channel_list_desc;

	}

}