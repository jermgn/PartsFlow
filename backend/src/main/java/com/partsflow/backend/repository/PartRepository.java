package com.partsflow.backend.repository;

import com.partsflow.backend.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
    
    boolean existsByReference(String reference);
}
