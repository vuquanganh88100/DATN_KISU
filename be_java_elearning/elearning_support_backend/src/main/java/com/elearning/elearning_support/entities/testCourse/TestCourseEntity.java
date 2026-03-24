package com.elearning.elearning_support.entities.testCourse;

import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseEntity;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "test_course", schema = "elearning_support_dev")
public class TestCourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="sequence")
    private Integer sequence;
    @Column(name="name")
    private String name;
    @Column(name="medium_question")
    private Integer mediumQuestion;
    @Column(name="easy_question")
    private Integer easyQuestion;
    @Column(name="hard_question")
    private Integer hardQuestion;
    @Column(name="total_point")
    private Integer totalPoint;
    @Column(name="duration")
    private Integer duration;
    @Column(name="question_quantity")
    private Integer questionQuantity;
    @Column(name="created_by")
    private Integer createdBy;
    @Column(name="testCourseWeight")
    private Integer testCourseWeightl;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private OnlineCourse onlineCourse;
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
    @OneToMany (mappedBy = "testCourseEntity")
    private List<TestCourseQuestion> testCourseQuestions;
    @OneToMany (mappedBy = "testCourseEntity")
    private List<ResultTestCourseEntity> resultTestCourseEntities;
}
