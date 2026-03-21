package com.lms.course.controller.mapper;

import com.lms.course.controller.dto.request.UpdateCourseRequest;
import com.lms.course.controller.dto.response.CourseResponse;
import com.lms.course.infrastructure.persistence.entity.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    CourseResponse toResponse(CourseEntity entity);

    List<CourseResponse> toResponseList(List<CourseEntity> entities);

    void updateCourseEntityFromRequest(UpdateCourseRequest request, @MappingTarget CourseEntity course);
}
