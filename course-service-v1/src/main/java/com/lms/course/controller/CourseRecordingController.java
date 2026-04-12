package com.lms.course.controller;

import com.lms.course.application.service.CourseRecordingService;
import com.lms.course.controller.dto.request.CreateRecordingRequest;
import com.lms.course.controller.dto.request.UpdateRecordingRequest;
import com.lms.course.controller.dto.response.CourseRecordingResponse;
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
@RequestMapping("/api/v1/courses/{courseId}/recordings")
@RequiredArgsConstructor
@Tag(name = "Course Recordings", description = "Recording management operations for courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseRecordingController {

    private final CourseRecordingService recordingService;

    @PostMapping("/sections/{sectionId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Create recording", description = "Creates a recording associated with a course section.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recording created",
                    content = @Content(schema = @Schema(implementation = CourseRecordingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or section not found", content = @Content)
    })
    public ResponseEntity<CourseRecordingResponse> create(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Section id")
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateRecordingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordingService.create(courseId, sectionId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping
    @Operation(summary = "List recordings", description = "Returns recordings for the given course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recordings retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<List<CourseRecordingResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(recordingService.getByCourseId(courseId));
    }

    @GetMapping("/{recordingId}")
    @Operation(summary = "Get recording by id", description = "Returns one recording by course id and recording id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recording found",
                    content = @Content(schema = @Schema(implementation = CourseRecordingResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recording not found", content = @Content)
    })
    public ResponseEntity<CourseRecordingResponse> getById(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Recording id")
            @PathVariable UUID recordingId) {
        return ResponseEntity.ok(recordingService.getById(courseId, recordingId));
    }

    @PutMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Update recording", description = "Updates one recording by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recording updated",
                    content = @Content(schema = @Schema(implementation = CourseRecordingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recording not found", content = @Content)
    })
    public ResponseEntity<CourseRecordingResponse> update(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Recording id")
            @PathVariable UUID recordingId,
            @Valid @RequestBody UpdateRecordingRequest request) {
        return ResponseEntity.ok(recordingService.update(courseId, recordingId, request));
    }

    @DeleteMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE')")
    @Operation(summary = "Delete recording", description = "Deletes one recording by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recording deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recording not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Recording id")
            @PathVariable UUID recordingId) {
        recordingService.delete(courseId, recordingId);
        return ResponseEntity.noContent().build();
    }

}
