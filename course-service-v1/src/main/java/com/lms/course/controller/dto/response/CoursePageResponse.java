package com.lms.course.controller.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CoursePageResponse", description = "Paginated course response")
public class CoursePageResponse {

    @ArraySchema(arraySchema = @Schema(description = "Current page content"), schema = @Schema(implementation = CourseResponse.class))
    private List<CourseResponse> content;

    @Schema(description = "Page metadata")
    private PageMetadata page;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PageMetadata")
    public static class PageMetadata {

        @Schema(example = "20")
        private int size;

        @Schema(example = "0")
        private int number;

        @Schema(example = "1")
        private long totalElements;

        @Schema(example = "1")
        private int totalPages;
    }
}

