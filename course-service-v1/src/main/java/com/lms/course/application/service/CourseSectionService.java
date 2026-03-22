package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateSectionRequest;
import com.lms.course.controller.dto.request.UpdateSectionRequest;
import com.lms.course.controller.dto.response.CourseSectionResponse;

import java.util.List;
import java.util.UUID;

public interface CourseSectionService {

    CourseSectionResponse create(UUID courseId, CreateSectionRequest request);

    List<CourseSectionResponse> getByCourseId(UUID courseId);

    CourseSectionResponse getById(UUID courseId, UUID sectionId);

    CourseSectionResponse update(UUID courseId, UUID sectionId, UpdateSectionRequest request);

    void delete(UUID courseId, UUID sectionId);

    List<CourseSectionResponse> reorder(UUID courseId, List<UUID> sectionIds);

}
