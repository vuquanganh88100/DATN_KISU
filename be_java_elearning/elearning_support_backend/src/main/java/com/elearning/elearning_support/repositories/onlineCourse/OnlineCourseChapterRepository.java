package com.elearning.elearning_support.repositories.onlineCourse;

import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Repository
public interface OnlineCourseChapterRepository extends JpaRepository<OnlineCourseChapter,Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "" +
            "DELETE FROM online_course_chapter_weight occw WHERE occw.online_course_id = :id")
    void deleteByOnlineCourseId(@Param("id") Long id);

    @Query(nativeQuery = true, value = "\n" +
            "SELECT c.id FROM online_course_chapter_weight occw \n" +
            "INNER JOIN chapter c\n" +
            "ON occw.chapter_id=c.id\n" +
            "WHERE occw.online_course_id = :onlineCourseId \n" +
            "ORDER BY c.orders ASC\n")
    List<Long> findChapterIdsByOnlineCourseId(@Param("onlineCourseId") Long onlineCourseId);
    @Query(nativeQuery = true,value = "" +
            "SELECT  occw.*,c.title,c.orders FROM chapter c\n" +
            "INNER JOIN online_course_chapter_weight occw \n" +
            "ON c.id=occw.chapter_id\n" +
            "WHERE occw.online_course_id =:courseId")
    List<Object[]>findChapterDetailByOnlineCourse(@Param("courseId") long onlineCourseId);

    List<OnlineCourseChapter> findByOnlineCourseId(long onlineCourseId);
}
