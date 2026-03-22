package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateSectionRequest;
import com.lms.course.controller.dto.request.UpdateSectionRequest;
import com.lms.course.controller.dto.response.CourseSectionResponse;
import com.lms.course.controller.mapper.CourseSectionMapper;
import com.lms.course.exception.InvalidOperationException;
import com.lms.course.exception.ResourceNotFoundException;
import com.lms.course.infrastructure.persistence.entity.CourseEntity;
import com.lms.course.infrastructure.persistence.entity.CourseSectionEntity;
import com.lms.course.infrastructure.persistence.repository.JpaCourseRepository;
import com.lms.course.infrastructure.persistence.repository.JpaCourseSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSectionServiceImpl implements CourseSectionService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseSectionRepository sectionRepository;
    private final CourseSectionMapper sectionMapper;

    @Override
    @Transactional
    public CourseSectionResponse create(UUID courseId, CreateSectionRequest request) {
        CourseEntity course = findCourseOrThrow(courseId);

        List<CourseSectionEntity> existingSections = sectionRepository.findByCourseIdOrderByPositionAsc(courseId);
        int nextPosition = existingSections.size() + 1;

        CourseSectionEntity section = CourseSectionEntity.builder()
                .course(course)
                .title(request.getTitle())
                .description(request.getDescription())
                .position(nextPosition)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .build();

        section = sectionRepository.save(section);
        log.info("Section created: '{}' at position {} in course {}", section.getTitle(), nextPosition, courseId);
        return sectionMapper.toResponse(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionResponse> getByCourseId(UUID courseId) {
        assertCourseExists(courseId);
        List<CourseSectionEntity> sections = sectionRepository.findByCourseIdOrderByPositionAsc(courseId);
        return sectionMapper.toResponseList(sections);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseSectionResponse getById(UUID courseId, UUID sectionId) {
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);
        return sectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public CourseSectionResponse update(UUID courseId, UUID sectionId, UpdateSectionRequest request) {
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);

        sectionMapper.updateCourseSectionEntityFromRequest(request, section);

        section = sectionRepository.save(section);
        log.info("Section updated: {} in course {}", sectionId, courseId);
        return sectionMapper.toResponse(section);
    }

    @Override
    @Transactional
    public void delete(UUID courseId, UUID sectionId) {
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);
        int deletedPosition = section.getPosition();

        sectionRepository.delete(section);

        List<CourseSectionEntity> remaining = sectionRepository.findByCourseIdOrderByPositionAsc(courseId);
        int pos = 1;
        for (CourseSectionEntity s : remaining) {
            if (s.getPosition() != pos) {
                s.setPosition(pos);
            }
            pos++;
        }
        sectionRepository.saveAll(remaining);

        log.info("Section deleted: {} from course {}, positions recalculated", sectionId, courseId);
    }

    @Override
    @Transactional
    public List<CourseSectionResponse> reorder(UUID courseId, List<UUID> sectionIds) {
        assertCourseExists(courseId);
        List<CourseSectionEntity> sections = sectionRepository.findByCourseIdOrderByPositionAsc(courseId);

        if (sectionIds.size() != sections.size()) {
            throw new InvalidOperationException(
                    "Must provide exactly " + sections.size() + " section IDs for reordering");
        }

        for (int i = 0; i < sectionIds.size(); i++) {
            UUID targetId = sectionIds.get(i);
            CourseSectionEntity section = sections.stream()
                    .filter(s -> s.getId().equals(targetId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Section", "id", targetId));
            section.setPosition(i + 1);
        }

        sectionRepository.saveAll(sections);
        log.info("Sections reordered in course {}", courseId);

        return sectionMapper.toResponseList(
                sectionRepository.findByCourseIdOrderByPositionAsc(courseId));
    }

    private CourseEntity findCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    private void assertCourseExists(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }
    }

    private CourseSectionEntity findSectionOrThrow(UUID courseId, UUID sectionId) {
        assertCourseExists(courseId);
        CourseSectionEntity section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new InvalidOperationException("Section " + sectionId + " does not belong to course " + courseId);
        }
        return section;
    }

}
