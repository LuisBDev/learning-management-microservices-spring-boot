package com.lms.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "course_recordings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRecordingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_url_id", unique = true, nullable = false)
    private ResourceUrlEntity resourceUrl;

    @Column(name = "recording_name", nullable = false, length = 255)
    private String recordingName;

    @Column(name = "class_date")
    private LocalDate classDate;

    @Column(name = "comment_text", columnDefinition = "text")
    private String commentText;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
