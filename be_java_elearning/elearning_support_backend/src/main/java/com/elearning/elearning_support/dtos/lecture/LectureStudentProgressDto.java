package com.elearning.elearning_support.dtos.lecture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureStudentProgressDto {
    // dungf cho user
    private long id;
    private long studentId;
    private long lectureId;
    private long maxWatchedTime;
    private long totalAnsCorrect;
    private boolean isCompleted;
}
