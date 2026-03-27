package com.lms.learning.controller.dto.request;

import com.lms.learning.domain.model.enums.GradeStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGradeRequest {

    private BigDecimal score;
    private GradeStatus gradeStatus;
    private String teacherComment;

}
