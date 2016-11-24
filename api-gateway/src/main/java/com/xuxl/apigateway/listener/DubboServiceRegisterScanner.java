package com.xuxl.apigateway.listener;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.xuxl.common.annotation.DubboService;

/**
 * 扫描DubboService注解的类
 * @author xuxl
 *
 */
public class DubboServiceRegisterScanner extends ClassPathBeanDefinitionScanner {
	
	private static final String DUBBO_APPLICATION_NAME = "spring.dubbo.name";
	
	private static final String DUBBO_REGISTER_ADDRESS = "spring.dubbo.address";
	
	private static final String DUBBO_SERVICE_CHECK = "spring.dubbo.check";
	
	private static final String DUBBO_SERVICE_TIMEOUT = "spring.dubbo.timeout";
	
	private static final String DUBBO_SERVICE_RETRIES = "spring.dubbo.retries";
	
	private ApplicationContext applicationContext;	

	public DubboServiceRegisterScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}
	
	public DubboServiceRegisterScanner(BeanDefinitionRegistry registry, ApplicationContext applicationContext) {
		super(registry);
		Assert.notNull(applicationContext, "applicationContext can not be null");
		this.applicationContext = applicationContext;
	}



	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
	}

	public int scan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = doScan(basePackages);
		return beanDefinitions.size();
	}
	

	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates) {
				String beanName = new AnnotationBeanNameGenerator().generateBeanName(candidate, getRegistry());
				if (checkCandidate(beanName, candidate)) {
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
					beanDefinitions.add(definitionHolder);
				}
			}
		}
		if(beanDefinitions.size() != 0) {
			ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
			String applicationConfigBeanName = "applicationConfig";
			ApplicationConfig applicationConfig = new ApplicationConfig(context.getEnvironment().getProperty(DUBBO_APPLICATION_NAME));
			context.getBeanFactory().registerSingleton(applicationConfigBeanName, applicationConfig);
			logger.info("register applicationConfig success");
			
			String registryConfigBeanName = "registryConfig";
			RegistryConfig registryConfig = new RegistryConfig(context.getEnvironment().getProperty(DUBBO_REGISTER_ADDRESS));
			registryConfig.setProtocol("dubbo");
			context.getBeanFactory().registerSingleton(registryConfigBeanName, registryConfig);
			logger.info("register registryConfig success");
			beanDefinitions.stream().forEach(holder -> {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
				String className = definition.getBeanClassName();
				try {
					Class<?> clazz = Class.forName(className);
					ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
					referenceConfig.setInterface(clazz);	
					referenceConfig.setApplication(applicationConfig);
					referenceConfig.setCheck(context.getEnvironment().getProperty(DUBBO_SERVICE_CHECK, Boolean.class));
					DubboService service = clazz.getAnnotation(DubboService.class);
					if(service != null) {
						referenceConfig.setVersion(service.version());
					}
					referenceConfig.setTimeout(context.getEnvironment().getProperty(DUBBO_SERVICE_TIMEOUT, Integer.class));
					referenceConfig.setRetries(context.getEnvironment().getProperty(DUBBO_SERVICE_RETRIES, Integer.class));
					referenceConfig.setRegistry(registryConfig);
					context.getBeanFactory().registerSingleton(className, referenceConfig);
					logger.info(String.format("register %s success", className));
				} catch (ClassNotFoundException e) {
					logger.error(String.format("register %s fail", className),e);
				}
			});
		}
		return beanDefinitions;
	}

}
