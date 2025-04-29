package com.example.demo.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drugs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Drug {
    @Id
    @Column(name = "batchId")
    private String batchId;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "manufacturer")
    private String manufacturer;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "price")
    private double price;
    
    @Column(name = "expiry")
    private Date expiryDate;
}
