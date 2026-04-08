package com.lms.enrollment.application.service;

import com.lms.enrollment.controller.dto.request.CreateEnrollmentRequest;
import com.lms.enrollment.controller.dto.request.UpdateEnrollmentStatusRequest;
import com.lms.enrollment.controller.dto.response.EnrollmentEventResponse;
import com.lms.enrollment.controller.dto.response.EnrollmentResponse;
import com.lms.enrollment.controller.mapper.EnrollmentMapper;
import com.lms.enrollment.domain.model.enums.EnrollmentEventType;
import com.lms.enrollment.domain.model.enums.EnrollmentStatus;
import com.lms.enrollment.exception.DuplicateResourceException;
import com.lms.enrollment.exception.InvalidOperationException;
import com.lms.enrollment.exception.ResourceNotFoundException;
import com.lms.enrollment.infrastructure.client.courseservicev1.CourseServiceClient;
import com.lms.enrollment.infrastructure.client.identityservicev1.IdentityServiceClient;
import com.lms.enrollment.infrastructure.persistence.entity.CourseEnrollmentEntity;
import com.lms.enrollment.infrastructure.persistence.entity.EnrollmentEventEntity;
import com.lms.enrollment.infrastructure.persistence.repository.JpaCourseEnrollmentRepository;
import com.lms.enrollment.infrastructure.persistence.repository.JpaEnrollmentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final JpaCourseEnrollmentRepository enrollmentRepository;
    private final JpaEnrollmentEventRepository eventRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseServiceClient courseServiceClient;
    private final IdentityServiceClient identityServiceClient;

    private static final Map<EnrollmentStatus, Set<EnrollmentStatus>> VALID_TRANSITIONS = Map.of(
            EnrollmentStatus.ACTIVE, Set.of(EnrollmentStatus.SUSPENDED, EnrollmentStatus.CANCELLED),
            EnrollmentStatus.SUSPENDED, Set.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.CANCELLED),
            EnrollmentStatus.CANCELLED, Set.of()
    );

    @Override
    @Transactional
    public EnrollmentResponse enroll(CreateEnrollmentRequest request, UUID triggeredBy) {

        if (!courseServiceClient.courseExistsAndPublished(request.getCourseId())) {
            throw new InvalidOperationException(
                    "Course " + request.getCourseId() + " does not exist or is not published");
        }

        if (!identityServiceClient.userExists(request.getStudentUserId())) {
            throw new ResourceNotFoundException("User", "id", request.getStudentUserId());
        }

        if (enrollmentRepository.existsByCourseIdAndStudentUserId(request.getCourseId(), request.getStudentUserId())) {
            throw new DuplicateResourceException(
                    "Student " + request.getStudentUserId() + " is already enrolled in course " + request.getCourseId());
        }

        CourseEnrollmentEntity enrollment = CourseEnrollmentEntity.builder()
                .courseId(request.getCourseId())
                .studentUserId(request.getStudentUserId())
                .status(EnrollmentStatus.ACTIVE)
                .enrolledAt(Instant.now())
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        recordEvent(enrollment, EnrollmentEventType.ENROLLMENT_CREATED, null, EnrollmentStatus.ACTIVE, null, triggeredBy);

        log.info("Student {} enrolled in course {} with enrollment {}", request.getStudentUserId(), request.getCourseId(), enrollment.getId());
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getById(UUID id) {
        return enrollmentMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getByCourseAndStudent(UUID courseId, UUID studentUserId) {
        CourseEnrollmentEntity enrollment = enrollmentRepository.findByCourseIdAndStudentUserId(courseId, studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for course " + courseId + " and student " + studentUserId));
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getByCourseId(UUID courseId, Pageable pageable) {
        return enrollmentRepository.findByCourseId(courseId, pageable).map(enrollmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getByStudentUserId(UUID studentUserId, Pageable pageable) {
        return enrollmentRepository.findByStudentUserId(studentUserId, pageable).map(enrollmentMapper::toResponse);
    }

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(UUID id, UpdateEnrollmentStatusRequest request, UUID triggeredBy) {
        CourseEnrollmentEntity enrollment = findOrThrow(id);
        EnrollmentStatus previousStatus = enrollment.getStatus();
        EnrollmentStatus newStatus = request.getStatus();

        validateTransition(previousStatus, newStatus);

        enrollment.setStatus(newStatus);
        enrollment.setUpdatedAt(Instant.now());
        enrollment = enrollmentRepository.save(enrollment);

        EnrollmentEventType eventType = resolveEventType(previousStatus, newStatus);
        recordEvent(enrollment, eventType, previousStatus, newStatus, request.getEventDetail(), triggeredBy);

        log.info("Enrollment {} status changed: {} -> {}", id, previousStatus, newStatus);
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentEventResponse> getEnrollmentHistory(UUID enrollmentId) {

        findOrThrow(enrollmentId);
        return enrollmentMapper.toEventResponseList(
                eventRepository.findByEnrollmentIdOrderByCreatedAtDesc(enrollmentId));

    }

    private void recordEvent(CourseEnrollmentEntity enrollment, EnrollmentEventType eventType,
                             EnrollmentStatus previousStatus, EnrollmentStatus newStatus,
                             String detail, UUID triggeredBy) {
        EnrollmentEventEntity event = EnrollmentEventEntity.builder()
                .enrollment(enrollment)
                .eventType(eventType)
                .previousStatus(previousStatus != null ? previousStatus.name() : null)
                .newStatus(newStatus.name())
                .eventDetail(detail)
                .triggeredBy(triggeredBy)
                .createdAt(Instant.now())
                .build();
        eventRepository.save(event);
    }

    private void validateTransition(EnrollmentStatus from, EnrollmentStatus to) {
        Set<EnrollmentStatus> allowed = VALID_TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidOperationException(
                    "Cannot transition enrollment from " + from + " to " + to);
        }
    }

    private EnrollmentEventType resolveEventType(EnrollmentStatus from, EnrollmentStatus to) {

        return switch (to) {
            case SUSPENDED -> EnrollmentEventType.ENROLLMENT_SUSPENDED;
            case CANCELLED -> EnrollmentEventType.ENROLLMENT_CANCELLED;
            case ACTIVE -> from == EnrollmentStatus.SUSPENDED ? EnrollmentEventType.ENROLLMENT_RESTORED : EnrollmentEventType.ENROLLMENT_ACTIVATED;
            default -> EnrollmentEventType.ENROLLMENT_ACTIVATED;
        };


    }

    private CourseEnrollmentEntity findOrThrow(UUID id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
    }

}
