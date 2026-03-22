package com.lms.course.controller.mapper;

import com.lms.course.controller.dto.response.CourseSectionResponse;
import com.lms.course.infrastructure.persistence.entity.CourseSectionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseSectionMapper {

    @Mapping(target = "courseId", source = "course.id")
    CourseSectionResponse toResponse(CourseSectionEntity entity);

    List<CourseSectionResponse> toResponseList(List<CourseSectionEntity> entities);

}
