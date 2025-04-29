package com.example.demo.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesRequest {
    private Long orderId;
    @NotBlank(message = "Order ID cannot be blank")
    private String batchId;
    @Min(value = 1, message = "Quantity should be atleast 1")
    private int quantity;
    @Positive(message = "Total price cannot be negative")
    private double totalPrice;
    @Positive(message = "Paid amount cannot be negative")
    private double paidAmount; // Amount actually paids
    @NotBlank(message = "Doctor name cannot be blank")
    private String doctorName;
}
