package com.elearning.elearning_support.dtos.onlineCourse;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
// cho student
public class OnlineCourseDetailDto {
    private OnlineCourseDto courseInfo;
    private Map<String, List<LectureDto>> chapterLectureMap;
    private Map<String,List<TestCourseDto>>testCourseLectureMap;
    private int totalQuestion;
    private long totalVideoDuration;
    private long totalChapter;
    private long totalLecture;
    private double totalCompletionPercent;
}
