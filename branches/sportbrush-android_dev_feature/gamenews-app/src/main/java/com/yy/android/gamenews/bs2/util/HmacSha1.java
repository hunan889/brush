package com.yy.android.gamenews.bs2.util;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha1 {
    
	private static final String HMAC_SHA1 = "HmacSHA1";

	public static byte[] getSignature(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {
		SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(data);
		return rawHmac;
	}
}
