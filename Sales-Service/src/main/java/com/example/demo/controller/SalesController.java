package com.example.demo.controller;

import com.example.demo.model.Sales;
import com.example.demo.model.SalesRequest;
import com.example.demo.repo.SalesRepository;
import com.example.demo.service.SalesReportService;
import com.example.demo.service.SalesService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Validated
public class SalesController {

    private final SalesService salesService;
    
    @Autowired
    private SalesRepository salesRepository;
    
    @Autowired
    private SalesReportService salesReportService;

    @PostMapping("/record")
    public ResponseEntity<Sales> recordSale(@Valid @RequestBody SalesRequest salesRequest) {		// record a new sale
        return ResponseEntity.ok(salesService.recordSale(salesRequest));
    }
    
    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadSalesReport() {			// download the pdf of the sales
        try {
            byte[] pdfBytes = salesReportService.generateSalesReport();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=sales_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Sales>> getSalesHistory(
        @RequestParam("startDate") 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {			// sales history

        List<Sales> salesHistory = salesRepository.findBySaleDate(startDate);
        
        if (salesHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(salesHistory);
    }
    
    @GetMapping("/getAll")
    public List<Sales> getAllSalesReport() {		// get all sales report
    	return salesService.getAllSales();
    }

}
