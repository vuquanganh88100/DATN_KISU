package com.elearning.elearning_support.dtos.testCourse;

import com.elearning.elearning_support.dtos.lecture.LectureQuestionDto;
import com.elearning.elearning_support.entities.testCourse.TestCourseQuestion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCourseDto {
    private Long id;
    private String name;
    private Integer mediumQuestion;
    private Integer easyQuestion;
    private Integer hardQuestion;
    private Integer totalPoint;
    private Integer testCourseWeight;
    private Integer duration;
    private Integer questionQuantity;
    private Integer createdBy;
    private long onlineCourseId;
    private long chapterId;
    private Integer sequence;
    private List<Long> questionId;
    private List<TestCourseQuestionDto> testCourseQuestionDtos;
}
