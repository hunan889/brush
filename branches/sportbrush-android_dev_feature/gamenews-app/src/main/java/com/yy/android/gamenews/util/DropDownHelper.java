package com.yy.android.gamenews.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yy.android.gamenews.ui.common.ImageAdapter;
import com.yy.android.sportbrush.R;

public class DropDownHelper {

	public static void showDropDownList(Context context, BaseAdapter adapter,
			View anchor, final OnDropDownClickListener listener) {
		final PopupWindow popup = new PopupWindow(context);
		popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.transparent_bg));
		popup.setOutsideTouchable(true);
		popup.setTouchable(true);
		popup.setFocusable(true);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View contentView = inflater.inflate(R.layout.dropdown_list, null);
		popup.setContentView(contentView);
		contentView.measure(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		ListView listView = (ListView) contentView.findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				popup.dismiss();
				if (listener != null) {
					DropDownItem item = (DropDownItem) parent.getAdapter()
							.getItem(position);
					listener.onClick(position, item.text);
				}
			}
		});
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setAdapter(adapter);
		popup.showAsDropDown(anchor);
	}

	public static void showDropDownList(Context context, View anchor,
			final String[] textArray, final Object[] resArray,
			int[] selectIndexes, OnDropDownClickListener listener) {

		ArrayList<DropDownItem> itemList = new ArrayList<DropDownItem>();
		for (int i = 0; i < textArray.length; i++) {
			DropDownItem item = new DropDownItem();
			if (resArray != null) {
				item.icon = resArray[i];
			}
			item.text = textArray[i];

			if (selectIndexes != null) {
				for (int index : selectIndexes) {
					if (i == index) {

						item.drawableSelected = true;
						item.textSelected = true;
					}
				}
			}
			itemList.add(item);
		}

		showDropDownList(context, anchor, itemList, listener);
	}

	public static void showDropDownList(Context context, View anchor,
			final String[] textArray, final Object[] resArray, int selectIndex,
			OnDropDownClickListener listener) {
		showDropDownList(context, anchor, textArray, resArray,
				new int[] { selectIndex }, listener);
	}

	public static void showDropDownList(Context context, View anchor,
			ArrayList<DropDownItem> itemList, OnDropDownClickListener listener) {

		if (itemList == null) {
			return;
		}
		ImageAdapter<DropDownItem> adapter = new ImageAdapter<DropDownItem>(
				context) {

			@Override
			public int getViewTypeCount() {
				return getCount();
			}

			@Override
			public int getItemViewType(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.dropdown_list_item, null);
					holder = new ViewHolder();
					holder.image = (ImageView) convertView
							.findViewById(R.id.dropdown_list_item_img);
					holder.text = (TextView) convertView
							.findViewById(R.id.dropdown_list_item_text);
					holder.divider = convertView
							.findViewById(R.id.dropdown_list_item_divider);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				DropDownItem item = getItem(position);
				if (item != null) {

					Log.d("", "[getView] convertView = " + convertView
							+ ", position = " + position + ", icon = "
							+ item.icon);

					holder.text.setText(item.text);
					Object img = item.icon;
					if (img == null) {
						holder.image.setVisibility(View.GONE);
					} else {
						if (img instanceof Integer) {
							holder.image.setImageResource((Integer) img);
						} else if (img instanceof String) {
							displayImage(String.valueOf(img), holder.image);
						}
					}

					if (item.drawableSelected) {
						holder.image.setSelected(true);
					} else {
						holder.image.setSelected(false);
					}
					if (item.textSelected) {
						holder.text.setSelected(true);
					} else {
						holder.text.setSelected(false);
					}
				}

				if (position == getCount() - 1) {
					holder.divider.setVisibility(View.GONE);
				} else {
					holder.divider.setVisibility(View.VISIBLE);
				}
				return convertView;
			}
		};
		adapter.setDataSource(itemList);

		showDropDownList(context, adapter, anchor, listener);
	}

	private static class ViewHolder {
		ImageView image;
		TextView text;
		View divider;
	}

	public static class DropDownItem {
		String text;
		Object icon;
		boolean drawableSelected;
		boolean textSelected;

		public DropDownItem() {
			this("", 0, false, false);
		}

		public DropDownItem(String text, Object icon, boolean drawableSelected,
				boolean textSelected) {
			this.text = text;
			this.icon = icon;
			this.drawableSelected = drawableSelected;
		}
	}

	public interface OnDropDownClickListener {
		public void onClick(int position, String text);
	}
}
