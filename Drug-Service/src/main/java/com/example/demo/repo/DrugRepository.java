package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Drug;

public interface DrugRepository extends JpaRepository<Drug, String> {
    boolean existsByName(String name);
    Optional<Drug> findByBatchId(String batchId);
}
