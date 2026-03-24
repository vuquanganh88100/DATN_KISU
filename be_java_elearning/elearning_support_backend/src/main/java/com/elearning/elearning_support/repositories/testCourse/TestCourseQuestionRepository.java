package com.elearning.elearning_support.repositories.testCourse;

import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCourseQuestionRepository extends JpaRepository<TestCourseQuestion,Long> {
    List<TestCourseQuestion> findByTestCourseEntityId(Long testCourseId);
}
