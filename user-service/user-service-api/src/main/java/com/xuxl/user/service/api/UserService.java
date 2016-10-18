package com.xuxl.user.service.api;

import java.util.Date;

import com.xuxl.common.annotation.ApiGroup;
import com.xuxl.common.annotation.ApiParameter;
import com.xuxl.common.annotation.HttpApi;
import com.xuxl.user.service.domain.User;

@ApiGroup(name = "user",owner = "xuxl")
public interface UserService {
	
	/**
	 * 基本数据类型测试
	 * @param age
	 * @return
	 */
	@HttpApi(desc = "根据年龄获得用户", name = "age",type = "get")
	User getUser(@ApiParameter(desc = "年龄", name = "age", required = true) int age);
	
	/**
	 * String类型测试
	 * @param name
	 * @return
	 */
	@HttpApi(desc = "根据名字获得用户", name = "name",type = "get")
	User getUser(@ApiParameter(desc = "名字", name = "name", required = true) String name);
	
	/**
	 * 日期类型测试，前端可以给数字
	 * @param date
	 * @return
	 */
	@HttpApi(desc = "根据生日获得用户", name = "birthday", type = "get")
	User getUser(@ApiParameter(desc = "生日", name = "birthday", required = true) Date date);

}
