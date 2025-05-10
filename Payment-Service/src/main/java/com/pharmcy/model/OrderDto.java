package com.pharmcy.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "order_table")
public class OrderDto {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batch_id;  // Drug being ordered
    private int quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, CONFIRMED, DISPATCHED, DELIVERED

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    
    private String doctorName;
    private String doctorContact;
    private String doctorEmail;

    @ElementCollection
    private List<String> drugNames;  // List of drug names in the order

    @ElementCollection
    private List<Double> drugPrices; // Prices of each drug in the order

    private double totalPrice;
    private double paidAmount; // Amount actually paid
    
    @Temporal(TemporalType.DATE)
    private Date pickupDate;
    
    private String razorpayOrderId;
    
}

