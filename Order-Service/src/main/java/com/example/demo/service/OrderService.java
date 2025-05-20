package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DrugDto;
import com.example.demo.dto.OrderDto;
//import com.example.demo.dto.OrderPaymentResponse;
import com.example.demo.dto.OrderStatus;
import com.example.demo.dto.PaymentRequest;
//import com.example.demo.dto.PaymentRequest;
//import com.example.demo.dto.PaymentResponse;
import com.example.demo.dto.SalesRequest;
import com.example.demo.exception.DrugNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.repo.DrugClient;
import com.example.demo.repo.OrderRepo;
//import com.example.demo.repo.PaymentClient;
import com.example.demo.repo.SalesClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	private OrderRepo orderRepo;
	
	@Autowired
	private SalesClient salesClient;
	
	@Autowired
	private DrugClient drugClient;
	
//	@Autowired
//	private PaymentClient paymentClient;
	
	public Order placeOrder(OrderDto orderDto, String username) throws InsufficientStockException, DrugNotFoundException {
		
		log.info("In the place order method");
		
		DrugDto drug;
		
		try {
			drug = drugClient.getDrugById(orderDto.getBatch_id());
		} catch(Exception e) {
			throw new DrugNotFoundException("Drug with batchID " + orderDto.getBatch_id() + " not found.");
		}

        if (drug.getQuantity() < orderDto.getQuantity()) {
    		log.info("Not enough stock available");
            throw new InsufficientStockException("Not enough stock available");
        }

        drugClient.reduceStock(orderDto.getBatch_id(), orderDto.getQuantity());

		log.info("Stock got reduced from the Drug-Service through Feign Client");
        
        Order order = new Order();
        order.setBatch_id(orderDto.getBatch_id());
        order.setQuantity(orderDto.getQuantity());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        order.setDoctorName(orderDto.getDoctorName());
        order.setDoctorContact(orderDto.getDoctorContact());
        order.setDoctorEmail(orderDto.getDoctorEmail());
        
        order.setDrugNames(List.of(drug.getName()));
        order.setDrugPrices(List.of(drug.getPrice()));
        
        double totalPrice = drug.getPrice() * orderDto.getQuantity();
        order.setTotalPrice(totalPrice);
        order.setPaidAmount(totalPrice);		// always full payment
        order.setUsername(username);
        
        
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DATE, 2); 	// setting pickup date as current day + 2 days
//        order.setPickupDate(cal.getTime());
        
//        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), order.getTotalPrice(), orderDto.getPaymentMethod());
        
//        paymentClient.makePayment(paymentRequest);
        
        
        return orderRepo.save(order);

    }
	
	public String verifyOrder(Long orderId) throws OrderNotFoundException {
		
		log.info("In the verify order method");
		
	    Order order = orderRepo.findById(orderId)
	            .orElseThrow(() -> new OrderNotFoundException("Order not found for orderId: " + orderId));

	    if (order.getStatus() != OrderStatus.PENDING) {
			log.info("Order cannot be verified in current state: {}", order.getStatus());
	        throw new OrderNotFoundException("Order cannot be verified in current state: " + order.getStatus());
	    }
	    
	    order.setStatus(OrderStatus.VERIFIED);
	    orderRepo.save(order);
	    
		log.info("The order with ID: {} got  verified", order.getId());
	    
	    SalesRequest salesRequest = new SalesRequest();
	    salesRequest.setOrderId(order.getId());
	    salesRequest.setBatchId(order.getBatch_id());
	    salesRequest.setQuantity(order.getQuantity());
	    salesRequest.setTotalPrice(order.getTotalPrice());
	    salesRequest.setPaidAmount(order.getPaidAmount());
	    salesRequest.setDoctorName(order.getDoctorName());


	    salesClient.recordSale(salesRequest);		// Sale service is called via feign client and record of the current verified order is stored
		log.info("Received Paid Amount: {}", order.getPaidAmount());
		log.info("The order with ID: {} has been verified", order.getId());
	    return "Order with orderId " + orderId + " has been verified.";
	}

	
	public String markAsPickedUp(Long orderId) throws OrderNotFoundException {
		
		log.info("In the mark as picked up order method");
		
	    Order order = orderRepo.findById(orderId)
	            .orElseThrow(() -> new OrderNotFoundException("Order not found for orderId: " + orderId));

	    if (order.getStatus() != OrderStatus.VERIFIED) {
			log.info("Order with orderId: {} must be VERIFIED before pickup", orderId);
	        throw new IllegalStateException("Order with orderId " + orderId + " must be VERIFIED before pickup.");
	    }

	    order.setStatus(OrderStatus.PICKED_UP);
	    order.setPickupDate(LocalDateTime.now());
	    orderRepo.save(order);
	    
		log.info("The order with orderId: {} has been PICKED_UP");

	    return "Order with orderId " + orderId + " has been marked as PICKED_UP.";
	}
	
	public List<Order> getAllOrders() {
		log.info("Displaying all the orders from the database");
		return orderRepo.findAll();
	}

	public List<Order> getPickedUpOrders() {
		log.info("Displaying only PICKED_UP orders form the database");
	    return orderRepo.findByStatus(OrderStatus.PICKED_UP);
	}

	public Order getOrderById(Long id) throws OrderNotFoundException {
	    Order order = orderRepo.findById(id)
	        .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));

	    if (order.getStatus() == OrderStatus.FAILED) {
	        order.setPaidAmount(0);
	    }
	    
	    orderRepo.save(order);

	    return order;
	}
	
	public Map<String, Object> calculateTotalPrice(String batchId, int quantity) throws InsufficientStockException {
	    DrugDto drug = drugClient.getDrugById(batchId);
	    if (drug.getQuantity() < quantity) {
	        throw new InsufficientStockException("Not enough stock for batch " + batchId);
	    }

	    double totalPrice = drug.getPrice() * quantity;

	    Map<String, Object> result = new HashMap<>();
	    result.put("unitPrice", drug.getPrice());
	    result.put("totalPrice", totalPrice);
	    result.put("availableStock", drug.getQuantity());
	    return result;
	}
	
	
	public List<Order> getOrdersByUsername(String username) {
	    List<Order> orders = orderRepo.findByUsername(username);
	    return orders;
	}

	public List<Order> getVerifiedOrders() {
		log.info("Listing VERIFIED Orders");
		return orderRepo.findByStatus(OrderStatus.VERIFIED);
	}


	
}
