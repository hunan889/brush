package com.duowan.android.base.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * @author yy:909012690@lishaoqi
 * @version 创建时间：2014-3-11 下午2:31:10
 */
public abstract class ListAdapter<T> extends BaseAdapter {

	private final Object mLock = new Object();
	private List<T> mObjects;

	protected final Context mContext;
	protected final LayoutInflater mInflater;

	public ListAdapter(Context context) {
		this(context, new ArrayList<T>());
	}

	public ListAdapter(Context context, List<T> objects) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mObjects = objects;
	}

	public List<T> getObjects() {
		return mObjects;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mObjects == null ? 0 : mObjects.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return mObjects == null ? null : mObjects.get(position);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void add(T object) {
		if (object == null)
			return;
		synchronized (mLock) {
			mObjects.add(object);
		}
	}

	/**
	 * Adds the specified Collection at the end of the array.
	 * 
	 * @param collection
	 *            The Collection to add at the end of the array.
	 */
	public void addAll(Collection<? extends T> collection) {
		if (collection == null || collection.isEmpty())
			return;
		synchronized (mLock) {
			mObjects.addAll(collection);
		}
		notifyDataSetChanged();
	}

	/**
	 * Adds the specified items at the end of the array.
	 * 
	 * @param items
	 *            The items to add at the end of the array.
	 */
	public void addAll(T... items) {
		if (items == null || items.length == 0)
			return;
		synchronized (mLock) {
			Collections.addAll(mObjects, items);
		}
		notifyDataSetChanged();
	}

	/**
	 * Inserts the specified object at the specified index in the array.
	 * 
	 * @param object
	 *            The object to insert into the array.
	 * @param index
	 *            The index at which the object must be inserted.
	 */
	public void insert(T object, int index) {
		if (object == null)
			return;
		synchronized (mLock) {
			mObjects.add(index, object);
		}
		notifyDataSetChanged();
	}

	/**
	 * Removes the specified object from the array.
	 * 
	 * @param object
	 *            The object to remove.
	 */
	public void remove(T object) {
		synchronized (mLock) {
			mObjects.remove(object);
		}
		notifyDataSetChanged();
	}

	public void remove(int index) {
		synchronized (mLock) {
			mObjects.remove(index);
		}
		notifyDataSetChanged();
	}

	/**
	 * Remove all elements from the list.
	 */
	public void clear() {
		synchronized (mLock) {
			mObjects.clear();
		}
		notifyDataSetChanged();
	}
}
