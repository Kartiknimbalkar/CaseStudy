package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.SupplierRestockHistory;

import java.util.List;

public interface SupplierRestockHistoryRepo extends JpaRepository<SupplierRestockHistory, Long> {
    List<SupplierRestockHistory> findByBatchId(String batchId);
}
