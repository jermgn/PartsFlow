package com.partsflow.backend.service;

import com.partsflow.backend.dto.modification.ModificationRequestCreateDTO;
import com.partsflow.backend.dto.modification.ModificationRequestResponseDTO;
import com.partsflow.backend.dto.modification.ModificationRequestUpdateDTO;
import com.partsflow.backend.model.*;
import com.partsflow.backend.repository.ModificationRequestRepository;
import com.partsflow.backend.repository.PartRepository;
import com.partsflow.backend.repository.RequesterRepository;
import com.partsflow.backend.repository.WorkshopRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModificationRequestService {

    private final ModificationRequestRepository modificationRequestRepository;
    private final PartRepository partRepository;
    private final RequesterRepository requesterRepository;
    private final WorkshopRepository workshopRepository;

    public ModificationRequestService(
            ModificationRequestRepository modificationRequestRepository,
            PartRepository partRepository,
            RequesterRepository requesterRepository,
            WorkshopRepository workshopRepository
    ) {
        this.modificationRequestRepository = modificationRequestRepository;
        this.partRepository = partRepository;
        this.requesterRepository = requesterRepository;
        this.workshopRepository = workshopRepository;
    }

    private ModificationRequestResponseDTO toDTO(ModificationRequest req) {
        return new ModificationRequestResponseDTO(
                req.getId(),
                req.getPart().getId(),
                req.getPart().getReference(),
                req.getPart().getName(),
                req.getRequester().getId(),
                req.getRequester().getFullName(),
                req.getWorkshop().getId(),
                req.getWorkshop().getName(),
                req.getStatus(),
                req.getComment(),
                req.getCreatedAt(),
                req.getUpdatedAt()
        );
    }

    public List<ModificationRequestResponseDTO> getAll() {
        return modificationRequestRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ModificationRequestResponseDTO getById(Long id) {
        ModificationRequest req = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modification Request not found"));

        return toDTO(req);
    }

    public ModificationRequestResponseDTO create(ModificationRequestCreateDTO dto) {

        Part part = partRepository.findById(dto.partId())
                .orElseThrow(() -> new IllegalArgumentException("Part not found"));

        Requester requester = requesterRepository.findById(dto.requesterId())
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));

        Workshop workshop = workshopRepository.findById(dto.workshopId())
                .orElseThrow(() -> new IllegalArgumentException("Workshop not found"));

        ModificationRequest req = ModificationRequest.builder()
                .part(part)
                .requester(requester)
                .workshop(workshop)
                .comment(dto.comment())
                .status(RequestStatus.PENDING)
                .build();

        modificationRequestRepository.save(req);

        return toDTO(req);
    }

    public ModificationRequestResponseDTO update(Long id, ModificationRequestUpdateDTO dto) {

        ModificationRequest existing = modificationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modification Request not found"));

        if (dto.comment() != null) existing.setComment(dto.comment());

        if (dto.status() != null) {
            existing.setStatus(dto.status());

            if (dto.status() == RequestStatus.VALIDATED) {
                Part part = existing.getPart();
                part.setStatus(PartStatus.REPLACED);
                partRepository.save(part);
            }
        }

        if (dto.workshopId() != null) {
            Workshop workshop = workshopRepository.findById(dto.workshopId())
                    .orElseThrow(() -> new IllegalArgumentException("Workshop not found"));
            existing.setWorkshop(workshop);
        }

        modificationRequestRepository.save(existing);

        return toDTO(existing);
    }

    public void delete(Long id) {
        if (!modificationRequestRepository.existsById(id)) {
            throw new RuntimeException("Modification Request not found");
        }
        modificationRequestRepository.deleteById(id);
    }

    public List<ModificationRequestResponseDTO> getByStatus(RequestStatus status) {
        return modificationRequestRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ModificationRequestResponseDTO> getByWorkshop(Long id) {
        return modificationRequestRepository.findByWorkshopId(id)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
