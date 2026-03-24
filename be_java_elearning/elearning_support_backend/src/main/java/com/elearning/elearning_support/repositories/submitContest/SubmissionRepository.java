package com.elearning.elearning_support.repositories.submitContest;

import com.elearning.elearning_support.dtos.judge0.StudentSubmissionProjection;
import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionContest,Long> {
    @Query(value =
            "SELECT s.problem_id AS problemId, s.verdict AS verdict " +
                    "FROM submission_contest s " +
                    "WHERE s.student_id = :studentId",
            nativeQuery = true)
    List<StudentSubmissionProjection> getStudentSubmissions(Long studentId);
    List<SubmissionContest> findByStudentId_Id(Long studentId);
    List<SubmissionContest> findByProblemContest_Id(Long problemId);
}
