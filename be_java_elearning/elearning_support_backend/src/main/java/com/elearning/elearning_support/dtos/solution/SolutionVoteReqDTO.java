package com.elearning.elearning_support.dtos.solution;

import lombok.Data;

@Data
public class SolutionVoteReqDTO {
    private Long solutionId;
    private Integer voteType; // 1 or -1
}
