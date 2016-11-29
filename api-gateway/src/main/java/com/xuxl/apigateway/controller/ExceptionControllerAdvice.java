package com.xuxl.apigateway.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.apigateway.common.BaseResponse;
import com.xuxl.common.exception.ServiceException;


@ControllerAdvice
public class ExceptionControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse<Object> processException(Exception exception) {
		logger.error(exception);
		BaseResponse<Object> response = new BaseResponse<>();
		response.setDate(new Date());
		if(exception instanceof ServiceException) {
			ServiceException serviceException = (ServiceException) exception;
			response.setCode(serviceException.getCode());
			response.setMsg(serviceException.getMsg());
		} else {
			ServiceException serviceException = new ServiceException(SystemReturnCode.UNKNOWN_ERROR);
			response.setCode(serviceException.getCode());
			response.setMsg(serviceException.getMsg());
		}
		return response;
	}
	
}
