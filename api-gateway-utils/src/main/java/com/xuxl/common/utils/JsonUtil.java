package com.xuxl.common.utils;

import java.io.IOException;
import java.util.Collection;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public static String convertJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error(String.format("json parse error %s", object), e);
		}
		return null;
	}
	
	
	public static <T> T convertObject(String json,Class<T> object) {
		try {
			return mapper.readValue(json, object);
		} catch (JsonParseException e) {
			log.error(String.format("convertObject has exception,content is %s,want to convert %s", json,object), e);
		} catch (JsonMappingException e) {
			log.error(String.format("convertObject has exception,content is %s,want to convert %s", json,object), e);
		} catch (IOException e) {
			log.error(String.format("convertObject has exception,content is %s,want to convert %s", json,object), e);
		}
		return null;
	}
	
	
	public static <T> Collection<T> convertCollection(String json,Class<?> collectionClass,Class<T> object) {
		JavaType javaType = mapper.getTypeFactory().constructParametrizedType(collectionClass, collectionClass,object);  
		try {
			return mapper.readValue(json, javaType);
		} catch (JsonParseException e) {
			log.error(String.format("convertCollection has exception,content is %s,want to convert %s", json,object), e);
		} catch (JsonMappingException e) {
			log.error(String.format("convertCollection has exception,content is %s,want to convert %s", json,object), e);
		} catch (IOException e) {
			log.error(String.format("convertCollection has exception,content is %s,want to convert %s", json,object), e);
		}
		return null;
	}

}
