package com.elearning.elearning_support.dtos.onlineCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentLearningPositionDto {
    private long id;  // ID của lecture hoặc testCourse
    private String type;  // "LECTURE" hoặc "TEST_COURSE"
    private String name;
    private long chapterId;
}
