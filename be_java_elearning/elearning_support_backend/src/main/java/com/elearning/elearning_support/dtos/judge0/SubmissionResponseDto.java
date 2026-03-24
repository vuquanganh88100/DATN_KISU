package com.elearning.elearning_support.dtos.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponseDto {
    private Long submissionId;
    private String verdict;
    private Double runtimeMs;
    private Integer memoryKb;
    private Integer passedTestcases;
    private Integer totalTestcases;
    private String language;
    private LocalDateTime submittedAt;
    private String topicName;
    private String problemNameContest;
    private Long problemId;
    private String studentName;
    private String sourceCoude;
}
