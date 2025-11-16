package com.partsflow.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "requesters")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Requester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String department;

    @NotBlank
    private String site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequesterRole businessRole;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
