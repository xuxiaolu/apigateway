package com.xuxl.apigateway.converter;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class StringToDateConverter implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		Long content = Long.parseLong(source);
		return new Date(content);
	}

}
