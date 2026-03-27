package com.lms.learning.controller.mapper;

import com.lms.learning.controller.dto.response.SubmissionResponse;
import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    SubmissionResponse toResponse(AssignmentSubmissionEntity entity);

    List<SubmissionResponse> toResponseList(List<AssignmentSubmissionEntity> entities);

}
