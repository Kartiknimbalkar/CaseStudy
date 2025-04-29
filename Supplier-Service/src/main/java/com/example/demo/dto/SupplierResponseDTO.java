package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierResponseDTO {
    private Long id;
    private String supplierName;
    private String supplierEmail;
    private String batchId;
    private int totalQuantitySupplied;
    private LocalDateTime lastRestockDate;
}
