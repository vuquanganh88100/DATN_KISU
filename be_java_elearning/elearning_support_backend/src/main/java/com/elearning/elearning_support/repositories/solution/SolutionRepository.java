package com.elearning.elearning_support.repositories.solution;

import com.elearning.elearning_support.entities.solution.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByProblemContest_Id(Long problemId);
}
