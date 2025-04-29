package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.DrugDto;
import com.example.demo.dto.RestockRequest;
import com.example.demo.entity.Drug;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repo.DrugRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DrugService {
    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {		// constructor injection
        this.drugRepository = drugRepository;
        log.info("Injecting DrugRepository via constructor injection");
    }

    public Drug getDrugById(String id) throws DrugNotFoundException {		// drug by id
    	
    	log.info("searching for Drugs by Id: {}", id);
        Optional<Drug> foundDrug = drugRepository.findById(id);
        
        if (foundDrug.isPresent()) {
			log.info("Drug found with Id: {}", id);
		} else {
			log.warn("No Drug found for Id: {}", id);
			throw new DrugNotFoundException("Drugs Not Found");
		}
        
        return foundDrug.get();
    }
    
    public Drug addDrug(DrugDto drugDto) {		// add a drug
    	log.info("Adding Drugs");
    	Drug drug = new Drug();
    	drug.setBatchId(drugDto.getBatchId());
    	drug.setName(drugDto.getName());
    	drug.setManufacturer(drugDto.getManufacturer());
    	drug.setPrice(drugDto.getPrice());
//    	drug.setQuantity(drugDto.getQuantity());
    	drug.setExpiryDate(drugDto.getExpiryDate());
    	
    	Drug savedDrug = drugRepository.save(drug);
    	log.info("Drugs saved successfully");
    	return savedDrug;
    }
    
    @Transactional
    public Drug updateDrug(String id, DrugDto drugDto) throws DrugNotFoundException {		// update drug
    	
    	log.info("Updating Drugs");
    	
        Drug drug = getDrugById(id);

        drug.setName(drugDto.getName());
        drug.setManufacturer(drugDto.getManufacturer());
        drug.setPrice(drugDto.getPrice());
//        drug.setQuantity(drugDto.getQuantity());
//        drug.setExpiryDate(drugDto.getExpiryDate());
        if (drugDto.getExpiryDate() != null) {
            drug.setExpiryDate(drugDto.getExpiryDate());
        } else {
        	drug.setExpiryDate(drug.getExpiryDate());
        }


    	log.info("Drugs updated successfully");
        
        return drugRepository.save(drug);
    }

    @Transactional
    public void deleteDrug(String id) throws DrugNotFoundException {		// delete drug
    	
    	log.info("Deleting Drugs with Id: {}", id);

        Drug drug = getDrugById(id);
        drugRepository.delete(drug);
        
    	log.info("Drugs deleted successfully with Id: {}", id);

    }

    
    public void restockDrugs(RestockRequest restockRequest) throws DrugNotFoundException {		// restock drug
    	
    		log.info("Restocking Drugs...");
    		Drug drug = drugRepository.findByBatchId(restockRequest.getBatchId()).orElseThrow(() -> new DrugNotFoundException("Drug Not found for the batch id: "+restockRequest.getBatchId()));
    		
    		drug.setQuantity(drug.getQuantity() + restockRequest.getQuantity());
    		drug.setExpiryDate(restockRequest.getExpiryDate());
    		
    		drugRepository.save(drug);
        	log.info("Drugs restocked successfully");

    }

    @Transactional
    public void reduceStock(String id, int quantity) throws DrugNotFoundException, InsufficientStockException{ 		// reduce stock after placing order
        Drug drug = getDrugById(id);

        if (drug.getQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available for drug: "+drug.getName());
        }

        drug.setQuantity(drug.getQuantity() - quantity);
        drugRepository.save(drug);
        
    	log.info("Drugs with Id: {} Reduced successfully post the order verification...", id);

    }

	public List<Drug> getAllDrugs() {							// list all drugs
		log.info("Displaying all the Drugs present in the Inventory");
		return drugRepository.findAll();
	}
}
