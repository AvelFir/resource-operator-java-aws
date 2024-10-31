package com.github.gmv.resource.operator.java.aws.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.github.gmv.resource.operator.java.aws")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
