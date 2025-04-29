package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.exception.RestockingException;
import com.example.demo.exception.SupplierNotFoundException;
import com.example.demo.model.Supplier;
import com.example.demo.model.SupplierRestockHistory;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.SupplierRepo;
import com.example.demo.repo.SupplierRestockHistoryRepo;
import com.example.demo.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    @InjectMocks
    private SupplierService supplierService;

    @Mock
    private SupplierRepo supplierRepo;

    @Mock
    private DrugClient drugClient;

    @Mock
    private SupplierRestockHistoryRepo supplierRestockHistoryRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddSupplier_success() {
        SupplierRegistrationDto dto = new SupplierRegistrationDto("Supplier A", "a@supplier.com", "BATCH001");

        when(supplierRepo.existsBySupplierNameAndSupplierEmail(any(), any())).thenReturn(false);
        when(supplierRepo.existsByBatchId(any())).thenReturn(false);
        when(supplierRepo.save(any())).thenReturn(new Supplier());

        SupplierRegistrationDto result = supplierService.addSupplier(dto);

        assertEquals("Supplier A", result.getSupplierName());
        verify(supplierRepo).save(any());
    }

    @Test
    void testUpdateSupplier_success() {
        Long id = 1L;
        Supplier existing = new Supplier();
        existing.setId(id);
        existing.setSupplierName("Old Name");
        existing.setSupplierEmail("old@mail.com");
        existing.setBatchId("BATCH001");

        SupplierUpdateDto dto = new SupplierUpdateDto("New Name", "new@mail.com", "BATCH1111");

        when(supplierRepo.findById(id)).thenReturn(Optional.of(existing));
        when(supplierRepo.save(any())).thenReturn(existing);

        Supplier updated = supplierService.updateSupplier(id, dto);

        assertEquals("New Name", updated.getSupplierName());
        assertEquals("new@mail.com", updated.getSupplierEmail());
        verify(supplierRepo).save(existing);
    }

    @Test
    void testUpdateSupplier_notFound() {
        SupplierUpdateDto dto = new SupplierUpdateDto("X", "x@mail.com", "BATCH");

        when(supplierRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.updateSupplier(99L, dto));
    }

    @Test
    void testDeleteSupplier_success() {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setSupplierName("Del");

        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepo).delete(supplier);
    }

    @Test
    void testDeleteSupplier_notFound() {
        when(supplierRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.deleteSupplier(1L));
    }

    @Test
    void testGetAllSuppliers_success() {
        Supplier s1 = new Supplier();
        s1.setId(1L); s1.setSupplierName("Supplier A"); s1.setSupplierEmail("a@mail.com");
        s1.setBatchId("BATCH01"); s1.setQuantity(10); s1.setRestockDate(LocalDateTime.now());

        Supplier s2 = new Supplier();
        s2.setId(2L); s2.setSupplierName("Supplier B"); s2.setSupplierEmail("b@mail.com");
        s2.setBatchId("BATCH02"); s2.setQuantity(20); s2.setRestockDate(LocalDateTime.now());

        when(supplierRepo.findAll()).thenReturn(List.of(s1, s2));

        List<SupplierResponseDTO> result = supplierService.getAllSupplier();

        assertEquals(2, result.size());
        assertEquals("Supplier A", result.get(0).getSupplierName());
        assertEquals("Supplier B", result.get(1).getSupplierName());
    }

    @Test
    void testGetSupplierHistoryByBatchId_notFound() {
        when(supplierRestockHistoryRepo.findByBatchId("UNKNOWN")).thenReturn(Collections.emptyList());

        assertThrows(SupplierNotFoundException.class,
                () -> supplierService.getSupplierHistoryByBatchId("UNKNOWN"));
    }

    @Test
    void testGetSupplierHistoryByBatchId_success() {
        SupplierRestockHistory history = new SupplierRestockHistory();
        history.setSupplierName("ABC Supplier");
        history.setBatchId("BATCH123");
        history.setQuantity(100);
        history.setRestockDate(LocalDateTime.now());

        when(supplierRestockHistoryRepo.findByBatchId("BATCH123"))
                .thenReturn(List.of(history));

        List<SupplierHistoryDto> result = supplierService.getSupplierHistoryByBatchId("BATCH123");

        assertEquals(1, result.size());
        assertEquals("ABC Supplier", result.get(0).getSupplierName());
    }

    @Test
    void testRestockDrug_success() {
        RestockRequest request = new RestockRequest("BATCH001", 50, new Date());

        Supplier supplier = new Supplier();
        supplier.setBatchId("BATCH001");
        supplier.setQuantity(20);
        supplier.setSupplierName("RestockSupplier");

        when(supplierRepo.findByBatchId("BATCH001")).thenReturn(List.of(supplier));

        doNothing().when(drugClient).restockDrugs(any(RestockRequest.class));
        when(supplierRepo.save(any())).thenReturn(supplier);
        when(supplierRestockHistoryRepo.save(any())).thenReturn(new SupplierRestockHistory());

        assertDoesNotThrow(() -> supplierService.restockDrug(request));

        verify(drugClient).restockDrugs(any());
        verify(supplierRepo).save(any());
        verify(supplierRestockHistoryRepo).save(any());
    }

    @Test
    void testRestockDrug_supplierNotFound() {
        when(supplierRepo.findByBatchId("INVALID")).thenReturn(Collections.emptyList());

        RestockRequest request = new RestockRequest("INVALID", 10, new Date());

        assertThrows(SupplierNotFoundException.class, () -> supplierService.restockDrug(request));
    }

    @Test
    void testRestockDrug_exceptionFromDrugClient() {
        RestockRequest request = new RestockRequest("BATCH002", 25, new Date());

        Supplier supplier = new Supplier();
        supplier.setBatchId("BATCH002");
        supplier.setQuantity(10);
        supplier.setSupplierName("FailSupplier");

        when(supplierRepo.findByBatchId("BATCH002")).thenReturn(List.of(supplier));
        doThrow(new RuntimeException("Service down")).when(drugClient).restockDrugs(any());

        assertThrows(RestockingException.class, () -> supplierService.restockDrug(request));
    }
}
