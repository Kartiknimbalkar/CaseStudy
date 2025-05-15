package com.pharmcy.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pharmcy.model.PaymentDto;

public interface PaymentRepo extends JpaRepository<PaymentDto, String> {
    PaymentDto findByRazorpayOrderId(String razorpayOrderId);
}
