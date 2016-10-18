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
	
	private String apiName;
	
	@JsonIgnore
	private Method method;
	
	
	private ApiMethodInfo apiMethodInfo;
	
	@JsonIgnore
	private Object proxy;
	
	private ApiParameterInfo[] apiParameterInfos;
	
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

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public ApiParameterInfo[] getApiParameterInfos() {
		return apiParameterInfos;
	}

	public void setApiParameterInfos(ApiParameterInfo[] apiParameterInfos) {
		this.apiParameterInfos = apiParameterInfos;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public ApiMethodInfo getApiMethodInfo() {
		return apiMethodInfo;
	}

	public void setApiMethodInfo(ApiMethodInfo apiMethodInfo) {
		this.apiMethodInfo = apiMethodInfo;
	}
	
}
