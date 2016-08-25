package com.xuxl.apigateway.common;

import java.util.Map;

public class ApiHolder {
	
	private static Map<String,ApiDefine> registerMap;

	public static Map<String, ApiDefine> getRegisterMap() {
		return registerMap;
	}

	public static void setRegisterMap(Map<String, ApiDefine> registerMap) {
		ApiHolder.registerMap = registerMap;
	}
	
	

}
