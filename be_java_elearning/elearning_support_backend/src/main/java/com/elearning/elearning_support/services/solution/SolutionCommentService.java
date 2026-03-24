package com.elearning.elearning_support.services.solution;

import com.elearning.elearning_support.dtos.solution.SolutionCommentCreateReqDTO;
import com.elearning.elearning_support.dtos.solution.SolutionCommentResDTO;

import java.util.List;

public interface SolutionCommentService {
    SolutionCommentResDTO createComment(SolutionCommentCreateReqDTO dto);
    List<SolutionCommentResDTO> getCommentsBySolution(Long solutionId);
}
