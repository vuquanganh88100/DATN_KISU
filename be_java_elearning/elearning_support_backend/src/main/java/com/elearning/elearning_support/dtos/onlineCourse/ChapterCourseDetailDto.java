package com.elearning.elearning_support.dtos.onlineCourse;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterCourseDetailDto {
    private long chapterId;
    private  long courseId;
    private String chapterName;
    private int chapterWeight;
    private int chapterSequence;
    private int lectureCount;
    private List<LectureDto> lectureList;
    private List<TestCourseDto>testCourseDtos;
}
