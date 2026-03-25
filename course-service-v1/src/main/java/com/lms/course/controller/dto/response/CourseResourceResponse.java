package com.lms.course.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lms.course.domain.model.enums.ResourceType;
import com.lms.course.domain.model.enums.ResourceUrlKind;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResourceResponse {

    private UUID id;
    private UUID courseId;
    private UUID sectionId;
    private ResourceType resourceType;
    private String title;
    private Integer position;
    private Boolean visible;
    private Boolean published;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;


    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TextDetail text;

    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UrlDetail url;

    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AssignmentDetail assignment;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextDetail {
        private String contentText;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlDetail {
        private String url;
        private ResourceUrlKind urlKind;
        private Boolean openInNewTab;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentDetail {
        private UUID assignmentId;
        private String instructionsText;
        private Instant availableFrom;
        private Instant dueAt;
        private BigDecimal maxScore;
        private Boolean allowResubmission;
    }

}
