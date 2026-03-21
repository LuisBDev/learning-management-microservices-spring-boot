package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateCourseRequest;
import com.lms.course.controller.dto.request.UpdateCourseRequest;
import com.lms.course.controller.dto.response.CourseResponse;
import com.lms.course.domain.model.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {

    CourseResponse create(CreateCourseRequest request, UUID createdBy);

    CourseResponse getById(UUID id);

    Page<CourseResponse> getAll(Pageable pageable);

    CourseResponse update(UUID id, UpdateCourseRequest request);

    CourseResponse updateStatus(UUID id, CourseStatus status);

    void delete(UUID id);

}
