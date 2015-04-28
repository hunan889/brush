package com.yy.android.gamenews.bs2.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Util {
	/**
	 * bs2对外协议authorization生成规则
	 * 
	 * @param method
	 *            http请求的方式
	 * @param filename
	 *            文件名
	 * @param expires
	 *            授权码的到期时间戳
	 * @param bucketName
	 *            bs2的bucket名称
	 * @param accessSecret
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getAuthorization(String method, String filename,
			String expires, String bucketName, String accessKey,
			String accessSecret) throws InvalidKeyException,
			NoSuchAlgorithmException {
		String content = method + '\n' + bucketName + '\n' + filename + '\n'
				+ expires + '\n';
		byte[] hmac;
		try {
			hmac = HmacSha1.getSignature(content.getBytes(),
					accessSecret.getBytes());
		} catch (InvalidKeyException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
		String temp = Base64.encode(hmac).replaceAll("\\+", "-")
				.replaceAll("/", "_");
		return accessKey + ":" + temp + ":" + expires;
	}

}
