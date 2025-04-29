package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsufficientStockException extends Exception{
	public InsufficientStockException(String message) {
		super(message);
		log.warn("Stock is insufficient");
	}
}
