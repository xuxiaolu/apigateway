package com.xuxl.apigateway.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.common.ApiInfo;
import com.xuxl.apigateway.common.ApiMethodInfo;
import com.xuxl.apigateway.context.ApiContext;
import com.xuxl.apigateway.context.ThreadContext;
import com.xuxl.apigateway.listener.RestApiParseListener;
import com.xuxl.common.annotation.http.api.ApiOperation.SecurityType;
import com.xuxl.common.utils.ParmaConstant;


public class StatelessAuthcFilter extends AccessControlFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(StatelessAuthcFilter.class);
	
	protected boolean isAccessAllowed(ServletRequest arg0, ServletResponse arg1, Object arg2) throws Exception {
		return false;
	}

	protected boolean onAccessDenied(ServletRequest arg0, ServletResponse arg1) throws Exception {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String uri = request.getRequestURI();
		String mt = uri.replace("/api/", "");
		ApiInfo apiInfo = RestApiParseListener.getRegisterMap().get(mt);
		if(apiInfo != null) {
			ApiMethodInfo methodInfo = apiInfo.getMethodInfo();
			if(methodInfo != null) {
				SecurityType security = methodInfo.getSecurity();
				if(SecurityType.SECURITY.equals(security)) {
					String _cid = request.getParameter(ParmaConstant.CLIENT_INFO);
					long _uid = 0L;
					if(_cid.indexOf(ParmaConstant.SEPARATOR) != -1) {
						_uid = Long.valueOf(_cid.substring(0, _cid.indexOf(ParmaConstant.SEPARATOR)));
					} 
					String _token = request.getParameter(ParmaConstant.TOKEN);
					StatelessToken token = new StatelessToken(_uid, _token, _cid);
					try {
						Subject subject = getSubject(request, response);
						subject.login(token);
						ApiContext context = ThreadContext.getContext();
						context.clear();
						context.setUserId(_uid);
						return true;
					} catch(Exception e) {
						logger.error("验证失败！-",e);
						return false;
					}
				} else {
					return true;
				} 
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
