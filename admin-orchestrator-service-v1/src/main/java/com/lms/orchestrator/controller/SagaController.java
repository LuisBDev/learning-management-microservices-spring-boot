package com.lms.orchestrator.controller;

import com.lms.orchestrator.application.service.CourseDeletionSagaService;
import com.lms.orchestrator.application.service.CourseEnrollmentSagaService;
import com.lms.orchestrator.domain.model.CourseEnrollmentSagaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orchestrator/saga")
@RequiredArgsConstructor
public class SagaController {

    private final CourseEnrollmentSagaService enrollmentSagaService;
    private final CourseDeletionSagaService deletionSagaService;

    @PostMapping("/course-enrollment")
    public ResponseEntity<String> createCourseWithEnrollment(@Valid @RequestBody CourseEnrollmentSagaRequest request) {
        enrollmentSagaService.executeSaga(request);
        return ResponseEntity.ok("SAGA initiated successfully");
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        deletionSagaService.executeSaga(id);
        return ResponseEntity.noContent().build();
    }
}
