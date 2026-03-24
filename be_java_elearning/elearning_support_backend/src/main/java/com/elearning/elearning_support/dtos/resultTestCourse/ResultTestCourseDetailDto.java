package com.elearning.elearning_support.dtos.resultTestCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResultTestCourseDetailDto {
    private Long id;
    private Long questionId;
    private Integer[] selectedAnsId;
    private Boolean isCorrect;
    private Long resultTestCourseId;
}
