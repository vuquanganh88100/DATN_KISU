package com.elearning.elearning_support.services.resultTestCourseService;

import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseDto;
import com.elearning.elearning_support.dtos.resultTestCourse.ResultTestCourseResDto;
import org.springframework.stereotype.Service;

@Service
public interface ResultTestCourseService {
    void saveResultTestCourse(ResultTestCourseDto dto);
    ResultTestCourseResDto getResStudentTest(long testCourseId);
    Long getResultId(long userId,long testId);
//    void exportedPdf(long testCourseId);
}
