package com.xuxl.apigateway.converter;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class StringToDateConverter implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		if(StringUtils.hasText(source)) {
			Long content = Long.parseLong(source);
			return new Date(content);
		} else {
			return null;
		}
	}

}
