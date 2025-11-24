package com.partsflow.backend.service;

import com.partsflow.backend.dto.requester.RequesterCreateDTO;
import com.partsflow.backend.dto.requester.RequesterResponseDTO;
import com.partsflow.backend.dto.requester.RequesterUpdateDTO;
import com.partsflow.backend.model.Requester;
import com.partsflow.backend.model.User;
import com.partsflow.backend.repository.RequesterRepository;
import com.partsflow.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequesterService {

    private final RequesterRepository requesterRepository;
    private final UserRepository userRepository;

    public RequesterService(RequesterRepository requesterRepository, UserRepository userRepository) {
        this.requesterRepository = requesterRepository;
        this.userRepository = userRepository;
    }

    public List<RequesterResponseDTO> getAll() {
        return requesterRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public RequesterResponseDTO getById(Long id) {
        Requester r = requesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        return toDTO(r);
    }

    public RequesterResponseDTO create(RequesterCreateDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (requesterRepository.existsByUser(user)) {
            throw new IllegalArgumentException("Requester already exists for this user");
        }

        Requester requester = Requester.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .department(dto.department())
                .site(dto.site())
                .businessRole(dto.businessRole())
                .user(user)
                .build();

        requesterRepository.save(requester);

        return toDTO(requester);
    }

    public RequesterResponseDTO update(Long id, RequesterUpdateDTO dto) {

        Requester existing = requesterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        if (dto.firstName() != null) existing.setFirstName(dto.firstName());
        if (dto.lastName() != null) existing.setLastName(dto.lastName());
        if (dto.department() != null) existing.setDepartment(dto.department());
        if (dto.site() != null) existing.setSite(dto.site());
        if (dto.businessRole() != null) existing.setBusinessRole(dto.businessRole());

        requesterRepository.save(existing);

        return toDTO(existing);
    }

    public void delete(Long id) {
        if (!requesterRepository.existsById(id)) {
            throw new RuntimeException("Requester not found");
        }

        requesterRepository.deleteById(id);
    }

    private RequesterResponseDTO toDTO(Requester r) {
        return new RequesterResponseDTO(
                r.getId(),
                r.getFirstName(),
                r.getLastName(),
                r.getFullName(),
                r.getDepartment(),
                r.getSite(),
                r.getBusinessRole(),
                r.getUser().getId()
        );
    }
}
