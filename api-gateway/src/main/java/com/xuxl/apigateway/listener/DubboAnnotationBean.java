package com.xuxl.apigateway.listener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.xuxl.common.annotation.dubbo.api.DubboService;


/**
 * 扫描DubboService注解的类
 * @author xuxl
 *
 */
public class DubboAnnotationBean implements BeanFactoryPostProcessor,ApplicationContextAware {
	
	private ApplicationContext context;
	
	private String[] packages;
	
	public DubboAnnotationBean(String[] packages) {
		this.packages = packages;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (packages == null || packages.length == 0) {
            return;
        }
        if (beanFactory instanceof BeanDefinitionRegistry) {
        	DubboServiceRegisterScanner scanner = new DubboServiceRegisterScanner((BeanDefinitionRegistry)beanFactory,context);
        	scanner.addIncludeFilter(new AnnotationTypeFilter(DubboService.class));
        	scanner.scan(packages);
        }
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	
}

