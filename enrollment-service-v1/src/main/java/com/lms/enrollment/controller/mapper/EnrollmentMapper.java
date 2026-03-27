package com.lms.enrollment.controller.mapper;

import com.lms.enrollment.controller.dto.response.EnrollmentEventResponse;
import com.lms.enrollment.controller.dto.response.EnrollmentResponse;
import com.lms.enrollment.infrastructure.persistence.entity.CourseEnrollmentEntity;
import com.lms.enrollment.infrastructure.persistence.entity.EnrollmentEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentResponse toResponse(CourseEnrollmentEntity entity);

    List<EnrollmentResponse> toResponseList(List<CourseEnrollmentEntity> entities);

    @Mapping(target = "enrollmentId", source = "enrollment.id")
    EnrollmentEventResponse toEventResponse(EnrollmentEventEntity entity);

    List<EnrollmentEventResponse> toEventResponseList(List<EnrollmentEventEntity> entities);

}
