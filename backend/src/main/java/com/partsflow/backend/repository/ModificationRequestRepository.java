package com.partsflow.backend.repository;

import com.partsflow.backend.model.ModificationRequest;
import com.partsflow.backend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {

    List<ModificationRequest> findByStatus(RequestStatus status);

    List<ModificationRequest> findByWorkshopId(Long workshopId);

    List<ModificationRequest> findByRequesterId(Long requesterId);

    List<ModificationRequest> findByPartId(Long partId);
}
