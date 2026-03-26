package com.lms.course.controller;

import com.lms.course.application.service.CourseRecordingService;
import com.lms.course.controller.dto.request.CreateRecordingRequest;
import com.lms.course.controller.dto.request.UpdateRecordingRequest;
import com.lms.course.controller.dto.response.CourseRecordingResponse;
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
@RequestMapping("/api/v1/courses/{courseId}/recordings")
@RequiredArgsConstructor
public class CourseRecordingController {

    private final CourseRecordingService recordingService;

    @PostMapping("/sections/{sectionId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseRecordingResponse> create(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateRecordingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordingService.create(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<List<CourseRecordingResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(recordingService.getByCourseId(courseId));
    }

    @GetMapping("/{recordingId}")
    public ResponseEntity<CourseRecordingResponse> getById(
            @PathVariable UUID courseId,
            @PathVariable UUID recordingId) {
        return ResponseEntity.ok(recordingService.getById(courseId, recordingId));
    }

    @PutMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseRecordingResponse> update(
            @PathVariable UUID courseId,
            @PathVariable UUID recordingId,
            @Valid @RequestBody UpdateRecordingRequest request) {
        return ResponseEntity.ok(recordingService.update(courseId, recordingId, request));
    }

    @DeleteMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID courseId,
            @PathVariable UUID recordingId) {
        recordingService.delete(courseId, recordingId);
        return ResponseEntity.noContent().build();
    }

}
