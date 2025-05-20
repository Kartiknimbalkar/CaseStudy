package com.example.demo.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderStatus;
import com.example.demo.exception.DrugNotFoundException;
//import com.example.demo.dto.OrderPaymentResponse;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.OrderRepo;
import com.example.demo.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@Validated
//@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepo orderRepo;
    
    @Autowired
    private DrugClient drugClient;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderDto orderDto, @RequestHeader("X-User-Name") String username) throws InsufficientStockException, DrugNotFoundException {		// place order
        Order order = orderService.placeOrder(orderDto, username);
        return ResponseEntity
                .status(201)
                .body(order);
    }
    
    @GetMapping("/price-stock")
    public ResponseEntity<Map<String, Object>> getPriceAndStockInfo(
            @RequestParam("batch_id") String batchId,
            @RequestParam("quantity") int quantity) {
        try {
            Map<String, Object> response = orderService.calculateTotalPrice(batchId, quantity);
            return ResponseEntity.ok(response);
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Something went wrong"));
        }
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
    
    @GetMapping("/verifiedOrders")
    public ResponseEntity<List<Order>> getVerifiedOrders() {		// list all verified orders
        return ResponseEntity.ok(orderService.getVerifiedOrders());
    }

    @GetMapping("/list")
    public List<Order> getAllOrders() {					// list all orders
        return orderService.getAllOrders();
    }
    
    @GetMapping("/get/{orderId}")
    public Order getOrderById(@PathVariable Long orderId) throws OrderNotFoundException {
    	return orderService.getOrderById(orderId);
    }
    
    @GetMapping("/getUserOrders")
    public ResponseEntity<List<Order>> getOrders(@RequestHeader("X-User-Name") String username) {
        List<Order> orders = orderService.getOrdersByUsername(username);
        return ResponseEntity.ok(orders);
    }



    
}
