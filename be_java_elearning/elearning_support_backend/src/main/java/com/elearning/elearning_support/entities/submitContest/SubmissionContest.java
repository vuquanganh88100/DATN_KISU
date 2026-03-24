package com.elearning.elearning_support.entities.submitContest;

import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.contest.TestCase;
import com.elearning.elearning_support.entities.contest.TopicContest;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "submission_contest")
public class SubmissionContest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "language_id", nullable = false)
    private Integer languageId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "verdict")
    private String verdict; // Accepted, Wrong Answer, Runtime Error,...

    @Column(name = "source_code")
    private String sourceCode;

    @Column(name = "runtime")
    private Double runtime;

    @Column(name = "memory")
    private Integer memory;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemContest problemContest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User studentId;

    @OneToMany(mappedBy = "submissionContest")
    private List<JudgeResult>judgeResults;



}
