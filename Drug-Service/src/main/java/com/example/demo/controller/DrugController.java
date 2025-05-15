package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DrugDto;
import com.example.demo.dto.RestockRequest;
import com.example.demo.entity.Drug;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repo.DrugRepository;
import com.example.demo.service.DrugService;

import jakarta.validation.Valid;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/drugs")
@Validated
public class DrugController {
    private final DrugService drugService;
    
    @Autowired
    private DrugRepository drugRepository;
    
    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }
    
    @PostMapping("/add")
    public ResponseEntity<Drug> addDrug(@Valid @RequestBody DrugDto drugDto) {		// Add a new Drug
        System.out.println("Received DTO: " + drugDto);
    	Drug newDrug = drugService.addDrug(drugDto);
    	return ResponseEntity.ok(newDrug);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Drug> updateDrug(@PathVariable String id, @Valid @RequestBody DrugDto drugDto) 		// Update drug
            throws DrugNotFoundException {
        Drug updatedDrug = drugService.updateDrug(id, drugDto);
        return ResponseEntity.ok(updatedDrug);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteDrug(@PathVariable String id) throws DrugNotFoundException {	// Delete drug
        drugService.deleteDrug(id);
        return ResponseEntity.ok("Drug deleted successfully!");
    }

    @GetMapping("/getAll")
    	public ResponseEntity<List<Drug>> getAllDrugs() {		// List all drugs in the store
    		return ResponseEntity.ok(drugService.getAllDrugs());
    	}
    
    @GetMapping("/get/{id}")
    public Drug getDrugById(@PathVariable String id) throws DrugNotFoundException {		// List drug by id
        return drugService.getDrugById(id);
    }
    
    @PostMapping("/restock")
    public ResponseEntity<String> restockDrugs(@Valid @RequestBody RestockRequest restockRequest) throws DrugNotFoundException {	// Restock for the Supplier service
    	drugService.restockDrugs(restockRequest);
    	return ResponseEntity.ok("Drugs Restored Successfully");
    }
    
    @PutMapping("/failureRestock/{batch_id}/{quantity}")			// Payment Failure Rollback
    public void failureRestock(@PathVariable String batch_id, @PathVariable int quantity) {
    	Drug drug = drugRepository.findByBatchId(batch_id).get();
    	drug.setQuantity(drug.getQuantity() + quantity);
    	drugRepository.save(drug);
    }

    @PutMapping("/{id}/reduce-stock")				// Reduce stock for the Order service
    public ResponseEntity<String> reduceStock(@PathVariable String id, @RequestParam int quantity) throws DrugNotFoundException, InsufficientStockException {		
        drugService.reduceStock(id, quantity);
        return ResponseEntity.ok("Stock updated successfully!");
    }

}


