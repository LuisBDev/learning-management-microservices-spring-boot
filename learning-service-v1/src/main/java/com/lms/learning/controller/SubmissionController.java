package com.lms.learning.controller;

import com.lms.learning.application.service.SubmissionService;
import com.lms.learning.controller.dto.request.CreateSubmissionRequest;
import com.lms.learning.controller.dto.response.SubmissionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody CreateSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.submit(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.getById(id));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<Page<SubmissionResponse>> getByAssignmentId(
            @PathVariable UUID assignmentId, Pageable pageable) {
        return ResponseEntity.ok(submissionService.getByAssignmentId(assignmentId, pageable));
    }

    @GetMapping("/student/{studentUserId}")
    public ResponseEntity<Page<SubmissionResponse>> getByStudentUserId(
            @PathVariable UUID studentUserId, Pageable pageable) {
        return ResponseEntity.ok(submissionService.getByStudentUserId(studentUserId, pageable));
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentUserId}")
    public ResponseEntity<SubmissionResponse> getByAssignmentAndStudent(
            @PathVariable UUID assignmentId,
            @PathVariable UUID studentUserId) {
        return ResponseEntity.ok(submissionService.getByAssignmentAndStudent(assignmentId, studentUserId));
    }

}
