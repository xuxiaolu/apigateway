package com.xuxl.apigateway.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.utils.HashUtils;

public class StatelessRealm extends AuthorizingRealm {
	
	private static final Logger logger = LoggerFactory.getLogger(StatelessRealm.class);
	
	public boolean supports(AuthenticationToken token) {
		return token instanceof StatelessToken;
	}

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo =  new SimpleAuthorizationInfo();
        authorizationInfo.addRole("admin");
        return authorizationInfo;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		StatelessToken statelessToken = (StatelessToken) token;
		long userId = (long) statelessToken.getPrincipal();
		String _cid = statelessToken.getCid();
		String serverDigetst = HashUtils.digest("1234",_cid);
		logger.info(serverDigetst);
        return new SimpleAuthenticationInfo(userId,serverDigetst,getName());
	}

}
