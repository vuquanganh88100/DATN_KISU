package com.elearning.elearning_support.services.solution.impl;

import com.elearning.elearning_support.dtos.solution.SolutionCommentCreateReqDTO;
import com.elearning.elearning_support.dtos.solution.SolutionCommentResDTO;
import com.elearning.elearning_support.entities.solution.Solution;
import com.elearning.elearning_support.entities.solution.SolutionComment;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.repositories.solution.SolutionCommentRepository;
import com.elearning.elearning_support.repositories.solution.SolutionRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.solution.SolutionCommentService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolutionCommentServiceImpl implements SolutionCommentService {

    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SolutionCommentRepository solutionCommentRepository;

    public SolutionCommentServiceImpl() {
    }

    @Override
    public SolutionCommentResDTO createComment(SolutionCommentCreateReqDTO dto) {
        Long userId = AuthUtils.getCurrentUserId();
        if (userId == null) return null;

        User user = userRepository.findById(userId).orElse(null);
        Solution solution = solutionRepository.findById(dto.getSolutionId()).orElse(null);
        if (user == null || solution == null) return null;

        SolutionComment comment = new SolutionComment();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setSolution(solution);
        comment.setCreatedAt(LocalDateTime.now());

        SolutionComment saved = solutionCommentRepository.save(comment);
        return map(saved);
    }

    @Override
    public List<SolutionCommentResDTO> getCommentsBySolution(Long solutionId) {
        return solutionCommentRepository.findBySolution_SolutionId(solutionId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private SolutionCommentResDTO map(SolutionComment c) {
        return SolutionCommentResDTO.builder()
                .id(c.getId())
                .content(c.getContent())
                .createdBy(c.getUser().getId())
                .createdAt(c.getCreatedAt())
                .build();
    }
}

