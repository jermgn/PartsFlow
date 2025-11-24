package com.partsflow.backend.service;

import com.partsflow.backend.dto.workshop.WorkshopCreateDTO;
import com.partsflow.backend.dto.workshop.WorkshopResponseDTO;
import com.partsflow.backend.dto.workshop.WorkshopUpdateDTO;
import com.partsflow.backend.model.Workshop;
import com.partsflow.backend.repository.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkshopService {

    private final WorkshopRepository workshopRepository;

    public WorkshopService(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    public List<WorkshopResponseDTO> getAll() {
        return workshopRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public WorkshopResponseDTO getById(Long id) {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop not found"));
        return toDTO(workshop);
    }

    public WorkshopResponseDTO create(WorkshopCreateDTO dto) {

        if (dto.code() == null || dto.code().isBlank()) {
            throw new IllegalArgumentException("Code is required");
        }

        if (workshopRepository.existsByCode(dto.code())) {
            throw new IllegalArgumentException("Workshop code already exists");
        }

        Workshop workshop = Workshop.builder()
                .name(dto.name())
                .department(dto.department())
                .code(dto.code())
                .build();

        workshopRepository.save(workshop);

        return toDTO(workshop);
    }

    public WorkshopResponseDTO update(Long id, WorkshopUpdateDTO dto) {

        Workshop existing = workshopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop not found"));

        if (dto.name() != null) existing.setName(dto.name());
        if (dto.department() != null) existing.setDepartment(dto.department());
        if (dto.code() != null) existing.setCode(dto.code());

        workshopRepository.save(existing);

        return toDTO(existing);
    }

    public void delete(Long id) {
        if (!workshopRepository.existsById(id)) {
            throw new RuntimeException("Workshop not found");
        }
        workshopRepository.deleteById(id);
    }

    private WorkshopResponseDTO toDTO(Workshop w) {
        return new WorkshopResponseDTO(
                w.getId(),
                w.getName(),
                w.getDepartment(),
                w.getCode()
        );
    }
}
