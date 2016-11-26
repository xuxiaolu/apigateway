package com.xuxl.apigateway.listener;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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
import com.xuxl.apigateway.common.ApiReturnFieldInfo;
import com.xuxl.apigateway.common.ApiReturnInfo;
import com.xuxl.apigateway.utils.DescriptionUtils;
import com.xuxl.common.annotation.http.api.Api;
import com.xuxl.common.annotation.http.api.ApiModel;
import com.xuxl.common.annotation.http.api.ApiModelProperty;
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
							String name = prefix.concat(SEPARATOR).concat(apiOperation.value());
							ApiMethodInfo methodInfo = new ApiMethodInfo();
							methodInfo.setDesc(apiOperation.desc());
							methodInfo.setOwner(apiOperation.owner());
							methodInfo.setType(apiOperation.method());
							methodInfo.setReturnInfo(genertorReturnInfo(method));
							
							ApiInfo apiInfo = new ApiInfo();
							apiInfo.setMethodInfo(methodInfo);
							apiInfo.setClassName(className);
							apiInfo.setProxy(proxy);
							apiInfo.setMethod(method);
							apiInfo.setName(name);
							apiInfo.setPrefix(prefix);
							Optional<MethodConfig> methodConfigOption = methodConfigList.stream().filter(methodConfig -> methodConfig.getName().equals(method.getName())).findFirst();
							if(methodConfigOption.isPresent()) {
								MethodConfig methodConfig = methodConfigOption.get();
								apiInfo.setTimeout(methodConfig.getRetries() > 0 ? (methodConfig.getRetries() + 1) * methodConfig.getTimeout() : methodConfig.getTimeout());
							} else {
								apiInfo.setTimeout(retries > 0 ? (retries + 1) * timeOut : timeOut);
							}
							Parameter[] parameters = method.getParameters();
							if(parameters.length == 0) {
								dubboRegisterMap.put(name, apiInfo);
							} else {
								ApiParameterInfo[] parameterInfos = new ApiParameterInfo[parameters.length];
								for(int i = 0,size = parameters.length; i < size; i++) {
									ApiParameterInfo parameterInfo = new ApiParameterInfo();
									Parameter parameter = parameters[i];
									
									String defaultValue = "";
									boolean isRequired = false;
									String description = "";
									String parameterName = "";
									Class<?> parameterType = parameter.getType();
									Class<?> genericParameterType = null;
									if(Collection.class.isAssignableFrom(parameterType)) {
										genericParameterType = (Class<?>)((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments()[0];
									}
									if(parameterType.isArray()) {
										genericParameterType = parameterType.getComponentType();
									}
									ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
									if(apiParam != null) {
										parameterName = apiParam.name();
										defaultValue = apiParam.defaultValue();
										isRequired = apiParam.required();
										description = apiParam.desc();
									} else {
										parameterName = parameter.getName();
										defaultValue = "";
										isRequired = false;
										description = parameter.getName();
									}
									parameterInfo.setClazz(parameterType);
									parameterInfo.setType(lowerCase(parameterType));
									if(genericParameterType != null) {
										parameterInfo.setGenericClazz(genericParameterType);
										parameterInfo.setGeneric(lowerCase(genericParameterType));
									}
									parameterInfo.setName(parameterName);
									parameterInfo.setDefaultValue(defaultValue);
									parameterInfo.setRequired(isRequired);
									parameterInfo.setDesc(description);
									parameterInfos[i] = parameterInfo;
								}
								apiInfo.setParameterInfos(parameterInfos);
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
	
	private String lowerCase(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private ApiReturnInfo genertorReturnInfo(Method method) {
		Class<?> clazz = method.getReturnType();
		
		if(!isAcceptReturnType(clazz)) {
			throw new RuntimeException("can not accept return type:" + clazz.getName());
		}
		
		if(Date.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.DATE_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(String.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.STRING_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.INT_INTEGER_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.DOUBLE_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.LONG_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.FLOAT_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.BOOLEAN_DESC);
			returnInfo.setType(lowerCase(clazz));
			return returnInfo;
		}
		
		if(Collection.class.isAssignableFrom(clazz)) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.COLLECTION_DESC);
			returnInfo.setType(lowerCase(clazz));
			Type genericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
			Class<?> genericClazz = (Class<?>) genericType;
			returnInfo.setGeneric(lowerCase(genericClazz));
			if(genericClazz.getAnnotation(ApiModel.class) != null) {
				Set<ApiReturnInfo> childReturnInfos = new HashSet<>(1);
				childReturnInfos.add(genertorReturnInfo(genericClazz));
				returnInfo.setChildReturns(childReturnInfos);
			}
			return returnInfo;
		}
		
		if(clazz.isArray()) {
			ApiReturnInfo returnInfo = new ApiReturnInfo();
			returnInfo.setDesc(DescriptionUtils.ARRAY_DESC);
			returnInfo.setType(lowerCase(Array.class));
			Class<?> genericClazz = clazz.getComponentType();
			returnInfo.setGeneric(lowerCase(genericClazz));
			if(genericClazz.getAnnotation(ApiModel.class) != null) {
				Set<ApiReturnInfo> childReturnInfos = new HashSet<>(1);
				childReturnInfos.add(genertorReturnInfo(genericClazz));
				returnInfo.setChildReturns(childReturnInfos);
			}
			return returnInfo;
		}
		
		if(clazz.getAnnotation(ApiModel.class) != null) {
			genertorReturnInfo(clazz);
		} 
		return null;
	}

	private ApiReturnInfo genertorReturnInfo(Class<?> clazz) {
		ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
		ApiReturnInfo returnInfo = new ApiReturnInfo();
		returnInfo.setDesc(apiModel.value());
		returnInfo.setType(lowerCase(clazz));
		Set<ApiReturnInfo> childReturnInfos = new HashSet<>();
		Field[] fields = clazz.getDeclaredFields();
		Set<ApiReturnFieldInfo> apiReturnFieldInfoList = Arrays.stream(fields)
				.filter(field -> field.getAnnotation(ApiModelProperty.class) != null)
				.map(field -> {
				field.setAccessible(true);
				ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
				Class<?> fieldType = field.getType();
				if(Date.class.isAssignableFrom(fieldType) || String.class.isAssignableFrom(fieldType) || ClassUtils.isPrimitiveOrWrapper(fieldType)) {
					ApiReturnFieldInfo fieldInfo = new ApiReturnFieldInfo();
					fieldInfo.setDesc(property.value());
					fieldInfo.setType(lowerCase(fieldType));
					field.setAccessible(false);
					return fieldInfo;
				} else if(fieldType.getAnnotation(ApiModel.class) != null) {
					ApiReturnFieldInfo fieldInfo = new ApiReturnFieldInfo();
					fieldInfo.setDesc(property.value());
					fieldInfo.setType(lowerCase(fieldType));
					childReturnInfos.add(genertorReturnInfo(fieldType));
					field.setAccessible(false);
					return fieldInfo;
				} else if(Collection.class.isAssignableFrom(fieldType)) {
					ApiReturnFieldInfo fieldInfo = new ApiReturnFieldInfo();
					fieldInfo.setDesc(property.value());
					fieldInfo.setType(lowerCase(fieldType));
					Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					Class<?> genericClazz = (Class<?>) genericType;
					fieldInfo.setGeneric(lowerCase(genericClazz));
					if(genericClazz.getAnnotation(ApiModel.class) != null) {
						childReturnInfos.add(genertorReturnInfo(genericClazz));
					}
					field.setAccessible(false);
					return fieldInfo;
				} else if(fieldType.isArray()) {
					ApiReturnFieldInfo fieldInfo = new ApiReturnFieldInfo();
					fieldInfo.setDesc(property.value());
					fieldInfo.setType(lowerCase(Array.class));
					Class<?> genericClazz = fieldType.getComponentType();
					fieldInfo.setGeneric(lowerCase(genericClazz));
					if(genericClazz.getAnnotation(ApiModel.class) != null) {
						childReturnInfos.add(genertorReturnInfo(genericClazz));
					}
					field.setAccessible(false);
					return fieldInfo;
				} else {
					ApiReturnFieldInfo fieldInfo = new ApiReturnFieldInfo();
					fieldInfo.setDesc(property.value());
					fieldInfo.setType(lowerCase(fieldType));
					field.setAccessible(false);
					return fieldInfo;
					
				}
		}).collect(Collectors.toSet());
		returnInfo.setFields(apiReturnFieldInfoList);
		if(childReturnInfos.size() > 0) {
			returnInfo.setChildReturns(childReturnInfos);
		}
		return returnInfo;
	}

	private boolean isAcceptReturnType(Class<?> clazz) {
		return  Date.class.isAssignableFrom(clazz) || 
				String.class.isAssignableFrom(clazz) || 
				clazz.getAnnotation(ApiModel.class) != null || 
				ClassUtils.isPrimitiveOrWrapper(clazz) ||
				Collection.class.isAssignableFrom(clazz) || 
				clazz.isArray();
	}
	
}
