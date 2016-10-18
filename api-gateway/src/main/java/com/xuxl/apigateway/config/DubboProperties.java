package com.xuxl.apigateway.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.dubbo")
public class DubboProperties {
	
	private String name;
	
	private String address;
	
	private Map<String,String> registryMap;
	
	private boolean isCheck;
	
	private int timeOut;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}


	public int getTimeOut() {
		return timeOut;
	}


	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public Map<String, String> getRegistryMap() {
		return registryMap;
	}

	public void setRegistryMap(Map<String, String> registryMap) {
		this.registryMap = registryMap;
	}

}
