package com.lms.course.controller;

import com.lms.course.application.service.CourseSectionService;
import com.lms.course.controller.dto.request.CreateSectionRequest;
import com.lms.course.controller.dto.request.UpdateSectionRequest;
import com.lms.course.controller.dto.response.CourseSectionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/sections")
@RequiredArgsConstructor
public class CourseSectionController {

    private final CourseSectionService sectionService;

    @PostMapping
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<CourseSectionResponse> create(
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectionService.create(courseId, request));
    }

    @GetMapping
    public ResponseEntity<List<CourseSectionResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(sectionService.getByCourseId(courseId));
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<CourseSectionResponse> getById(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(sectionService.getById(courseId, sectionId));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<CourseSectionResponse> update(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateSectionRequest request) {
        return ResponseEntity.ok(sectionService.update(courseId, sectionId, request));
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        sectionService.delete(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<List<CourseSectionResponse>> reorder(
            @PathVariable UUID courseId,
            @RequestBody List<UUID> sectionIds) {
        return ResponseEntity.ok(sectionService.reorder(courseId, sectionIds));
    }

}
