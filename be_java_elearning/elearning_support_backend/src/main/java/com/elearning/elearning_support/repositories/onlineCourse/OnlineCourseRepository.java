package com.elearning.elearning_support.repositories.onlineCourse;

import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OnlineCourseRepository extends JpaRepository<OnlineCourse, Long>, JpaSpecificationExecutor<OnlineCourse> {
    @Query(nativeQuery = true, value = "" +
            "WITH CourseChapters AS ( " +
            "    SELECT " +
            "        occw.online_course_id, " +
            "    CAST(json_agg(json_build_object(\n" +
            "'chapterId', occw.chapter_id,\n" +
            "'chapterWeight', occw.chapter_weight\n" +
            ")) AS text)AS onlineCourseChapters " +
            "    FROM " +
            "        online_course_chapter_weight occw " +
            "    GROUP BY " +
            "        occw.online_course_id " +
            "), " +
            "CourseTeachers AS ( " +
            "    SELECT " +
            "        uoc.online_course_id, " +
            "        array_to_string(array_agg(DISTINCT uoc.user_id), ',') AS teacherIds " +
            "    FROM " +
            "        user_online_course uoc " +
            "WHERE \n" +
            "        uoc.role_type = 'Teacher' "+
            "    GROUP BY " +
            "        uoc.online_course_id " +
            ") " +
            "SELECT " +
            "    oc.*, " +
            "    ct.teacherIds, " +
            "    cc.onlineCourseChapters " +
            "FROM " +
            "    online_course oc " +
            "LEFT JOIN " +
            "    CourseTeachers ct " +
            "    ON oc.id = ct.online_course_id " +
            "LEFT JOIN " +
            "    CourseChapters cc " +
            "    ON oc.id = cc.online_course_id " +
            "ORDER BY " +
            "    oc.id ASC " +
            "LIMIT :pageSize OFFSET :offset")
    List<Map<String, Object>> getAllPagedCourses(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    @Query(nativeQuery = true,value = "" +
            "   WITH CourseChapters AS (\n" +
            "        SELECT \n" +
            "            occw.online_course_id, \n" +
            "            CAST(\n" +
            "                json_agg(\n" +
            "                    json_build_object(\n" +
            "                        'chapterId', occw.chapter_id,\n" +
            "                        'chapterWeight', occw.chapter_weight\n" +
            "                    )\n" +
            "                ) AS text\n" +
            "            ) AS onlineCourseChapters\n" +
            "        FROM \n" +
            "            online_course_chapter_weight occw\n" +
            "        GROUP BY \n" +
            "            occw.online_course_id\n" +
            "    ),\n" +
            "    CourseTeachers AS (\n" +
            "        SELECT \n" +
            "            uoc.online_course_id, \n" +
            "            array_to_string(array_agg(DISTINCT uoc.user_id), ',') AS teacherIds\n" +
            "        FROM \n" +
            "            user_online_course uoc\n " +
            "WHERE \n" +
            "        uoc.role_type = 'Teacher' "+
            "        GROUP BY \n" +
            "            uoc.online_course_id\n" +
            "    ),\n" +
            "    FilteredCourses AS (\n" +
            "        SELECT DISTINCT \n" +
            "            uoc.online_course_id\n" +
            "        FROM \n" +
            "            user_online_course uoc\n" +
            "        WHERE \n" +
            "            uoc.user_id = :userId -- Lọc user_id tại đây\n" +
            "    )\n" +
            "    SELECT \n" +
            "        oc.*, \n" +
            "        ct.teacherIds, \n" +
            "        cc.onlineCourseChapters\n" +
            "    FROM \n" +
            "        online_course oc\n" +
            "    JOIN \n" +
            "        CourseTeachers ct \n" +
            "        ON oc.id = ct.online_course_id\n" +
            "    JOIN \n" +
            "        CourseChapters cc \n" +
            "        ON oc.id = cc.online_course_id\n" +
            "    JOIN \n" +
            "        FilteredCourses fc \n" +
            "        ON oc.id = fc.online_course_id\n" +
            "    ORDER BY \n" +
            "        oc.id ASC\n" +
            "    LIMIT :pageSize OFFSET :offset")
    List<Map<String, Object>> getPagedCoursesByUserId(@Param("userId") Long userId,
                                                      @Param("pageSize") int pageSize,
                                                      @Param("offset") int offset);



}
