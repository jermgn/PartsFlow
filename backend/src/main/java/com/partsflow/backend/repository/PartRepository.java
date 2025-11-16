package com.partsflow.backend.repository;

import com.partsflow.backend.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByReference(String reference);

    boolean existsByReference(String reference);
}
