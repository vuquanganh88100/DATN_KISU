package com.elearning.elearning_support.dtos.resultTestCourse;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultTestCourseDetailResDto {
    private Long questionId;
    private String questionContent;
    private List<AnswerResDTO>answerResDTOS;
    private List<Integer>selectedAns;
    private boolean isCorrect;

}
