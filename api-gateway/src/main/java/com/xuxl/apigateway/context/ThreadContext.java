package com.xuxl.apigateway.context;

public class ThreadContext {
	
	private static final ThreadLocal<ApiContext> LOCAL = new ThreadLocal<>();
	
	private ThreadContext() {};
	
	public static ApiContext getContext() {
		ApiContext result = LOCAL.get();
		if(result == null) {
			result = new ApiContext();
			LOCAL.set(result);
			return result;
		} else {
			return result;
		}
	}
	
}