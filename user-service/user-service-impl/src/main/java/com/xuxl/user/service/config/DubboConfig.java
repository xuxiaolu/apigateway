package com.xuxl.user.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;

@Configuration
public class DubboConfig {
	
	@Value("${dubbo.zookeeper}")
	private String zookeeperAddress;
	
	@Value("${dubbo.port}")
	private Integer dubboPort;
	
	private static final String PACKAGE = "com.xuxl.user.service.impl";
	
		
	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("user-service-provider");
		applicationConfig.setOwner("xuxl");
		return applicationConfig;
	}
	
	@Bean
	public RegistryConfig registryConfig() {
		return new RegistryConfig(zookeeperAddress);
	}
	
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setPort(dubboPort);
		return protocolConfig;
	}
	
	@Bean
	public static AnnotationBean annotationBean() {
		AnnotationBean annotationBean = new AnnotationBean();
		annotationBean.setPackage(PACKAGE);
		return annotationBean;
	}
	

}
