package com.elearning.elearning_support.services.contest;

import com.elearning.elearning_support.dtos.contest.ProblemContestDto;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemContestService {
    ProblemContestDto createProblem(ProblemContestDto problemDto);
    ProblemContestDto updateProblem(Long id, ProblemContestDto problemDto);
    ProblemContestDto getProblemById(Long id);
    List<ProblemContestDto> getProblemsByTopicId(Long topicId);
    void deleteProblem(Long id);
    ProblemContestDto convertToDtoWithoutTestCases(ProblemContest entity);
    Page<ProblemContestDto> getAllProblems(Pageable pageable, long studentId);
}
