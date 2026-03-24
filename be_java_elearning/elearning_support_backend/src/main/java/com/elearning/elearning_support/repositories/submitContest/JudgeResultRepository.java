package com.elearning.elearning_support.repositories.submitContest;

import com.elearning.elearning_support.entities.submitContest.JudgeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JudgeResultRepository extends JpaRepository<JudgeResult,Long> {
}
