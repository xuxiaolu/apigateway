package com.xuxl.apigateway.common;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiDefine implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String className;
	
	private String apiName;
	
	@JsonIgnore
	private Method method;
	
	private ApiMethodDefine apiMethodDefine;
	
	@JsonIgnore
	private Object object;
	
	private ApiParameterDefine[] apiParameterArray;

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

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public ApiParameterDefine[] getApiParameterArray() {
		return apiParameterArray;
	}

	public void setApiParameterArray(ApiParameterDefine[] apiParameterArray) {
		this.apiParameterArray = apiParameterArray;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public ApiMethodDefine getApiMethodDefine() {
		return apiMethodDefine;
	}

	public void setApiMethodDefine(ApiMethodDefine apiMethodDefine) {
		this.apiMethodDefine = apiMethodDefine;
	}
	
}
