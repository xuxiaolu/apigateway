package com.xuxl.common.code;

import java.io.Serializable;

public abstract class AbstractReturnCode implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
    private final String desc;
    
    private final int code;
    
    private final AbstractReturnCode display;

    public AbstractReturnCode(String desc, int code) {
        this.desc = desc;
        this.code = code;
        this.display = this;
    }

    public AbstractReturnCode(int code, AbstractReturnCode shadow) {
        this.desc = null;
        this.code = code;
        this.display = shadow;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public AbstractReturnCode getDisplay() {
        return display;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

}
