package com.xuxl.apigateway.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xuxl.apigateway.common.ApiInfo;
import com.xuxl.apigateway.common.ApiHolder;


@RestController
@RequestMapping("/docs")
public class DocsController {
	
	@RequestMapping
	public Map<String, List<ApiInfo>> document() {
		Map<String, List<ApiInfo>> result = new HashMap<>();
		List<ApiInfo> apiDefineList = new LinkedList<>();
		Map<String,ApiInfo> registerMap = ApiHolder.getRegisterMap();
		registerMap.forEach((key,value) -> {
			apiDefineList.add(value);
		});
		if(!apiDefineList.isEmpty()) {
			result = apiDefineList.stream().collect(Collectors.groupingBy(ApiInfo :: getPrefix));
		}
		return result;
	}

}
