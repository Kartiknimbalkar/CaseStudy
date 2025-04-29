package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierUpdateDto {
	
    @NotBlank(message = "Supplier Name is required")
    private String supplierName;

    @NotBlank(message = "Supplier Email is required")
    private String supplierEmail;
    
    private String batchId;
    
}
