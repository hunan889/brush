package com.yy.android.gamenews.ui.view.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FrameFragmentLayout extends RelativeLayout {

	public static final int CONTAINER_ID = 0xabcdef;
	public static final int FOOTER_ID = 0xfedcba;
	private LinearLayout mFooter;
	private FrameLayout mContainer;

	public FrameFragmentLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FrameFragmentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FrameFragmentLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContainer = new FrameLayout(getContext());
		mContainer.setId(CONTAINER_ID);
		// setId(CONTAINER_ID);

		LayoutParams containerParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		addView(mContainer, containerParams);

		mFooter = new LinearLayout(getContext());
		mFooter.setId(FOOTER_ID);
		LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(mFooter, footerParams);

		containerParams.addRule(RelativeLayout.ABOVE, FOOTER_ID);
		footerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

	}

	private List<FrameFragmentItem> mItemList = new ArrayList<FrameFragmentItem>();

	public void show(int index) {
		if (index >= mItemList.size() || index < 0) {
			return;
		}
		FragmentTabTransaction transaction = FragmentTabTransaction
				.beginTransaction((FragmentActivity) getContext());

		for (int i = 0; i < mItemList.size(); i++) {

			FrameFragmentItem item = mItemList.get(i);
			if (i == index) {
				transaction.show(item);
				mFooter.getChildAt(i).setSelected(true);
			} else {
				transaction.hide(item);
				mFooter.getChildAt(i).setSelected(false);
			}
		}

		transaction.commit();
	}

	public void hide(int index) {
		if (index >= mItemList.size() || index < 0) {
			return;
		}
		FragmentTabTransaction transaction = FragmentTabTransaction
				.beginTransaction((FragmentActivity) getContext());

		mFooter.getChildAt(index).setSelected(false);
		transaction.hide(mItemList.get(index));
		transaction.commit();
	}

	public void restore(List<FrameFragmentItem> itemList) {
		if (itemList == null) {
			return;
		}

		for (final FrameFragmentItem item : itemList) {
			mItemList.add(item);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT);
			params.weight = 1;

			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (item.isSelectable()) {
						int index = mItemList.indexOf(item);
						show(mItemList.indexOf(item));
						if (mOnFrameChangeListener != null) {
							mOnFrameChangeListener.onChange(index, item);
						}
					}
				}
			});
			mFooter.addView(item.getButton(), params);
		}
	}

	public void add(List<FrameFragmentItem> itemList) {

		if (itemList == null) {
			return;
		}

		FragmentTabTransaction transaction = FragmentTabTransaction
				.beginTransaction((FragmentActivity) getContext());
		for (final FrameFragmentItem item : itemList) {
			mItemList.add(item);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT);
			params.weight = 1;

			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (item.isSelectable()) {
						int index = mItemList.indexOf(item);
						show(mItemList.indexOf(item));
						if (mOnFrameChangeListener != null) {
							mOnFrameChangeListener.onChange(index, item);
						}
					}
				}
			});
			mFooter.addView(item.getButton(), params);

			transaction.add(item);
		}
		transaction.commit();
	}

	public void add(FrameFragmentItem[] itemArray) {
		if (itemArray == null) {
			return;
		}

		add(Arrays.asList(itemArray));
	}

	public void add(final FrameFragmentItem item) {
		add(new FrameFragmentItem[] { item });
	}

	public void remove(FrameFragmentItem item) {
		FragmentTabTransaction transaction = FragmentTabTransaction
				.beginTransaction((FragmentActivity) getContext());
		mItemList.remove(item);
		transaction.remove(item).commit();
		mFooter.removeView(item.getButton());
	}

	public List<FrameFragmentItem> getItemList() {
		return mItemList;
	}

	public void setOnFrameChangeListener(OnFrameChangeListener listener) {
		mOnFrameChangeListener = listener;
	}

	private OnFrameChangeListener mOnFrameChangeListener;

	public interface OnFrameChangeListener {
		/**
		 * 当tab选中态发生变化时调用
		 * 
		 * @param index
		 *            当前选中的tab
		 * @param item
		 *            选中的tab对象
		 */
		public void onChange(int index, FrameFragmentItem item);
	}
}
