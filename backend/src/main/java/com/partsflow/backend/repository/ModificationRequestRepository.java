package com.partsflow.backend.repository;

import com.partsflow.backend.model.ModificationRequest;
import com.partsflow.backend.model.RequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {

    List<ModificationRequest> findByStatus(RequestStatus status);

    List<ModificationRequest> findByWorkshopId(Long workshopId);
}
