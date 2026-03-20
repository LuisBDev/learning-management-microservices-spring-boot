package com.lms.course.infrastructure.persistence.entity;

import com.lms.course.domain.model.enums.ResourceUrlKind;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_urls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", unique = true, nullable = false)
    private CourseResourceEntity resource;

    @Column(name = "url", columnDefinition = "text", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "url_kind", length = 30)
    private ResourceUrlKind urlKind;

    @Column(name = "open_in_new_tab")
    private Boolean openInNewTab;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
