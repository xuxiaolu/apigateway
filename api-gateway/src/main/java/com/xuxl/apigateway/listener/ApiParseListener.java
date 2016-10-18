package com.xuxl.apigateway.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuxl.apigateway.common.ApiDefine;
import com.xuxl.apigateway.common.ApiHolder;
import com.xuxl.apigateway.common.ApiMethodDefine;
import com.xuxl.apigateway.common.ApiParameterDefine;
import com.xuxl.apigateway.config.DubboProperties;
import com.xuxl.common.annotation.ApiGroup;
import com.xuxl.common.annotation.ApiParameter;
import com.xuxl.common.annotation.Description;
import com.xuxl.common.annotation.HttpApi;

@Component
@EnableConfigurationProperties(DubboProperties.class)
public class ApiParseListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiParseListener.class);
	
	public static final String SEPARATOR = "/";

	@Autowired
	private DubboProperties properties;
	
	private Object getRefer(ApplicationContext context,String className) {
		ReferenceConfig<?> referenceConfig = context.getBean(className, ReferenceConfig.class);
		return referenceConfig.get();
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ObjectMapper mapper = new ObjectMapper();
		ApplicationContext context = event.getApplicationContext();
		registerDubboBean(context);
		Map<String,ApiDefine> dubboRegisterMap = new HashMap<>();
		
		Map<String,String> registryMap = properties.getRegistryMap();
		Set<String> classNames = registryMap.keySet();
		classNames.stream().forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				ApiGroup group = clazz.getAnnotation(ApiGroup.class);
				if(Objects.nonNull(group)) {
					String prefix = group.name();
					Object object = getRefer(context, className);
					Method[] methodArray = clazz.getMethods();
					Stream.of(methodArray).forEach(method -> {
						HttpApi httpApi = method.getAnnotation(HttpApi.class);
						if(Objects.nonNull(httpApi)) {
							String suffix = httpApi.name();
							String name = prefix + SEPARATOR + suffix;
							ApiMethodDefine methodDefine = new ApiMethodDefine();
							methodDefine.setDescription(httpApi.desc());
							methodDefine.setOwner(httpApi.owner());
							methodDefine.setApiType(httpApi.type());
							Class<?> returnType = method.getReturnType();
							if(Collection.class.isAssignableFrom(returnType)) {
								methodDefine.setReturnType(returnType);
								Type genericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
								Class<?> genericClazz = (Class<?>) genericType;
								if(isAcceptReturnType(genericClazz)) {
									methodDefine.setGenericType(genericClazz);
									try {
										Object instance = genericClazz.newInstance();
										String json = mapper.writeValueAsString(instance);
										methodDefine.setJson(json);
									} catch (InstantiationException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (JsonProcessingException e) {
										e.printStackTrace();
									}
								} else {
									
								}
							} else if(returnType.isArray()) {
								methodDefine.setReturnType(returnType);
								Class<?> genericClazz = returnType.getComponentType();
								if(isAcceptReturnType(genericClazz)) {
									methodDefine.setGenericType(genericClazz);
									try {
										Object instance = genericClazz.newInstance();
										String json = mapper.writeValueAsString(instance);
										methodDefine.setJson(json);
									} catch (InstantiationException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (JsonProcessingException e) {
										e.printStackTrace();
									}
								}  else {
									
								}
							} else if (isAcceptReturnType(returnType)) {
								methodDefine.setReturnType(returnType);
								try {
									Object instance = returnType.newInstance();
									String json = mapper.writeValueAsString(instance);
									methodDefine.setJson(json);
								} catch (InstantiationException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
								
							} else {
								
							}
							ApiDefine api = new ApiDefine();
							api.setApiMethodDefine(methodDefine);
							api.setClassName(clazz.getName());
							api.setObject(object);
							api.setMethod(method);
							api.setApiName(name);
							api.setPrefix(prefix);
							Parameter[] parameterArray = method.getParameters();
							if(parameterArray.length == 0) {
								dubboRegisterMap.put(name, api);
							} else {
								ApiParameterDefine[] apiParameterDefineArray = new ApiParameterDefine[parameterArray.length];
								for(int i = 0,size = parameterArray.length; i < size; i++) {
									ApiParameterDefine apiParameterDefine = new ApiParameterDefine();
									Parameter parameter = parameterArray[i];
									ApiParameter apiParameter = parameter.getAnnotation(ApiParameter.class);
									if(Objects.nonNull(apiParameter)) {
										Class<?> parameterType = parameter.getType();
										String parameterName = apiParameter.name();
										String defaultValue = apiParameter.defaultValue();
										boolean isRequired = apiParameter.required();
										String description = apiParameter.desc();
										if(Collection.class.isAssignableFrom(parameterType)) {
											Class<?> genericParameterType = (Class<?>)((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0];
											apiParameterDefine.setGenericParameterType(genericParameterType);
										}
										if(parameterType.isArray()) {
											Class<?> genericParameterType = parameterType.getComponentType();
											apiParameterDefine.setGenericParameterType(genericParameterType);
										}
										apiParameterDefine.setType(parameterType);
										apiParameterDefine.setName(parameterName);
										apiParameterDefine.setDefaultValue(defaultValue);
										apiParameterDefine.setRequired(isRequired);
										apiParameterDefine.setDescription(description);
										apiParameterDefineArray[i] = apiParameterDefine;
									} else {
										Class<?> parameterType = parameter.getType();
										String parameterName = parameter.getName();
										String defaultValue = "";
										boolean isRequired = false;
										
										if(Collection.class.isAssignableFrom(parameterType)) {
											Class<?> genericParameterType = (Class<?>)((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0];
											apiParameterDefine.setGenericParameterType(genericParameterType);
										}
										if(parameterType.isArray()) {
											Class<?> genericParameterType = parameterType.getComponentType();
											apiParameterDefine.setGenericParameterType(genericParameterType);
										}
										
										apiParameterDefine.setType(parameterType);
										apiParameterDefine.setName(parameterName);
										apiParameterDefine.setDefaultValue(defaultValue);
										apiParameterDefine.setRequired(isRequired);
										apiParameterDefineArray[i] = apiParameterDefine;
									}
								}
								api.setApiParameterArray(apiParameterDefineArray);
								dubboRegisterMap.put(name, api);
							}
						}
					});
				}
			} catch (ClassNotFoundException e) {
				logger.error(String.format("%s is not found", className),e);
			}
		});
		ApiHolder.setRegisterMap(Collections.unmodifiableMap(dubboRegisterMap));
	}

	private void registerDubboBean(ApplicationContext applicationContext) {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		
		String applicationConfigBeanName = "applicationConfig";
		ApplicationConfig applicationConfig = new ApplicationConfig(properties.getName());
		context.getBeanFactory().registerSingleton(applicationConfigBeanName, applicationConfig);
		logger.info("register applicationConfig success");
		
		String registryConfigBeanName = "registryConfig";
		RegistryConfig registryConfig = new RegistryConfig(properties.getAddress());
		registryConfig.setProtocol("dubbo");
		context.getBeanFactory().registerSingleton(registryConfigBeanName, registryConfig);
		logger.info("register registryConfig success");
		
		Map<String,String> registryMap = properties.getRegistryMap();
		registryMap.forEach((className,version) -> {
			try {
				Class<?> clazz = Class.forName(className);
				ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
				referenceConfig.setInterface(clazz);	
				referenceConfig.setApplication(applicationConfig);
				referenceConfig.setCheck(properties.isCheck());
				referenceConfig.setVersion(version);
				referenceConfig.setTimeout(properties.getTimeOut());
				referenceConfig.setRetries(0);
				referenceConfig.setRegistry(registryConfig);
				context.getBeanFactory().registerSingleton(className, referenceConfig);
				logger.info(String.format("register %s success", className));
			} catch (ClassNotFoundException e) {
				logger.error(String.format("register %s fail", className),e);
			}
		});
	}

	private boolean isAcceptReturnType(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Objects.nonNull(clazz.getAnnotation(Description.class)) || ClassUtils.isPrimitiveOrWrapper(clazz);
	}
	
}
