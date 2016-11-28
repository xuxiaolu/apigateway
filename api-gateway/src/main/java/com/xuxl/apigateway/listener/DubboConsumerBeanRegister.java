package com.xuxl.apigateway.listener;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class DubboConsumerBeanRegister implements ImportBeanDefinitionRegistrar {

	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableDubboConsumerBean.class.getName()));
        String[] packages = attributes.getStringArray("basePackages");
        BeanDefinitionBuilder dubboAnnotationBean = BeanDefinitionBuilder.genericBeanDefinition(DubboConsumerBean.class);
        dubboAnnotationBean.addConstructorArgValue(packages);
        registry.registerBeanDefinition("dubboAnnotationBean", dubboAnnotationBean.getRawBeanDefinition());
	}

}
