package com.elearning.elearning_support.services.solution;

import com.elearning.elearning_support.dtos.solution.SolutionCreateReqDTO;
import com.elearning.elearning_support.dtos.solution.SolutionResDTO;

import java.util.List;

public interface SolutionService {
    SolutionResDTO createSolution(SolutionCreateReqDTO dto);
    List<SolutionResDTO> getSolutionsByProblem(Long problemId);
}

