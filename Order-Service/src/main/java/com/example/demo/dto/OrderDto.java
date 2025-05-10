package com.example.demo.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
	private Long id;
	@NotBlank(message = "Batch ID cannot be null")
    private String batch_id;
	@Min(value = 1, message = "Quantity should be minimum 1")
    private int quantity;
    private OrderStatus status;
    private Date orderDate;
    
//    @NotBlank(message = "Payment Method should not be Blank")
    private String paymentMethod;		// 

    // New fields
    @NotBlank(message = "Doctor name cannot be empty")
    private String doctorName;
    @NotBlank(message = "Contact shouldn't be empty")
    private String doctorContact;
    @NotBlank(message = "Email ID shouldn't be empty")
    @Email
    private String doctorEmail;
    
//    private List<String> drugNames;
//    private List<Double> drugPrices;
//    private double totalPrice;
    
//    @Positive(message = "Paid Amount cannot be negative")
    private double paidAmount; // Amount actually paid
    private Date pickupDate;
}
