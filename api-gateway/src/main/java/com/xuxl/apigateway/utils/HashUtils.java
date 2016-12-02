package com.xuxl.apigateway.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

public class HashUtils {
	
	private static int HASH_ITERATIONS = 2;

	public static String digest(String key, String salt) {
		SimpleHash hash = new SimpleHash("MD5", key, salt, HASH_ITERATIONS);
		return hash.toHex();
	}

}
