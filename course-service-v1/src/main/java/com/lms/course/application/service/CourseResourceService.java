package com.lms.course.application.service;

import com.lms.course.controller.dto.request.*;
import com.lms.course.controller.dto.response.CourseResourceResponse;

import java.util.List;
import java.util.UUID;

public interface CourseResourceService {

    CourseResourceResponse createText(UUID courseId, UUID sectionId, CreateTextResourceRequest request, UUID createdBy);

    CourseResourceResponse createUrl(UUID courseId, UUID sectionId, CreateUrlResourceRequest request, UUID createdBy);

    CourseResourceResponse createAssignment(UUID courseId, UUID sectionId, CreateAssignmentResourceRequest request, UUID createdBy);

    List<CourseResourceResponse> getBySectionId(UUID courseId, UUID sectionId);

    CourseResourceResponse getById(UUID courseId, UUID resourceId);

    CourseResourceResponse updateText(UUID courseId, UUID resourceId, UpdateTextResourceRequest request);

    CourseResourceResponse updateUrl(UUID courseId, UUID resourceId, UpdateUrlResourceRequest request);

    CourseResourceResponse updateAssignment(UUID courseId, UUID resourceId, UpdateAssignmentResourceRequest request);

    void delete(UUID courseId, UUID resourceId);

    List<CourseResourceResponse> reorder(UUID courseId, UUID sectionId, List<UUID> resourceIds);

}
