package com.elearning.elearning_support.entities.resultTestCourse;

import com.elearning.elearning_support.entities.users.User;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@Table(name = "result_test_course_detail", schema = "elearning_support_dev")
public class ResultTestCourseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="question_id")
    private Long questionId;
    @Column(name = "selected_ans_id")
    @Type(type = "int-array")
    private Integer[] selectedAnsId;
    @Column(name="is_correct")
    private Boolean isCorrect;
    @ManyToOne
    @JoinColumn(name="result_test_course_id",nullable = false)
    private ResultTestCourseEntity resultTestCourse;

}
