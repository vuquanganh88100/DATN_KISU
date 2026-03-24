package com.elearning.elearning_support.services.solution.impl;

import com.elearning.elearning_support.dtos.solution.SolutionCreateReqDTO;
import com.elearning.elearning_support.dtos.solution.SolutionResDTO;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.solution.Solution;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.contest.ProblemContestRepository;
import com.elearning.elearning_support.repositories.solution.SolutionRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.solution.SolutionService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepository solutionRepository;
    private final ProblemContestRepository problemContestRepository;
    private final UserRepository userRepository;

    @Override
    public SolutionResDTO createSolution(SolutionCreateReqDTO dto) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return null;

        User user = userRepository.findById(userId).orElse(null);
        ProblemContest problem = problemContestRepository.findById(dto.getProblemId()).orElse(null);
        if (user == null || problem == null) return null;

        Solution solution = new Solution();
        solution.setTitle(dto.getTitle());
        solution.setLanguageId(dto.getLanguageId());
        solution.setContent(dto.getContent());
        solution.setTimeComplex(dto.getTimeComplex());
        solution.setSpaceComplex(dto.getSpaceComplex());
        solution.setUser(user);
        solution.setProblemContest(problem);
        solution.setCreatedAt(LocalDateTime.now());

        return map(solutionRepository.save(solution));
    }

    @Override
    public List<SolutionResDTO> getSolutionsByProblem(Long problemId) {
        return solutionRepository.findByProblemContest_Id(problemId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private SolutionResDTO map(Solution s) {
        return SolutionResDTO.builder()
                .solutionId(s.getSolutionId())
                .title(s.getTitle())
                .languageId(s.getLanguageId())
                .content(s.getContent())
                .timeComplex(s.getTimeComplex())
                .spaceComplex(s.getSpaceComplex())
                .createdBy(s.getUser().getId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}

