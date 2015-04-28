package com.yy.android.gamenews.util;

import com.yy.android.gamenews.util.thread.BackgroundTask;

public abstract class CacheManager {

	public interface CacheOperationListener {
		public void onReadFinish(int tag, Object key, Object value);

		public void onWriteFinish(int tag, Object key);
	}

	public void read(int tag, Object key) {
		new ReadTask().execute(tag, key);
	}

	public void write(int tag, Object key, Object o) {
		new WriteTask().execute(tag, key, o);
	}

	protected abstract Object readImpl(Object key);

	protected abstract void writeImpl(Object key, Object o);

	private CacheOperationListener mCacheOperationListener;

	public void setCacheOperationListener(CacheOperationListener listener) {
		mCacheOperationListener = listener;
	}

	private class ReadTask extends BackgroundTask<Object, Void, Object> {
		private int tag;
		private Object key;

		@Override
		protected Object doInBackground(Object... params) {

			tag = (Integer) params[0];
			Object key = params[1];
			Object value = readImpl(key);

			return value;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mCacheOperationListener != null) {
				mCacheOperationListener.onReadFinish(tag, key, result);
			}

			super.onPostExecute(result);
		}
	}

	private class WriteTask extends BackgroundTask<Object, Void, Void> {
		private int tag;
		private Object key;
		private Object value;

		@Override
		protected Void doInBackground(Object... params) {

			tag = (Integer) params[0];
			key = params[1];
			value = params[2];

			writeImpl(key, value);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCacheOperationListener != null) {
				mCacheOperationListener.onReadFinish(tag, key, result);
				mCacheOperationListener.onWriteFinish(tag, key);
			}

			super.onPostExecute(result);
		}
	}
}
