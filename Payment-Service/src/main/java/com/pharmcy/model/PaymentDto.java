package com.pharmcy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "paystatus")
public class PaymentDto {
    @Id
    private String razorpayOrderId;    // primary key
    private Long   clientOrderRef;     // you can store a temp client-generated ID
    private double total;              // amount in rupees
    private String orderStatus;        // CREATED, PAYMENT_DONE, FAILED
    private String razorpayPaymentId;  // set on success callback
    private Long order_id;
}
