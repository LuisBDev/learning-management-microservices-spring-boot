package com.lms.learning.controller;

import com.lms.learning.application.service.CourseCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning/courses")
@RequiredArgsConstructor
public class LearningCourseRestController {

    private final CourseCleanupService cleanupService;

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourseLearningData(@PathVariable UUID courseId) {
        cleanupService.deleteCourseData(courseId);
        return ResponseEntity.noContent().build();
    }
}
