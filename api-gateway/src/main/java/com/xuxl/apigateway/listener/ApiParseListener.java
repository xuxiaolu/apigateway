package com.xuxl.apigateway.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.xuxl.apigateway.common.ApiInfo;
import com.xuxl.apigateway.common.ApiMethodInfo;
import com.xuxl.apigateway.common.ApiParameterInfo;
import com.xuxl.common.annotation.http.api.Api;
import com.xuxl.common.annotation.http.api.ApiModel;
import com.xuxl.common.annotation.http.api.ApiOperation;
import com.xuxl.common.annotation.http.api.ApiParam;

/**
 * 生成rest api
 * @author xuxl
 *
 */
@Component
public class ApiParseListener implements ApplicationListener<ContextRefreshedEvent> {
	
	public static final String SEPARATOR = "/";
	
	private AtomicBoolean flag = new AtomicBoolean(false);
	
	private static Map<String,ApiInfo> registerMap;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(flag.compareAndSet(false, true)) {
			ApplicationContext context = event.getApplicationContext();
			Map<String, ReferenceConfig> configMap = context.getBeansOfType(ReferenceConfig.class);
			Map<String,ApiInfo> dubboRegisterMap = new HashMap<>();
			configMap.forEach((className,referenceBean) -> {
				Class<?> clazz = referenceBean.getInterfaceClass();
				Api api = AnnotationUtils.findAnnotation(clazz, Api.class);
				if(api != null) {
					String prefix = api.value();
					Object proxy = referenceBean.get();
					List<MethodConfig> methodConfigList = referenceBean.getMethods();
					int timeOut = referenceBean.getTimeout();
					int retries = referenceBean.getRetries();
					Method[] methods = clazz.getMethods();
					Stream.of(methods).forEach(method -> {
						ApiOperation apiOperation = AnnotationUtils.findAnnotation(method, ApiOperation.class);
						if(apiOperation != null) {
							String suffix = apiOperation.value();
							String name = prefix + SEPARATOR + suffix;
							ApiMethodInfo apiMethodInfo = new ApiMethodInfo();
							apiMethodInfo.setDescription(apiOperation.desc());
							apiMethodInfo.setOwner(apiOperation.owner());
							apiMethodInfo.setMethod(apiOperation.method());
							
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
							ApiInfo apiInfo = new ApiInfo();
							apiInfo.setApiMethodInfo(apiMethodInfo);
							apiInfo.setClassName(className);
							apiInfo.setProxy(proxy);
							apiInfo.setMethod(method);
							apiInfo.setApiName(name);
							apiInfo.setPrefix(prefix);
							Optional<MethodConfig> methodConfigOption = methodConfigList.stream().filter(methodConfig -> methodConfig.getName().equals(method.getName())).findFirst();
							if(methodConfigOption.isPresent()) {
								MethodConfig methodConfig = methodConfigOption.get();
								apiInfo.setTimeOut(methodConfig.getRetries() > 0 ? (methodConfig.getRetries() + 1) * methodConfig.getTimeout() : methodConfig.getTimeout());
							} else {
								apiInfo.setTimeOut(retries > 0 ? (retries + 1) * timeOut : timeOut);
							}
							Parameter[] parameterArray = method.getParameters();
							if(parameterArray.length == 0) {
								dubboRegisterMap.put(name, apiInfo);
							} else {
								ApiParameterInfo[] apiParameterInfos = new ApiParameterInfo[parameterArray.length];
								for(int i = 0,size = parameterArray.length; i < size; i++) {
									ApiParameterInfo apiParameterInfo = new ApiParameterInfo();
									Parameter parameter = parameterArray[i];
									ApiParam apiParameter = parameter.getAnnotation(ApiParam.class);
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
								apiInfo.setApiParameterInfos(apiParameterInfos);
								dubboRegisterMap.put(name, apiInfo);
							}
						}
					});
				}
			});
			registerMap = Collections.unmodifiableMap(dubboRegisterMap);
		}
	}
	
	public static Map<String, ApiInfo> getRegisterMap() {
		return registerMap;
	}

	private boolean isAcceptReturnType(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Objects.nonNull(clazz.getAnnotation(ApiModel.class)) || ClassUtils.isPrimitiveOrWrapper(clazz);
	}
	
}
