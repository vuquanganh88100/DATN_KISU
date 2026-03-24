package com.elearning.elearning_support.repositories.solution;

import com.elearning.elearning_support.entities.solution.SolutionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolutionVoteRepository extends JpaRepository<SolutionVote, Long> {
    Optional<SolutionVote> findByUser_IdAndSolution_SolutionId(Long userId, Long solutionId);
}

