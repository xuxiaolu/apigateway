package com.xuxl.apigateway.config;

import java.util.stream.Stream;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class DubboConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {
	
	private static final String PROTOCOL_NAME = "dubbo";

	private Environment environment;
	

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		
		DubboProperties properties = DubboProperties.load(environment);
		
		BeanDefinitionBuilder applicationConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(ApplicationConfig.class);
		applicationConfigBuilder.addPropertyValue("name", properties.getName());
		String applicationConfigBeanName = "applicationConfig";
		registry.registerBeanDefinition(applicationConfigBeanName , applicationConfigBuilder.getRawBeanDefinition());
		
		BeanDefinitionBuilder registryConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
		registryConfigBuilder.addPropertyValue("address", properties.getAddress()).addPropertyValue("protocol", PROTOCOL_NAME);
		String registryConfigBeanName = "registryConfig";
		registry.registerBeanDefinition(registryConfigBeanName , registryConfigBuilder.getRawBeanDefinition());
		
		String classNames = properties.getRegistryClass();
		String[] classArray = classNames.split(";");
		Stream.of(classArray).forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				BeanDefinitionBuilder referenceConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(ReferenceConfig.class);
				referenceConfigBuilder.addPropertyValue("timeout", properties.getTimeOut()).addPropertyValue("version", properties.getVersion()).addPropertyValue("retries", 0).addPropertyValue("check", properties.isCheck()).addPropertyReference("application", applicationConfigBeanName).addPropertyReference("registries", registryConfigBeanName).addPropertyValue("interfaceClass", clazz);
				registry.registerBeanDefinition(clazz.getName(), referenceConfigBuilder.getRawBeanDefinition());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		});
		
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		
	}


}
