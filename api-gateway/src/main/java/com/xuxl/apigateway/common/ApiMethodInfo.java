package com.xuxl.apigateway.common;

import java.io.Serializable;

public class ApiMethodInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String desc;
	
	private String owner;
	
	private String type;
	
	private ApiReturnInfo returnInfo;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public ApiReturnInfo getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(ApiReturnInfo returnInfo) {
		this.returnInfo = returnInfo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
