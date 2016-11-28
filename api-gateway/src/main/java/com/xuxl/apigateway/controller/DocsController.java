package com.xuxl.apigateway.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xuxl.apigateway.common.ApiInfo;
import com.xuxl.apigateway.common.DocResponse;
import com.xuxl.apigateway.listener.RestApiParseListener;


@RestController
@RequestMapping("/docs")
public class DocsController {
	
	@RequestMapping(method = RequestMethod.GET)
	public List<DocResponse> docs() {
		List<DocResponse> responseList = new ArrayList<DocResponse>();
		List<ApiInfo> apiDefineList = new LinkedList<>();
		Map<String,ApiInfo> registerMap = RestApiParseListener.getRegisterMap();
		registerMap.forEach((key,value) -> {
			apiDefineList.add(value);
		});
		if(!apiDefineList.isEmpty()) {
			Map<String, List<ApiInfo>> result = apiDefineList.stream().collect(Collectors.groupingBy(ApiInfo :: getPrefix));
			result.forEach((group,apiInfos) -> {
				responseList.add(new DocResponse(group, apiInfos));
			});
		}
		return responseList;
	}

}
