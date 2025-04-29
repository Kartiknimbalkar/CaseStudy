package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.RestockRequest;
import com.example.demo.dto.SupplierHistoryDto;
import com.example.demo.dto.SupplierRegistrationDto;
import com.example.demo.dto.SupplierResponseDTO;
import com.example.demo.dto.SupplierUpdateDto;
import com.example.demo.exception.RestockingException;
import com.example.demo.exception.SupplierAlreadyExistsException;
import com.example.demo.exception.SupplierNotFoundException;
import com.example.demo.model.Supplier;
import com.example.demo.model.SupplierRestockHistory;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.SupplierRepo;
import com.example.demo.repo.SupplierRestockHistoryRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SupplierService {

	@Autowired
	private SupplierRepo supplierRepo;
	
	@Autowired
	private DrugClient client;
	
	@Autowired
	private SupplierRestockHistoryRepo supplierRestockHistoryRepo;
	
	public SupplierRegistrationDto addSupplier(SupplierRegistrationDto dto) {		// add new supplier
	    log.info("Checking if supplier already exists...");

	    // Check if a supplier with the same name and email exists
	    boolean exists = supplierRepo.existsBySupplierNameAndSupplierEmail(
	        dto.getSupplierName(), dto.getSupplierEmail()
	    );

	    if (exists) {
	        throw new SupplierAlreadyExistsException("Supplier with the same name and email already exists.");
	    }

	    // ensuring only one supplier per drug batchid
	    boolean batchIdExists = supplierRepo.existsByBatchId(dto.getBatchId());
	    if (batchIdExists) {
	        throw new SupplierAlreadyExistsException("A supplier already exists for this batch ID.");
	    }

	    Supplier supplier = new Supplier();
	    supplier.setSupplierName(dto.getSupplierName());
	    supplier.setSupplierEmail(dto.getSupplierEmail());
	    supplier.setBatchId(dto.getBatchId());
//	    supplier.setRestockDate(new Date());

	    supplierRepo.save(supplier);

	    log.info("Supplier registered: {}", dto.getSupplierName());
	    return dto;
	}



	
	public void restockDrug(RestockRequest request) {			// restock drugs
	    // Check if a supplier exists for the given batch ID
	    Supplier supplier = supplierRepo.findByBatchId(request.getBatchId())
	                                    .stream()
	                                    .findFirst()
	                                    .orElse(null);

	    if (supplier == null) {
	        throw new SupplierNotFoundException("No supplier found for this batch ID.");
	    }

	    // Prepare restock request for Drug-Service
	    RestockRequest restockRequest = new RestockRequest();
	    restockRequest.setBatchId(request.getBatchId());
	    restockRequest.setQuantity(request.getQuantity());
	    restockRequest.setExpiryDate(request.getExpiryDate());

	    try {
	        // Call Drug-Service to restock using the feign client
	        client.restockDrugs(restockRequest);

	        // Update supplier's quantity in the db for records
	        supplier.setQuantity(supplier.getQuantity() + request.getQuantity());
	        supplier.setRestockDate(LocalDateTime.now());
	        supplierRepo.save(supplier);
	        
	        SupplierRestockHistory history = new SupplierRestockHistory();
	        history.setSupplierName(supplier.getSupplierName());
	        history.setBatchId(supplier.getBatchId());
	        history.setQuantity(request.getQuantity());
	        history.setRestockDate(LocalDateTime.now());

	        supplierRestockHistoryRepo.save(history);


	        log.info("Successfully restocked drug and updated supplier quantity for batchId: {}", request.getBatchId());
	    } catch (Exception e) {
	        throw new RestockingException("Error occurred while restocking the drug.");
	    }
	}

	
	public Supplier updateSupplier(Long id, SupplierUpdateDto supplierDto) {		// update a supplier
		
		log.info("In Update Supplier Method...");
		
	    Supplier supplier = supplierRepo.findById(id)
	            .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));

	    supplier.setSupplierName(supplierDto.getSupplierName());
	    supplier.setSupplierEmail(supplierDto.getSupplierEmail());
	    if (supplierDto.getBatchId() != null) {
		    supplier.setBatchId(supplierDto.getBatchId());
		} else {
			supplier.setBatchId(supplier.getBatchId());
		}
	    
	    log.info("Updated the Supplier Successfully");

	    return supplierRepo.save(supplier);
	}

	public void deleteSupplier(Long id) {			// delete a supplier
		
		log.info("In Delete Supplier Method...");
		
	    Supplier supplier = supplierRepo.findById(id)
	            .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));
	    
	    log.info("Supplier Deleted Successfully");
	    
	    supplierRepo.delete(supplier);
	}
	
	public List<SupplierResponseDTO> getAllSupplier() {		// get all suppliers
	    List<Supplier> suppliers = supplierRepo.findAll();
	    return suppliers.stream()
	        .map(s -> new SupplierResponseDTO(
	            s.getId(),
	            s.getSupplierName(),
	            s.getSupplierEmail(),
	            s.getBatchId(),
	            s.getQuantity(),
	            s.getRestockDate()
	        ))
	        .toList();
	}


	
	public List<SupplierHistoryDto> getSupplierHistoryByBatchId(String batchId) {		// history
	    log.info("Fetching complete restock history for batch ID: {}", batchId);

	    List<SupplierRestockHistory> historyList = supplierRestockHistoryRepo.findByBatchId(batchId);
	    
	    if (historyList.isEmpty()) {
	        throw new SupplierNotFoundException("No restock history found for batch ID: " + batchId);
	    }

	    return historyList.stream()
	        .map(history -> new SupplierHistoryDto(
	            history.getSupplierName(),
	            history.getQuantity(),
	            history.getRestockDate()
	        ))
	        .collect(Collectors.toList());
	}
}
