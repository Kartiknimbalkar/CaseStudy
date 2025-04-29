package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;
    private String batchId; // Foreign Key (Drug Batch ID)
    private int quantity;
    private double totalPrice;
    private double paidAmount;
    private double balance;
    
    @Column(name = "sale_date")
    private LocalDate saleDate;

    private String doctorName;
    
    public Sales(Long orderId, String batchId, int quantity, double totalPrice) {
        this.orderId = orderId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    
}

