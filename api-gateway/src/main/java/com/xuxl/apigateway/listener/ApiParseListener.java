package com.xuxl.apigateway.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Description;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.xuxl.apigateway.common.ApiDefine;
import com.xuxl.apigateway.common.ApiHolder;
import com.xuxl.apigateway.common.ApiMethodDefine;
import com.xuxl.apigateway.common.ApiParameterDefine;
import com.xuxl.apigateway.common.SpringBean;
import com.xuxl.common.annotation.ApiGroup;
import com.xuxl.common.annotation.ApiParameter;
import com.xuxl.common.annotation.HttpApi;

@Component
public class ApiParseListener implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${dubbo.class}")
	private String classNames;
	
	@Autowired
	private SpringBean bean;
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String,ApiDefine> registerMap = new HashMap<>();
		String[] classNameArray = classNames.split(";");
		Stream.of(classNameArray).forEach(className -> {
			try {
				Class<?> clazz = Class.forName(className);
				ApiGroup group = clazz.getAnnotation(ApiGroup.class);
				Object object = bean.getBean(clazz.getName(), clazz);
				if(Objects.nonNull(group)) {
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
								Type genericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
								try {
									Class<?> genericClazz = Class.forName(((Class<?>)genericType).getName());
									if(String.class.isAssignableFrom(genericClazz)) {
										methodDefine.setReturnType(genericClazz);
									} else if(Objects.nonNull(genericClazz.getAnnotation(Description.class))) {
										methodDefine.setReturnType(genericClazz);
									} else if(ClassUtils.isPrimitiveOrWrapper(genericClazz)) {
										methodDefine.setReturnType(genericClazz);
									} else {
										
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							} else if(returnType.isArray()) {
								Class<?> genericClazz = returnType.getComponentType();
								if(String.class.isAssignableFrom(genericClazz)) {
									methodDefine.setReturnType(genericClazz);
								} else if(Objects.nonNull(genericClazz.getAnnotation(Description.class))) {
									methodDefine.setReturnType(genericClazz);
								} else if(ClassUtils.isPrimitiveOrWrapper(genericClazz)) {
									methodDefine.setReturnType(genericClazz);
								} else {
									
								}
							} else if (String.class.isAssignableFrom(returnType)) {
								methodDefine.setReturnType(returnType);
							} else if (ClassUtils.isPrimitiveOrWrapper(returnType)) {
								methodDefine.setReturnType(returnType);
							} else {
								if(Objects.nonNull(returnType.getAnnotation(Description.class))) {
									methodDefine.setReturnType(returnType);
								}
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
				e.printStackTrace();
			}
		});
		ApiHolder.setRegisterMap(Collections.unmodifiableMap(registerMap));
	}

}
