package com.elearning.elearning_support.repositories.solution;

import com.elearning.elearning_support.entities.solution.SolutionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionCommentRepository extends JpaRepository<SolutionComment, Long> {
    List<SolutionComment> findBySolution_SolutionId(Long solutionId);
}

