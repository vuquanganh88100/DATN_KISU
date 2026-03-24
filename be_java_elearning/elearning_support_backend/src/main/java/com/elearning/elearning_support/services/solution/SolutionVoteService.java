package com.elearning.elearning_support.services.solution;

import com.elearning.elearning_support.dtos.solution.SolutionVoteReqDTO;

public interface SolutionVoteService {
    boolean vote(SolutionVoteReqDTO dto);
}
