package com.pharmcy.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmcy.model.PaymentDto;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentDto, Long> {

	PaymentDto findByRazorpayOrderId(String razorpayId);

}
