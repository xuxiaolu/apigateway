package com.xuxl.apigateway.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.common.exception.ServiceException;

@Component
public class ServiceBean implements ApplicationContextAware {
	
	private ApplicationContext context;
	
	@SuppressWarnings("unchecked")
	public <T> T getService(String name, Class<T> clazz) throws ServiceException {
		ReferenceConfig<T> config = context.getBean(name,ReferenceConfig.class);
		if(config == null) {
			throw new ServiceException(SystemReturnCode.DUBBO_SERVICE_NOTFOUND_ERROR);
		} else {
			return config.get();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

}
