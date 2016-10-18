package com.xuxl.apigateway.common;

import java.util.Map;

public class ApiHolder {
	
	private static Map<String,ApiInfo> registerMap;

	public static Map<String, ApiInfo> getRegisterMap() {
		return registerMap;
	}

	public static void setRegisterMap(Map<String, ApiInfo> registerMap) {
		ApiHolder.registerMap = registerMap;
	}
	
	

}
