package com.lms.course.infrastructure.persistence.entity;

import com.lms.course.domain.model.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "course_resources", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"section_id", "position"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResourceEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSectionEntity section;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 30)
    private ResourceType resourceType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "created_by")
    private UUID createdBy;

}
