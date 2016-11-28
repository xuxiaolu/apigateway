package com.xuxl.apigateway.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DubboConsumerBeanRegister.class)
public @interface EnableDubboConsumerBean {
	
	String[] basePackages();
	
}
