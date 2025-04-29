package com.example.demo.dto;

import java.util.Date;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrugDto {
	
	@NotBlank(message = "Batch ID is required")
	private String batchId;
	@NotBlank(message = "Name is required")
	private String name;
	@NotBlank(message = "Manufacturer is required")
	private String manufacturer;
	@Positive(message = "Price must be positive")
	private double price;
//	@Min(value = 1, message = "Minimum Quantity should be atleast 1")
//    private int quantity;
//	@NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private Date expiryDate;
	
}
