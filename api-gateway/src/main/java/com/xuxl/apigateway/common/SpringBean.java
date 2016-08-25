package com.xuxl.apigateway.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ReferenceConfig;

@Component
public class SpringBean implements ApplicationContextAware {
	
	private ApplicationContext context;
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String name, Class<T> clazz) {
		ReferenceConfig<T> config = context.getBean(name,ReferenceConfig.class);
		if(config == null) {
			return null;
		} else {
			return config.get();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

}
