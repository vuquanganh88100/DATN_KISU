package com.elearning.elearning_support.entities.submitContest;

import com.elearning.elearning_support.entities.contest.TestCase;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class JudgeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "actual_output", nullable = true)
    private String actualOutput;

    @Column(name = "execution_time", nullable = true)
    private Double executionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private SubmissionContest submissionContest;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;
}
