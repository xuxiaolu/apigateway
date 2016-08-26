package com.xuxl.apigateway.listener;

import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.xuxl.apigateway.config.DubboProperties;

public class DubboRegisterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboRegisterInitializer.class);
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		DubboProperties properties = DubboProperties.load(applicationContext.getEnvironment());
		String applicationConfigBeanName = "applicationConfig";
		ApplicationConfig applicationConfig = new ApplicationConfig(properties.getName());
		applicationContext.getBeanFactory().registerSingleton(applicationConfigBeanName, applicationConfig);
		logger.info("register applicationConfig success");
		
		String registryConfigBeanName = "registryConfig";
		RegistryConfig registryConfig = new RegistryConfig(properties.getAddress());
		registryConfig.setProtocol("dubbo");
		applicationContext.getBeanFactory().registerSingleton(registryConfigBeanName, registryConfig);
		logger.info("register registryConfig success");
		
		String classNames = properties.getRegistryClass();
		String[] classArray = classNames.split(";");
		Stream.of(classArray).forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
				referenceConfig.setApplication(applicationConfig);
				referenceConfig.setTimeout(properties.getTimeOut());
				referenceConfig.setVersion(properties.getVersion());
				referenceConfig.setRegistry(registryConfig);
				referenceConfig.setInterface(clazz);
				referenceConfig.setCheck(properties.isCheck());
				referenceConfig.setRetries(0);
				applicationContext.getBeanFactory().registerSingleton(className, referenceConfig.get());
				logger.info(String.format("register %s success", className));
			} catch (ClassNotFoundException e) {
				logger.info(String.format("register %s fail", className),e);
			}
		});
	}
}
