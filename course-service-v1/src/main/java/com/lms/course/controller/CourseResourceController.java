package com.lms.course.controller;

import com.lms.course.application.service.CourseResourceService;
import com.lms.course.controller.dto.request.*;
import com.lms.course.controller.dto.response.CourseResourceResponse;
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
@RequestMapping("/api/v1/courses/{courseId}")
@RequiredArgsConstructor
public class CourseResourceController {

    private final CourseResourceService resourceService;

    @PostMapping("/sections/{sectionId}/resources/text")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseResourceResponse> createText(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateTextResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createText(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @PostMapping("/sections/{sectionId}/resources/assignment")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    public ResponseEntity<CourseResourceResponse> createAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateAssignmentResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createAssignment(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @PostMapping("/sections/{sectionId}/resources/url")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseResourceResponse> createUrl(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateUrlResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createUrl(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping("/sections/{sectionId}/resources")
    public ResponseEntity<List<CourseResourceResponse>> getBySectionId(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(resourceService.getBySectionId(courseId, sectionId));
    }

    @GetMapping("/resources/{resourceId}")
    public ResponseEntity<CourseResourceResponse> getById(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId) {
        return ResponseEntity.ok(resourceService.getById(courseId, resourceId));
    }

    @PutMapping("/resources/{resourceId}/text")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseResourceResponse> updateText(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateTextResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateText(courseId, resourceId, request));
    }

    @PutMapping("/resources/{resourceId}/assignment")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    public ResponseEntity<CourseResourceResponse> updateAssignment(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateAssignmentResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateAssignment(courseId, resourceId, request));
    }

    @PutMapping("/resources/{resourceId}/url")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<CourseResourceResponse> updateUrl(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateUrlResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateUrl(courseId, resourceId, request));
    }

    @DeleteMapping("/resources/{resourceId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID courseId,
            @PathVariable UUID resourceId) {
        resourceService.delete(courseId, resourceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sections/{sectionId}/resources/reorder")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    public ResponseEntity<List<CourseResourceResponse>> reorder(
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @RequestBody List<UUID> resourceIds) {
        return ResponseEntity.ok(resourceService.reorder(courseId, sectionId, resourceIds));
    }

}
