package com.elearning.elearning_support.entities.contest;

import com.elearning.elearning_support.entities.subject.Subject;
import com.elearning.elearning_support.entities.submitContest.JudgeResult;
import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.parsing.Problem;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "test_case_contest")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "input", nullable = false)
    private String input;
    @Column(name = "expected_output", nullable = false)
    private String expectedOutput;
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;
    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemContest problemContest;
    @OneToMany(mappedBy = "testCase")
    private List<JudgeResult> judgeResults;
}
