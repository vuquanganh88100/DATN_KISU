package com.elearning.elearning_support.repositories.contest;

import com.elearning.elearning_support.entities.contest.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase,Long> {
    List<TestCase> findByProblemContest_Id(Long problemId);
    List<TestCase>
    findTop2ByProblemContest_IdAndIsPublicTrue(Long problemId);
}
