package com.elearning.elearning_support.dtos.onlineCourse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCourseChapterDto {
    private Long chapterId;
    private int chapterWeight;
    private Long onlineCourseId;
}
