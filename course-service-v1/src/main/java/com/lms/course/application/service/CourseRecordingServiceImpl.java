package com.lms.course.application.service;

import com.lms.course.controller.dto.request.CreateRecordingRequest;
import com.lms.course.controller.dto.request.UpdateRecordingRequest;
import com.lms.course.controller.dto.response.CourseRecordingResponse;
import com.lms.course.domain.model.enums.ResourceType;
import com.lms.course.domain.model.enums.ResourceUrlKind;
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
public class CourseRecordingServiceImpl implements CourseRecordingService {

    private final JpaCourseRepository courseRepository;
    private final JpaCourseSectionRepository sectionRepository;
    private final JpaCourseResourceRepository resourceRepository;
    private final JpaResourceUrlRepository urlRepository;
    private final JpaCourseRecordingRepository recordingRepository;

    @Override
    @Transactional
    public CourseRecordingResponse create(UUID courseId, UUID sectionId, CreateRecordingRequest request, UUID createdBy) {
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
                .published(request.getPublished() != null ? request.getPublished() : true)
                .createdBy(createdBy)
                .build();
        resource = resourceRepository.save(resource);

        ResourceUrlEntity resourceUrl = ResourceUrlEntity.builder()
                .resource(resource)
                .url(request.getUrl())
                .urlKind(ResourceUrlKind.RECORDING)
                .openInNewTab(request.getOpenInNewTab() != null ? request.getOpenInNewTab() : true)
                .build();
        resourceUrl = urlRepository.save(resourceUrl);

        CourseRecordingEntity recording = CourseRecordingEntity.builder()
                .resourceUrl(resourceUrl)
                .recordingName(request.getRecordingName())
                .classDate(request.getClassDate())
                .commentText(request.getCommentText())
                .uploadedAt(Instant.now())
                .build();
        recordingRepository.save(recording);

        log.info("Recording created: '{}' in section {} of course {}", request.getRecordingName(), sectionId, courseId);
        return buildResponse(recording, resourceUrl, resource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRecordingResponse> getByCourseId(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        List<CourseResourceEntity> urlResources = resourceRepository.findByCourseId(courseId).stream()
                .filter(r -> r.getResourceType() == ResourceType.URL)
                .toList();

        return urlResources.stream()
                .map(resource -> {
                    ResourceUrlEntity url = urlRepository.findByResourceId(resource.getId()).orElse(null);
                    if (url == null || url.getUrlKind() != ResourceUrlKind.RECORDING) return null;
                    CourseRecordingEntity recording = recordingRepository.findByResourceUrlId(url.getId()).orElse(null);
                    if (recording == null) return null;
                    return buildResponse(recording, url, resource);
                })
                .filter(r -> r != null)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseRecordingResponse getById(UUID courseId, UUID recordingId) {
        CourseRecordingEntity recording = findRecordingOrThrow(recordingId);
        ResourceUrlEntity url = recording.getResourceUrl();
        CourseResourceEntity resource = url.getResource();

        if (!resource.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Recording", "id", recordingId);
        }

        return buildResponse(recording, url, resource);
    }

    @Override
    @Transactional
    public CourseRecordingResponse update(UUID courseId, UUID recordingId, UpdateRecordingRequest request) {
        CourseRecordingEntity recording = findRecordingOrThrow(recordingId);
        ResourceUrlEntity url = recording.getResourceUrl();
        CourseResourceEntity resource = url.getResource();

        if (!resource.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Recording", "id", recordingId);
        }

        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getVisible() != null) resource.setVisible(request.getVisible());
        if (request.getPublished() != null) resource.setPublished(request.getPublished());
        resourceRepository.save(resource);

        if (request.getUrl() != null) url.setUrl(request.getUrl());
        if (request.getOpenInNewTab() != null) url.setOpenInNewTab(request.getOpenInNewTab());
        url.setUpdatedAt(Instant.now());
        urlRepository.save(url);

        if (request.getRecordingName() != null) recording.setRecordingName(request.getRecordingName());
        if (request.getClassDate() != null) recording.setClassDate(request.getClassDate());
        if (request.getCommentText() != null) recording.setCommentText(request.getCommentText());
        recording.setUpdatedAt(Instant.now());
        recordingRepository.save(recording);

        log.info("Recording updated: {} in course {}", recordingId, courseId);
        return buildResponse(recording, url, resource);
    }

    @Override
    @Transactional
    public void delete(UUID courseId, UUID recordingId) {
        CourseRecordingEntity recording = findRecordingOrThrow(recordingId);
        ResourceUrlEntity url = recording.getResourceUrl();
        CourseResourceEntity resource = url.getResource();

        if (!resource.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Recording", "id", recordingId);
        }

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

        log.info("Recording deleted: {} from course {}", recordingId, courseId);
    }

    private CourseRecordingResponse buildResponse(CourseRecordingEntity recording,
                                                  ResourceUrlEntity url,
                                                  CourseResourceEntity resource) {
        return CourseRecordingResponse.builder()
                .id(recording.getId())
                .resourceId(resource.getId())
                .courseId(resource.getCourse().getId())
                .sectionId(resource.getSection().getId())
                .resourceTitle(resource.getTitle())
                .url(url.getUrl())
                .urlKind(url.getUrlKind())
                .openInNewTab(url.getOpenInNewTab())
                .recordingName(recording.getRecordingName())
                .classDate(recording.getClassDate())
                .commentText(recording.getCommentText())
                .visible(resource.getVisible())
                .published(resource.getPublished())
                .uploadedAt(recording.getUploadedAt())
                .updatedAt(recording.getUpdatedAt())
                .build();
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

    private CourseRecordingEntity findRecordingOrThrow(UUID recordingId) {
        return recordingRepository.findById(recordingId)
                .orElseThrow(() -> new ResourceNotFoundException("Recording", "id", recordingId));
    }

}
