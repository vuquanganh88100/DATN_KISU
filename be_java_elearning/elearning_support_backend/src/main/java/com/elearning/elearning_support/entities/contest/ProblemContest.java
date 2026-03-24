package com.elearning.elearning_support.entities.contest;

import com.elearning.elearning_support.entities.submitContest.SubmissionContest;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.internals.Topic;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "problem_contest")
public class ProblemContest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // để dùng với sequence auto-increment
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "input_format", nullable = false, columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", nullable = false, columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "constraints", columnDefinition = "TEXT")
    private String constraints;
    @Column(name = "level", columnDefinition = "TEXT")
    private String level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User teacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicContest topicContest;
    @OneToMany(mappedBy = "problemContest")
    private List<TestCase>testCases;

    @OneToMany(mappedBy = "problemContest")
    private List<SubmissionContest>submissionContests;

}
