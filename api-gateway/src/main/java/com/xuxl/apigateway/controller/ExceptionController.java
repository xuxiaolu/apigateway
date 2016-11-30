package com.xuxl.apigateway.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.apigateway.common.BaseResponse;

@RequestMapping("/error")
@RestController
public class ExceptionController implements ErrorController {

	public String getErrorPath() {
		return "";
	}
	
	@RequestMapping(value = "/404")
    public BaseResponse<Object> error404(HttpServletRequest request) {
		BaseResponse<Object> response = new BaseResponse<>();
		response.setCode(SystemReturnCode.NOT_FOUND_ERROR.getCode());
		response.setMsg(SystemReturnCode.NOT_FOUND_ERROR.getMsg());
        return response;
    }

	@RequestMapping(value = "/500")
	public BaseResponse<Object> error500(HttpServletRequest request) {
		BaseResponse<Object> response = new BaseResponse<>();
		response.setCode(SystemReturnCode.INTERNAL_ERROR.getCode());
		response.setMsg(SystemReturnCode.INTERNAL_ERROR.getMsg());
		return response;
	}

}
