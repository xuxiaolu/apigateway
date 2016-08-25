package com.xuxl.apigateway.config;

import java.util.stream.Stream;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboConfiguration.class);
	
	private static final String PROTOCOL_NAME = "dubbo";

	private Environment environment;
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		
		DubboProperties properties = DubboProperties.load(environment);
		
		String applicationConfigBeanName = "applicationConfig";
		BeanDefinitionBuilder applicationConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(ApplicationConfig.class);
		applicationConfigBuilder.addPropertyValue("name", properties.getName());
		registry.registerBeanDefinition(applicationConfigBeanName , applicationConfigBuilder.getRawBeanDefinition());
		logger.info("register applicationConfig success");
		
		String registryConfigBeanName = "registryConfig";
		BeanDefinitionBuilder registryConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
		registryConfigBuilder.addPropertyValue("address", properties.getAddress()).addPropertyValue("protocol", PROTOCOL_NAME);
		registry.registerBeanDefinition(registryConfigBeanName , registryConfigBuilder.getRawBeanDefinition());
		logger.info("register registryConfig success");
		String classNames = properties.getRegistryClass();
		String[] classArray = classNames.split(";");
		Stream.of(classArray).forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				BeanDefinitionBuilder referenceConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceConfig.class);
				referenceConfigBuilder.addPropertyValue("timeout", properties.getTimeOut()).addPropertyValue("version", properties.getVersion()).addPropertyValue("retries", 0).addPropertyValue("check", properties.isCheck()).addPropertyReference("application", applicationConfigBeanName).addPropertyReference("registries", registryConfigBeanName).addPropertyValue("interfaceClass", clazz);
				registry.registerBeanDefinition(className, referenceConfigBuilder.getRawBeanDefinition());
				logger.info(String.format("register %s success", className));
			} catch (ClassNotFoundException e) {
				logger.error(String.format("register %s fail", className),e);
			}
		});
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		
	}


}
