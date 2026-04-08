package com.lms.enrollment.controller;

import com.lms.enrollment.application.service.EnrollmentService;
import com.lms.enrollment.controller.dto.request.CreateEnrollmentRequest;
import com.lms.enrollment.controller.dto.request.UpdateEnrollmentStatusRequest;
import com.lms.enrollment.controller.dto.response.EnrollmentEventResponse;
import com.lms.enrollment.controller.dto.response.EnrollmentResponse;
import com.lms.enrollment.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ENROLLMENT_MANAGE')")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody CreateEnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enroll(request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getById(id));
    }

    @GetMapping("/check")
    public ResponseEntity<EnrollmentResponse> getByCourseAndStudent(
            @RequestParam UUID courseId,
            @RequestParam UUID studentUserId) {
        return ResponseEntity.ok(enrollmentService.getByCourseAndStudent(courseId, studentUserId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<EnrollmentResponse>> getByCourseId(
            @PathVariable UUID courseId, Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getByCourseId(courseId, pageable));
    }

    @GetMapping("/student/{studentUserId}")
    public ResponseEntity<Page<EnrollmentResponse>> getByStudentUserId(
            @PathVariable UUID studentUserId, Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getByStudentUserId(studentUserId, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ENROLLMENT_MANAGE')")
    public ResponseEntity<EnrollmentResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<EnrollmentEventResponse>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentHistory(id));
    }

    @DeleteMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEnrollmentsByCourse(@PathVariable UUID courseId) {
        enrollmentService.deleteEnrollmentsByCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
