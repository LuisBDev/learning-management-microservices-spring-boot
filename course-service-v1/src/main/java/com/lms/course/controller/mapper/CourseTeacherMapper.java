package com.lms.course.controller.mapper;

import com.lms.course.controller.dto.response.CourseTeacherResponse;
import com.lms.course.infrastructure.persistence.entity.CourseTeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseTeacherMapper {

    @Mapping(target = "courseId", source = "course.id")
    CourseTeacherResponse toResponse(CourseTeacherEntity entity);

    List<CourseTeacherResponse> toResponseList(List<CourseTeacherEntity> entities);

}
