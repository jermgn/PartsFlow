package com.partsflow.backend.repository;

import com.partsflow.backend.model.Requester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequesterRepository extends JpaRepository<Requester, Long> {

    boolean existsByUserId(Long userId);

}
