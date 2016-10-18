package com.xuxl.apigateway.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xuxl.apigateway.common.ApiDefine;
import com.xuxl.apigateway.common.ApiHolder;


@RestController
@RequestMapping("/docs")
public class DocsController {
	
	@RequestMapping
	public Map<String, List<ApiDefine>> document() {
		Map<String, List<ApiDefine>> result = new HashMap<>();
		List<ApiDefine> apiDefineList = new LinkedList<>();
		Map<String,ApiDefine> registerMap = ApiHolder.getRegisterMap();
		registerMap.forEach((key,value) -> {
			apiDefineList.add(value);
		});
		if(!apiDefineList.isEmpty()) {
			result = apiDefineList.stream().collect(Collectors.groupingBy(ApiDefine :: getPrefix));
		}
		
		return result;
	}

}
