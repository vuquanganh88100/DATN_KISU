package com.elearning.elearning_support.dtos.resultTestCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultTestCourseDto {
    private long id;
    private long testCourseId;
    private long userId;
//    private Integer resultPoint;
    private Date submitTime;
    private List<ResultTestCourseDetailDto> resultTestCourseDetailDtoList;
    private Long totalTimeTodo;

}
