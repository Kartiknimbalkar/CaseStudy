package com.example.service;

import com.example.demo.dto.*;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.OrderRepo;
import com.example.demo.repo.SalesClient;
import com.example.demo.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private DrugClient drugClient;

    @Mock
    private SalesClient salesClient;

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

    @Nested
    @DisplayName("placeOrder()")
    class PlaceOrderTests {

        @Test
        @DisplayName("succeeds when stock is sufficient")
        void shouldPlaceOrderWhenStockIsSufficient() throws Exception {
            // Arrange
            when(drugClient.getDrugById("B123")).thenReturn(drug);
            when(drugClient.reduceStock("B123", 5)).thenReturn(null);
            when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                o.setId(1L);
                o.setPickupDate(LocalDateTime.now().plusDays(2));
                return o;
            });

            // Act
            Order saved = orderService.placeOrder(orderDto, "username");

            // Assert
            assertAll(
                () -> assertEquals(OrderStatus.PENDING,   saved.getStatus()),
                () -> assertEquals("Paracetamol",          saved.getDrugNames().get(0)),
                () -> assertEquals(50.0,                   saved.getTotalPrice(), 0.001),
                () -> assertEquals("Dr. Strange",          saved.getDoctorName()),
                () -> assertEquals(5,                      saved.getQuantity()),
                () -> assertNotNull(saved.getPickupDate(), "pickupDate must be set by save-stub")
            );
            verify(drugClient).reduceStock("B123", 5);
            verify(orderRepo).save(any(Order.class));
        }

        @Test
        @DisplayName("throws InsufficientStockException when stock is low")
        void shouldFailWhenStockLow() throws DrugNotFoundException {
            // Arrange
            drug.setQuantity(3);
            when(drugClient.getDrugById("B123")).thenReturn(drug);

            // Act & Assert
            InsufficientStockException ex = assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(orderDto, "username")
            );
            assertEquals("Not enough stock available", ex.getMessage());
        }

        @Test
        @DisplayName("throws DrugNotFoundException when downstream service fails")
        void shouldFailWhenDrugNotFound() throws DrugNotFoundException {
            // Arrange
            when(drugClient.getDrugById("B123")).thenThrow(new RuntimeException("Service down"));

            // Act & Assert
            DrugNotFoundException ex = assertThrows(
                DrugNotFoundException.class,
                () -> orderService.placeOrder(orderDto, "username")
            );
            assertEquals("Drug with batchID B123 not found.", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("verifyOrder()")
    class VerifyOrderTests {

        @Test
        @DisplayName("succeeds when order is PENDING")
        void shouldVerifyPendingOrder() throws OrderNotFoundException {
            // Arrange
            Order o = new Order();
            o.setId(1L);
            o.setStatus(OrderStatus.PENDING);
            o.setBatch_id("B123");
            o.setQuantity(2);
            o.setDoctorName("Dr. House");
            o.setPaidAmount(20.0);
            o.setTotalPrice(20.0);
            when(orderRepo.findById(1L)).thenReturn(Optional.of(o));
            when(orderRepo.save(any(Order.class))).thenReturn(o);

            // Act
            String res = orderService.verifyOrder(1L);

            // Assert
            assertTrue(res.contains("verified"));
            verify(orderRepo).save(o);
            verify(salesClient).recordSale(any(SalesRequest.class));
        }

        @Test
        @DisplayName("throws OrderNotFoundException when not found")
        void shouldFailWhenOrderMissing() {
            // Arrange
            when(orderRepo.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.verifyOrder(999L)
            );
            assertEquals("Order not found for orderId: 999", ex.getMessage());
        }

        @Test
        @DisplayName("throws OrderNotFoundException when status ≠ PENDING")
        void shouldFailWhenNotPending() {
            // Arrange
            Order o = new Order();
            o.setId(1L);
            o.setStatus(OrderStatus.VERIFIED);
            when(orderRepo.findById(1L)).thenReturn(Optional.of(o));

            // Act & Assert
            OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.verifyOrder(1L)
            );
            assertTrue(ex.getMessage().contains("Order cannot be verified in current state"));
        }
    }

    @Nested
    @DisplayName("markAsPickedUp()")
    class MarkAsPickedUpTests {

        @Test
        @DisplayName("succeeds when order is VERIFIED")
        void shouldMarkPickedUpWhenVerified() throws OrderNotFoundException {
            // Arrange
            Order o = new Order();
            o.setId(1L);
            o.setStatus(OrderStatus.VERIFIED);
            when(orderRepo.findById(1L)).thenReturn(Optional.of(o));
            when(orderRepo.save(any(Order.class))).thenReturn(o);

            // Act
            String res = orderService.markAsPickedUp(1L);

            // Assert
            assertTrue(res.contains("PICKED_UP"));
            verify(orderRepo).save(o);
        }

        @Test
        @DisplayName("throws IllegalStateException when status ≠ VERIFIED")
        void shouldFailWhenNotVerified() {
            // Arrange
            Order o = new Order();
            o.setId(2L);
            o.setStatus(OrderStatus.PENDING);
            when(orderRepo.findById(2L)).thenReturn(Optional.of(o));

            // Act & Assert
            IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderService.markAsPickedUp(2L)
            );
            assertEquals("Order with orderId 2 must be VERIFIED before pickup.", ex.getMessage());
        }

        @Test
        @DisplayName("throws OrderNotFoundException when order is missing")
        void shouldFailWhenMissing() {
            // Arrange
            when(orderRepo.findById(100L)).thenReturn(Optional.empty());

            // Act & Assert
            OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.markAsPickedUp(100L)
            );
            assertEquals("Order not found for orderId: 100", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getAllOrders() & getPickedUpOrders()")
    class RetrievalTests {

        @Test
        @DisplayName("getAllOrders returns full list")
        void shouldReturnAll() {
            // Arrange
            List<Order> list = List.of(new Order(), new Order());
            when(orderRepo.findAll()).thenReturn(list);

            // Act
            List<Order> res = orderService.getAllOrders();

            // Assert
            assertEquals(2, res.size());
        }

        @Test
        @DisplayName("getPickedUpOrders returns only PICKED_UP")
        void shouldReturnOnlyPickedUp() {
            // Arrange
            Order picked = new Order();
            picked.setStatus(OrderStatus.PICKED_UP);
            when(orderRepo.findByStatus(OrderStatus.PICKED_UP)).thenReturn(List.of(picked));

            // Act
            List<Order> res = orderService.getPickedUpOrders();

            // Assert
            assertEquals(1, res.size());
            assertEquals(OrderStatus.PICKED_UP, res.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("getOrderById()")
    class GetByIdTests {

        @Test
        @DisplayName("resets paidAmount to 0 when status is FAILED")
        void shouldResetPaidWhenFailed() throws OrderNotFoundException {
            // Arrange
            Order o = new Order();
            o.setId(10L);
            o.setStatus(OrderStatus.FAILED);
            o.setPaidAmount(100.0);
            when(orderRepo.findById(10L)).thenReturn(Optional.of(o));
            when(orderRepo.save(any(Order.class))).thenReturn(o);

            // Act
            Order res = orderService.getOrderById(10L);

            // Assert
            assertEquals(0, res.getPaidAmount(), 0.001);
            verify(orderRepo).save(o);
        }

        @Test
        @DisplayName("throws OrderNotFoundException when missing")
        void shouldFailWhenMissing() {
            // Arrange
            when(orderRepo.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            OrderNotFoundException ex = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(999L)
            );
            assertEquals("Order not found with ID: 999", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("calculateTotalPrice()")
    class CalculatePriceTests {

        @Test
        @DisplayName("returns correct unitPrice, totalPrice and availableStock")
        void shouldCalculateCorrectly() throws InsufficientStockException {
            // Arrange
            when(drugClient.getDrugById("B123")).thenReturn(drug);

            // Act
            Map<String, Object> map = orderService.calculateTotalPrice("B123", 5);

            // Assert
            assertAll(
                () -> assertEquals(10.0, map.get("unitPrice")),
                () -> assertEquals(50.0, map.get("totalPrice")),
                () -> assertEquals(100,   map.get("availableStock"))
            );
        }

        @Test
        @DisplayName("throws InsufficientStockException when stock is low")
        void shouldFailWhenStockLow() {
            // Arrange
            drug.setQuantity(3);
            when(drugClient.getDrugById("B123")).thenReturn(drug);

            // Act & Assert
            InsufficientStockException ex = assertThrows(
                InsufficientStockException.class,
                () -> orderService.calculateTotalPrice("B123", 5)
            );
            assertEquals("Not enough stock for batch B123", ex.getMessage());
        }
    }
}
