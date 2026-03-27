package com.lms.learning.infrastructure.persistence.entity;

import com.lms.learning.domain.model.enums.GradeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assignment_grades")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentGradeEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "student_user_id", nullable = false)
    private UUID studentUserId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private AssignmentSubmissionEntity submission;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_status", nullable = false, length = 30)
    private GradeStatus gradeStatus;

    @Column(name = "teacher_comment", columnDefinition = "text")
    private String teacherComment;

    @Column(name = "graded_by_user_id")
    private UUID gradedByUserId;

    @Column(name = "graded_at")
    private Instant gradedAt;


}
