package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RestockRequest;
import com.example.demo.dto.SupplierHistoryDto;
import com.example.demo.dto.SupplierRegistrationDto;
import com.example.demo.dto.SupplierResponseDTO;
import com.example.demo.dto.SupplierUpdateDto;
import com.example.demo.exception.SupplierNotFoundException;
import com.example.demo.model.Supplier;
import com.example.demo.service.SupplierService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/suppliers")
@Validated
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    
    // restock drugs
    @PostMapping("/restock")
    public ResponseEntity<String> restockDrug(@Valid @RequestBody RestockRequest request) {
        try {
            supplierService.restockDrug(request);
            return ResponseEntity.ok("Drug restocked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<SupplierRegistrationDto> addSupplier(@Valid @RequestBody SupplierRegistrationDto supplierDto) {		// add a new supplier
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.addSupplier(supplierDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    

	@PutMapping("/update/{id}")								// update a existing supplier
	public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierUpdateDto supplierDto) {
	    try {
	        Supplier updatedSupplier = supplierService.updateSupplier(id, supplierDto);
	        return ResponseEntity.ok(updatedSupplier);
	    } catch (SupplierNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {			// delete a supplier
	    try {
	        supplierService.deleteSupplier(id);
	        return ResponseEntity.ok("Supplier deleted successfully.");
	    } catch (SupplierNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}

	@GetMapping("/history/{batchId}")		// history
	public ResponseEntity<List<SupplierHistoryDto>> getHistory(@PathVariable String batchId) {
	    List<SupplierHistoryDto> history = supplierService.getSupplierHistoryByBatchId(batchId);
	    return ResponseEntity.ok(history);
	}
    
    @GetMapping("/listAll")	
    public List<SupplierResponseDTO> getAllHistory() {					// list all the suppliers
    	return supplierService.getAllSupplier();
    }
}
