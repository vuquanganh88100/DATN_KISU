package com.elearning.elearning_support.services.studentLecture;

import com.elearning.elearning_support.dtos.lecture.LearningLectureStudentProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureStudentProgressDto;
import com.elearning.elearning_support.dtos.onlineCourse.*;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface StudentLectureService {
//    OnlineCourseDto getStudentCourseById(Long courseId);
    Map<String, List<LectureDto>> getLectureByChapter(long courseId);
    Map<String, List<TestCourseDto>> getTestCourseByChapter(long courseId);

//    OnlineCourseDetailDto getCourseDetail(long courseId);
    OnlineCourseLearningDetailDto getLearningCourseDetail(long courseId);
    LearningLectureStudentProgressDto getLearningLectureDetail(long lectureId);
    LectureStudentProgressDto saveTime(long time,long userId,long lectureId);
    LectureStudentProgressDto saveTotalAnsCorrect(boolean studentAns,long userId,long lectureId);
    void resetProgress(long userId,long lectureId);
    long getRequiredTime(long lectureId);
    long getRequiredAnsCorrect(long lectureId);
    long findFirstLectureId(List<ChapterCourseDetailDto> chapterCourseDetailDtos);
    CurrentLearningPositionDto determineCurrentPosition(
            List<ChapterCourseDetailDto> chapters,
            List<Long> completedLectures,
            List<Long> completedTestCourse);
    LectureStudentProgressDto checkStatus(long userId, long lectureId);
//    List<OnlineCourseDetailDto> getAllCourseRegistedByStudent();
    QuestionListResDTO exportLectureQuestion(long lectureId);
    Map<Long, LectureProgressDto>getInfomationLectureProgress(long userId);
}
