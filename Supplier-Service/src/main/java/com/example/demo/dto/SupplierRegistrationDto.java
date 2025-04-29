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
public class SupplierRegistrationDto {
    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotBlank(message = "Supplier email is required")
    private String supplierEmail;

    @NotBlank(message = "Batch ID is required")
    private String batchId;
}
