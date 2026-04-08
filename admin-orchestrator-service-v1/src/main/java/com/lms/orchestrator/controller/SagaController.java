package com.lms.orchestrator.controller;

import com.lms.orchestrator.application.service.CourseEnrollmentSagaService;
import com.lms.orchestrator.domain.model.CourseEnrollmentSagaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orchestrator/saga")
@RequiredArgsConstructor
public class SagaController {

    private final CourseEnrollmentSagaService sagaService;

    @PostMapping("/course-enrollment")
    public ResponseEntity<String> createCourseWithEnrollment(@Valid @RequestBody CourseEnrollmentSagaRequest request) {
        sagaService.executeSaga(request);
        return ResponseEntity.ok("SAGA initiated successfully");
    }
}
