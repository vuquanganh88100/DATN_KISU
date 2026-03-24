package com.elearning.elearning_support.dtos.onlineCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCourseLearningDetailDto {
    private List<Long> lectureCompleted;
    private List<Long>testCourseCompleted;
    private List<ChapterCourseDetailDto> chapterCourseDetailDtos;
    private int totalLecture;
    private long currentLecture;
    private double totalCompletetionPercent;
    private CurrentLearningPositionDto currentPosition;

}
