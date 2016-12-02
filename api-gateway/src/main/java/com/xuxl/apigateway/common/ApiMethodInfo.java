package com.xuxl.apigateway.common;

import java.io.Serializable;

import com.xuxl.common.annotation.http.api.ApiOperation.SecurityType;

public class ApiMethodInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String desc;
	
	private String owner;
	
	private String type;
	
	private ApiReturnInfo returnInfo;
	
	private SecurityType security;

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

	public SecurityType getSecurity() {
		return security;
	}

	public void setSecurity(SecurityType security) {
		this.security = security;
	}

}
