package com.elearning.elearning_support.services.lecture;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.dtos.lecture.LectureQuestionDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LectureService {
    void addAllInformationLecture(LectureDto lectureDto);
    Integer getMaxSequence(long chapterId,long onlineCourseId);
    List<LectureDto>getAllLectureByChapter(long chapterId,long onlineCourseId);
    QuestionListResDTO getLecture(long lectureId);
    List<LectureDto> getAllLectureOfCourse(long chapterId, long onlineCourseId);
    LectureDto mapLectureToDto(Lecture lecture);
    List<LectureMaterialDto> getLectureMaterials(Long lectureId);
    List<LectureQuestionDto> getLectureQuestions(Long lectureId);
    List<AnswerResDTO> getAnswersByQuestionId(Long questionId);
    LectureDto getLectureDetail(long lectureId);
//    List<ChapterCourseDetailDto> getChapterCourseDetail(long courseId);

}
