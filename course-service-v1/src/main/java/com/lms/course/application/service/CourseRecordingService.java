package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateRecordingRequest;
import com.lms.course.controller.dto.request.UpdateRecordingRequest;
import com.lms.course.controller.dto.response.CourseRecordingResponse;

import java.util.List;
import java.util.UUID;

public interface CourseRecordingService {

    CourseRecordingResponse create(UUID courseId, UUID sectionId, CreateRecordingRequest request, UUID createdBy);

    List<CourseRecordingResponse> getByCourseId(UUID courseId);

    CourseRecordingResponse getById(UUID courseId, UUID recordingId);

    CourseRecordingResponse update(UUID courseId, UUID recordingId, UpdateRecordingRequest request);

    void delete(UUID courseId, UUID recordingId);

}
