package com.xuxl.apigateway.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.apigateway.common.BaseResponse;
import com.xuxl.common.exception.ServiceException;


@ControllerAdvice
public class ExceptionControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public BaseResponse<Object> processException(Throwable throwable) {
		logger.error(throwable);
		BaseResponse<Object> response = new BaseResponse<>();
		response.setDate(new Date());
		if(HystrixTimeoutException.class.isInstance(throwable)) {
			response.setCode(SystemReturnCode.TIMEOUT_ERROR.getCode());
			response.setMsg(SystemReturnCode.TIMEOUT_ERROR.getMsg());
		} else if(ServiceException.class.isInstance(throwable)){
			response.setCode(((ServiceException)throwable).getCode());
			response.setMsg(((ServiceException)throwable).getMsg());
			
		} else {
			ServiceException serviceException = new ServiceException(SystemReturnCode.UNKNOWN_ERROR);
			response.setCode(serviceException.getCode());
			response.setMsg(serviceException.getMsg());
		}
		return response;
	}
	
}
