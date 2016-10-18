package com.xuxl.common.exception;

import java.io.Serializable;

import com.xuxl.common.code.AbstractReturnCode;

public class ServiceException extends RuntimeException implements Serializable {

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
		super("code:[" + code.getDesc() + ":" + code.getCode() + "],msg:" + msg);
		this.code = code.getCode();
		this.displayCode = code.getDisplay().getCode();
		this.description = code.getDisplay().getDesc();
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public int getDisplayCode() {
		return displayCode;
	}

	public String getMsg() {
		return msg;
	}

	public String getDescription() {
		return description;
	}
}
