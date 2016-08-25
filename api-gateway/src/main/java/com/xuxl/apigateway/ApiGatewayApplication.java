package com.xuxl.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xuxl.apigateway.annotation.EnableDubboConfiguration;

@SpringBootApplication
@EnableDubboConfiguration
public class ApiGatewayApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	
}
