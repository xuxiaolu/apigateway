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
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.xuxl.apigateway.common.ApiHolder;
import com.xuxl.apigateway.common.ApiInfo;
import com.xuxl.apigateway.common.ApiMethodInfo;
import com.xuxl.apigateway.common.ApiParameterInfo;
import com.xuxl.common.annotation.ApiGroup;
import com.xuxl.common.annotation.ApiParameter;
import com.xuxl.common.annotation.Description;
import com.xuxl.common.annotation.HttpApi;

@Component
public class ApiParseListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiParseListener.class);
	
	public static final String SEPARATOR = "/";

	@SuppressWarnings("rawtypes")
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ReferenceConfig> configMap = context.getBeansOfType(ReferenceConfig .class);
		Map<String,ApiInfo> dubboRegisterMap = new HashMap<>();
		configMap.forEach((className,referenceConfig) -> {
			try {
				Class<?> clazz = Class.forName(className);	
				ApiGroup group = clazz.getAnnotation(ApiGroup.class);
				if(Objects.nonNull(group)) {
					String prefix = group.name();
					Object proxy = referenceConfig.get();
					Method[] methods = clazz.getMethods();
					Stream.of(methods).forEach(method -> {
						HttpApi httpApi = method.getAnnotation(HttpApi.class);
						if(Objects.nonNull(httpApi)) {
							String suffix = httpApi.name();
							String name = prefix + SEPARATOR + suffix;
							ApiMethodInfo apiMethodInfo = new ApiMethodInfo();
							apiMethodInfo.setDescription(httpApi.desc());
							apiMethodInfo.setOwner(httpApi.owner());
							apiMethodInfo.setApiType(httpApi.type());
							
							Class<?> returnType = method.getReturnType();
							if(Collection.class.isAssignableFrom(returnType)) {
								apiMethodInfo.setReturnType(returnType);
								Type genericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
								Class<?> genericClazz = (Class<?>) genericType;
								if(isAcceptReturnType(genericClazz)) {
									apiMethodInfo.setGenericType(genericClazz);
								} else {
									
								}
							} else if(returnType.isArray()) {
								apiMethodInfo.setReturnType(returnType);
								Class<?> genericClazz = returnType.getComponentType();
								if(isAcceptReturnType(genericClazz)) {
									apiMethodInfo.setGenericType(genericClazz);
								}  else {
									
								}
							} else if (isAcceptReturnType(returnType)) {
								apiMethodInfo.setReturnType(returnType);
							} else {
								
							}
							ApiInfo api = new ApiInfo();
							api.setApiMethodInfo(apiMethodInfo);
							api.setClassName(className);
							api.setProxy(proxy);
							api.setMethod(method);
							api.setApiName(name);
							api.setPrefix(prefix);
							Parameter[] parameterArray = method.getParameters();
							if(parameterArray.length == 0) {
								dubboRegisterMap.put(name, api);
							} else {
								ApiParameterInfo[] apiParameterInfos = new ApiParameterInfo[parameterArray.length];
								for(int i = 0,size = parameterArray.length; i < size; i++) {
									ApiParameterInfo apiParameterInfo = new ApiParameterInfo();
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
											apiParameterInfo.setGenericParameterType(genericParameterType);
										}
										if(parameterType.isArray()) {
											Class<?> genericParameterType = parameterType.getComponentType();
											apiParameterInfo.setGenericParameterType(genericParameterType);
										}
										apiParameterInfo.setType(parameterType);
										apiParameterInfo.setName(parameterName);
										apiParameterInfo.setDefaultValue(defaultValue);
										apiParameterInfo.setRequired(isRequired);
										apiParameterInfo.setDescription(description);
										apiParameterInfos[i] = apiParameterInfo;
									} else {
										Class<?> parameterType = parameter.getType();
										String parameterName = parameter.getName();
										String defaultValue = "";
										boolean isRequired = false;
										
										if(Collection.class.isAssignableFrom(parameterType)) {
											Class<?> genericParameterType = (Class<?>)((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0];
											apiParameterInfo.setGenericParameterType(genericParameterType);
										}
										if(parameterType.isArray()) {
											Class<?> genericParameterType = parameterType.getComponentType();
											apiParameterInfo.setGenericParameterType(genericParameterType);
										}
										
										apiParameterInfo.setType(parameterType);
										apiParameterInfo.setName(parameterName);
										apiParameterInfo.setDefaultValue(defaultValue);
										apiParameterInfo.setRequired(isRequired);
										apiParameterInfos[i] = apiParameterInfo;
									}
								}
								api.setApiParameterInfos(apiParameterInfos);
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

	private boolean isAcceptReturnType(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Objects.nonNull(clazz.getAnnotation(Description.class)) || ClassUtils.isPrimitiveOrWrapper(clazz);
	}
	
}
