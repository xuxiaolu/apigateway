package com.xuxl.apigateway.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.xuxl.apigateway.shiro.StatelessAuthcFilter;
import com.xuxl.apigateway.shiro.StatelessDefaultSubjectFactory;
import com.xuxl.apigateway.shiro.StatelessRealm;

@Configuration
public class ShiroConfiguration {

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public StatelessDefaultSubjectFactory subjectFactory() {
		StatelessDefaultSubjectFactory subjectFactory = new StatelessDefaultSubjectFactory();
		return subjectFactory;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/api/*");
		filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
		return filterRegistration;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager());
		Map<String, Filter> filters = new HashMap<>();
		filters.put("statelessAuthc", new StatelessAuthcFilter());
		factoryBean.setFilters(filters);
		Map<String, String> chains = new HashMap<>();
		chains.put("/api/*", "statelessAuthc");
		return factoryBean;
	}

	@Bean
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(statelessRealm());
		manager.setSubjectFactory(subjectFactory());
		manager.setSessionManager(webSessionManager());
		SecurityUtils.setSecurityManager(manager);
		return manager;
	}

	@Bean(name = "sessionManager")
	public DefaultWebSessionManager webSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionValidationSchedulerEnabled(false);
		return sessionManager;
	}

	@Bean
	@DependsOn(value = "lifecycleBeanPostProcessor")
	public StatelessRealm statelessRealm() {
		StatelessRealm statelessRealm = new StatelessRealm();
		statelessRealm.setCachingEnabled(false);
		return statelessRealm;
	}

}
