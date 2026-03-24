package com.elearning.elearning_support.dtos.lecture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureDto {
    private Long id;
    private String lectureName;
    private Integer requiredTime;
    private Integer lectureWeight;
    private Integer sequence;
    private Integer totalQuestion;
    private Integer requiredCorrectAns;
    private  Integer videoDuration;
    private List<LectureMaterialDto>lectureMaterialDtos;
    private String urlVideo;
    private List<LectureQuestionDto>lectureQuestionDtos;
    private long chapterId;
    private long onlineCourseId;
    private Integer requiredWatchTime;
}
