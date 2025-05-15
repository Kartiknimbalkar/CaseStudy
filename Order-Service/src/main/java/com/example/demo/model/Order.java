package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import com.example.demo.dto.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batch_id;  // Drug being ordered
    private int quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, CONFIRMED, DISPATCHED, DELIVERED

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime orderDate;
    
    private String doctorName;
    private String doctorContact;
    private String doctorEmail;

    @ElementCollection
    private List<String> drugNames;  // List of drug names in the order

    @ElementCollection
    private List<Double> drugPrices; // Prices of each drug in the order

    private double totalPrice;
    private double paidAmount; // Amount actually paid
    
    private LocalDateTime pickupDate;
    
}

