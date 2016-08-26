package com.xuxl.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

@SpringBootApplication
public class ApiGatewayApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
		logger.info("api-gateway has started");
	}
}
