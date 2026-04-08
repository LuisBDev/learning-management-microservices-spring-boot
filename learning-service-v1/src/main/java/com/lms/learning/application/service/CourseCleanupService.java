package com.lms.learning.application.service;

import java.util.UUID;

public interface CourseCleanupService {
    void deleteCourseData(UUID courseId);
}
