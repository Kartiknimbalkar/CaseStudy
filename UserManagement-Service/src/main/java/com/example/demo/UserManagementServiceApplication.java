package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class UserManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementServiceApplication.class, args);
//		PasswordEncoder encoder = new BCryptPasswordEncoder();
//		  boolean isMatch = encoder.matches("doctor123", "$2a$10$V.Ml9Z/LcYoA9m9WLaNw1uL7ixk0qOi2n8or7SPljoj0fLe/Ogcyu"); // Replace with actual hash
//		  System.out.println("Password valid: " + isMatch);
	}

}
