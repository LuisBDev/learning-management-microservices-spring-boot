package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateCourseRequest;
import com.lms.course.controller.dto.request.UpdateCourseRequest;
import com.lms.course.controller.dto.response.CourseResponse;
import com.lms.course.controller.mapper.CourseMapper;
import com.lms.course.domain.model.enums.CourseStatus;
import com.lms.course.exception.DuplicateResourceException;
import com.lms.course.exception.ResourceNotFoundException;
import com.lms.course.infrastructure.persistence.entity.CourseEntity;
import com.lms.course.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseSectionRepository sectionRepository;
    private final JpaCourseTeacherRepository teacherRepository;
    private final JpaCourseResourceRepository resourceRepository;
    private final JpaCourseRecordingRepository recordingRepository;
    private final JpaAssignmentRepository assignmentRepository;
    private final JpaAssignmentMaterialFileRepository assignmentMaterialFileRepository;
    private final JpaResourceFileRepository resourceFileRepository;
    private final JpaResourceTextRepository resourceTextRepository;
    private final JpaResourceUrlRepository resourceUrlRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse create(CreateCourseRequest request, UUID createdBy) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course with code '" + request.getCode() + "' already exists");
        }

        CourseEntity course = CourseEntity.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .summary(request.getSummary())
                .status(CourseStatus.PUBLISHED)
                .createdBy(createdBy)
                .build();

        course = courseRepository.save(course);
        log.info("Course created: {} ({}) - ID: {}", course.getTitle(), course.getCode(), course.getId());
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(UUID id) {
        CourseEntity course = findCourseOrThrow(id);
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAll(Pageable pageable) {
        log.info("Get all courses request: {}", pageable);
        return courseRepository.findAll(pageable).map(courseMapper::toResponse);
    }

    @Override
    @Transactional
    public CourseResponse update(UUID id, UpdateCourseRequest request) {

        CourseEntity course = findCourseOrThrow(id);

        courseMapper.updateCourseEntityFromRequest(request, course);

        course = courseRepository.save(course);
        log.info("Course updated: {}", course.getId());
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateStatus(UUID id, CourseStatus status) {

        CourseEntity course = findCourseOrThrow(id);

        CourseStatus previousStatus = course.getStatus();
        course.setStatus(status);

        course = courseRepository.save(course);
        log.info("Course {} status changed: {} -> {}", course.getId(), previousStatus, status);
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        
        log.info("Starting physical cascade delete for course: {}", id);

        // 1. Delete granular resource data (deepest level)
        assignmentMaterialFileRepository.deleteByCourseId(id);
        assignmentRepository.deleteByCourseId(id);
        resourceFileRepository.deleteByCourseId(id);
        resourceTextRepository.deleteByCourseId(id);
        resourceUrlRepository.deleteByCourseId(id);

        // 2. Delete intermediate entities
        resourceRepository.deleteByCourseId(id);
        recordingRepository.deleteByCourseId(id);
        teacherRepository.deleteByCourseId(id);
        sectionRepository.deleteByCourseId(id);

        // 3. Delete root entity
        courseRepository.deleteById(id);

        log.info("Successfully deleted course and all its related data: {}", id);
    }

    private CourseEntity findCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

}
