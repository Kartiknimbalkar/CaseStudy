package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long orderId;
    private Double amount;
//    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL", etc.

}
