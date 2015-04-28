package com.duowan.android.base;

public class Tea {

	public native byte[] encrypt2(byte[] key, byte[] in);

	public native byte[] decrypt2(byte[] key, byte[] in);

	static {
		System.loadLibrary("tea");
	}
}
