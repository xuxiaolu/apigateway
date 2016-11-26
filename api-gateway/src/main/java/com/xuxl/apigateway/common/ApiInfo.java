package com.xuxl.apigateway.common;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String className;
	
	@JsonIgnore
	private String prefix;
	
	@JsonIgnore
	private Object proxy;
	
	@JsonIgnore
	private Method method;
	
	@JsonIgnore
	private int timeout;
	
	private String name;
	
	private ApiMethodInfo methodInfo;
	
	private ApiParameterInfo[] parameterInfos;
	
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ApiMethodInfo getMethodInfo() {
		return methodInfo;
	}

	public void setMethodInfo(ApiMethodInfo methodInfo) {
		this.methodInfo = methodInfo;
	}

	public ApiParameterInfo[] getParameterInfos() {
		return parameterInfos;
	}

	public void setParameterInfos(ApiParameterInfo[] parameterInfos) {
		this.parameterInfos = parameterInfos;
	}

}
