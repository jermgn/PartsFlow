package com.partsflow.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "part_id" , nullable = false)
    private Part part;

    @NotBlank
    @Column(nullable = false)
    private String requester;

    @Column(nullable = false)
    private String workshop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime createdAt;

    @Column(length = 500)
    private String comment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
