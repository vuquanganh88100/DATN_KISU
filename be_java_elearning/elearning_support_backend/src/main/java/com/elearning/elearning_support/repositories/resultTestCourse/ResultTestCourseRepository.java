package com.elearning.elearning_support.repositories.resultTestCourse;

import com.elearning.elearning_support.entities.resultTestCourse.ResultTestCourseEntity;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.services.resultTestCourseService.ResultTestCourseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultTestCourseRepository extends JpaRepository<ResultTestCourseEntity,Long> {
    @Query(nativeQuery = true,value = "" +
            "SELECT r.test_course_id FROM online_course oc\n" +
            "INNER JOIN test_course t \n" +
            "ON oc.id =t.course_id\n" +
            "INNER JOIN result_test_course r\n" +
            "ON t.id =r.test_course_id\n" +
            "WHERE r.user_id = :userId AND oc.id = :onlineCourseId\n" +
            "ORDER BY\n" +
            "t.chapter_id,t.sequence")
    List<Long> findCompletedTestInCourse(@Param("userId") long userId,
                                         @Param("onlineCourseId") long onlineCourseId);
    ResultTestCourseEntity findByUserIdAndTestCourseEntityId(Long userId, Long testCourseId);


}
