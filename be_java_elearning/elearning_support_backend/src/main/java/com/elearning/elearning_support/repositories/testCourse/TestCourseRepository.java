package com.elearning.elearning_support.repositories.testCourse;

import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCourseRepository  extends JpaRepository<TestCourseEntity,Long> {
    @Query(nativeQuery = true,value = "" +
            "SELECT * FROM test_course tc WHERE tc.chapter_id= :chapterId AND tc.course_id= :courseId")
    List<TestCourseEntity> getTestCourseByChapterAndCourse(@Param("chapterId") long chapterId, @Param("courseId") long onlineCourseId);
    @Query(nativeQuery = true,value = "SELECT tc.* \n" +
            "FROM result_test_course rtc\n" +
            "INNER JOIN test_course tc\n" +
            "ON rtc.test_course_id = tc.id\n" +
            "INNER JOIN online_course oc\n" +
            "ON tc.course_id = oc.id\n" +
            "WHERE rtc.user_id = :userId AND tc.course_id = :courseId")
    List<TestCourseEntity> getTestCourseCompletedInCourse(@Param("userId")long userId,@Param("courseId") long courseId);
    @Query(nativeQuery = true,value = "" +
            "SELECT oc.id,oc.course_name,MAX(rtc.submit_time) AS last_updated\n" +
            "FROM online_course oc \n" +
            "INNER JOIN test_course tc ON oc.id=tc.course_id\n" +
            "INNER JOIN result_test_course rtc ON tc.id=rtc.test_course_id\n" +
            "WHERE oc.id = :onlineCourseId AND rtc.user_id= :userId\n" +
            "GROUP BY oc.id ")
    Object getLastTimeToDoTest(@Param("onlineCourseId")long onlineCourseId,@Param("userId") long userId);
}
