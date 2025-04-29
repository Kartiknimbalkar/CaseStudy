package com.example.demo;

import com.example.demo.dto.DrugDto;
import com.example.demo.dto.RestockRequest;
import com.example.demo.entity.Drug;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repo.DrugRepository;
import com.example.demo.service.DrugService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrugServiceTest {

    @InjectMocks
    private DrugService drugService;

    @Mock
    private DrugRepository drugRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDrug_shouldReturnSavedDrug() {
        DrugDto dto = new DrugDto("B123", "Paracetamol", "Cipla", 25.0, new Date());
        Drug saved = new Drug("B123", "Paracetamol", "Cipla", 100, 25.0, new Date());

        when(drugRepository.save(any(Drug.class))).thenReturn(saved);

        Drug result = drugService.addDrug(dto);

        assertEquals("Paracetamol", result.getName());
        assertEquals("B123", result.getBatchId());
        verify(drugRepository).save(any(Drug.class));
    }

    @Test
    void testGetDrugById_found() throws DrugNotFoundException {
        Drug drug = new Drug("B123", "Dolo", "Micro Labs", 50, 20.0, new Date());
        when(drugRepository.findById("B123")).thenReturn(Optional.of(drug));

        Drug found = drugService.getDrugById("B123");

        assertEquals("Dolo", found.getName());
    }

    @Test
    void testGetDrugById_notFound() {
        when(drugRepository.findById("B404")).thenReturn(Optional.empty());

        assertThrows(DrugNotFoundException.class, () -> drugService.getDrugById("B404"));
    }

    @Test
    void testUpdateDrug_success() throws DrugNotFoundException {
        String id = "B123";
        Drug existing = new Drug(id, "Old", "OldManu", 20, 10.0, new Date());
        DrugDto dto = new DrugDto(id, "New", "NewManu", 12.5, new Date());

        when(drugRepository.findById(id)).thenReturn(Optional.of(existing));
        when(drugRepository.save(any(Drug.class))).thenReturn(existing);

        Drug updated = drugService.updateDrug(id, dto);

        assertEquals("New", updated.getName());
        assertEquals("NewManu", updated.getManufacturer());
//        assertEquals(100, updated.getQuantity());
    }

    @Test
    void testDeleteDrug_success() throws DrugNotFoundException {
        Drug drug = new Drug("B123", "TestDrug", "TestLab", 30, 12.0, new Date());
        when(drugRepository.findById("B123")).thenReturn(Optional.of(drug));

        drugService.deleteDrug("B123");

        verify(drugRepository).delete(drug);
    }

    @Test
    void testRestockDrug_success() throws DrugNotFoundException {
        Drug drug = new Drug("B123", "Amox", "Sun Pharma", 20, 30.0, new Date());
        RestockRequest request = new RestockRequest("B123", 50, new Date());

        when(drugRepository.findByBatchId("B123")).thenReturn(Optional.of(drug));
        when(drugRepository.save(any(Drug.class))).thenReturn(drug);

        drugService.restockDrugs(request);

        verify(drugRepository).save(drug);
        assertEquals(70, drug.getQuantity());
    }

    @Test
    void testReduceStock_success() throws DrugNotFoundException, InsufficientStockException {
        Drug drug = new Drug("B123", "Dolo", "Micro Labs", 50, 20.0, new Date());

        when(drugRepository.findById("B123")).thenReturn(Optional.of(drug));
        when(drugRepository.save(any(Drug.class))).thenReturn(drug);

        drugService.reduceStock("B123", 20);

        assertEquals(30, drug.getQuantity());
    }

    @Test
    void testReduceStock_insufficientQuantity() {
        Drug drug = new Drug("B123", "Dolo", "Micro Labs", 10, 20.0, new Date());
        when(drugRepository.findById("B123")).thenReturn(Optional.of(drug));

        assertThrows(InsufficientStockException.class, () -> drugService.reduceStock("B123", 50));
    }

    @Test
    void testGetAllDrugs_shouldReturnList() {
        List<Drug> list = List.of(
            new Drug("B1", "Drug1", "Manu1", 100, 10.0, new Date()),
            new Drug("B2", "Drug2", "Manu2", 200, 20.0, new Date())
        );

        when(drugRepository.findAll()).thenReturn(list);

        List<Drug> result = drugService.getAllDrugs();
        assertEquals(2, result.size());
    }
}
