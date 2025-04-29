package com.example.demo.dto;

import com.example.demo.model.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPaymentResponse {

    private Order order;
    private PaymentResponse paymentResponse;

}
