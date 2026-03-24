package com.elearning.elearning_support.repositories.lecture;

import com.elearning.elearning_support.entities.lecture.LectureQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LectureQuestionRepository extends JpaRepository<LectureQuestion,Long> {
@Query(nativeQuery = true,value = "" +
        "SELECT lq.* FROM lecture l\n" +
        "INNER JOIN lecture_question lq\n" +
        "ON l.lecture_id =lq.lecture_id\n" +
        "WHERE l.lecture_id = :lectureId")
List<LectureQuestion> getLectureQuestionByLectureId(long lectureId);
@Query(nativeQuery = true, value="" +
        "SELECT lq.*,q.content,q.is_multiple_ans FROM lecture_question lq INNER JOIN question q\n" +
        "ON lq.question_id=q.id WHERE lq.lecture_id =:lectureId")
    List<Object[]> getDetailQuestionByLectureId(long lectureId);
}
