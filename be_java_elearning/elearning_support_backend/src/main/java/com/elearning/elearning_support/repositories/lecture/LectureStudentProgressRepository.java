package com.elearning.elearning_support.repositories.lecture;

import com.elearning.elearning_support.entities.lecture.LectureStudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureStudentProgressRepository extends JpaRepository<LectureStudentProgress, Long> {
    @Query(value = "SELECT * FROM student_lecture_progress stp WHERE stp.student_id = :studentId AND stp.lecture_id = :lectureId", nativeQuery = true)
    LectureStudentProgress findByStudentIdAndLectureId(@Param("studentId") Long studentId, @Param("lectureId") Long lectureId);

    @Query(value = "SELECT slp.lecture_id FROM online_course oc \n" +
            "INNER JOIN lecture l ON oc.id =l.online_course_id\n" +
            "INNER JOIN student_lecture_progress slp on l.lecture_id=slp.lecture_id\n" +
            "WHERE slp.student_id = :studentId AND slp.is_completed=TRUE AND oc.id = :onlineCourseId" +
            " ORDER BY \n" +
            "    l.chapter_id ASC, \n" +
            "    l.sequence ASC;",
            nativeQuery = true)
    List<Long> findLectureIdCompleted(@Param("studentId") long studentId, @Param("onlineCourseId") long onlineCourseId);
    Optional<LectureStudentProgress> findByUserIdAndLectureId(Long userId, Long lectureId);
    @Query(value="SELECT \n" +
            "    oc.id, oc.course_name,  \n" +
            "    MAX(slp.last_updated) AS last_updated\n" +
            "FROM \n" +
            "    online_course oc\n" +
            "INNER JOIN \n" +
            "    lecture l ON oc.id = l.online_course_id\n" +
            "INNER JOIN \n" +
            "    student_lecture_progress slp ON l.lecture_id = slp.lecture_id\n" +
            "WHERE \n" +
            "    oc.id = :onlineCourseId AND slp.student_id = :studentId " +
            "GROUP BY \n" +
            "    oc.id;", nativeQuery = true)
    Object  getLastTimeLearning(@Param("studentId") long studentId,@Param("onlineCourseId") long onlineCourseId);
    List<LectureStudentProgress> findByUser_Id(Long userId);
}
