package com.xuxl.apigateway.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xuxl.apigateway.common.Response;
import com.xuxl.common.exception.ServiceException;


@ControllerAdvice
public class ExceptionControllerAdvice {
	
	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public Response processException(ServiceException exception) {
		Response response = new Response();
		response.setCode(exception.getDisplayCode());
		response.setMessage(exception.getMsg());
		return response;
		
	}
	

}
