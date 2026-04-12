package com.lms.course.controller;

import com.lms.course.application.service.CourseService;
import com.lms.course.controller.dto.request.CreateCourseRequest;
import com.lms.course.controller.dto.request.UpdateCourseRequest;
import com.lms.course.controller.dto.request.UpdateCourseStatusRequest;
import com.lms.course.controller.dto.response.CoursePageResponse;
import com.lms.course.controller.dto.response.CourseResponse;
import com.lms.course.security.SecurityContextHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course lifecycle operations")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_CREATE')")
    @Operation(summary = "Create course", description = "Creates a new course owned by the current authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created",
                    content = @Content(schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse response = courseService.create(request, SecurityContextHelper.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List courses", description = "Returns a paginated list of courses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courses retrieved",
                    content = @Content(
                            schema = @Schema(implementation = CoursePageResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "content": [
                                        {
                                          "id": "97121825-56e2-4ada-b150-39a07e85d3ff",
                                          "code": "CV-PBI-2026-1",
                                          "title": "Power BI 01",
                                          "summary": "Summary",
                                          "status": "PUBLISHED",
                                          "createdBy": "a0089e7d-9589-4496-9be0-6745a04ae3ad",
                                          "createdAt": "2026-04-06T18:29:54.433642Z",
                                          "updatedAt": "2026-04-06T18:29:54.433642Z"
                                        }
                                      ],
                                      "page": {
                                        "size": 20,
                                        "number": 0,
                                        "totalElements": 1,
                                        "totalPages": 1
                                      }
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<Page<CourseResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(courseService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by id", description = "Returns one course by its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found",
                    content = @Content(schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<CourseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_UPDATE')")
    @Operation(summary = "Update course", description = "Updates editable fields of an existing course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated",
                    content = @Content(schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<CourseResponse> update(
            @Parameter(description = "Course id")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('COURSE_PUBLISH')")
    @Operation(summary = "Update course status", description = "Updates publication status for a course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course status updated",
                    content = @Content(schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<CourseResponse> updateStatus(
            @Parameter(description = "Course id")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseStatusRequest request) {
        return ResponseEntity.ok(courseService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course", description = "Deletes a course by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
