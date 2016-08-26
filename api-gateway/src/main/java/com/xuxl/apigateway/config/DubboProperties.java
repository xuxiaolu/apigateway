package com.xuxl.apigateway.config;

import org.springframework.core.env.Environment;

public class DubboProperties {
	
	private static final String DUBBO_APPLICATION_NAME = "dubbo.name";
	
	private static final String DUBBO_ZOOKEEPER_ADDRESS = "dubbo.address";
	
	private static final String REGISTRY_CLASS = "dubbo.class";
	
	private static final String VERSION = "dubbo.version";
	
	private static final String CHECK = "dubbo.check";
	
	private static final String TIMEOUT = "dubbo.timeout";
	
	private static final String DEFAULT_BUDDO_ZOOKEEPER_ADDRESS = "zookeeper://127.0.0.1:2181";
	
	private String name;
	
	private String address;
	
	private String registryClass;
	
	private String version;
	
	private boolean isCheck;
	
	private int timeOut;
	
	public static DubboProperties load(Environment environment) {
		DubboProperties properties = new DubboProperties();
		properties.setName(environment.getProperty(DUBBO_APPLICATION_NAME,"apiw"));
		properties.setAddress(environment.getProperty(DUBBO_ZOOKEEPER_ADDRESS, DEFAULT_BUDDO_ZOOKEEPER_ADDRESS));
		properties.setCheck(environment.getProperty(CHECK, boolean.class, false));
		properties.setVersion(environment.getProperty(VERSION,"latest"));
		properties.setRegistryClass(environment.getProperty(REGISTRY_CLASS, ""));
		properties.setTimeOut(environment.getProperty(TIMEOUT, int.class, 6000));
		return properties;
	}
	

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

	public String getRegistryClass() {
		return registryClass;
	}

	public void setRegistryClass(String registryClass) {
		this.registryClass = registryClass;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
	
	

}
