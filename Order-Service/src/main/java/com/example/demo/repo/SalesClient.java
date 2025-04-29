package com.example.demo.repo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.SalesRequest;

@FeignClient(name = "Sales-Service", url = "http://localhost:8085")
public interface SalesClient {

    @PostMapping("/sales/record")
    void recordSale(@RequestBody SalesRequest salesRequest);
}
