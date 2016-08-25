package com.xuxl.apigateway.common;

import java.io.Serializable;

public class ApiMethodDefine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Class<?> returnType;
	
	private String description;
	
	private String owner;

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
