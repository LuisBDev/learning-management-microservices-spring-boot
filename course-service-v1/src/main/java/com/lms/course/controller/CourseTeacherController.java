package com.lms.course.controller;

import com.lms.course.application.service.CourseTeacherService;
import com.lms.course.controller.dto.request.AssignTeacherRequest;
import com.lms.course.controller.dto.response.CourseTeacherResponse;
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
@RequestMapping("/api/v1/courses/{courseId}/teachers")
@RequiredArgsConstructor
@Tag(name = "Course Teachers", description = "Teacher assignment operations for courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseTeacherController {

    private final CourseTeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign teacher", description = "Assigns a teacher to the course.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Teacher assigned",
                    content = @Content(schema = @Schema(implementation = CourseTeacherResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or teacher not found", content = @Content)
    })
    public ResponseEntity<CourseTeacherResponse> assign(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Valid @RequestBody AssignTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.assign(courseId, request, SecurityContextHelper.getCurrentUserId()));
    }

    @GetMapping
    @Operation(summary = "List teachers", description = "Returns teacher assignments for a course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teachers retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<List<CourseTeacherResponse>> getByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(teacherService.getByCourseId(courseId));
    }

    @DeleteMapping("/{teacherAssignmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove teacher assignment", description = "Removes a teacher assignment from a course.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Teacher assignment removed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = @Content)
    })
    public ResponseEntity<Void> remove(
            @Parameter(description = "Course id")
            @PathVariable UUID courseId,
            @Parameter(description = "Teacher assignment id")
            @PathVariable UUID teacherAssignmentId) {
        teacherService.remove(courseId, teacherAssignmentId);
        return ResponseEntity.noContent().build();
    }

}
