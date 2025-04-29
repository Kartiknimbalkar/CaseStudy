package com.example.demo.repo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.RestockRequest;

@FeignClient(name = "Drug-Service", url = "http://localhost:8082/drugs")
public interface DrugClient {
	
	@PostMapping("/restock")
	void restockDrugs(@RequestBody RestockRequest restockRequest);

}