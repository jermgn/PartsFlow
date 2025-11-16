package com.partsflow.backend.repository;

import com.partsflow.backend.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {

    boolean existsByName(String name);
}
