package com.lms.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", unique = true, nullable = false)
    private CourseResourceEntity resource;

    @Column(name = "instructions_text", columnDefinition = "text")
    private String instructionsText;

    @Column(name = "available_from")
    private Instant availableFrom;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "allow_resubmission")
    private Boolean allowResubmission;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
