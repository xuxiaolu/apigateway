package com.xuxl.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.xuxl.apigateway.listener.DubboRegisterInitializer;

@SpringBootApplication
public class ApiGatewayApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(ApiGatewayApplication.class);
		application.addInitializers(new DubboRegisterInitializer());
		application.run(args);
		logger.info("api-gateway has started");
	}
}
