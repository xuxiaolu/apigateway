package com.xuxl.apigateway.listener;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.xuxl.common.annotation.dubbo.api.DubboService;


/**
 * 扫描DubboService注解的类
 * @author xuxl
 *
 */
public class DubboConsumerBean implements BeanFactoryPostProcessor,ApplicationContextAware {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboConsumerBean.class); 
	
	private static final String DUBBO_APPLICATION_NAME = "spring.dubbo.name";
	
	private static final String DUBBO_REGISTER_ADDRESS = "spring.dubbo.address";
		
	private static final String SEPARATOR = ";";
	
	private ApplicationContext context;
	
	private String[] packages;
	
	public DubboConsumerBean(String[] packages) {
		this.packages = packages;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (packages == null || packages.length == 0) {
            return;
        }
        if (beanFactory instanceof BeanDefinitionRegistry) {
        	DubboConsumerRegisterScanner scanner = new DubboConsumerRegisterScanner((BeanDefinitionRegistry)beanFactory,context);
        	scanner.addIncludeFilter(new AnnotationTypeFilter(DubboService.class));
        	scanner.scan(packages);
        }
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		String applicationConfigBeanName = "applicationConfig";
		ApplicationConfig applicationConfig = new ApplicationConfig(context.getEnvironment().getProperty(DUBBO_APPLICATION_NAME));
		context.getBeanFactory().registerSingleton(applicationConfigBeanName, applicationConfig);
		logger.info("register applicationConfig success");
		
		String registryConfigBeanName = "%s@registryConfig";
		String addressSum = context.getEnvironment().getProperty(DUBBO_REGISTER_ADDRESS, String.class);
		String[] addresses = addressSum.trim().split(SEPARATOR);
		Arrays.stream(addresses).map(address -> {
			RegistryConfig registryConfig = new RegistryConfig(address.trim());
			registryConfig.setProtocol("dubbo");
			context.getBeanFactory().registerSingleton(String.format(registryConfigBeanName, address), registryConfig);
			logger.info("register ".concat(String.format(registryConfigBeanName, address)).concat(" success"));
			return registryConfig;
		}).collect(Collectors.toList());
	}
	
}

