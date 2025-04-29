package com.example.demo.exception;

public class RestockingException extends RuntimeException {
    public RestockingException(String message) {
        super(message);
    }
}
