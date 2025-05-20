package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.OrderStatus;
import com.example.demo.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long>{

	List<Order> findByStatus(OrderStatus status);
	List<Order> findByUsername(String username);
}
