package com.elearning.elearning_support.repositories.onlineCourse;

import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserOnlineCourseRepository extends JpaRepository<UserOnlineCourse,Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "" +
            "DELETE FROM user_online_course uoc WHERE uoc.online_course_id = :id")
    void deleteByOnlineCourseId(@Param("id") Long id);
    List<UserOnlineCourse> findUserOnlineCourseByOnlineCourseId(long onlineCourseId);
    @Query(nativeQuery = true,value = "" +
            "SELECT * FROM user_online_course uoc WHERE uoc.user_id = :userId AND uoc.online_course_id = :onlineCourseId AND uoc.role_type = 'Student';\n")
    UserOnlineCourse checkExistStudent(@Param("userId") long userId,@Param("onlineCourseId") long onlineCourseId);
    @Query(nativeQuery = true,
    value = "" +
            "SELECT * FROM user_online_course uoc WHERE uoc.user_id = :userId AND " +
            "uoc.role_type = 'Student'")
    List<UserOnlineCourse> getCourseRegistedByUser(@Param("userId") long userId);
}
