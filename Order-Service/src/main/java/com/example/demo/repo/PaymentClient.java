//package com.example.demo.repo;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import com.example.demo.dto.PaymentRequest;
//
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//
//@FeignClient(name = "payment-service")
//public interface PaymentClient {
//
//	@PostMapping("/api/data")
//	void makePayment(@RequestBody PaymentRequest paymentRequest);
//	
//}
