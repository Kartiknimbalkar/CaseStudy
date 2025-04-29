package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SupplierHistoryDto {
    private String supplierName;
    private int quantity;
    private LocalDateTime restockDate;
}
