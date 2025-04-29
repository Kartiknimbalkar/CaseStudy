package com.example.demo.service;

import com.example.demo.model.Sales;
import com.example.demo.model.SalesRequest;
import com.example.demo.repo.SalesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesServiceTest {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesService salesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecordSale() {
        SalesRequest request = new SalesRequest(1L, "B123", 10, 500.0, 300.0, "Dr. Strange");

        Sales savedSales = new Sales();
        savedSales.setId(1L);
        savedSales.setOrderId(1L);
        savedSales.setBatchId("B123");
        savedSales.setQuantity(10);
        savedSales.setTotalPrice(500.0);
        savedSales.setPaidAmount(300.0);
        savedSales.setBalance(200.0);
        savedSales.setSaleDate(LocalDate.now());
        savedSales.setDoctorName("Dr. Strange");

        when(salesRepository.save(any(Sales.class))).thenReturn(savedSales);

        Sales result = salesService.recordSale(request);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(200.0, result.getBalance());
        assertEquals("Dr. Strange", result.getDoctorName());

        verify(salesRepository, times(1)).save(any(Sales.class));
    }
}
