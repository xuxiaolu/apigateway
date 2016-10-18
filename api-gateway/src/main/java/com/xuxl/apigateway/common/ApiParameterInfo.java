package com.xuxl.apigateway.common;

import java.io.Serializable;

public class ApiParameterInfo implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
     * 参数类型
     */
    private Class<?> type;
    
    /**
     * 泛型参数类型
     */
    private Class<?> genericParameterType;
    
    
    /**
     * 默认值字符串形式
     */
    private String defaultValue;
    
    
    /**
     * 是否必须
     */
    private boolean isRequired;
    
    
    /**
     * 参数名
     */
    private String name;
    
    
    /**
     * 参数描述
     */
    private String description;


	public Class<?> getType() {
		return type;
	}


	public void setType(Class<?> type) {
		this.type = type;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public boolean isRequired() {
		return isRequired;
	}


	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Class<?> getGenericParameterType() {
		return genericParameterType;
	}

	public void setGenericParameterType(Class<?> genericParameterType) {
		this.genericParameterType = genericParameterType;
	}
	
	
	
}
