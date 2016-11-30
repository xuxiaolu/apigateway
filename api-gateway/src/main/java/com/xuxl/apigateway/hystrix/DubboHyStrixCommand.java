package com.xuxl.apigateway.hystrix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.RpcException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.xuxl.apigateway.code.SystemReturnCode;
import com.xuxl.apigateway.common.BaseResponse;
import com.xuxl.common.exception.ServiceException;

public class DubboHyStrixCommand extends HystrixCommand<BaseResponse<Object>> {
	
	private static final Logger logger = LoggerFactory.getLogger(DubboHyStrixCommand.class);

	private final Object proxy;
	
	private final Method method;
	
	private final Object[] args;

	public DubboHyStrixCommand(Object proxy, Method method, Object[] args,int timeout) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(method.getDeclaringClass().getName()))
				.andCommandKey(HystrixCommandKey.Factory.asKey(method.getName()))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withFallbackEnabled(true)
						.withCircuitBreakerRequestVolumeThreshold(20)
						.withCircuitBreakerSleepWindowInMilliseconds(3000)
						.withCircuitBreakerErrorThresholdPercentage(50)
						.withExecutionTimeoutInMilliseconds(timeout)
						.withExecutionTimeoutEnabled(true))
				);
		this.proxy = proxy;
		this.method = method;
		this.args = args;
	}
	
	protected BaseResponse<Object> run() throws Exception {
		long start = System.currentTimeMillis();
		try {
			Object result = method.invoke(proxy, args);
			BaseResponse<Object> response = new BaseResponse<>();
			response.setCode(SystemReturnCode.SUCCESS.getCode());
			response.setMsg(SystemReturnCode.SUCCESS.getMsg());
			response.setDate(new Date());
			response.setResult(result);
			return response;
		} finally {
			long end = System.currentTimeMillis() - start;
			logger.info(String.format("invoke %s.%s method, take %s ms", method.getDeclaringClass().getName(), method.getName(), end));
		}
	}
	
	protected BaseResponse<Object> getFallback() {
		Throwable throwable = getExecutionException();
		if(throwable == null) return null;
		logger.error(throwable);
		BaseResponse<Object> response = new BaseResponse<>();
		response.setDate(new Date());
		if(InvocationTargetException.class.isInstance(throwable)) {
			InvocationTargetException ie = (InvocationTargetException) throwable;
			Throwable target = ie.getTargetException();
			if(ServiceException.class.isInstance(target)) {
				response.setCode(((ServiceException)target).getCode());
				response.setMsg(((ServiceException)target).getMsg());
			} else if(RpcException.class.isInstance(target)) {
				RpcException re = (RpcException) target;
				int code = re.getCode();
				switch (code) {
				case RpcException.UNKNOWN_EXCEPTION:
					response.setCode(SystemReturnCode.UNKNOWN_ERROR.getCode());
					response.setMsg(SystemReturnCode.UNKNOWN_ERROR.getMsg());
					break;
				case RpcException.NETWORK_EXCEPTION:
					response.setCode(SystemReturnCode.NETWORK_ERROR.getCode());
					response.setMsg(SystemReturnCode.NETWORK_ERROR.getMsg());
					break;
				case RpcException.TIMEOUT_EXCEPTION:
					response.setCode(SystemReturnCode.TIMEOUT_ERROR.getCode());
					response.setMsg(SystemReturnCode.TIMEOUT_ERROR.getMsg());
					break;
				case RpcException.FORBIDDEN_EXCEPTION:
					response.setCode(SystemReturnCode.FORBIDDED_ERROR.getCode());
					response.setMsg(SystemReturnCode.FORBIDDED_ERROR.getMsg());
					break;
				case RpcException.SERIALIZATION_EXCEPTION:
					response.setCode(SystemReturnCode.SERIALIZATION_ERROR.getCode());
					response.setMsg(SystemReturnCode.SERIALIZATION_ERROR.getMsg());
				default:
					response.setCode(SystemReturnCode.UNKNOWN_ERROR.getCode());
					response.setMsg(SystemReturnCode.UNKNOWN_ERROR.getMsg());
					break;
				}
			}
		} else if(HystrixTimeoutException.class.isInstance(throwable)) {
			response.setCode(SystemReturnCode.TIMEOUT_ERROR.getCode());
			response.setMsg(SystemReturnCode.TIMEOUT_ERROR.getMsg());
		} else {
			response.setCode(SystemReturnCode.UNKNOWN_ERROR.getCode());
			response.setMsg(SystemReturnCode.UNKNOWN_ERROR.getMsg());
		}
		return response;
	}
	
}
