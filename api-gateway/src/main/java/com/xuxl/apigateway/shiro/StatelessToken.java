package com.xuxl.apigateway.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class StatelessToken implements AuthenticationToken {

	private static final long serialVersionUID = 1L;
	
    private long userId; 
    
    private String digest;
    
    private String cid;
    
	public StatelessToken(long userId, String digest, String cid) {
		this.userId = userId;
		this.digest = digest;
		this.cid = cid;
	}
	
	public Object getPrincipal() {
		return userId;
	}

	public Object getCredentials() {
		return digest;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}
	
}
