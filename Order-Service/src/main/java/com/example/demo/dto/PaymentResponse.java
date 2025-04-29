package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResponse {
    private String transactionId;
    private String status; // "SUCCESS", "FAILED"
    private String message;

}
