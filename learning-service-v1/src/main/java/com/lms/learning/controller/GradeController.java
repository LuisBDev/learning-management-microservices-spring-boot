package com.lms.learning.controller;

import com.lms.learning.application.service.GradeService;
import com.lms.learning.controller.dto.request.GradeSubmissionRequest;
import com.lms.learning.controller.dto.request.UpdateGradeRequest;
import com.lms.learning.controller.dto.response.GradeResponse;
import com.lms.learning.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ASSIGNMENT_GRADE')")
    public ResponseEntity<GradeResponse> grade(@Valid @RequestBody GradeSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gradeService.grade(request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gradeService.getById(id));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<Page<GradeResponse>> getByAssignmentId(
            @PathVariable UUID assignmentId, Pageable pageable) {
        return ResponseEntity.ok(gradeService.getByAssignmentId(assignmentId, pageable));
    }

    @GetMapping("/student/{studentUserId}")
    public ResponseEntity<Page<GradeResponse>> getByStudentUserId(
            @PathVariable UUID studentUserId, Pageable pageable) {
        return ResponseEntity.ok(gradeService.getByStudentUserId(studentUserId, pageable));
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentUserId}")
    public ResponseEntity<GradeResponse> getByAssignmentAndStudent(
            @PathVariable UUID assignmentId,
            @PathVariable UUID studentUserId) {
        return ResponseEntity.ok(gradeService.getByAssignmentAndStudent(assignmentId, studentUserId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ASSIGNMENT_GRADE')")
    public ResponseEntity<GradeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGradeRequest request) {
        return ResponseEntity.ok(gradeService.update(id, request, SecurityContextHelper.getCurrentUserId()));
    }

}
