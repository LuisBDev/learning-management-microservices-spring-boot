package com.lms.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_texts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", unique = true, nullable = false)
    private CourseResourceEntity resource;

    @Column(name = "content_text", columnDefinition = "text")
    private String contentText;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
