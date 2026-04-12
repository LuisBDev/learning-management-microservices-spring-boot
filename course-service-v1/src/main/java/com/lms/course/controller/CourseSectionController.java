package com.lms.course.controller;

import com.lms.course.application.service.CourseSectionService;
import com.lms.course.controller.dto.request.CreateSectionRequest;
import com.lms.course.controller.dto.request.UpdateSectionRequest;
import com.lms.course.controller.dto.response.CourseSectionResponse;
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
@RequestMapping("/api/v1/courses/{courseId}/sections")
@RequiredArgsConstructor
@Tag(name = "Course Sections", description = "Course section management operations")
@SecurityRequirement(name = "bearerAuth")
public class CourseSectionController {

    private final CourseSectionService sectionService;

    @PostMapping
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    @Operation(summary = "Create section", description = "Creates a section inside a course.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Section created",
                    content = @Content(schema = @Schema(implementation = CourseSectionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<CourseSectionResponse> create(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sectionService.create(courseId, request));
    }

    @GetMapping
    @Operation(summary = "List sections", description = "Returns all sections for the given course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<List<CourseSectionResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(sectionService.getByCourseId(courseId));
    }

    @GetMapping("/{sectionId}")
    @Operation(summary = "Get section by id", description = "Returns a section by course id and section id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Section found",
                    content = @Content(schema = @Schema(implementation = CourseSectionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Section not found", content = @Content)
    })
    public ResponseEntity<CourseSectionResponse> getById(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(sectionService.getById(courseId, sectionId));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    @Operation(summary = "Update section", description = "Updates a section by course id and section id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Section updated",
                    content = @Content(schema = @Schema(implementation = CourseSectionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Section not found", content = @Content)
    })
    public ResponseEntity<CourseSectionResponse> update(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateSectionRequest request) {
        return ResponseEntity.ok(sectionService.update(courseId, sectionId, request));
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    @Operation(summary = "Delete section", description = "Deletes a section by course id and section id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Section deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Section not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId) {
        sectionService.delete(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    @Operation(summary = "Reorder sections", description = "Reorders sections using the provided ordered list of section ids.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sections reordered"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<List<CourseSectionResponse>> reorder(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @RequestBody List<UUID> sectionIds) {
        return ResponseEntity.ok(sectionService.reorder(courseId, sectionIds));
    }

}
