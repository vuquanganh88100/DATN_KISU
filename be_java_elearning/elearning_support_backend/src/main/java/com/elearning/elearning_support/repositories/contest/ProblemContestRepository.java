package com.elearning.elearning_support.repositories.contest;

import com.elearning.elearning_support.dtos.contest.ProblemStatisticProjection;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemContestRepository extends JpaRepository<ProblemContest, Long> {
    List<ProblemContest> findByTopicContestId(Long topicId);
    @Query(value =
            "SELECT s.problem_id AS problemId, " +
                    "       COUNT(s.id) AS totalSubmissions, " +
                    "       SUM(CASE WHEN s.verdict = 'ACCEPTED' THEN 1 ELSE 0 END) AS totalAC " +
                    "FROM submission_contest s " +
                    "GROUP BY s.problem_id",
            nativeQuery = true)
    List<ProblemStatisticProjection> getProblemStatistics();
}
