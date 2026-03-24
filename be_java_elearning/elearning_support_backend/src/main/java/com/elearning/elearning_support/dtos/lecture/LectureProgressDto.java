package com.elearning.elearning_support.dtos.lecture;

import lombok.Data;

@Data
public class LectureProgressDto {
    // dung cho admin theo doi tien do sinh vien
    private String lectureName;
    private long lectureId;
    private double percentCorrectAns;
    private double percentWatchedTime;
}
