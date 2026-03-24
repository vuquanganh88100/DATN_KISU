package com.elearning.elearning_support.services.solution.impl;

import com.elearning.elearning_support.dtos.solution.SolutionVoteReqDTO;
import com.elearning.elearning_support.entities.solution.Solution;
import com.elearning.elearning_support.entities.solution.SolutionVote;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.solution.SolutionRepository;
import com.elearning.elearning_support.repositories.solution.SolutionVoteRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.solution.SolutionVoteService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolutionVoteServiceImpl implements SolutionVoteService {

    @Autowired
    SolutionVoteRepository voteRepository;
    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean vote(SolutionVoteReqDTO dto) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return false;

        User user = userRepository.findById(userId).orElse(null);
        Solution solution = solutionRepository.findById(dto.getSolutionId()).orElse(null);
        if (user == null || solution == null) return false;

        SolutionVote vote = voteRepository
                .findByUser_IdAndSolution_SolutionId(userId, dto.getSolutionId())
                .orElse(new SolutionVote());

        vote.setUser(user);
        vote.setSolution(solution);
        vote.setVoteType(dto.getVoteType());

        voteRepository.save(vote);
        return true;
    }
}

