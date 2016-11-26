package com.xuxl.apigateway.common;

import java.io.Serializable;
import java.util.Set;

public class ApiReturnInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private String desc;
	
	private String generic;
	
	private Set<ApiReturnFieldInfo> fields;
	
	private Set<ApiReturnInfo> childReturns;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGeneric() {
		return generic;
	}

	public void setGeneric(String generic) {
		this.generic = generic;
	}

	public Set<ApiReturnFieldInfo> getFields() {
		return fields;
	}

	public void setFields(Set<ApiReturnFieldInfo> fields) {
		this.fields = fields;
	}

	public Set<ApiReturnInfo> getChildReturns() {
		return childReturns;
	}

	public void setChildReturns(Set<ApiReturnInfo> childReturns) {
		this.childReturns = childReturns;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childReturns == null) ? 0 : childReturns.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((generic == null) ? 0 : generic.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiReturnInfo other = (ApiReturnInfo) obj;
		if (childReturns == null) {
			if (other.childReturns != null)
				return false;
		} else if (!childReturns.equals(other.childReturns))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (generic == null) {
			if (other.generic != null)
				return false;
		} else if (!generic.equals(other.generic))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
