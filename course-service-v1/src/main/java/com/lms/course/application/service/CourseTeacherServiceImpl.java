package com.lms.course.application.service;

import com.lms.course.controller.dto.request.AssignTeacherRequest;
import com.lms.course.controller.dto.response.CourseTeacherResponse;
import com.lms.course.controller.mapper.CourseTeacherMapper;
import com.lms.course.exception.DuplicateResourceException;
import com.lms.course.exception.ResourceNotFoundException;
import com.lms.course.infrastructure.client.identityservicev1.IdentityServiceClient;
import com.lms.course.infrastructure.persistence.entity.CourseEntity;
import com.lms.course.infrastructure.persistence.entity.CourseTeacherEntity;
import com.lms.course.infrastructure.persistence.repository.JpaCourseRepository;
import com.lms.course.infrastructure.persistence.repository.JpaCourseTeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseTeacherRepository teacherRepository;
    private final CourseTeacherMapper teacherMapper;
    private final IdentityServiceClient identityServiceClient;

    @Override
    @Transactional
    public CourseTeacherResponse assign(UUID courseId, AssignTeacherRequest request, UUID assignedBy) {

        UUID teacherUserId = request.getTeacherUserId();
        
        identityServiceClient.getUserById(teacherUserId);

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (teacherRepository.existsByCourseIdAndTeacherUserId(courseId, teacherUserId)) {
            throw new DuplicateResourceException(
                    "Teacher " + teacherUserId + " is already assigned to course " + courseId);
        }

        CourseTeacherEntity teacher = CourseTeacherEntity.builder()
                .course(course)
                .teacherUserId(teacherUserId)
                .assignedBy(assignedBy)
                .assignedAt(Instant.now())
                .build();

        teacher = teacherRepository.save(teacher);
        log.info("Teacher {} assigned to course {}", teacherUserId, courseId);
        return teacherMapper.toResponse(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseTeacherResponse> getByCourseId(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }
        return teacherMapper.toResponseList(teacherRepository.findByCourseId(courseId));
    }

    @Override
    @Transactional
    public void remove(UUID courseId, UUID teacherAssignmentId) {
        CourseTeacherEntity teacher = teacherRepository.findById(teacherAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseTeacher", "id", teacherAssignmentId));

        if (!teacher.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("CourseTeacher", "id", teacherAssignmentId);
        }

        teacherRepository.delete(teacher);
        log.info("Teacher assignment {} removed from course {}", teacherAssignmentId, courseId);
    }

}
