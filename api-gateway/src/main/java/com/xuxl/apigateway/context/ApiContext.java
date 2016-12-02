package com.xuxl.apigateway.context;

public class ApiContext {
	
	private long userId;
	
	private String terminal;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	
	public void clear() {
		userId = 0;
		terminal = null;
	}

}
