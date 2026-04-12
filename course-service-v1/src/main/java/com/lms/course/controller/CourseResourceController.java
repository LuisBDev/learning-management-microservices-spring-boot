package com.lms.course.controller;

import com.lms.course.application.service.CourseResourceService;
import com.lms.course.controller.dto.request.*;
import com.lms.course.controller.dto.response.CourseResourceResponse;
import com.lms.course.security.SecurityContextHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Course Resources", description = "Course resource management operations")
@SecurityRequirement(name = "bearerAuth")
public class CourseResourceController {

    private final CourseResourceService resourceService;

    @PostMapping("/sections/{sectionId}/resources/text")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Create text resource", description = "Creates a text resource in a section.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Text resource created",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> createText(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateTextResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createText(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @PostMapping("/sections/{sectionId}/resources/assignment")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    @Operation(summary = "Create assignment resource", description = "Creates an assignment resource in a section.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assignment resource created",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> createAssignment(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateAssignmentResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createAssignment(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @PostMapping("/sections/{sectionId}/resources/url")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Create URL resource", description = "Creates a URL resource in a section.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "URL resource created",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> createUrl(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateUrlResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createUrl(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping("/sections/{sectionId}/resources")
    @Operation(summary = "List section resources", description = "Returns resources for one section.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<List<CourseResourceResponse>> getBySectionId(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(resourceService.getBySectionId(courseId, sectionId));
    }

    @GetMapping("/resources/{resourceId}")
    @Operation(summary = "Get resource by id", description = "Returns one resource by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource found",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> getById(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Resource id")
            @PathVariable UUID resourceId) {
        return ResponseEntity.ok(resourceService.getById(courseId, resourceId));
    }

    @PutMapping("/resources/{resourceId}/text")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Update text resource", description = "Updates a text resource by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> updateText(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Resource id")
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateTextResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateText(courseId, resourceId, request));
    }

    @PutMapping("/resources/{resourceId}/assignment")
    @PreAuthorize("hasAuthority('ASSIGNMENT_MANAGE')")
    @Operation(summary = "Update assignment resource", description = "Updates an assignment resource by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> updateAssignment(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Resource id")
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateAssignmentResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateAssignment(courseId, resourceId, request));
    }

    @PutMapping("/resources/{resourceId}/url")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Update URL resource", description = "Updates a URL resource by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resource updated",
                    content = @Content(schema = @Schema(implementation = CourseResourceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<CourseResourceResponse> updateUrl(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Resource id")
            @PathVariable UUID resourceId,
            @Valid @RequestBody UpdateUrlResourceRequest request) {
        return ResponseEntity.ok(resourceService.updateUrl(courseId, resourceId, request));
    }

    @DeleteMapping("/resources/{resourceId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Delete resource", description = "Deletes one resource by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resource deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Resource id")
            @PathVariable UUID resourceId) {
        resourceService.delete(courseId, resourceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sections/{sectionId}/resources/reorder")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Reorder resources", description = "Reorders resources inside a section using an ordered list of resource ids.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources reordered"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course, section or resource not found", content = @Content)
    })
    public ResponseEntity<List<CourseResourceResponse>> reorder(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @RequestBody List<UUID> resourceIds) {
        return ResponseEntity.ok(resourceService.reorder(courseId, sectionId, resourceIds));
    }

}
