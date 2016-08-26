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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.common.ApiDefine;
import com.xuxl.apigateway.common.ApiHolder;
import com.xuxl.apigateway.common.ApiMethodDefine;
import com.xuxl.apigateway.common.ApiParameterDefine;
import com.xuxl.common.annotation.ApiGroup;
import com.xuxl.common.annotation.ApiParameter;
import com.xuxl.common.annotation.Description;
import com.xuxl.common.annotation.HttpApi;

@Component
public class ApiParseListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiParseListener.class);

	@Value("${dubbo.class}")
	private String classNames;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String,ApiDefine> registerMap = new HashMap<>();
		ApplicationContext context = event.getApplicationContext();
		String[] classNameArray = classNames.split(";");
		Stream.of(classNameArray).forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				ApiGroup group = clazz.getAnnotation(ApiGroup.class);
				if(Objects.nonNull(group)) {
					Object object = context.getBean(className);
					Method[] methodArray = clazz.getMethods();
					Stream.of(methodArray).forEach(method -> {
						HttpApi httpApi = method.getAnnotation(HttpApi.class);
						if(Objects.nonNull(httpApi)) {
							String name = httpApi.name();
							ApiMethodDefine methodDefine = new ApiMethodDefine();
							methodDefine.setDescription(httpApi.desc());
							methodDefine.setOwner(httpApi.owner());
							Class<?> returnType = method.getReturnType();
							if(Collection.class.isAssignableFrom(returnType)) {
								methodDefine.setReturnType(returnType);
								Type genericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
								Class<?> genericClazz = (Class<?>) genericType;
								if(isAcceptReturnType(genericClazz)) {
									methodDefine.setGenericType(genericClazz);
								} else {
									
								}
							} else if(returnType.isArray()) {
								methodDefine.setReturnType(returnType);
								Class<?> genericClazz = returnType.getComponentType();
								if(isAcceptReturnType(genericClazz)) {
									methodDefine.setGenericType(genericClazz);
								}  else {
									
								}
							} else if (isAcceptReturnType(returnType)) {
								methodDefine.setReturnType(returnType);
							} else {
								
							}
							ApiDefine api = new ApiDefine();
							api.setApiMethodDefine(methodDefine);
							api.setClassName(clazz.getName());
							api.setObject(object);
							api.setMethod(method);
							api.setApiName(name);
							Parameter[] parameterArray = method.getParameters();
							if(parameterArray.length == 0) {
								registerMap.put(name, api);
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
								registerMap.put(name, api);
							}
						}
					});
				}
			} catch (ClassNotFoundException e) {
				logger.error(String.format("%s is not found", className),e);
			}
		});
		ApiHolder.setRegisterMap(Collections.unmodifiableMap(registerMap));
	}

	private boolean isAcceptReturnType(Class<?> clazz) {
		return Date.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Objects.nonNull(clazz.getAnnotation(Description.class)) || ClassUtils.isPrimitiveOrWrapper(clazz);
	}
	
}
