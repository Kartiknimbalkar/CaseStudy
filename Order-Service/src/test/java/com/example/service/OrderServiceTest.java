package com.example.service;

import com.example.demo.dto.*;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.OrderRepo;
//import com.example.demo.repo.PaymentClient;
import com.example.demo.repo.SalesClient;
import com.example.demo.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private DrugClient drugClient;

    @Mock
    private SalesClient salesClient;

//    @Mock
//    private PaymentClient paymentClient;

    private DrugDto drug;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        drug = new DrugDto("B123", "Paracetamol", "Pharma Inc", 100, 10.0);

        orderDto = new OrderDto();
        orderDto.setBatch_id("B123");
        orderDto.setQuantity(5);
        orderDto.setDoctorName("Dr. Strange");
        orderDto.setDoctorContact("1234567890");
        orderDto.setDoctorEmail("strange@hospital.com");
        orderDto.setPaymentMethod("CARD");
    }

    @Test
    void testPlaceOrderSuccess() throws InsufficientStockException, DrugNotFoundException {
        when(drugClient.getDrugById("B123")).thenReturn(drug);
        when(drugClient.reduceStock("B123", 5)).thenReturn(null);
//        doNothing().when(paymentClient).makePayment(any(PaymentRequest.class));
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order savedOrder = orderService.placeOrder(orderDto);

        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals("Paracetamol", savedOrder.getDrugNames().get(0));
        assertEquals(50.0, savedOrder.getTotalPrice());
        assertEquals("Dr. Strange", savedOrder.getDoctorName());
        assertEquals(5, savedOrder.getQuantity());
        assertNotNull(savedOrder.getPickupDate());
    }


    @Test
    void testPlaceOrderFailsWhenStockIsLow() {
        drug.setQuantity(3);
        when(drugClient.getDrugById("B123")).thenReturn(drug);
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(orderDto));
    }

    @Test
    void testVerifyOrderSuccess() throws OrderNotFoundException {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setBatch_id("B123");
        order.setQuantity(2);
        order.setDoctorName("Dr. House");
        order.setPaidAmount(20.0);
        order.setTotalPrice(20.0);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        String result = orderService.verifyOrder(1L);
        assertTrue(result.contains("verified"));
        verify(salesClient).recordSale(any(SalesRequest.class));
    }

    @Test
    void testVerifyOrderNotFound() {
        when(orderRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.verifyOrder(999L));
    }

    @Test
    void testMarkAsPickedUp() throws OrderNotFoundException {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.VERIFIED);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        String result = orderService.markAsPickedUp(1L);
        assertTrue(result.contains("PICKED_UP"));
    }

    @Test
    void testMarkAsPickedUpFailsIfNotVerified() {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepo.findById(2L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> orderService.markAsPickedUp(2L));
    }

    @Test
    void testGetAllOrders() {
        List<Order> mockOrders = List.of(new Order(), new Order());
        when(orderRepo.findAll()).thenReturn(mockOrders);

        List<Order> result = orderService.getAllOrders();
        assertEquals(2, result.size());
    }

    @Test
    void testGetPickedUpOrders() {
        Order pickedOrder = new Order();
        pickedOrder.setStatus(OrderStatus.PICKED_UP);
        when(orderRepo.findByStatus(OrderStatus.PICKED_UP)).thenReturn(List.of(pickedOrder));

        List<Order> result = orderService.getPickedUpOrders();
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PICKED_UP, result.get(0).getStatus());
    }
}
