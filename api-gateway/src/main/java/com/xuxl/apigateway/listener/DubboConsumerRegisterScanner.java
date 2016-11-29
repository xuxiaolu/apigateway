package com.xuxl.apigateway.listener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.DisposableBean;
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

import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.xuxl.common.annotation.dubbo.api.DubboMethod;
import com.xuxl.common.annotation.dubbo.api.DubboService;

/**
 * 扫描DubboService注解的类并注册
 * @author xuxl
 *
 */
public class DubboConsumerRegisterScanner extends ClassPathBeanDefinitionScanner implements DisposableBean {
	
	private ApplicationContext applicationContext;	
	
	private static final ConcurrentHashMap<String,ReferenceBean<?>> referenceMap = new ConcurrentHashMap<>();

	public DubboConsumerRegisterScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}
	
	public DubboConsumerRegisterScanner(BeanDefinitionRegistry registry, ApplicationContext applicationContext) {
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
			beanDefinitions.stream().forEach(holder -> {
				GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
				String className = definition.getBeanClassName();
				ReferenceBean<?> referenceBean = referenceMap.get(className);
				if(referenceBean == null) {
					try {
						referenceBean = new ReferenceBean<>();
						referenceBean.setApplicationContext(applicationContext);
						Class<?> clazz = Class.forName(className);
						referenceBean.setInterface(clazz);
						Method[] methods = clazz.getDeclaredMethods();
						List<MethodConfig> methodConfigList = Arrays.stream(methods).filter(method -> method.getAnnotation(DubboMethod.class) != null).map(method -> {
							MethodConfig methodConfig = new MethodConfig();
							DubboMethod dubboMethod = method.getAnnotation(DubboMethod.class);
							methodConfig.setName(method.getName());
							methodConfig.setRetries(dubboMethod.retries());
							methodConfig.setTimeout(dubboMethod.timeOut());
							return methodConfig;
						}).collect(Collectors.toList());
						referenceBean.setMethods(methodConfigList);
						referenceBean.setCheck(false);
						DubboService service = clazz.getAnnotation(DubboService.class);
						if(service != null) {
							referenceBean.setVersion(service.version());
							referenceBean.setTimeout(service.timeout());
							referenceBean.setRetries(service.retries());
						}
						try {
							referenceBean.afterPropertiesSet();
		                } catch (RuntimeException e) {
		                    throw (RuntimeException) e;
		                } catch (Exception e) {
		                    throw new IllegalStateException(e.getMessage(), e);
		                }
						referenceMap.putIfAbsent(className, referenceBean);
						context.getBeanFactory().registerSingleton(className, referenceBean.get());
						logger.info(String.format("register %s success", className));
					} catch (ClassNotFoundException e) {
						logger.error(String.format("register %s fail", className),e);
					}
				}
			});
		}
		return beanDefinitions;
	}

	public static ConcurrentHashMap<String, ReferenceBean<?>> getReferencemap() {
		return referenceMap;
	}

	public void destroy() throws Exception {
		for (ReferenceConfig<?> referenceConfig : referenceMap.values()) {
            try {
                referenceConfig.destroy();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
	}

}
