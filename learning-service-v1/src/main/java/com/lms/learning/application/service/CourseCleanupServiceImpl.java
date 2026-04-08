package com.lms.learning.application.service;

import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentGradeRepository;
import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentSubmissionFileRepository;
import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseCleanupServiceImpl implements CourseCleanupService {

    private final JpaAssignmentGradeRepository gradeRepository;
    private final JpaAssignmentSubmissionRepository submissionRepository;
    private final JpaAssignmentSubmissionFileRepository fileRepository;

    @Override
    @Transactional
    public void deleteCourseData(UUID courseId) {
        log.info("Deleting all learning data for course: {}", courseId);
        
        // 1. Delete files first (dependent on submissions)
        fileRepository.deleteByCourseId(courseId);
        
        // 2. Delete grades (dependent on submissions)
        gradeRepository.deleteByCourseId(courseId);
        
        // 3. Delete submissions
        submissionRepository.deleteByCourseId(courseId);
        
        log.info("Successfully deleted learning data for course: {}", courseId);
    }
}
