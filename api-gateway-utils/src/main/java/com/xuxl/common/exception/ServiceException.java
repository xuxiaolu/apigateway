package com.xuxl.common.exception;

import java.io.Serializable;

import com.xuxl.common.code.AbstractReturnCode;

public class ServiceException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;
	
	private int displayCode;
	
	private String description;

	private String msg;

	public ServiceException(AbstractReturnCode code) {
		this(code, code.getDesc());
	}

	public ServiceException(AbstractReturnCode code, String msg) {
		super("code:" + code.getDesc() + ":" + code.getCode() + "msg:" + msg);
		this.code = code.getCode();
		this.displayCode = code.getDisplay().getCode();
		this.description = code.getDisplay().getDesc();
		this.msg = msg;
	}

	/*
	 * 内部使用code
	 */
	public int getCode() {
		return code;
	}

	/*
	 * 对外显示code
	 */
	public int getDisplayCode() {
		return displayCode;
	}

	/*
	 * 内部使用message
	 */
	public String getMsg() {
		return msg;
	}

	/*
	 * 对外显示message
	 */
	public String getDescription() {
		return description;
	}
}
