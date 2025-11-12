package com.partsflow.backend.repository;

import com.partsflow.backend.model.ModificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {
    
}