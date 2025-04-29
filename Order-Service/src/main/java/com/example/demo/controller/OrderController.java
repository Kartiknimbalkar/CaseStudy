package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderDto;
//import com.example.demo.dto.OrderPaymentResponse;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderDto orderDto) throws InsufficientStockException {		// place order
        Order order = orderService.placeOrder(orderDto);
        return ResponseEntity
                .status(201)
                .body(order);
    }

    @PutMapping("/verify/{orderId}")
    public ResponseEntity<String> verifyOrder(@PathVariable Long orderId) throws OrderNotFoundException {		// verify the order
        return ResponseEntity.ok(orderService.verifyOrder(orderId));
    }

    @PutMapping("/pickedup/{orderId}")
    public ResponseEntity<String> markAsPickedUp(@PathVariable Long orderId) throws OrderNotFoundException {	// add order to pickedup section
        return ResponseEntity.ok(orderService.markAsPickedUp(orderId));
    }
    
    @GetMapping("/pickedUpOrders")
    public ResponseEntity<List<Order>> getPickedUpOrders() {		// list all pickedup orders
        return ResponseEntity.ok(orderService.getPickedUpOrders());
    }

    @GetMapping("/list")
    public List<Order> getAllOrders() {					// list all orders
        return orderService.getAllOrders();
    }
}
