package com.partsflow.backend.service;

import com.partsflow.backend.dto.part.PartCreateDTO;
import com.partsflow.backend.dto.part.PartResponseDTO;
import com.partsflow.backend.dto.part.PartUpdateDTO;
import com.partsflow.backend.model.Part;
import com.partsflow.backend.model.PartStatus;
import com.partsflow.backend.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<PartResponseDTO> getAll() {
        return partRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public PartResponseDTO getById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        return toDTO(part);
    }

    public PartResponseDTO create(PartCreateDTO dto) {

        if (dto.reference() == null || dto.reference().isBlank()) {
            throw new IllegalArgumentException("Reference is required");
        }

        if (partRepository.existsByReference(dto.reference())) {
            throw new IllegalArgumentException("Part reference already exists");
        }

        Part part = Part.builder()
                .reference(dto.reference())
                .name(dto.name())
                .supplier(dto.supplier())
                .version(dto.version())
                .status(PartStatus.ACTIVE)
                .build();

        partRepository.save(part);
        return toDTO(part);
    }

    public PartResponseDTO update(Long id, PartUpdateDTO dto) {

        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        if (dto.name() != null) part.setName(dto.name());
        if (dto.supplier() != null) part.setSupplier(dto.supplier());
        if (dto.version() != null) part.setVersion(dto.version());
        if (dto.status() != null) part.setStatus(dto.status());

        partRepository.save(part);
        return toDTO(part);
    }

    public void delete(Long id) {

        if (!partRepository.existsById(id)) {
            throw new RuntimeException("Part not found");
        }

        partRepository.deleteById(id);
    }

    private PartResponseDTO toDTO(Part part) {
        return new PartResponseDTO(
                part.getId(),
                part.getReference(),
                part.getName(),
                part.getSupplier(),
                part.getVersion(),
                part.getStatus()
        );
    }
}
