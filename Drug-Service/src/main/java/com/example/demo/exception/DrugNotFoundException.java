package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DrugNotFoundException extends Exception{
	public DrugNotFoundException(String message) {
		super(message);
		log.warn("Drug not found");
	}
}
