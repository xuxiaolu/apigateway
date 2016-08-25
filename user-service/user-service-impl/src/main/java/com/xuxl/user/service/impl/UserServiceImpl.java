package com.xuxl.user.service.impl;


import java.util.Date;

import com.alibaba.dubbo.config.annotation.Service;
import com.xuxl.user.service.api.UserService;
import com.xuxl.user.service.domain.User;

@Service(version = "latest")
public class UserServiceImpl implements UserService {

	public User getUser(int age) {
		User user = new User();
		user.setAge(age);
		return user;
	}

	public User getUser(String name) {
		User user = new User();
		user.setName(name);
		return user;
	}

	public User getUser(Date date) {
		User user = new User();
		user.setBirthday(date);
		return user;
	}

}
