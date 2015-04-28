package com.yy.android.gamenews.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yy.android.gamenews.ui.common.ImageAdapter;

/**
 * 为需要使用viewholder的adapter进行一层封装
 * @author liuchaoqun
 *
 * @param <E> 对象类型
 * @param <H> ViewHolder类型
 */
public abstract class ViewHolderAdapter<E, H> extends ImageAdapter<E> {
	public ViewHolderAdapter(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		H holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					getViewResourceId(getItemViewType(position)), null);
			holder = getHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (H) convertView.getTag();
		}

		E item = getItem(position);
		updateHolder(item, holder);
		return convertView;
	}

	protected abstract int getViewResourceId(int type);

	protected abstract H getHolder(View convertView);

	protected abstract void updateHolder(E item, H holder);
}
