package com.lms.course.controller.mapper;

import com.lms.course.controller.dto.request.UpdateSectionRequest;
import com.lms.course.controller.dto.response.CourseSectionResponse;
import com.lms.course.infrastructure.persistence.entity.CourseSectionEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseSectionMapper {

    @Mapping(target = "courseId", source = "course.id")
    CourseSectionResponse toResponse(CourseSectionEntity entity);

    List<CourseSectionResponse> toResponseList(List<CourseSectionEntity> entities);

    void updateCourseSectionEntityFromRequest(UpdateSectionRequest request, @MappingTarget CourseSectionEntity section);
}
