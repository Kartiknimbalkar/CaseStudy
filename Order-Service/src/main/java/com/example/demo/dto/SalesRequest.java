package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesRequest {
    private Long orderId;
    private String batchId;
    private int quantity;
    private double totalPrice;
    private double paidAmount; // Actually paid amount
    private String doctorName;
}
