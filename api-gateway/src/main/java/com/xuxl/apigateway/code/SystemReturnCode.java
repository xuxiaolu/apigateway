package com.xuxl.apigateway.code;

import com.xuxl.common.code.AbstractReturnCode;

public class SystemReturnCode extends AbstractReturnCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SystemReturnCode(String desc, int code) {
		super(desc, code);
	}
	
	public final static int CODE_DUBBO_SERVICE_NOTFOUND_ERROR = -100;
	
	public final static SystemReturnCode DUBBO_SERVICE_NOTFOUND_ERROR = new SystemReturnCode("dubbo服务找不到", CODE_DUBBO_SERVICE_NOTFOUND_ERROR);
	
	public final static int CODE_SECURITY_ERROR = -110;
	
	public final static SystemReturnCode SECURITY_ERROR = new SystemReturnCode("安全异常", CODE_SECURITY_ERROR);
	
	public final static int CODE_UNKNOWN_METHOD_ERROR = -120;
	
	public final static SystemReturnCode UNKNOWN_METHOD_ERROR = new SystemReturnCode("mt参数服务端无法识别", CODE_UNKNOWN_METHOD_ERROR);
	
	public final static int CODE_UNKNOWN_ERROR = -130;
	
	public final static SystemReturnCode UNKNOWN_ERROR = new SystemReturnCode("未知错误", CODE_UNKNOWN_ERROR);
	
	
	public final static int CODE_PARAMETER_ERROR = -140;
	
	public final static SystemReturnCode PARAMETER_ERROR = new SystemReturnCode("参数错误", CODE_PARAMETER_ERROR);
	
	public final static int CODE_TIMEOUT_ERROR = -150;
	
	public final static SystemReturnCode TIMEOUT_ERROR = new SystemReturnCode("访问目标接口超时", CODE_TIMEOUT_ERROR);
	
	public final static int CODE_FORBIDDED_ERROR = -160;
	
	public final static SystemReturnCode FORBIDDED_ERROR = new SystemReturnCode("禁止访问目标接口", CODE_FORBIDDED_ERROR);

	public final static int CODE_SERIALIZATION_ERROR = -170;
	
	public final static SystemReturnCode SERIALIZATION_ERROR = new SystemReturnCode("序列化错误", CODE_SERIALIZATION_ERROR);
	
	public final static int CODE_NETWORK_ERROR = -180;
	
	public final static SystemReturnCode NETWORK_ERROR = new SystemReturnCode("网络错误", CODE_NETWORK_ERROR);
}
