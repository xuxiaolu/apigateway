package com.xuxl.apigateway.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiParameterInfo implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
     * 参数类型
     */
	@JsonIgnore
    private Class<?> clazz;
    
    /**
     * 泛型参数类型
     */
	@JsonIgnore
    private Class<?> genericClazz;
    
    /**
     * 默认值字符串形式
     */
	@JsonIgnore
    private String defaultValue;
	
	/**
	 * 参数类型的短名字
	 */
	private String type;
	
    /**
     * 泛型参数类型的短名字
     */
	private String generic;
    
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
    private String desc;


	public Class<?> getClazz() {
		return clazz;
	}


	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}


	public Class<?> getGenericClazz() {
		return genericClazz;
	}


	public void setGenericClazz(Class<?> genericClazz) {
		this.genericClazz = genericClazz;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getGeneric() {
		return generic;
	}


	public void setGeneric(String generic) {
		this.generic = generic;
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

	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
