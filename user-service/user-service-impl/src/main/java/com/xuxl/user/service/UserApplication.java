package com.xuxl.user.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class UserApplication {
	
	public static void main(String[] args) throws InterruptedException {
		new SpringApplicationBuilder(UserApplication.class).web(false).run(args);
		CountDownLatch closeLatch = new CountDownLatch(1);
		closeLatch.await();
	}

}
