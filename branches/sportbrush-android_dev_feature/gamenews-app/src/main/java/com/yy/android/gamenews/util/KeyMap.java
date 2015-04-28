package com.yy.android.gamenews.util;

import java.util.ArrayList;
import java.util.List;

public class KeyMap<K, V>
{
	private List<K> keyList = new ArrayList<K>();
	private List<V> valueList = new ArrayList<V>();
	public int size() {
		return keyList.size();
	}
	public boolean isEmpty() {
		return keyList.size() == 0;
	}
	public boolean containsKey(Object key) {
		return keyList.contains(key);
	}
	public boolean containsValue(Object value) {
		return valueList.contains(value);
	}
	public V get(Object key) {
		int index = keyList.indexOf(key);
		return valueList.get(index);
	}
	public V put(K key, V value) {
		if(keyList.contains(key)) {
			int index = keyList.indexOf(key);
			V obj = valueList.get(index);
			valueList.set(index, value);
			return obj;
		} else {
			keyList.add(key);
			valueList.add(value);
		}
		return null;
	}
	public V remove(Object key) {
		if(keyList.contains(key)) {
			int index = keyList.indexOf(key);
			V obj = valueList.get(index);
			valueList.remove(obj);
			keyList.remove(key);
			return obj;
		}
		return null;
	}
	
	public void clear() {
		keyList.clear();
		valueList.clear();
	}
	
	public List<K> keySet() {
		return keyList;
	}
	
	@Override
	public String toString() {

		String msg = "";
		for(int i = 0; i < keyList.size(); i++) {
			K key = keyList.get(i);
			V value = valueList.get(i);
			msg += "[K:" + key + ", V:" + value + "]";
		}
		
		return msg;
	}
	
}
