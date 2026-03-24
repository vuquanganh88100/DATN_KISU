package com.elearning.elearning_support.dtos.contest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicContestDto {
    private Long id;
    private String name;
    private int numsOfProblems;
}
