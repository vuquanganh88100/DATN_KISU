package com.elearning.elearning_support.repositories.lecture;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture,Long> {
    @Query(nativeQuery = true,value = "" +
            "SELECT MAX(l.sequence) FROM lecture l " +
            "WHERE l.chapter_id = :chapterId " +
            "AND l.online_course_id = :onlineCourseId")
    Integer findMaxSequenceByChapterOfCourse(long chapterId,long onlineCourseId);
//    @Query(nativeQuery = true,
//            value = "SELECT " +
//                    "    l.\"lecture_id\" AS \"id\", " +
//                    "    l.\"lecture_name\" AS \"lectureName\", " +
//                    "    l.\"required_watch_time\" AS \"requiredTime\", " +
//                    "    l.\"lecture_weight\" AS \"lectureWeight\", " +
//                    "    l.\"sequence\", " +
//                    "    l.\"total_question\" AS \"totalQuestion\", " +
//                    "    l.\"required_correct_ans\" AS \"requiredCorrectAns\", " +
//                    "    l.\"video_duration\" AS \"videoDuration\", " +
//                    "    lm.\"id\" AS \"lectureMaterialId\", " +
//                    "    lm.\"type\", " +
//                    "    lm.\"file_ex\", " +
//                    "    lm.\"size\", " +
//                    "    lm.\"stored_type\", " +
//                    "    lm.\"file_path\", " +
//                    "    lm.\"external_link\", " +
//                    "    lq.\"id\" AS \"lectureQuestionId\", " +
//                    "    lq.\"time_start\", " +
//                    "    lq.\"time_end\", " +
//                    "    lq.\"question_id\" " +
//                    "FROM \"lecture\" l " +
//                    "LEFT JOIN \"lecture_material\" lm ON l.\"lecture_id\" = lm.\"lecture_id\" " +
//                    "LEFT JOIN \"lecture_question\" lq ON l.\"lecture_id\" = lq.\"lecture_id\" " +
//                    "WHERE l.\"chapter_id\" = :chapterId AND l.\"online_course_id\" = :onlineCourseId")
//    List<LectureDto> getLecture(long chapterId, long onlineCourseId);
    @Query(nativeQuery = true,value = "" +
            "SELECT * FROM lecture l \n" +
            "WHERE l.chapter_id = :chapterId AND l.online_course_id = :onlineCourseId")
    List<Lecture>getLecture(long chapterId,long onlineCourseId);


}
