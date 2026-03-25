package com.lms.course.application.service;

import com.lms.course.controller.dto.request.*;
import com.lms.course.controller.dto.response.CourseResourceResponse;
import com.lms.course.domain.model.enums.ResourceType;
import com.lms.course.exception.InvalidOperationException;
import com.lms.course.exception.ResourceNotFoundException;
import com.lms.course.infrastructure.persistence.entity.*;
import com.lms.course.infrastructure.persistence.repository.*;
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
public class CourseResourceServiceImpl implements CourseResourceService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseSectionRepository sectionRepository;
    private final JpaCourseResourceRepository resourceRepository;
    private final JpaResourceTextRepository textRepository;
    private final JpaResourceUrlRepository urlRepository;
    private final JpaAssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public CourseResourceResponse createText(UUID courseId, UUID sectionId, CreateTextResourceRequest request, UUID createdBy) {

        CourseEntity course = findCourseOrThrow(courseId);
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);
        int nextPosition = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId).size() + 1;

        CourseResourceEntity resource = CourseResourceEntity.builder()
                .course(course)
                .section(section)
                .resourceType(ResourceType.TEXT)
                .title(request.getTitle())
                .position(nextPosition)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .published(Boolean.TRUE)
                .createdBy(createdBy)
                .build();
        resource = resourceRepository.save(resource);

        ResourceTextEntity text = ResourceTextEntity.builder()
                .resource(resource)
                .contentText(request.getContentText())
                .build();
        textRepository.save(text);

        log.info("Text resource created: '{}' in section {} of course {}", request.getTitle(), sectionId, courseId);
        return buildResponse(resource, text, null, null);
    }

    @Override
    @Transactional
    public CourseResourceResponse createUrl(UUID courseId, UUID sectionId, CreateUrlResourceRequest request, UUID createdBy) {

        CourseEntity course = findCourseOrThrow(courseId);
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);
        int nextPosition = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId).size() + 1;

        CourseResourceEntity resource = CourseResourceEntity.builder()
                .course(course)
                .section(section)
                .resourceType(ResourceType.URL)
                .title(request.getTitle())
                .position(nextPosition)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .published(Boolean.TRUE)
                .createdBy(createdBy)
                .build();
        resource = resourceRepository.save(resource);

        ResourceUrlEntity url = ResourceUrlEntity.builder()
                .resource(resource)
                .url(request.getUrl())
                .urlKind(request.getUrlKind())
                .openInNewTab(request.getOpenInNewTab() != null ? request.getOpenInNewTab() : true)
                .build();
        urlRepository.save(url);

        log.info("URL resource created: '{}' in section {} of course {}", request.getTitle(), sectionId, courseId);
        return buildResponse(resource, null, url, null);
    }

    @Override
    @Transactional
    public CourseResourceResponse createAssignment(UUID courseId, UUID sectionId, CreateAssignmentResourceRequest request, UUID createdBy) {
        CourseEntity course = findCourseOrThrow(courseId);
        CourseSectionEntity section = findSectionOrThrow(courseId, sectionId);
        int nextPosition = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId).size() + 1;

        CourseResourceEntity resource = CourseResourceEntity.builder()
                .course(course)
                .section(section)
                .resourceType(ResourceType.ASSIGNMENT)
                .title(request.getTitle())
                .position(nextPosition)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .published(Boolean.TRUE)
                .createdBy(createdBy)
                .build();
        resource = resourceRepository.save(resource);

        AssignmentEntity assignment = AssignmentEntity.builder()
                .resource(resource)
                .instructionsText(request.getInstructionsText())
                .availableFrom(request.getAvailableFrom())
                .dueAt(request.getDueAt())
                .maxScore(request.getMaxScore())
                .allowResubmission(request.getAllowResubmission() != null ? request.getAllowResubmission() : false)
                .build();
        assignmentRepository.save(assignment);

        log.info("Assignment resource created: '{}' in section {} of course {}", request.getTitle(), sectionId, courseId);
        return buildResponse(resource, null, null, assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResourceResponse> getBySectionId(UUID courseId, UUID sectionId) {
        findSectionOrThrow(courseId, sectionId);
        List<CourseResourceEntity> resources = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId);
        return resources.stream().map(this::buildResponseWithDetails).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResourceResponse getById(UUID courseId, UUID resourceId) {
        CourseResourceEntity resource = findResourceOrThrow(courseId, resourceId);
        return buildResponseWithDetails(resource);
    }

    @Override
    @Transactional
    public CourseResourceResponse updateText(UUID courseId, UUID resourceId, UpdateTextResourceRequest request) {
        CourseResourceEntity resource = findResourceOrThrow(courseId, resourceId);
        if (resource.getResourceType() != ResourceType.TEXT) {
            throw new InvalidOperationException("Resource " + resourceId + " is not a TEXT resource");
        }


        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getVisible() != null) resource.setVisible(request.getVisible());
        if (request.getPublished() != null) resource.setPublished(request.getPublished());
        resource = resourceRepository.save(resource);

        ResourceTextEntity text = textRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceText", "resourceId", resourceId));
        text.setContentText(request.getContentText());
        text.setUpdatedAt(Instant.now());
        textRepository.save(text);

        log.info("Text resource updated: {} in course {}", resourceId, courseId);
        return buildResponse(resource, text, null, null);
    }

    @Override
    @Transactional
    public CourseResourceResponse updateUrl(UUID courseId, UUID resourceId, UpdateUrlResourceRequest request) {
        CourseResourceEntity resource = findResourceOrThrow(courseId, resourceId);
        if (resource.getResourceType() != ResourceType.URL) {
            throw new InvalidOperationException("Resource " + resourceId + " is not a URL resource");
        }

        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getVisible() != null) resource.setVisible(request.getVisible());
        if (request.getPublished() != null) resource.setPublished(request.getPublished());
        resource = resourceRepository.save(resource);

        ResourceUrlEntity url = urlRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceUrl", "resourceId", resourceId));
        url.setUrl(request.getUrl());
        if (request.getUrlKind() != null) url.setUrlKind(request.getUrlKind());
        if (request.getOpenInNewTab() != null) url.setOpenInNewTab(request.getOpenInNewTab());
        url.setUpdatedAt(Instant.now());
        urlRepository.save(url);

        log.info("URL resource updated: {} in course {}", resourceId, courseId);
        return buildResponse(resource, null, url, null);
    }

    @Override
    @Transactional
    public CourseResourceResponse updateAssignment(UUID courseId, UUID resourceId, UpdateAssignmentResourceRequest request) {
        CourseResourceEntity resource = findResourceOrThrow(courseId, resourceId);
        if (resource.getResourceType() != ResourceType.ASSIGNMENT) {
            throw new InvalidOperationException("Resource " + resourceId + " is not an ASSIGNMENT resource");
        }

        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getVisible() != null) resource.setVisible(request.getVisible());
        if (request.getPublished() != null) resource.setPublished(request.getPublished());
        resource = resourceRepository.save(resource);

        AssignmentEntity assignment = assignmentRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "resourceId", resourceId));
        if (request.getInstructionsText() != null) assignment.setInstructionsText(request.getInstructionsText());
        if (request.getAvailableFrom() != null) assignment.setAvailableFrom(request.getAvailableFrom());
        if (request.getDueAt() != null) assignment.setDueAt(request.getDueAt());
        if (request.getMaxScore() != null) assignment.setMaxScore(request.getMaxScore());
        if (request.getAllowResubmission() != null) assignment.setAllowResubmission(request.getAllowResubmission());
        assignment.setUpdatedAt(Instant.now());
        assignmentRepository.save(assignment);

        log.info("Assignment resource updated: {} in course {}", resourceId, courseId);
        return buildResponse(resource, null, null, assignment);
    }

    @Override
    @Transactional
    public void delete(UUID courseId, UUID resourceId) {
        CourseResourceEntity resource = findResourceOrThrow(courseId, resourceId);
        UUID sectionId = resource.getSection().getId();

        resourceRepository.delete(resource);

        List<CourseResourceEntity> remaining = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId);
        int pos = 1;
        for (CourseResourceEntity r : remaining) {
            if (r.getPosition() != pos) {
                r.setPosition(pos);
            }
            pos++;
        }
        resourceRepository.saveAll(remaining);

        log.info("Resource deleted: {} from course {}, positions recalculated", resourceId, courseId);
    }

    @Override
    @Transactional
    public List<CourseResourceResponse> reorder(UUID courseId, UUID sectionId, List<UUID> resourceIds) {
        findSectionOrThrow(courseId, sectionId);
        List<CourseResourceEntity> resources = resourceRepository.findBySectionIdOrderByPositionAsc(sectionId);

        if (resourceIds.size() != resources.size()) {
            throw new InvalidOperationException(
                    "Must provide exactly " + resources.size() + " resource IDs for reordering");
        }

        for (int i = 0; i < resourceIds.size(); i++) {
            UUID targetId = resourceIds.get(i);
            CourseResourceEntity resource = resources.stream()
                    .filter(r -> r.getId().equals(targetId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", targetId));
            resource.setPosition(i + 1);
        }
        resourceRepository.saveAll(resources);

        log.info("Resources reordered in section {} of course {}", sectionId, courseId);
        return resourceRepository.findBySectionIdOrderByPositionAsc(sectionId).stream()
                .map(this::buildResponseWithDetails)
                .toList();
    }

    private CourseResourceResponse buildResponseWithDetails(CourseResourceEntity resource) {
        ResourceTextEntity text = null;
        ResourceUrlEntity url = null;
        AssignmentEntity assignment = null;

        switch (resource.getResourceType()) {
            case TEXT -> text = textRepository.findByResourceId(resource.getId()).orElse(null);
            case URL -> url = urlRepository.findByResourceId(resource.getId()).orElse(null);
            case ASSIGNMENT -> assignment = assignmentRepository.findByResourceId(resource.getId()).orElse(null);
            case FILE -> { /* FILE type detail loaded separately when implemented */ }
        }

        return buildResponse(resource, text, url, assignment);
    }

    private CourseResourceResponse buildResponse(CourseResourceEntity resource,
                                                 ResourceTextEntity text,
                                                 ResourceUrlEntity url,
                                                 AssignmentEntity assignment) {

        CourseResourceResponse.CourseResourceResponseBuilder builder = CourseResourceResponse.builder()
                .id(resource.getId())
                .courseId(resource.getCourse().getId())
                .sectionId(resource.getSection().getId())
                .resourceType(resource.getResourceType())
                .title(resource.getTitle())
                .position(resource.getPosition())
                .visible(resource.getVisible())
                .published(resource.getPublished())
                .createdBy(resource.getCreatedBy())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt());

        if (text != null) {
            builder.text(CourseResourceResponse.TextDetail.builder()
                    .contentText(text.getContentText())
                    .build());
        }
        if (url != null) {
            builder.url(CourseResourceResponse.UrlDetail.builder()
                    .url(url.getUrl())
                    .urlKind(url.getUrlKind())
                    .openInNewTab(url.getOpenInNewTab())
                    .build());
        }
        if (assignment != null) {
            builder.assignment(CourseResourceResponse.AssignmentDetail.builder()
                    .assignmentId(assignment.getId())
                    .instructionsText(assignment.getInstructionsText())
                    .availableFrom(assignment.getAvailableFrom())
                    .dueAt(assignment.getDueAt())
                    .maxScore(assignment.getMaxScore())
                    .allowResubmission(assignment.getAllowResubmission())
                    .build());
        }

        return builder.build();
    }

    private CourseEntity findCourseOrThrow(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    private CourseSectionEntity findSectionOrThrow(UUID courseId, UUID sectionId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }
        CourseSectionEntity section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Section", "id", sectionId);
        }
        return section;
    }

    private CourseResourceEntity findResourceOrThrow(UUID courseId, UUID resourceId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }
        CourseResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));
        if (!resource.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Resource", "id", resourceId);
        }
        return resource;
    }

}
