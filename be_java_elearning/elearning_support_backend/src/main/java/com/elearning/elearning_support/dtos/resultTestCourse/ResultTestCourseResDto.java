package com.elearning.elearning_support.dtos.resultTestCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultTestCourseResDto {
    private Double studentPoint;
    private Integer testPoint;
    private List<ResultTestCourseDetailResDto> resultTestCourseDetailResDtos;
}
