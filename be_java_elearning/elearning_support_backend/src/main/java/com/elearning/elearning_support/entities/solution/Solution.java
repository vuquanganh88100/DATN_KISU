package com.elearning.elearning_support.entities.solution;

import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.contest.ProblemContest;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.entities.lecture.LectureQuestion;
import com.elearning.elearning_support.entities.lecture.LectureStudentProgress;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "solution", schema = "elearning_support_dev")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solution_id", nullable = false)
    private Long solutionId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "language_id", nullable = false)
    private Integer languageId;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "time_complex", nullable = false)
    private String timeComplex;
    @Column(name = "space_complex", nullable = false)
    private String spaceComplex;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemContest problemContest;
    @OneToMany(mappedBy = "solution")
    private List<SolutionComment>solutionComments;

    @OneToMany(mappedBy = "solution")
    private List<SolutionVote>solutionVotes;
}
