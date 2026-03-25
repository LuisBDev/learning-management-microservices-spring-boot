package com.lms.course.controller;

import com.lms.course.application.service.CourseTeacherService;
import com.lms.course.controller.dto.request.AssignTeacherRequest;
import com.lms.course.controller.dto.response.CourseTeacherResponse;
import com.lms.course.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/teachers")
@RequiredArgsConstructor
public class CourseTeacherController {

    private final CourseTeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseTeacherResponse> assign(
            @PathVariable UUID courseId,
            @Valid @RequestBody AssignTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.assign(courseId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<List<CourseTeacherResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(teacherService.getByCourseId(courseId));
    }

    @DeleteMapping("/{teacherAssignmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remove(
            @PathVariable UUID courseId,
            @PathVariable UUID teacherAssignmentId) {
        teacherService.remove(courseId, teacherAssignmentId);
        return ResponseEntity.noContent().build();
    }

}
