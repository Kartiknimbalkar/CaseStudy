package com.example.demo.service;

import com.example.demo.model.Sales;
import com.example.demo.model.SalesRequest;
import com.example.demo.repo.SalesRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {
    
    private final SalesRepository salesRepository;

    public Sales recordSale(SalesRequest request) {		// record a sale
    	
    	log.info("In the record sale method");
    	
        Sales sales = new Sales();
        sales.setOrderId(request.getOrderId());
        sales.setBatchId(request.getBatchId());
        sales.setQuantity(request.getQuantity());
        sales.setTotalPrice(request.getTotalPrice());
        sales.setPaidAmount(request.getPaidAmount());

        double balance = request.getTotalPrice() - request.getPaidAmount();
        sales.setBalance(balance);
        sales.setSaleDate(LocalDate.now());
        sales.setDoctorName(request.getDoctorName());

        log.info("Sale Recorded via the Order-Service's Verify method");
        
        return salesRepository.save(sales);
    }
    
    public List<Sales> getSalesHistory(LocalDate startDate) {		// get sales history from date
    	log.info("Listing the Sales History from Date : {}", startDate);
        return salesRepository.findBySaleDate(startDate);
    }
    
    public List<Sales> getAllSales() {		// get all sales
    	log.info("Listing All the Sales");
    	return salesRepository.findAll();
    }
}
