package com.example.demo.repo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.DrugDto;


@FeignClient(name = "Drug-Service", url = "http://localhost:8082/drugs")
public interface DrugClient {

    @GetMapping("/get/{id}")
    DrugDto getDrugById(@PathVariable String id);
    
    @PutMapping("/{id}/reduce-stock")
    String reduceStock(@PathVariable String id, @RequestParam int quantity);
    
    @PutMapping("/failureRestock/{batch_id}/{quantity}")
    void failureRestock(@PathVariable String batch_id, @PathVariable int quantity);
    
}