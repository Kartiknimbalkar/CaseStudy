package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Sales;


import java.util.List;
import java.time.LocalDate;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    List<Sales> findBySaleDate(LocalDate saleDate);
}
