package com.lms.learning.controller.mapper;

import com.lms.learning.controller.dto.response.GradeResponse;
import com.lms.learning.infrastructure.persistence.entity.AssignmentGradeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    @Mapping(target = "submissionId", source = "submission.id")
    GradeResponse toResponse(AssignmentGradeEntity entity);

    List<GradeResponse> toResponseList(List<AssignmentGradeEntity> entities);

}
