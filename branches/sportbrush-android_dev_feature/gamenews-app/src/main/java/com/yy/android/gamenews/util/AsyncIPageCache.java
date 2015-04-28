package com.yy.android.gamenews.util;

import com.yy.android.gamenews.util.thread.BackgroundTask;

public class AsyncIPageCache {

	private static AsyncIPageCache mInstance = new AsyncIPageCache();

	private IPageCache mPageCache;

	private AsyncIPageCache() {
		mPageCache = new IPageCache();
	}

	public static AsyncIPageCache getInstance() {
		return mInstance;
	}

	public class WriteCacheTask<T> extends BackgroundTask<Object, Void, Void> {
		private OnCacheListener<T> mOnCacheListener;

		public WriteCacheTask(OnCacheListener<T> listener) {
			mOnCacheListener = listener;
		}

		@Override
		protected Void doInBackground(Object... params) {

			String key = (String) params[0];
			T value = (T) params[1];
			int duration = (Integer) params[2];
			boolean isJceObject = (Boolean) params[3];

			write(key, value, duration, isJceObject);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mOnCacheListener != null) {
				mOnCacheListener.onWrite();
			}
			super.onPostExecute(result);
		}
	}

	public <T> void writeAsync(String key, T value, int duration,
			boolean isJceObject, OnCacheListener<T> listener) {
		new WriteCacheTask<T>(listener).execute(key, value, duration,
				isJceObject);
	}

	public <T> void readAsync(String key, T object, boolean isJceObject, OnCacheListener<T> listener) {
		new ReadCacheTask<T>(listener).execute(key, object, isJceObject);
	}

	public <T> void write(String key, T value, int duration, boolean isJceObject) {
		if (isJceObject) {
			mPageCache.setJceObject(key, value, duration);
		} else {
			mPageCache.setObject(key, value, duration);
		}
	}

	public <T> T read(String key, T object, boolean isJceObject) {
		if (isJceObject) {
			return mPageCache.getJceObject(key, object);
		} else {
			return mPageCache.getObject(key);
		}
	}

	public interface OnCacheListener<T> {
		public void onRead(T value);

		public void onWrite();
	}

	public class ReadCacheTask<T> extends BackgroundTask<Object, Void, T> {
		private OnCacheListener<T> mOnCacheListener;

		public ReadCacheTask(OnCacheListener<T> listener) {
			mOnCacheListener = listener;
		}

		@Override
		protected T doInBackground(Object... params) {

			String key = (String) params[0];
			T object = (T) params[1];
			boolean isJceObject = (Boolean) params[2];

			return read(key, object, isJceObject);
		}

		@Override
		protected void onPostExecute(T result) {
			if (mOnCacheListener != null) {
				mOnCacheListener.onRead(result);
			}
			super.onPostExecute(result);
		}
	}
}
