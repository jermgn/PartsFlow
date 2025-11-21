package com.partsflow.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_steps")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modification_request_id")
    private ModificationRequest modificationRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole approverRole;

    private String comment;

    private LocalDateTime validatedAt;

    @PrePersist
    protected void onCreate() {
        if (status == ApprovalStatus.APPROVED || status == ApprovalStatus.REJECTED) {
            validatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == ApprovalStatus.APPROVED || status == ApprovalStatus.REJECTED) {
            validatedAt = LocalDateTime.now();
        }
    }
}
