package com.xuxl.apigateway.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.apigateway.common.Response;
import com.xuxl.common.exception.ServiceException;


@ControllerAdvice
public class ExceptionControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Response processException(Exception exception) {
		logger.error(exception);
		Response response = new Response();
		if(exception instanceof ServiceException) {
			ServiceException serviceException = (ServiceException) exception;
			response.setCode(serviceException.getDisplayCode());
			response.setMessage(serviceException.getDescription());
		} else {
			ServiceException serviceException = new ServiceException(SystemReturnCode.UNKNOWN_ERROR);
			response.setCode(serviceException.getDisplayCode());
			response.setMessage(serviceException.getDescription());
		}
		return response;
		
	}
	

}
