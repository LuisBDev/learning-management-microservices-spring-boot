package com.lms.course.application.service;

import com.lms.course.controller.dto.request.AssignTeacherRequest;
import com.lms.course.controller.dto.response.CourseTeacherResponse;

import java.util.List;
import java.util.UUID;

public interface CourseTeacherService {

    CourseTeacherResponse assign(UUID courseId, AssignTeacherRequest request, UUID assignedBy);

    List<CourseTeacherResponse> getByCourseId(UUID courseId);

    void remove(UUID courseId, UUID teacherAssignmentId);

}
