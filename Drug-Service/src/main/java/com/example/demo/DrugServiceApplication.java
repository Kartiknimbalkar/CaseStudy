package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.example.demo.*")
@Slf4j
public class DrugServiceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(DrugServiceApplication.class, args);
		log.info("Drug Service Application Started...");
	}

}
