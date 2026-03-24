package com.elearning.elearning_support.dtos.solution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolutionCommentResDTO {
    private Long id;
    private String content;
    private Long createdBy;
    private LocalDateTime createdAt;
}

