package com.elearning.elearning_support.dtos.solution;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.elearning.elearning_support.utils.DateUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolutionResDTO {
    private Long solutionId;
    private String title;
    private Integer languageId;
    private String content;
    private String timeComplex;
    private String spaceComplex;
    private Long createdBy;
    private LocalDateTime createdAt;


}
