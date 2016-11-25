package com.xuxl.apigateway.common;

import java.io.Serializable;
import java.util.List;

public class DocResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String group;
	
	private List<ApiInfo> apiInfos;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<ApiInfo> getApiInfos() {
		return apiInfos;
	}

	public void setApiInfos(List<ApiInfo> apiInfos) {
		this.apiInfos = apiInfos;
	}

	public DocResponse(String group, List<ApiInfo> apiInfos) {
		this.group = group;
		this.apiInfos = apiInfos;
	}
	
	
	
	
}
