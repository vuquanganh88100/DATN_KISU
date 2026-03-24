package com.elearning.elearning_support.services.testCourse;

import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public interface TestCourseService {
    public void addTestCourse(TestCourseDto testCourseDto);
    List<TestCourseDto> getAllTestOfChapterCourse(long chapterId, long onlineCourseId);
    TestCourseDto getTestCourseOverview(long testCourseId);
    TestCourseDto getDetailTestCourse(long testCourseId);
    Timestamp getLastTimeToDoTest(long onlineCourseId,long userId);
}
