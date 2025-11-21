package com.partsflow.backend.repository;

import com.partsflow.backend.model.Requester;
import com.partsflow.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequesterRepository extends JpaRepository<Requester, Long> {
    
    boolean existsByUser(User user);
}
