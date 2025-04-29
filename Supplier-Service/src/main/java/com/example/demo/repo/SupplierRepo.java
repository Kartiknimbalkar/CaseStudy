package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Supplier;

import java.util.List;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {
    List<Supplier> findByBatchId(String batchId);
    boolean existsBySupplierNameAndSupplierEmail(String supplierName, String supplierEmail);
    boolean existsByBatchId(String batchId);
}
