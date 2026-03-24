package com.elearning.elearning_support.repositories.contest;

import com.elearning.elearning_support.dtos.contest.TopicContestDto;
import com.elearning.elearning_support.dtos.contest.TopicContestProjection;
import com.elearning.elearning_support.entities.contest.TopicContest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicContestRepository extends JpaRepository<TopicContest, Long> {
    @Query(value =
            "SELECT t.id AS id, " +
                    "       t.name AS name, " +
                    "       COUNT(p.id) AS numsOfProblems " +
                    "FROM topic_contest t " +
                    "LEFT JOIN problem_contest p ON t.id = p.topic_id " +
                    "GROUP BY t.id, t.name",
            nativeQuery = true)
    List<TopicContestProjection> findAllWithProblemCount();
}
