package com.lms.course.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assignment_material_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//TODO: consider if we want to store files in the database or use an external storage (e.g., AWS S3) and just keep metadata in the DB
//TODO: The implementation logic is pending
public class AssignmentMaterialFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private AssignmentEntity assignment;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "file_data", columnDefinition = "bytea")
    private byte[] fileData;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

}
