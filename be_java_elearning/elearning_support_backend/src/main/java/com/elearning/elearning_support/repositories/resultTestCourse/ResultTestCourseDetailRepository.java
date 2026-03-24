package com.elearning.elearning_support.repositories.resultTestCourse;

import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultTestCourseDetailRepository  extends JpaRepository<ResultTestCourseDetail,Long> {
    ResultTestCourseDetail findByQuestionIdAndResultTestCourseId(Long questionId, Long resultTestCourseId);

}
