package com.spring;

import org.beanio.builder.StreamBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMongodbApplication.class, args);
	}
	
	@Bean
	public StreamBuilder streamBuilder() {
		return new StreamBuilder("");
	}
}
