package com.pharmcy.model.repo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pharmcy.model.OrderDto;

@FeignClient(name = "order-service")
public interface OrderClient {

	@GetMapping("/orders/get/{id}")
	OrderDto getOrderById(@PathVariable Long id);
	
}
