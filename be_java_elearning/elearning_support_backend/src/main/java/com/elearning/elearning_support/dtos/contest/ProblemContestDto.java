package com.elearning.elearning_support.dtos.contest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemContestDto {
    private long id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private Long teacherId;
    private Long topicContestId;
    private String topicContestName;
    private List<TestCaseDto> testCases;
    private String level;
    private Long totalSubmissions;
    private Long totalAC;
    private Double acRate;
    private String status;
}

